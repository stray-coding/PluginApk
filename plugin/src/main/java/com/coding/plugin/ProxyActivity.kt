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
 * @emil: 229101253@qq.com
 * @des: 代理activity,在该activity(这里暂且叫他proxy)的onCreate()方法中，
 * 反射生成我们需要的activity（这里暂且叫他plugin）的实例，然后调用plugin.onCreate()实现加载；
 * 接着在proxy的其他生命周期中方法中调用plugin的生命周期，从而模拟plugin的正常生命周期路径。
 */
class ProxyActivity : Activity() {
    companion object {
        private const val TAG = "ProxyActivity"
    }

    private lateinit var pluginInterface: PluginInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        try {
            val clsName = intent.getStringExtra(PluginManager.TAG_NEW_ACTIVITY_NAME)
            Log.d(TAG, "activity name:$clsName")
            val cls = PluginManager.pluginDexClassLoader.loadClass(clsName)
            val newInstance = cls.newInstance()
            if (newInstance is PluginInterface) {
                pluginInterface = newInstance
                pluginInterface.attach(this)
                val bundle = Bundle()
                pluginInterface.onCreate(bundle)
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun startActivity(intent: Intent) {
        val newIntent = Intent(this, ProxyActivity::class.java)
        newIntent.putExtra(PluginManager.TAG_NEW_ACTIVITY_NAME, intent.component!!.className)
        super.startActivity(newIntent)
    }

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
        pluginInterface.onStart()
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        pluginInterface.onResume()
        super.onResume()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        pluginInterface.onRestart()
        super.onRestart()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        pluginInterface.onPause()
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        pluginInterface.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        pluginInterface.onDestroy()
        super.onDestroy()
    }

    override fun finish() {
        Log.d(TAG, "finish")
        pluginInterface.finish()
        super.finish()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        pluginInterface.onBackPressed()
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d(TAG, "onNewIntent")
        pluginInterface.onNewIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        pluginInterface.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        pluginInterface.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged")
        pluginInterface.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }
}