package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
        val email = findViewById(R.id.email) as EditText
        val pass = findViewById(R.id.pass) as EditText

        val btnlogin = findViewById(R.id.login) as Button

        btnlogin.setOnClickListener{
            val ema : String = email.text.toString()
            val pas : String = email.text.toString()

            if (ema.trim().length == 0){
                Toast.makeText(applicationContext, "Email field is empty", Toast.LENGTH_SHORT).show()
            }
            if (pas.trim().length == 0){
                Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT).show()
            }

            if (ema.equals("abc@gmail.com")&&(pas.equals("123456"))){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
