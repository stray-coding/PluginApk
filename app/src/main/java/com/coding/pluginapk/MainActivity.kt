package com.coding.pluginapk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.coding.plugin.PluginManager
import com.coding.plugin.ProxyActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_to_plugin).setOnClickListener {
            try {
                startApk()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startApk() {
        val intent = Intent(this@MainActivity, ProxyActivity::class.java)
        intent.putExtra("className", "com.coding.other.Main1Activity")
        startActivity(intent)
    }
}