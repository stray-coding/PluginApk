package com.coding.plugin

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: 229101253@qq.com
 * @des:activity的生命周期管理
 */
interface PluginInterface {
    fun attach(activity: Activity)
    fun onCreate(saveInstance: Bundle?)
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onNewIntent(newIntent: Intent?)
    fun onRestart()
    fun onDestroy()
    fun finish()
    fun onBackPressed()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    fun onConfigurationChanged(newConfig: Configuration)
}