package com.coding.pluginapk

import android.app.Application
import android.content.Context
import com.coding.plugin.PluginManager
import java.io.File

/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: 229101253@qq.com
 * @des:
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val patchDir: File = this.getDir("patch", Context.MODE_PRIVATE)
        val hotFixFile = File(patchDir, "other-debug.apk")
        PluginManager.loadApk(this, hotFixFile.absolutePath, "com.coding.other")
    }
}