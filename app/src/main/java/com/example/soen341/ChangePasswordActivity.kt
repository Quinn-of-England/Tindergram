package com.example.soen341

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Adding in toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Add back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Values from text entries
        val newPass = findViewById<EditText>(R.id.new_password)
        val newConf = findViewById<EditText>(R.id.new_conf)

        // User clicks button to change password
        val btnChange = findViewById<Button>(R.id.change)
        btnChange.setOnClickListener {
            // New passwords from form
            val inPassword: String = newPass.text.toString()
            val inConfirmPassword: String = newConf.text.toString()
            // Check if new password meets requirements
            var canChange = true
            when {
                inPassword.trim().isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        "Password field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inPassword.trim().length < 6 -> {
                    Toast.makeText(
                        applicationContext,
                        "Password must be 6 characters or greater",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inConfirmPassword.trim().isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        "Password confirmation field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inPassword != inConfirmPassword -> {
                    Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT)
                        .show()
                    canChange = false
                }
            }// If new passwords matches requirements, password will be changed
            if (canChange) {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        // Variables needed
        val url = Constants.CHANGE_URL
        val newPass = findViewById<EditText>(R.id.new_password)
        val inPassword: String = newPass.text.toString()
        val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports successful password change
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent) // User goes back to the settings page
                    }// If no response/invalid response received
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["password"] = inPassword
                params["id"] = id
                return params
            }
        }
        // Request queue
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}
