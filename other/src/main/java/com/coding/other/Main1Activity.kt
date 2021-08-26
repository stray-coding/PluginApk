package com.coding.other

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.coding.plugin.PluginActivity

class Main1Activity : PluginActivity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        Log.d("Main1Activity orgin", "onCreate11111")
        Log.d("Main1Activity orgin", "packageName:$packageName")
        Log.d("Main1Activity orgin", "baseContext$baseContext")
        Log.d("Main1Activity orgin", "baseContext$theme")
        setContentView(
            R.layout.activity_main1
            //PluginResUtils.getLayoutId("activity_main1")
        )
        Log.d("Main1Activity orgin", "baseContext$baseContext")
        Log.d("Main1Activity orgin", "baseContext${theme}")

        findViewById<Button>(
            R.id.button
            //PluginResUtils.getViewId("button")
        ).setOnClickListener {
            startActivity(Intent(this@Main1Activity, Main2Activity::class.java))
        }
    }

    override fun onDestroy() {
        Log.d("Main1Activity orgin", "onDestroy11111111")
        super.onDestroy()
    }
}