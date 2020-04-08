package com.example.soen341

import FileDataPart
import VolleyImageRequest
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.ConstraintAnchor
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import java.nio.charset.Charset

class RequestHandler constructor(context: Context)
{
    companion object
    {
        @Volatile
        private var INSTANCE: RequestHandler? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this)
            {
                INSTANCE ?: RequestHandler(context).also{
                    INSTANCE = it
                }
            }
    }

    // Variables for notification
    val CHANNEL_ID = "com.example.soen341"
    val textTitle = "New Picture Uploaded"
    val intent = Intent(context, HomeActivity::class.java).apply{
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    // Create requestQueue
    val requestQueue: RequestQueue by lazy{
        Volley.newRequestQueue(context.applicationContext)
    }

    // Add any request to request queue
    fun <T> addToRequestQueue(req: Request<T>)
    {
        requestQueue.add(req)
    }
     fun loginUser(context : Context , name: String, password: String, callback: VolleyCallback)
    {
        // All variables needed imported to method
        val url = Constants.LOGIN_URL

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String>
            { response ->
                try
                {
                    val obj = JSONObject(response)

                    if (obj.getString("error").equals("true")){
                        throw JSONException(obj.getString("message"))
                    }
                    else { // Server reports successful login
                        SharedPrefManager.getInstance(context).userLoginPref(
                            obj.getInt("id"),
                            obj.getString("username"),
                            obj.getString("email")
                        )
                        callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))

                    }// If no response/invalid response received
                } catch (e: JSONException)
                {
                    callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))

                }
            },
            Response.ErrorListener {
                callback.onResponse(mutableMapOf("error" to "1","message" to it.toString()))
            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = name
                params["password"] = password
                return params
            }
        }
        // Request queue
        this.addToRequestQueue(stringRequest)
    }
    fun registerUser(context: Context, name : String, email : String, password : String, callback: VolleyCallback)
    {
        // All variables needed imported to method
        val url = Constants.REGISTER_URL


        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response -> // JSON response from the server
                try
                {
                    val obj = JSONObject(response)
                    if(obj.getString("error").equals("true"))
                        throw JSONException(obj.getString("message"))

                    else { // Server reports successful account addition
                        SharedPrefManager.getInstance(context).userLoginPref(
                            obj.getInt("id"),
                            obj.getString("username"),
                            obj.getString("email")
                        )
                        callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))

                    }// If no response/invalid response received
                }catch (e: JSONException)
                {
                    callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))
                }
            },
            Response.ErrorListener {
                callback.onResponse(mutableMapOf("error" to "1","message" to it.toString()))

            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = name
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        this.addToRequestQueue(stringRequest)
    }
    fun updateImageList(context: Context, callback : VolleyCallback)
    {

        //dispatcher thread working here...
        val req = JsonObjectRequest(
            Request.Method.GET,Constants.BATCH_IMAGES+"?id="+SharedPrefManager.getInstance(context).getUserID()
            + "&imageIdsAlreadySeen=" + SharedPrefManager.getInstance(context).getViewedImages(),null, Response.Listener {
                    response ->
                try
                {
                    //main thread takes over...
                    if(response.getString("error").equals("1")){
                        throw JSONException(response.getString("message"))
                    }
                    else {
                        callback.onResponse(mutableMapOf("error" to "0","message" to response.getString("message")))
                    }
                    val size: Int = response.getInt("size")

                    for(i in 0..size-1)
                    {
                        var array : JSONObject = response.getJSONObject("$i")

                    SharedPrefManager.getInstance(context).addToImageQueue(array)

                    }

                }
                catch (e : Exception)
                {
                    callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                    error ->
                callback.onResponse(mutableMapOf("error" to "1","message" to error.toString()))
            })
        this.addToRequestQueue(req)

    }

    fun updateNotifications(context: Context)
    {
        val req = JsonObjectRequest(Request.Method.GET,
            Constants.GET_NOTIFICATIONS + "?userId=" + SharedPrefManager.getInstance(context).getUserID()
            , null, Response.Listener {
                    response ->
                try
                {
                    if(response.getString("error") == "true")
                        throw JSONException(response.getString("message"))
                    else
                    {
                        if(! response.getString("message").equals(""))
                        {
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_whatshot_black_24dp)
                            .setContentTitle(textTitle)
                            .setContentText(response.getString("message"))
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        with(NotificationManagerCompat.from(context))
                        {
                            // notificationId is a unique int for each notification that you must define
                            notify(0, builder.build())
                        }
                        }
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                    error -> error.printStackTrace()
            })
        this.addToRequestQueue(req)
    }

    fun saveImageToServer(imageData:ByteArray? , comments : String ,context: Context, callback: VolleyCallback)
    {
        val req = object: VolleyImageRequest(Method.GET, Constants.IMAGE_URL , Response.Listener {
                response ->

            val obj = JSONObject(response.data.toString(Charsets.UTF_8))
            try{
                 if(obj.getString("error").equals("1"))
                    throw JSONException(obj.getString("message"))
                else {
                    Toast.makeText(context, "Image posted!", Toast.LENGTH_SHORT).show()
                    callback.onResponse(
                        mutableMapOf("error" to "0", "message" to obj.getString("message"))
                    )
                    }
            }
            catch (e : JSONException){
                callback.onResponse(mutableMapOf("error" to "1","message" to e.toString() ))
            }
        },
            Response.ErrorListener { error ->
                callback.onResponse(mutableMapOf("error" to "1","message" to error.toString() ))

            }) {
            override fun getByteData(): MutableMap<String, FileDataPart>?
            {
                val params = HashMap<String,FileDataPart>()
                params["imageFile"] = FileDataPart("imageName", imageData!!,"jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String>
            {
                val params = HashMap<String,String>()
                params["likes"] = "0"
                params["comments"] = comments
                params["authorId"] = SharedPrefManager.getInstance(context).getUserID().toString()
                return params
            }
        }
        this.addToRequestQueue(req)

    }

    fun followUser(query: String, context: Context)
    {
        // Variables needed
        val url = Constants.FOLLOW_URL
        val user = SharedPrefManager.getInstance(context).getUserUsername().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // String response from the server
                try
                {
                    val obj = JSONObject(response)
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false")
                    { // Server reports user follow
                        println("Follow successful")
                    }// If no response/invalid response received
                }catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["followedUser"] = query
                params["followerUser"] = user
                return params
            }
        }
        // Request queue
        this.addToRequestQueue(stringRequest)
    }
    fun postComment(comment : String, username : String , imageId : Int)
    {
        var status : Boolean = false
        val req = object : StringRequest(
            Method.POST, Constants.COMMENT_PICTURE,
            Response.Listener<String> { response -> // String response from the server
                try
                {

                    val obj : JSONObject = JSONObject(response)
                    if (obj.getString("error") == "true")
                    {
                        throw JSONException(obj.getString("message"))
                    }
                    else
                    {
                        status = true
                    }
                }catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                    error -> println(error) })
           {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()
                params["username"] = username
                params["id"] = imageId.toString()
                params["comment"] = comment
                return params
            }
        }
        this.addToRequestQueue(req)

    }

    fun likeImage(userId : String, imageId : String, context: Context, callback: VolleyCallback)
    {
        val req = object  : StringRequest(Method.POST, Constants.LIKE,
                Response.Listener { response ->
                    try
                    {
                        val obj = JSONObject(response)
                        if(obj.getString("error").equals("true"))
                            throw JSONException(obj.getString("message"))
                        else
                        {
                            callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))
                        }
                    }
                    catch (e : JSONException)
                    {

                        Toast.makeText(context,"You've aleady liked this image",Toast.LENGTH_SHORT).show()
                        callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))
                    }
                },
            Response.ErrorListener { error ->
                callback.onResponse(mutableMapOf("error" to "1","message" to error.toString()))
            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()
                params["userId"] = userId
                params["imageId"] = imageId
                return params
            }
        }
        this.addToRequestQueue(req)
    }
     fun changeEmail(context: Context, email : String, userId : String , callback: VolleyCallback)
    {
        // Variables needed
        val url = Constants.CHANGE_URL

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String>
            { response -> // JSON response from the server
                try
                {
                    val obj = JSONObject(response)
                    if (obj.getString("error").equals("true")){
                        throw JSONException(obj.getString("message"))
                    }
                    else
                    { // Server reports successful email address change
                        SharedPrefManager.getInstance(context).setUserEmail(email)
                        callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))

                    }// If no response/invalid response received
                }catch (e: JSONException){
                    callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))

                }
            },
            Response.ErrorListener {
                callback.onResponse(mutableMapOf("error" to "1","message" to it.toString()))
            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["email"] = email
                params["id"] = userId
                return params
            }
        }
        // Request queue
        this.addToRequestQueue(stringRequest)
    }

    fun changePassword(context: Context, password : String, userId : String, callback: VolleyCallback)
    {
    // Variables needed
    val url = Constants.CHANGE_URL

    // String request created, when created will execute a POST to the SQL server
    val stringRequest = object : StringRequest(
        Method.POST, url,
        Response.Listener<String>
        { response -> // JSON response from the server
            try
            {
                val obj = JSONObject(response)

                if (obj.getString("error").equals("true"))
                    throw JSONException(obj.getString("message"))
                else{

                    callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))

                }// If no response/invalid response received
            }catch (e: JSONException)
            {
                callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))
            }
        },
        Response.ErrorListener {
            callback.onResponse(mutableMapOf("error" to "1","message" to it.toString()))

        }){
        @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String>
        { // Parameters added to POST request
            val params = HashMap<String, String>()
            params["password"] = password
            params["id"] = userId
            return params
        }
    }
    // Request queue
    this.addToRequestQueue(stringRequest)
}
    fun changeUsername(context: Context, username : String, userId : String, callback: VolleyCallback)
    {
        // Variables needed
        val url = Constants.CHANGE_URL

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String>
            { response -> // JSON response from the server
                try
                {
                    val obj = JSONObject(response)

                    if (obj.getString("error").equals("true"))
                        throw JSONException(obj.getString("message"))
                    else{
                        SharedPrefManager.getInstance(context).setUserUsername(username)
                        callback.onResponse(mutableMapOf("error" to "0","message" to obj.getString("message")))

                    }// If no response/invalid response received
                }catch (e: JSONException)
                {
                    callback.onResponse(mutableMapOf("error" to "1","message" to e.toString()))
                }
            },
            Response.ErrorListener {
                callback.onResponse(mutableMapOf("error" to "1","message" to it.toString()))

            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["username"] = username
                params["id"] = userId
                return params
            }
        }
        // Request queue
        this.addToRequestQueue(stringRequest)
    }
}