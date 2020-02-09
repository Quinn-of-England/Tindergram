package com.example.test

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class login : AppCompatActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                val intent = Intent(this, home::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
