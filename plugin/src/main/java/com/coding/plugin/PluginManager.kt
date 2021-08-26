package com.coding.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import dalvik.system.DexClassLoader
import java.lang.reflect.Method


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: stray-coding@foxmail.com
 * @des:插件管理类，主要进行classloader和resource的生成,从而调用apk中的资源和类。
 */
@SuppressLint("StaticFieldLeak")
object PluginManager {
    private const val TAG = "PluginManager"
    const val TAG_NEW_ACTIVITY_NAME = "activity_class_name"

    /**
     * true 作为插件使用，供其他app动态加载该apk,
     * @see loadApk(ctx,apkPath)后，
     * false 作为独立的app使用
     * 在调用 {@link #loadApk(ctx, apkPath)} 方法后，标志位true
     * */
    var isPlugin = false
        private set

    /**
     * 插件apk的包名，用于resource的正确加载
     * */
    var pluginPackageName = ""
    var appCtx: Context? = null
        private set
    var pluginAssets: AssetManager? = null
        private set
    var pluginRes: Resources? = null
        private set
    var pluginDexClassLoader: DexClassLoader? = null
        private set
    var pluginPackageArchiveInfo: PackageInfo? = null
        private set


    @SuppressLint("DiscouragedPrivateApi")
    fun loadApk(ctx: Context?, apkPath: String, packageName: String) {
        if (ctx == null) {
            Log.d(TAG, "ctx is null, apk cannot be loaded dynamically")
            throw RuntimeException("ctx is null, apk cannot be loaded dynamically")
        }
        pluginPackageName = packageName
        isPlugin = true
        try {
            appCtx = ctx.applicationContext
            pluginDexClassLoader = DexClassLoader(
                apkPath,
                appCtx!!.getDir("dex2opt", Context.MODE_PRIVATE).absolutePath,
                null,
                appCtx!!.classLoader
            )
            pluginPackageArchiveInfo =
                appCtx!!.packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)!!

            pluginAssets = AssetManager::class.java.newInstance()
            val addAssetPath: Method =
                AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(pluginAssets, apkPath)
            val superResources: Resources? = ctx.resources
            pluginRes = Resources(
                pluginAssets,
                superResources?.displayMetrics,
                superResources?.configuration
            )
            Log.d(TAG, "dynamic loading of apk success")
        } catch (e: Exception) {
            Log.d(TAG, "dynamic loading of apk failed")
            isPlugin = false
            e.printStackTrace()
        }
    }

    //传入
    fun startActivity(ctx: Context, actName: String) {
        val intent = Intent(ctx, ProxyActivity::class.java)
        intent.putExtra(TAG_NEW_ACTIVITY_NAME, actName)
        ctx.startActivity(intent)
    }

    //传入
    fun startActivity(ctx: Context, actName: String, bundle: Bundle) {
        val intent = Intent(ctx, ProxyActivity::class.java)
        intent.putExtra(TAG_NEW_ACTIVITY_NAME, actName)
        intent.putExtras(bundle)
        ctx.startActivity(intent)
    }
}