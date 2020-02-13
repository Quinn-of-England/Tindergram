package com.example.soen341

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonClick = findViewById<Button>(R.id.login)
        buttonClick.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.pass)

        val btnLogin = findViewById<Button>(R.id.login)



        btnLogin.setOnClickListener {
            val ema: String = email.text.toString()
            val pas: String = pass.text.toString()

            when {
                ema.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                ema.trim().isEmpty() && pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Email and Password fields are empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            if (ema == "abc@gmail.com" && (pas == "123456")) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
