package com.example.soen341

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.HashMap

open class HomeActivity : AppCompatActivity() {

    var first : Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Swipe between images
        val image = findViewById<ImageView>(R.id.home_image)
        image.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {

            // Swipe right will like image and switch to next one
            override fun onSwipeRight() {
                // TODO Add Like Image
                updateImage()
            }

            // Swipe left will switch to next image
            override fun onSwipeLeft() {
                updateImage()
            }
            // Swipe from top to bottom must still be given an action
            override fun onSwipeBottom() {
                // TODO decide what action it does
            }
            // Swipe from bottom to top will add a comment tab
            override fun onSwipeTop() {
                addComment(findViewById(add_comment.id))
                addComment(findViewById(post_comment.id))
            }
        })

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
            //launch two concurrent jobs
            val job1 = launch {
                imageBackgroundProcess()
            }
            val job2 = launch {
                notificationBackgroundProcess()
            }
        }
    }


    fun addComment(view_comment:View){
        view_comment.visibility = if (view_comment.visibility == View.INVISIBLE){
            View.VISIBLE

        } else{
            View.INVISIBLE
        }
    }

    fun updateImage(){
        var image  : ImageContainer? = SharedPrefManager.getInstance(this).getImageContainer()
        home_image.setImageBitmap(image?.getImageBitmap())
        add_comment.setText(image?.comments)
        SharedPrefManager.getInstance(this).updateViewedImages(image?.imageID!!)
        println(SharedPrefManager.getInstance(this).getViewedImages())
     }

    suspend fun imageBackgroundProcess(){

        while(true) {

            RequestHandler.getInstance(this).updateImageList(this)
            delay(10000)
            if(first) {
                withContext(Main) {
                    updateImage()
                    first = false
                }
            }
        }
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

    suspend fun notificationBackgroundProcess(){
        while(true) {
            RequestHandler.getInstance(this).updateNotifications(this)
            delay(10000)

        }
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
                        println("Follow successful")
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

