package com.example.soen341

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_change_email.*
import org.json.JSONException
import org.json.JSONObject

class ChangeEmailActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
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
            when
            {
                !inEmail.trim().isEmailValid() ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Email address is not valid",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inEmail.trim().isEmpty() ->
                {
                    Toast.makeText(
                        applicationContext,
                        "Email address field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
            }// If new email address matches requirements, email address will be changed
            if (canChange)
            {         val newEmail = findViewById<EditText>(R.id.new_email)
                val inEmail: String = newEmail.text.toString()
                val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()

                RequestHandler.getInstance(this@ChangeEmailActivity).changeEmail(this@ChangeEmailActivity,inEmail,id,object :VolleyCallback{
                    override fun onResponse(response: MutableMap<String, String>?) {
                        assert(response!!["error"].equals("0"))

                        val intent = Intent(this@ChangeEmailActivity, SettingsActivity::class.java)
                        startActivity(intent) // User goes back to the settings page

                    }
                })
            }
        }

        // User clicks cancel
        val btnCancel = findViewById<Button>(R.id.cancel_button)
        btnCancel.setOnClickListener{
            finish()
        }
    }


    // Check if email address is of valid format
    private fun String.isEmailValid(): Boolean
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
