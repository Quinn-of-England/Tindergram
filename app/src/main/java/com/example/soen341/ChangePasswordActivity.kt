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

class ChangePasswordActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

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
            when
            {
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
            if (canChange)
            {
                val newPass = findViewById<EditText>(R.id.new_password)
                val inPassword: String = newPass.text.toString()
                val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()
                RequestHandler.getInstance(this@ChangePasswordActivity).changePassword(this@ChangePasswordActivity,inPassword,id,object : VolleyCallback{
                    override fun onResponse(response: MutableMap<String, String>?) {
                        assert(response!!["error"].equals("0"))
                        val intent = Intent(this@ChangePasswordActivity, SettingsActivity::class.java)
                        startActivity(intent) // User goes back to the settings page

                    }
                })

            }
        }

        // User clicks cancel
        val btnCancel = findViewById<Button>(R.id.cancel_button)
        btnCancel.setOnClickListener {
            finish()
        }
    }


}
