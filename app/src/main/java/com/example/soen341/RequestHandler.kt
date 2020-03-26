package com.example.soen341

import FileDataPart
import VolleyImageRequest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class RequestHandler constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: RequestHandler? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RequestHandler(context).also {
                    INSTANCE = it
                }
            }
    }

    // Variables for notification
    val CHANNEL_ID = "com.example.soen341"
    val textTitle = "New Picture Uploaded"
    val intent = Intent(context, HomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    // Create requestQueue
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    // Add any request to request queue
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun updateImageList(context: Context){
        //dispatcher thread working here...
        val req = JsonObjectRequest(
            Request.Method.GET,Constants.BATCH_IMAGES+"?id="+SharedPrefManager.getInstance(context).getUserID()
            ,null, Response.Listener {
                    response ->
                try{
                    //main thread takes over...
                    println("Thread : ${Thread.currentThread().name}")

                    val size: Int = response.getInt("size")
                    for(i in 0 until size){
                        val array : JSONObject = response.getJSONObject("$i")
                        SharedPrefManager.getInstance(context).addToImageQueue(array)
                    }
                    //when you first an image should appear, regardless of the swipe ( theres pron a better way of doing )
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
        this.addToRequestQueue(req)

    }

    fun updateNotifications(context: Context){
        val req = JsonObjectRequest(Request.Method.GET,
            Constants.GET_NOTIFICATIONS + "?userId=" + SharedPrefManager.getInstance(context).getUserID()
            , null, Response.Listener {
                    response ->
                try{
                    if(response.getString("error") == "true")
                        throw JSONException(response.getString("message"))
                    else {
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_whatshot_black_24dp)
                            .setContentTitle(textTitle)
                            .setContentText(response.getString("message"))
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(0, builder.build())
                        }
                    }
                }
                catch (e : Exception){
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                    error -> error.printStackTrace()
            })
        this.addToRequestQueue(req)
    }

    fun saveImageToServer(imageData:ByteArray? , comments : String ,context: Context){
        val req = object: VolleyImageRequest(Method.POST, Constants.IMAGE_URL , Response.Listener { response ->
            Toast.makeText(context,"Image posted!",Toast.LENGTH_SHORT).show()
        },
            Response.ErrorListener { error ->
                error("Failure")
            }) {
            override fun getByteData(): MutableMap<String, FileDataPart>? {
                val params = HashMap<String,FileDataPart>()
                //filename is just userID--pictureCount for now
                params["imageFile"] = FileDataPart("imageName", imageData!!,"jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String,String>()
                params["likes"] = "1337"
                params["comments"] = comments
                params["authorId"] = SharedPrefManager.getInstance(context).getUserID().toString()
                return params
            }
        }
        this.addToRequestQueue(req)
    }

    fun followUser(query: String, context: Context){
        // Variables needed
        val url = Constants.FOLLOW_URL
        val user = SharedPrefManager.getInstance(context).getUserUsername().toString()

        // String request created, when created will execute a POST to the SQL server
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response -> // String response from the server
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show() // Server output printed to user
                    if (obj.getString("error") == "false") { // Server reports user follow
                        println("Follow successful")
                    }// If no response/invalid response received
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show() }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> { // Parameters added to POST request
                val params = HashMap<String, String>()
                params["followedUser"] = query
                params["followerUser"] = user
                return params
            }
        }
        // Request queue
        this.addToRequestQueue(stringRequest)
    }

}