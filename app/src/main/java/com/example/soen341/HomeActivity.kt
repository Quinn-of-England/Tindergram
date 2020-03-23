package com.example.soen341

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.exampl.ImageActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

open class HomeActivity : AppCompatActivity() {

    var first : Boolean = true

    fun UpdateImage(){
        var image  : ImageContainer? = SharedPrefManager.getInstance(this).GetImageContainer()
        home_image.setImageBitmap(image?.imageBitmap)
        add_comment.setText(image?.comments)
     }

    suspend fun startBackgroundProcess(){

        while(true) {

            UpdateImageList()
            delay(5000)

        }
    }
     fun UpdateImageList(){
         //dispatcher thread working here...
         val req = JsonObjectRequest(
            Request.Method.GET,Constants.BATCH_IMAGES+"?id="+SharedPrefManager.getInstance(this).getUserID(),null, Response.Listener {
                    response ->
                try{
                    //main thread takes over...
                    println("Thread : ${Thread.currentThread().name}")

                    val size: Int = response.getInt("size")
                    for(i in 0..size-1){
                        var array : JSONObject = response.getJSONObject("$i")
                        SharedPrefManager.getInstance(this).AddToImageQueue(array)
                    }
                    if(first){
                        UpdateImage()
                        first = false

                    }
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                    error ->
                println("ERROR")
                println(error.toString())
            })
        RequestHandler.getInstance(applicationContext).addToRequestQueue(req)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val animDrawable = root_layout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()



        // If not logged in, go back to login page
        if (!SharedPrefManager.getInstance(applicationContext).isUserLoggedIn()) {
            val intent = Intent(this, ImageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Adding in toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        CoroutineScope(IO).launch {
        startBackgroundProcess()
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

        // Search bar
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Enter User to Follow"
        searchView.maxWidth = Integer.MAX_VALUE
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload -> {
                val intent = Intent(this, ImageActivity::class.java)
                startActivity(intent)
                return true

            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
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
        val user = SharedPrefManager.getInstance(applicationContext).getUserUsername().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports user follow

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
                params["followedUser"] = query
                params["followerUser"] = user
                return params
            }
        }
        // Request queue
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

}

