package cn.imrhj.wechatjump

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

/**
 * Created by rhj on 03/01/2018.
 */
class FullScreenView(context: Context?) : View(context) {
    private val OFFSET_Y = 200
    private var mShowLine = false
    private var mFirstReady = false
    private var mSecondReady = false
    private var mX1 = 0f
    private var mY1 = 0f
    private var mX2 = 0f
    private var mY2 = 0f
    private val mPaintRed: Paint
    private val mPaintBlue: Paint
    private var mListener: (cmd: String) -> Unit = {}

    init {
        setBackgroundColor(0x40000000)
        mPaintRed = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintRed.color = Color.RED
        mPaintBlue = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBlue.color = Color.BLUE
    }

    fun reset() {
        mShowLine = false
        mFirstReady = false
        mSecondReady = false
        mX1 = 0f
        mY1 = 0f
        mX2 = 0f
        mY2 = 0f
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mFirstReady) {
                    mX1 = event.x
                    mY1 = event.y
                    mShowLine = true
                    invalidate()
                } else if (!mSecondReady) {
                    mX2 = event.x
                    mY2 = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!mFirstReady) {
                    mX1 = event.x
                    mY1 = event.y
                    mFirstReady = true
                    invalidate()
                } else if (!mSecondReady) {
                    mX2 = event.x
                    mY2 = event.y
                    mSecondReady = true
                    invalidate()
                }
            }
        }

        if (mFirstReady && mSecondReady) {
            doCommand()
            reset()
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mShowLine) {
            drawLine(canvas, mX1, mY1 - OFFSET_Y, mPaintRed)
            if (mFirstReady) {
                drawLine(canvas, mX2, mY2 - OFFSET_Y, mPaintBlue)
            }
        }
    }

    private fun drawLine(canvas: Canvas?, x: Float, y: Float, paint: Paint) {
        canvas?.drawLine(x - 200, y, x + 200, y, paint)
        canvas?.drawLine(x, y - 200, x, y + 200, paint)
    }

    private fun doCommand() {
        // 获取距离
        val distance = Math.sqrt(Math.pow((mX2 - mX1).toDouble(), 2.0) + Math.pow((mY2 - mY1).toDouble(), 2.0))
        val pressTime = distance * 1.35
        val command = "input swipe 320 410 320 410 ${pressTime.toInt()}"
        this.mListener(command)
    }

    fun setCommandListener(listener: (cmd: String) -> Unit) {
        this.mListener = listener
    }

}