package cn.imrhj.wechatjump

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private val PRESS_COEFFICIENT_PREF = "PRESS_COEFFICIENT_PREF"
    private var show = false
    private var fullShow = false
    private var controlButton: Button? = null
    private var fullScreenView: FullScreenView? = null
    private var mDisplayMetrics = DisplayMetrics()
    private var mDefaultPressCoefficient: Double = 1.392

    private val fullWindowParams = LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT,
            0,
            0,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_SYSTEM_ALERT,
            FLAG_NOT_TOUCH_MODAL.or(FLAG_NOT_FOCUSABLE),
            PixelFormat.TRANSPARENT
    )

    private val windowParams = LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT,
            0,
            0,
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_SYSTEM_ALERT),
            FLAG_NOT_TOUCH_MODAL.or(FLAG_NOT_FOCUSABLE),
            PixelFormat.TRANSPARENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        //获取屏幕分辨率
        windowManager.defaultDisplay.getRealMetrics(mDisplayMetrics)

        controlButton = Button(applicationContext)
        controlButton?.text = "显示蒙层"
        windowParams.gravity = Gravity.START.or(Gravity.TOP)

        fullWindowParams.gravity = Gravity.START.or(Gravity.TOP)
        fullScreenView = FullScreenView(applicationContext, mDisplayMetrics.heightPixels)
        mDefaultPressCoefficient = fullScreenView?.getPressConfig() ?: mDefaultPressCoefficient
        fullScreenView?.setCloseListener {
            showFullScreenView(false)
        }

        fullScreenView?.setCommandListener {
            if (fullShow) {
                windowManager.removeView(fullScreenView)
                fullShow = false
                Thread {
                    Thread.sleep(1000)
                    Commander.execRootCmdSilent(it)
                    Thread.sleep(1000)
                    runOnUiThread {
                        windowManager.addView(fullScreenView, fullWindowParams)
                        fullShow = true
                    }
                }.start()
            }
        }

        controlButton?.setOnClickListener {
            if (Commander.haveRoot()) {
                showFullScreenView(true)
                showView(false)
            } else {
                Toast.makeText(this, "没有Root权限，不好搞啊", Toast.LENGTH_LONG).show()
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

        // 初始化偏好
        val defaultPressCoefficient = fullScreenView?.getPressConfig()?.toFloat() ?: 1.392f
        val personPressCoefficient = sharedPreference.getFloat(PRESS_COEFFICIENT_PREF, defaultPressCoefficient)
        if (defaultPressCoefficient != personPressCoefficient) {
            fullScreenView?.setConfig(personPressCoefficient.toDouble())
        }

        etPressCoefficient.setText(personPressCoefficient.toString(), TextView.BufferType.EDITABLE)
        btnCoefficient.setOnClickListener {
            // 设置值
            val coefficient = etPressCoefficient.text.toString().toDoubleOrNull()
            if (coefficient == null || coefficient == 0.0) {
                Toast.makeText(this, "请检查输入的值", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // 首先隐藏所有显示的
            if (fullShow) showFullScreenView(false)
            if (show) showView(false)
            fullScreenView?.setConfig(coefficient)
            // 存储
            sharedPreference.edit().putFloat(PRESS_COEFFICIENT_PREF, coefficient.toFloat()).apply()
            Toast.makeText(this, "设置成功", Toast.LENGTH_LONG).show()
        }
        btnReset.setOnClickListener {
            if (fullShow) showFullScreenView(false)
            if (show) showView(false)
            etPressCoefficient.setText(mDefaultPressCoefficient.toString(), TextView.BufferType.EDITABLE)
            sharedPreference.edit().putFloat(PRESS_COEFFICIENT_PREF, mDefaultPressCoefficient.toFloat()).apply()
            fullScreenView?.setConfig(mDefaultPressCoefficient)
            Toast.makeText(this, "恢复默认值成功", Toast.LENGTH_LONG).show()
        }
    }

    private fun showFullScreenView(show: Boolean) {
        this.fullShow = show
        if (show) {
            windowManager.addView(fullScreenView, fullWindowParams)
            fullScreenView?.reset()
        } else {
            windowManager.removeView(fullScreenView)
        }
    }

    /**
     * 初始化view,添加悬浮窗
     */
    private fun showView(show: Boolean) {
        this.show = show
        if (show) {
            windowManager.addView(controlButton, windowParams)
        } else {
            windowManager.removeView(controlButton)
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
            windowManager.removeView(controlButton)
        }
        if (fullShow) {
            windowManager.removeView(fullScreenView)
        }
        super.onDestroy()
    }

}
