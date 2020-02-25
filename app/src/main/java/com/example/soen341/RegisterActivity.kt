package com.example.soen341

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val buttonClick = findViewById<Button>(R.id.register)
        buttonClick.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        val name = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.enterPass)
        val confirmPassword = findViewById<EditText>(R.id.confirmPass)

        val btnRegister = findViewById<Button>(R.id.register)

        btnRegister.setOnClickListener {
            val inName: String = name.text.toString()
            val inEmail: String = email.text.toString()
            val inPassword: String = password.text.toString()
            val inConfirmPassword: String = confirmPassword.text.toString()

            when {
                inName.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                inEmail.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                inPassword.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                inConfirmPassword.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password confirmation field is empty", Toast.LENGTH_SHORT)
                        .show()
                }
                inPassword != inConfirmPassword -> {
                    Toast.makeText(applicationContext, "Password does not match", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
