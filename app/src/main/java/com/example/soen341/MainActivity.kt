package com.example.soen341

import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.pass)

        val btnLogin = findViewById<Button>(R.id.login)
        val btnReg = findViewById<Button>(R.id.register)

        btnLogin.setOnClickListener {
            val user: String = username.text.toString()
            val pas: String = pass.text.toString()

            when {
                user.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                user.trim().isEmpty() && pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username and Password fields are empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            if (user == "a" && (pas == "1")) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Wrong username or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
