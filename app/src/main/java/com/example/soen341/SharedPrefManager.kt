package com.example.soen341

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SharedPrefManager constructor(context: Context) {
    private val PREF_NAME = "userSharedPref"
    private val KEY_USERNAME = "username"
    private val KEY_USER_ID = "user_id"
    private val KEY_USER_EMAIL = "user_email"
    private val KEY_CURRENTLY_VIEWED_IMAGE_ID = "image_id"
    private val KEY_HAS_LIKED_CURRENT_IMAGE = "false"
    private var imageQueue : Queue<ImageContainer>? = LinkedList<ImageContainer>()
    val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private var viewed_images: String?  = ""

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
    fun isImageQueueEmpty() : Boolean{
        return imageQueue!!.isEmpty()
    }
    fun addToImageQueue(container : JSONObject) {

        imageQueue?.offer(ImageContainer(container.getString("image"),
            container.getInt("likes"),container.getJSONArray("comments"),
            container.getString("authorId"),container.getInt("id")))
        updateViewedImages(container.getInt("id"))

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
    fun isUserLikedCurrentImage() : Boolean{
        return sharedPref.getBoolean(KEY_HAS_LIKED_CURRENT_IMAGE,false)
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
    fun setUserHasLikedCurrentImage(){
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_HAS_LIKED_CURRENT_IMAGE, true)
        editor.apply()

    }
    fun setUserEmail(email:String) {
        val editor = sharedPref.edit()
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }
    fun setCurrentImageID(id : Int){
        val editor = sharedPref.edit()
        editor.putInt(KEY_CURRENTLY_VIEWED_IMAGE_ID, id)
        editor.apply()
    }
    fun getCurrentImageID() : Int{
        return sharedPref.getInt(KEY_CURRENTLY_VIEWED_IMAGE_ID,0)
    }

    fun userLogoutPref() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
    fun updateViewedImages(id : Int) {
            this.viewed_images += ((id).toString() + ",")
    }
    fun getViewedImages() : String? {
        return this.viewed_images
    }

}
class ImageContainer(var imageData: String, var likes: Int, var commentArray: JSONArray, var authorID : String, var imageID : Int){
    private var imageBitmap : Bitmap? = null
    private var commentMap : MutableMap<String,String> = mutableMapOf()
    private fun convertBase64ToBitmap(){
        val imageBytes : ByteArray = Base64.decode(this.imageData, Base64.DEFAULT)
        this.imageBitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
    }
    private fun convertJsonArrayToMap(){
        var lambda : (JSONObject) -> Unit = { obj : JSONObject -> commentMap.put(obj.getString("author"),obj.getString("comment"))}
        (0..(commentArray.length()-1)).forEach { lambda(commentArray.getJSONObject(it)) }
    }
    init{
        //image is sent as a base64 string, gotta turn that into a bitmap/uri that android can use...
        convertBase64ToBitmap()
        convertJsonArrayToMap()
    }
    fun getImageBitmap() : Bitmap?{
        return this.imageBitmap
    }
    fun getComments() : MutableMap<String,String>{
        return this.commentMap
    }
}