package cn.imrhj.wechatjump

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.RelativeLayout
import cn.imrhj.wechatjump.config.Config1280x720
import cn.imrhj.wechatjump.config.Config1920x1080
import cn.imrhj.wechatjump.config.Config2160x1080
import cn.imrhj.wechatjump.config.Config2560x1440

/**
 * Created by rhj on 03/01/2018.
 */
class FullScreenView(context: Context?, height: Int) : RelativeLayout(context) {

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
    private var mCmdListener: (cmd: String) -> Unit = {}
    private var mCloseListener: () -> Unit = {}

    private var mSwipeX1: Int
    private var mSwipeY1: Int
    private var mSwipeX2: Int
    private var mSwipeY2: Int
    private var mPressCoefficient: Double

    init {
        setBackgroundColor(0x40000000)
        mPaintRed = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintRed.color = Color.RED
        mPaintBlue = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintBlue.color = Color.BLUE

        val button = Button(context)
        button.text = "关闭"
        button.setOnClickListener { mCloseListener() }
        addView(button)
        Log.d(Thread.currentThread().name, "class = FullScreenView rhjlog : " + height)

        when (height) {
            1280 -> {
                mSwipeX1 = Config1280x720.SWIPE_X1
                mSwipeY1 = Config1280x720.SWIPE_Y1
                mSwipeX2 = Config1280x720.SWIPE_X2
                mSwipeY2 = Config1280x720.SWIPE_Y2
                mPressCoefficient = Config1280x720.PRESS_COEFFICIENT
            }
            1920 -> { //1080p
                mSwipeX1 = Config1920x1080.SWIPE_X1
                mSwipeY1 = Config1920x1080.SWIPE_Y1
                mSwipeX2 = Config1920x1080.SWIPE_X2
                mSwipeY2 = Config1920x1080.SWIPE_Y2
                mPressCoefficient = Config1920x1080.PRESS_COEFFICIENT
            }
            2160 -> {
                mSwipeX1 = Config2160x1080.SWIPE_X1
                mSwipeY1 = Config2160x1080.SWIPE_Y1
                mSwipeX2 = Config2160x1080.SWIPE_X2
                mSwipeY2 = Config2160x1080.SWIPE_Y2
                mPressCoefficient = Config2160x1080.PRESS_COEFFICIENT
            }
            2560 -> {
                mSwipeX1 = Config2160x1080.SWIPE_X1
                mSwipeY1 = Config2160x1080.SWIPE_Y1
                mSwipeX2 = Config2160x1080.SWIPE_X2
                mSwipeY2 = Config2160x1080.SWIPE_Y2
                mPressCoefficient = Config2160x1080.PRESS_COEFFICIENT
            }
            else -> {
                mSwipeX1 = Config2560x1440.SWIPE_X1
                mSwipeY1 = Config2560x1440.SWIPE_Y1
                mSwipeX2 = Config2560x1440.SWIPE_X2
                mSwipeY2 = Config2560x1440.SWIPE_Y2
                mPressCoefficient = Config2560x1440.PRESS_COEFFICIENT
            }
        }

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
        val pressTime = distance * mPressCoefficient
        val command = "input swipe ${getRdm(mSwipeX1)} ${getRdm(mSwipeY1)} ${getRdm(mSwipeX2)} ${getRdm(mSwipeY2)} ${pressTime.toInt()}"
        this.mCmdListener(command)
    }

    private fun getRdm(value: Int): Int {
        return (value + Math.random() * 50).toInt()

    }

    fun setCommandListener(listener: (cmd: String) -> Unit) {
        this.mCmdListener = listener
    }

    fun setCloseListener(listener: () -> Unit) {
        this.mCloseListener = listener
    }

    fun setConfig(pressCoefficient: Double) {
        this.mPressCoefficient = pressCoefficient
    }

    fun getPressConfig(): Double {
        return mPressCoefficient
    }


}