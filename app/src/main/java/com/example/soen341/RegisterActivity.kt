package com.example.soen341

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Variables for extracting text from register page text fields
        val name = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.enterPass)
        val confirmPassword = findViewById<EditText>(R.id.confirmPass)

        // User wishes to return to login page, already has an account
        val returnLogin = findViewById<TextView>(R.id.returnToLogin)
        returnLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // User clicks register
        val btnRegister = findViewById<Button>(R.id.register)
        btnRegister.setOnClickListener {
            // Converting text from register page text fields to string
            val inName: String = name.text.toString()
            val inEmail: String = email.text.toString()
            val inPassword: String = password.text.toString()
            val inConfirmPassword: String = confirmPassword.text.toString()

            // canRegister will go to false if any condition is not met
            // Text field entries will be checked to ensure they follow requirements
            var canRegister = true
            when {
                inName.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username field is empty", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inName.trim().length < 4 -> {
                    Toast.makeText(applicationContext, "Username must be 4 characters or greater", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inEmail.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Email field is empty", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inPassword.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inPassword.trim().length < 6 -> {
                    Toast.makeText(applicationContext, "Password must be 6 characters or greater", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inConfirmPassword.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password confirmation field is empty", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
                inPassword != inConfirmPassword -> {
                    Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    canRegister = false
                }
            }// If all entries match requirements, account will be created
            if (canRegister) {
                registerUser()
            }
        }
    }
    private fun registerUser() {
        // All variables needed imported to method
        val url = DbConstants.REGISTER_URL
        val name = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.enterPass)
        val inName: String = name.text.toString()
        val inEmail: String = email.text.toString()
        val inPassword: String = password.text.toString()
        // Create request queue TODO, make an app-based queue (singleton class)
        val queue = Volley.newRequestQueue(this)

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports successful account addition
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent) // User goes to login page to login with new account
                    }// If no response / invalid response received (no message or error)
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = inName
                params["email"] = inEmail
                params["password"] = inPassword
                return params
            }
        }
        queue.add(stringRequest)
    }
}
