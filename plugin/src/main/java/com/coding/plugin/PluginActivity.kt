package com.coding.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: stray-coding@foxmail.com
 * @des:插件化(在其他APK中动态加载)Activity的基类，
 * 切记   继承该类的activity，尽量减少使用this
 * 如果在生命周期有特殊操作，子类重写即可，否则判断 该类是否为插件，不是插件的话直接super即可
 */
abstract class PluginActivity : AppCompatActivity(), IPlugin {
    private var TAG = this::class.java.simpleName

    /**
     * 代理activity的实例
     * */
    lateinit var proxy: Activity

    override fun attach(activity: Activity) {
        proxy = activity
    }

    override fun onCreate(bundle: Bundle?) {
        if (!PluginManager.isPlugin) {
            super.onCreate(bundle)
            proxy = this
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        if (!PluginManager.isPlugin) {
            super.onStart()
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        if (!PluginManager.isPlugin) {
            super.onResume()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        if (!PluginManager.isPlugin) {
            super.onPause()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        if (!PluginManager.isPlugin) {
            super.onStop()
        }
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        if (!PluginManager.isPlugin) {
            super.onRestart()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (!PluginManager.isPlugin) {
            super.onNewIntent(intent)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        if (!PluginManager.isPlugin) {
            super.onDestroy()
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if (!PluginManager.isPlugin) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        Log.d(TAG, "finish")
        if (!PluginManager.isPlugin) {
            super.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!PluginManager.isPlugin) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        if (!PluginManager.isPlugin) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (!PluginManager.isPlugin) {
            super.onConfigurationChanged(newConfig)
        }
    }

    override fun startActivity(intent: Intent?) {
        if (!PluginManager.isPlugin) {
            super.startActivity(intent)
        } else {
            proxy.startActivity(intent)
        }
    }

    override fun getLayoutInflater(): LayoutInflater {
        return if (!PluginManager.isPlugin)
            super.getLayoutInflater()
        else
            proxy.layoutInflater

    }

    override fun getWindowManager(): WindowManager? {
        return if (!PluginManager.isPlugin)
            super.getWindowManager()
        else
            proxy.windowManager
    }

    override fun getApplicationInfo(): ApplicationInfo? {
        return if (!PluginManager.isPlugin)
            super.getApplicationInfo()
        else
            proxy.applicationInfo
    }

    override fun getBaseContext(): Context {
        return if (!PluginManager.isPlugin)
            super.getBaseContext()
        else
            proxy.baseContext
    }

    override fun getResources(): Resources {
        return if (!PluginManager.isPlugin)
            super.getResources()
        else
            proxy.resources
    }

    override fun getAssets(): AssetManager {
        return if (!PluginManager.isPlugin)
            super.getAssets()
        else
            proxy.assets
    }

    override fun getClassLoader(): ClassLoader {
        return if (!PluginManager.isPlugin)
            super.getClassLoader()
        else
            proxy.classLoader
    }

    override fun <T : View?> findViewById(id: Int): T {
        return if (!PluginManager.isPlugin) {
            super.findViewById(id)
        } else {
            proxy.findViewById<T>(id)
        }
    }

    //    override fun findViewById(id: Int): View? {
//        return if (!PluginManager.isPlugin) {
//            super.findViewById(id)
//        } else {
//            proxy.findViewById(id)
//        }
//    }

    override fun setContentView(layoutResID: Int) {
        if (!PluginManager.isPlugin) {
            super.setContentView(layoutResID)
        } else {
            proxy.setContentView(layoutResID)
        }
    }

    override fun setContentView(view: View?) {
        if (!PluginManager.isPlugin) {
            super.setContentView(view)
        } else {
            proxy.setContentView(view)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (!PluginManager.isPlugin) {
            super.setContentView(view, params)
        } else {
            proxy.setContentView(view, params)
        }
    }


    override fun getWindow(): Window {
        return if (!PluginManager.isPlugin) {
            super.getWindow()
        } else {
            proxy.window
        }
    }

    override fun getPackageName(): String {
        return if (!PluginManager.isPlugin) {
            super.getPackageName()
        } else {
            proxy.packageName
        }
    }

}