package cn.imrhj.wechatjump

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var show = false
    private var fullShow = false
    private var view: ImageView? = null
    private var fullScreen: FullScreenView? = null

    private val fullWindowParams = LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT,
            0,
            0,
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_PHONE,
            FLAG_NOT_TOUCH_MODAL.or(FLAG_NOT_FOCUSABLE),
            PixelFormat.TRANSPARENT
    )

    private val windowParams = LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT,
            0,
            0,
            (if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_PHONE) + 1,
            FLAG_NOT_TOUCH_MODAL.or(FLAG_NOT_FOCUSABLE),
            PixelFormat.TRANSPARENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

    }

    private fun initView() {
        view = ImageView(applicationContext)
        view?.setImageResource(R.mipmap.ic_launcher)
        windowParams.gravity = Gravity.START.or(Gravity.TOP)

        fullWindowParams.gravity = Gravity.START.or(Gravity.TOP)
        fullScreen = FullScreenView(applicationContext)
        fullScreen?.setCommandListener {
            if (fullShow) {
                windowManager.removeView(fullScreen)
                fullShow = false
                Thread {
                    Thread.sleep(1000)
                    Commander.execRootCmdSilent(it)
                    Thread.sleep(1000)
                    runOnUiThread {
                        windowManager.addView(fullScreen, fullWindowParams)
                        fullShow = true
                    }
                }.start()
            }
        }

        view?.setOnClickListener {
            if (Commander.haveRoot()) {
                showFullScreenView(!fullShow)
            }
        }

        button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkOps()) {
                    showView(!show)
                }
            } else {
                showView(!show)
            }
        }
    }

    private fun showFullScreenView(show: Boolean) {
        this.fullShow = show
        if (show) {
            windowManager.addView(fullScreen, fullWindowParams)
        } else {
            fullScreen?.reset()
            windowManager.removeView(fullScreen)
        }
    }

    /**
     * 初始化view,添加悬浮窗
     */
    private fun showView(show: Boolean) {
        this.show = show
        if (show) {
            windowManager.addView(view, windowParams)
        } else {
            windowManager.removeView(view)
        }
        button.text = if (show) "隐藏悬浮窗" else "显示悬浮窗"
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkOps(): Boolean {
        if (Settings.canDrawOverlays(this)) {
            return true
        }

        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
        startActivityForResult(intent, 10)
        return false
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10) {
            if (Settings.canDrawOverlays(this)) {
                showView(!show)
            }
        }
    }

    override fun onDestroy() {
        if (show) {
            windowManager.removeView(view)
        }
        if (fullShow) {
            windowManager.removeView(fullScreen)
        }
        super.onDestroy()
    }

}
