package com.coding.plugin

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: stray-coding@foxmail.com
 * @des: 代理activity,在该activity(这里暂且叫他proxy)的onCreate()方法中，
 * 反射生成我们需要的activity（这里暂且叫他plugin）的实例，然后调用plugin.onCreate()实现加载；
 * 接着在proxy的其他生命周期中方法中调用plugin的生命周期，从而模拟plugin的正常生命周期路径。
 */
class ProxyActivity : Activity() {
    companion object {
        private const val TAG = "ProxyActivity"
    }

    private var plugin: IPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        if (PluginManager.isPlugin) {
            try {
                val clsName = intent.getStringExtra(PluginManager.TAG_NEW_ACTIVITY_NAME)
                Log.d(TAG, "activity name:$clsName")
                val cls = PluginManager.pluginDexClassLoader!!.loadClass(clsName)
                val newInstance = cls.newInstance()
                if (newInstance is IPlugin) {
                    plugin = newInstance
                    plugin?.attach(this)
                    val bundle = Bundle()
                    plugin?.onCreate(bundle)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun startActivity(intent: Intent) {
        val newIntent = Intent(this, ProxyActivity::class.java)
        newIntent.putExtra(PluginManager.TAG_NEW_ACTIVITY_NAME, intent.component!!.className)
        super.startActivity(newIntent)
    }

//    override fun getBaseContext(): Context {
//        return if (!PluginManager.isPlugin)
//            super.getBaseContext()
//        else
//            PluginManager.appCtx!!
//    }

    override fun getResources(): Resources? {
        return if (!PluginManager.isPlugin)
            super.getResources()
        else
            PluginManager.pluginRes
    }

    override fun getAssets(): AssetManager? {
        return if (!PluginManager.isPlugin)
            super.getAssets()
        else
            PluginManager.pluginAssets
    }

    override fun getClassLoader(): ClassLoader? {
        return if (!PluginManager.isPlugin)
            super.getClassLoader()
        else
            PluginManager.pluginDexClassLoader
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        plugin?.onStart()
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        plugin?.onResume()
        super.onResume()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        plugin?.onRestart()
        super.onRestart()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        plugin?.onPause()
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        plugin?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        plugin?.onDestroy()
        super.onDestroy()
    }

    override fun finish() {
        Log.d(TAG, "finish")
        plugin?.finish()
        super.finish()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        plugin?.onBackPressed()
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d(TAG, "onNewIntent")
        plugin?.onNewIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        plugin?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        plugin?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged")
        plugin?.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }
}