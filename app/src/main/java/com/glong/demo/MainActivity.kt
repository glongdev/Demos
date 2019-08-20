package com.glong.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startActivity(Intent(this@MainActivity, LandActivity::class.java))
        }
        button2.setOnClickListener {
            startActivity(Intent(this@MainActivity, LandActivity::class.java).apply{
                putExtra("isDemo",false)
            })
        }
    }
}
