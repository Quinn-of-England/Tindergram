package com.example.soen341

import FileDataPart
import VolleyImageRequest
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
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
                    for(i in 0..size-1){
                        var array : JSONObject = response.getJSONObject("$i")
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

    fun updateNotifications(context : Context){
        val req = JsonObjectRequest(Request.Method.GET,
            Constants.GET_NOTIFICATIONS + "?userId=" + SharedPrefManager.getInstance(context).getUserID()
            , null, Response.Listener {
                    response ->
                try{
                    if(response.getString("error") == "true")
                        throw JSONException(response.getString("message"))
                    else {

                        var toast : Toast = Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.TOP or Gravity.CENTER,0, 200)
                        toast.show()
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

    fun saveImageToServer(imageData:ByteArray? , context: Context){
        val req = object: VolleyImageRequest(Method.POST, Constants.IMAGE_URL , Response.Listener {
                response ->
            //Toast.makeText(context,"Image posted!",Toast.LENGTH_SHORT).show()
                println("image posted : $response ")
        },
            Response.ErrorListener {
                    error ->
                error("Failure")
                println(error)
            }) {
            override fun getByteData(): MutableMap<String, FileDataPart>? {
                var params = HashMap<String,FileDataPart>()
                //filename is just userID--pictureCount for now
                params["imageFile"] = FileDataPart("imageName", imageData!!,"jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String,String>()
                 params["likes"] = "1337"
                params["comments"] = "hardcoded for now"
                params["authorId"] = SharedPrefManager.getInstance(context).getUserID().toString()
                return params
            }
        }
        this.addToRequestQueue(req)
    }

}