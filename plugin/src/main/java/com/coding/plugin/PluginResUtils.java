package com.coding.plugin;

import android.util.Log;

/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: 229101253@qq.com
 * @des: 插件资源工具类，主要对插件apk中resource进行生成, 插件中的所有资源必须用该类来获取id
 */
public class PluginResUtils {
    private static final String TAG = "PluginResUtils";

    public static int getViewId(String name) {
        return getId("id", name);
    }

    public static int getStyleId(String name) {
        return getId("style", name);
    }

    public static int getAnimId(String name) {
        return getId("anim", name);
    }

    public static int getStringId(String name) {
        return getId("string", name);
    }

    public static int getArrayId(String name) {
        return getId("array", name);
    }

    public static int getColorId(String name) {
        return getId("color", name);
    }

    public static int getDrawableId(String name) {
        return getId("drawable", name);
    }

    public static int getLayoutId(String name) {
        return getId("layout", name);
    }

    /**
     * 作为插件时：因为res资源只有该插件apk含有，所以包名应该为该Module中AndroidManifest中的包名，并且应该由我们实现的pluginRes去get
     */
    private static int getId(String className, String name) {
        try {
            if (!PluginManager.INSTANCE.isPlugin()) {
                return PluginUtils.getApp().getResources().getIdentifier(name, className, PluginUtils.getApp().getPackageName());
            }
            return PluginManager.INSTANCE.getPluginRes().getIdentifier(name, className, PluginManager.INSTANCE.getPluginPackageName());
        } catch (Exception e) {
            Log.w(TAG, e.getMessage() + "");
        }
        return 0;
    }
}
