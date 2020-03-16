package com.example.soen341

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.exampl.ImageActivity

class Utilities:VolleyCallback {
//A singleton for functionality such as retrieving or setting values using volley requests
companion object {
    fun instance() : Utilities = Utilities()
}

fun getUserIDFromUsername(username : String, context : Context, callback: VolleyCallback)  {

    val req = StringRequest(Request.Method.GET,Constants.GETTERS_URL+"getIdFromUsername.php?username=${username}", Response.Listener {
        response ->  callback.onSucess(response)
    }, Response.ErrorListener {
        error ->  callback.onError(error.toString())
    })
    RequestHandler.getInstance(context).addToRequestQueue(req)
 }

    override fun onSucess(response: String){
    }

    override fun onError(error: String) {
    }
}

//If we want to parse the response of a GET request, we must implemenent a callback function system. They will be overriden during the contruction of the request, to suit our needs.
interface VolleyCallback{
    fun onSucess(result:String)
    fun onError(error: String)
}