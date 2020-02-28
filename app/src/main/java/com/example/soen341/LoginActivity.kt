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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.pass)

        val btnLogin = findViewById<Button>(R.id.login)
        val btnReg = findViewById<Button>(R.id.register)

        btnReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val user: String = username.text.toString()
            val pas: String = pass.text.toString()

            var canLogin = true
            when {
                user.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username field is empty", Toast.LENGTH_SHORT).show()
                    canLogin = false
                }
                user.trim().length < 4 -> {
                    Toast.makeText(applicationContext, "Username must be 4 characters or greater", Toast.LENGTH_SHORT).show()
                    canLogin = false
                }
                pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Password field is empty", Toast.LENGTH_SHORT).show()
                    canLogin = false

                }
                pas.trim().length < 6 -> {
                    Toast.makeText(applicationContext, "Password must be 6 characters or greater", Toast.LENGTH_SHORT).show()
                    canLogin = false

                }
                user.trim().isEmpty() && pas.trim().isEmpty() -> {
                    Toast.makeText(applicationContext, "Username and Password fields are empty", Toast.LENGTH_SHORT).show()
                    canLogin = false
                }
            }
            if (canLogin) {
                loginUser()
            }
        }
    }
    private fun loginUser() {
        // All variables needed imported to method
        val url = Constants.LOGIN_URL
        val name = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.pass)
        val inName: String = name.text.toString()
        val inPassword: String = password.text.toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports successful login
                        //TODO Add sharedPref for user logged in
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent) // User goes to the homepage
                    }// If no response/invalid response received
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = inName
                params["password"] = inPassword
                return params
            }
        }
        // Request queue (using singleton for entire app)
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}