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
import kotlinx.android.synthetic.main.activity_change_email.*
import org.json.JSONException
import org.json.JSONObject

class ChangeEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        // User clicks button to change email address
        val newEmail = findViewById<EditText>(R.id.new_email)
        val btnChange = findViewById<Button>(R.id.change)
        btnChange.setOnClickListener {
            // New email address from form
            val inEmail: String = newEmail.text.toString()
            // Checks to ensure new email meets requirements
            var canChange = true
            when {
                !inEmail.trim().isEmailValid() -> {
                    Toast.makeText(
                        applicationContext,
                        "Email address is not valid",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inEmail.trim().isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        "Email address field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
            }// If new email address matches requirements, email address will be changed
            if (canChange) {
                changeEmail()
            }
        }

        // User clicks cancel
        val btnCancel = findViewById<Button>(R.id.cancel_button)
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun changeEmail() {
        // Variables needed
        val url = Constants.CHANGE_URL
        val newEmail = findViewById<EditText>(R.id.new_email)
        val inEmail: String = newEmail.text.toString()
        val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports successful email address change
                        SharedPrefManager.getInstance(applicationContext).setUserEmail(
                            inEmail
                        )
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
                params["email"] = inEmail
                params["id"] = id
                return params
            }
        }
        // Request queue
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
    // Check if email address is of valid format
    private fun String.isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
