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

class ChangeUsernameActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        // User click button to change username
        val newName = findViewById<EditText>(R.id.new_name)
        val btnChange = findViewById<Button>(R.id.change)
        btnChange.setOnClickListener{
            // New username from form
            val inName: String = newName.text.toString()
            // Checks to ensure that the new username meets requirements
            var canChange = true
            when
                {
                inName.trim().length < 4 -> {
                    Toast.makeText(
                        applicationContext,
                        "Username must be 4 characters or greater",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                }
                inName.trim().isEmpty() ->
                    {
                    Toast.makeText(
                        applicationContext,
                        "Username field is empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    canChange = false
                    }
            }// If new username matches requirements, username will be changed
            if (canChange)
            {
                changeUser()
            }
        }

        // User clicks cancel
        val btnCancel = findViewById<Button>(R.id.cancel_button)
        btnCancel.setOnClickListener{
            finish()
        }
    }
    private fun changeUser()
        {
        // Variables needed
        val url = Constants.CHANGE_URL
        val newName = findViewById<EditText>(R.id.new_name)
        val inName: String = newName.text.toString()
        val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String>
                { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false")
                    { // Server reports successful username change
                        SharedPrefManager.getInstance(applicationContext).setUserUsername(
                            inName
                        )
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent) // User goes back to the settings page
                    }// If no response/invalid response received
                }catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = inName
                params["id"] = id
                return params
            }
        }
        // Request queue
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}
