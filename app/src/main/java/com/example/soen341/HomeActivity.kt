package com.example.soen341


import android.app.SearchManager

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val animDrawable = root_layout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        // If not logged in, go back to login page
        if (!SharedPrefManager.getInstance(applicationContext).isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Adding in toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Upload image button
        UploadImageActivityButton.setOnClickListener {
            val intent = Intent(this,ImageActivity::class.java)
            startActivity(intent)
        }

        // Code for debugging SharedPrefManager
//        if (SharedPrefManager.getInstance(applicationContext).isUserLoggedIn()) {
//            Toast.makeText(applicationContext, "User pref is logged in", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            Toast.makeText(applicationContext, "ERROR: User pref is not logged in", Toast.LENGTH_SHORT).show()
//        }

    }

    // Adding in main menu in top right
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        // Searchbar
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                followUser(query)
                return false
            }
        })
            return true
    }

    // Handling main menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_log_out -> {
                SharedPrefManager.getInstance(applicationContext).userLogoutPref()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun followUser(query: String){
        // Variables needed
        val url = Constants.FOLLOW_URL
        val id = SharedPrefManager.getInstance(applicationContext).getUserID().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports successful username change
                        // Follow success
                    }// If no response/invalid response received
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["followedId"] = query
                params["followerId"] = id
                return params
            }
        }
        // Request queue
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}
