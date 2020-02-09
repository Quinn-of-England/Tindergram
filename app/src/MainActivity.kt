package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_click=findViewById<Button>(R.id.click)
            button_click.setOnClickListener{
                val intent = Intent(this, login::class.java)
                startActivity(intent)
            }
    }
}
