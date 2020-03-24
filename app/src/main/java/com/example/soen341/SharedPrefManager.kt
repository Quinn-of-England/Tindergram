package com.example.soen341

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import org.json.JSONObject
import java.util.*

class SharedPrefManager constructor(context: Context) {
    private val PREF_NAME = "userSharedPref"
    private val KEY_USERNAME = "username"
    private val KEY_USER_ID = "user_id"
    private val KEY_USER_EMAIL = "user_email"
    private var imageQueue : Queue<ImageContainer>? = LinkedList<ImageContainer>()

    val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SharedPrefManager? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefManager(context).also {
                    INSTANCE = it
                }
            }
    }

    fun getImageContainer() : ImageContainer? {
             return imageQueue?.poll()
    }

    fun addToImageQueue(container : JSONObject) : Unit{
        imageQueue?.offer(ImageContainer(container.getString("image"),
            container.getInt("likes"),container.getString("comments"),
            container.getString("authorId"),container.getInt("id")))
    }

    fun userLoginPref(id:Int, username:String, email:String) {
        val editor = sharedPref.edit()
        editor.putInt(KEY_USER_ID, id)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun isUserLoggedIn() : Boolean {
        return sharedPref.getString(KEY_USERNAME, null) != null
    }

    fun getUserUsername() : String? {
        return sharedPref.getString(KEY_USERNAME, null)
    }

    fun getUserEmail() : String? {
        return sharedPref.getString(KEY_USER_EMAIL, null)
    }

    fun getUserID() : Int {
        return sharedPref.getInt(KEY_USER_ID, 0)
    }

    fun setUserUsername(username:String) {
        val editor = sharedPref.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun setUserEmail(email:String) {
        val editor = sharedPref.edit()
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun userLogoutPref() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}

class ImageContainer(var imageData: String, var likes: Int, var comments: String, var authorID : String, var imageID : Int){
    var imageBitmap : Bitmap? = null
    private fun ConvertBase64ToBitmap(){
        val imageBytes : ByteArray = Base64.decode(this.imageData, Base64.DEFAULT)
        this.imageBitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
    }
    init{
        ConvertBase64ToBitmap()
    }
}
