package com.coding.pluginapk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.coding.plugin.PluginManager
import com.coding.plugin.ProxyActivity
import java.io.File

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_load_apk).setOnClickListener {
            try {
                val patchDir: File = this.getDir("patch", Context.MODE_PRIVATE)
                val hotFixFile = File(patchDir, "other-debug.apk")
                if (!hotFixFile.exists()) {
                    hotFixFile.writeBytes(assets.open("other-debug.apk").readBytes())
                }
                PluginManager.loadApk(this, hotFixFile.absolutePath, "com.coding.other")
                Toast.makeText(this, "load apk success", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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