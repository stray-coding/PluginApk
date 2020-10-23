package com.coding.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import dalvik.system.DexClassLoader
import java.lang.reflect.Method


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: 229101253@qq.com
 * @des:插件管理类，主要进行class和resource加载
 */

object PluginManager {
    private const val TAG = "PluginManager"
    const val TAG_IS_PLUGIN = "is_plugin"

    /**
     * true 作为插件使用，供其他app动态加载该apk,
     * @see loadApk(ctx,apkPath)后，
     * false 作为独立的app使用
     * 在调用 {@link #loadApk(ctx, apkPath)} 方法后，标志位true
     * */
    var is_plugin = false
        private set

    /**
     * 插件apk的包名，用于resource的正确加载
     * */
    var plugin_package_name = ""
    private lateinit var appCtx: Context
    lateinit var pluginAssets: AssetManager
    lateinit var pluginRes: Resources
    lateinit var pluginDexClassLoader: DexClassLoader
    lateinit var pluginPackageArchiveInfo: PackageInfo


    @SuppressLint("DiscouragedPrivateApi")
    fun loadApk(ctx: Context?, apkPath: String, packageName: String) {
        if (ctx == null) {
            Log.d(TAG, "ctx is null, apk cannot be loaded dynamically")
            throw RuntimeException("ctx is null, apk cannot be loaded dynamically")
        }
        plugin_package_name = packageName
        is_plugin = true
        try {
            appCtx = ctx.applicationContext
            pluginDexClassLoader = DexClassLoader(
                apkPath,
                appCtx.getDir("dexOpt", Context.MODE_PRIVATE).absolutePath,
                null,
                appCtx.classLoader
            )
            pluginPackageArchiveInfo =
                appCtx.packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)!!
            /**
             * 生成assets、resource
             * */
            pluginAssets = AssetManager::class.java.newInstance()
            val addAssetPath: Method = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(pluginAssets, apkPath)
            val superResources: Resources? = ctx.resources
            pluginRes = Resources(
                pluginAssets,
                superResources?.displayMetrics,
                superResources?.configuration
            )
            Log.d(TAG, "dynamic loading of apk success")
        } catch (e: Exception) {
            Log.d(TAG, "dynamic loading of apl failed")
            is_plugin = false
            e.printStackTrace()
        }
    }
}