package com.example.soen341

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

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
}
interface VolleyCallback{
    fun onSucess(result:String) : String
    fun onError(result: String) : String
}