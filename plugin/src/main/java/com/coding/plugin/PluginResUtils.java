package com.coding.plugin;

import android.util.Log;


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

    private static int getId(String className, String name) {
        /**
         * 作为插件时：因为res资源只有该插件apk含有，所以报名应该为该Module中AndroidManifest中的包名，并且应该由我们实现的pluginRes去get
         */
        try {
            if (!PluginManager.INSTANCE.is_plugin()) {
                return PluginUtils.getApp().getResources().getIdentifier(name, className, PluginUtils.getApp().getPackageName());
            } else {
                return PluginManager.INSTANCE.getPluginRes().getIdentifier(name, className, PluginManager.INSTANCE.getPlugin_package_name());
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage() + "");
        }
        return 0;
    }
}
