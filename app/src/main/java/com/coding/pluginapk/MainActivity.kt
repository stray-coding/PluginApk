package com.coding.pluginapk

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coding.plugin.PluginManager
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_load_apk).setOnClickListener {
            try {
                val patchDir: File = this.getDir("patch", Context.MODE_PRIVATE)
                val hotFixFile = File(patchDir, "other-debug.apk")
                hotFixFile.writeBytes(assets.open("other-debug.apk").readBytes())
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
        PluginManager.startActivity(this@MainActivity, "com.coding.other.Main1Activity")
//        val intent = Intent(this@MainActivity, ProxyActivity::class.java)
//        intent.putExtra(PluginManager.TAG_NEW_ACTIVITY_NAME, "com.coding.other.Main1Activity")
//        startActivity(intent)
    }
}