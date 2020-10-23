package com.coding.other

import android.os.Bundle
import android.util.Log
import com.coding.plugin.PluginActivity
import com.coding.plugin.PluginResUtils

class Main2Activity : PluginActivity() {
    override fun onCreate(bundle: Bundle?) {

        super.onCreate(bundle)
        Log.d("Main2Activity orgin", "onCreate2222222")
        setContentView(
            PluginResUtils.getLayoutId("activity_main2")
            //R.layout.activity_main2
            //resources.getIdentifier("activity_main2", "layout", packageName)
        )
    }

    override fun onDestroy() {
        Log.d("Main2Activity orgin", "onDestroy222222")
        super.onDestroy()

    }
}