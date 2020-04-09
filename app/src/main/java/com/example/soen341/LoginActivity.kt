package com.example.soen341

import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // If already logged in, skip login page
        if (SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
        {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Variables for extracting text from login page text fields
        val username = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.pass)

        // User clicks on register
        val btnReg = findViewById<Button>(R.id.register)
        btnReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // User clicks on login
        val btnLogin = findViewById<Button>(R.id.login)
        btnLogin.setOnClickListener {
            val user: String = username.text.toString()
            val pas: String = pass.text.toString()

            var canLogin = true
            when
            {
                user.trim().isEmpty() ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Username field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canLogin = false
                }
                user.trim().length < 4 ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Username must be 4 characters or greater",
                        Toast.LENGTH_SHORT
                    ).show()
                    canLogin = false
                }
                pas.trim().isEmpty() ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Password field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canLogin = false

                }
                pas.trim().length < 6 ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Password must be 6 characters or greater",
                        Toast.LENGTH_SHORT
                    ).show()
                    canLogin = false

                }
                user.trim().isEmpty() && pas.trim().isEmpty() ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Username and Password fields are empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canLogin = false
                }
            }
            if (canLogin)
            {
                val name = findViewById<EditText>(R.id.username)
                val password = findViewById<EditText>(R.id.pass)
                val inName: String = name.text.toString()
                val inPassword: String = password.text.toString()

                RequestHandler.getInstance(this@LoginActivity).loginUser(this@LoginActivity,inName,inPassword , object : VolleyCallback{
                    override fun onResponse(response: MutableMap<String, String>?) {
                        assert(response!!["error"].equals("0"))
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                })
            }
        }
    }

    override fun onBackPressed()
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }


}
