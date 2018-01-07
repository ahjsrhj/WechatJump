package cn.imrhj.wechatjump

import android.app.Application
import android.util.Log
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import java.io.File

/**
 * Created by rhj on 07/01/2018.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        val path = File(cacheDir, "log").absolutePath
        Log.d(Thread.currentThread().name, "class = App rhjlog init: path" + path)
        val printer = FilePrinter.Builder(path).build()
        XLog.init(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE, printer, AndroidPrinter())
    }
}