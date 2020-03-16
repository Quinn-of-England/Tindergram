package com.example.soen341

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.exampl.ImageActivity

class Utilities:VolleyCallback {
companion object {
    fun instance() : Utilities = Utilities()
}

fun getUserIDFromUsername(username : String, context : Context)  {
    println("HELLLLLLLLLLLLLLLLLLLLLLLLLLLLLO")

    val req = StringRequest(Request.Method.GET,Constants.GETTERS_URL+"getIdFromUsername.php?username=gorbachev", Response.Listener {
        response ->  onSucess(response)
    }, Response.ErrorListener {
        error ->  onError(error.toString())
    })
    RequestHandler.getInstance(context).addToRequestQueue(req)
 }

    override fun onSucess(response: String) :String{
        println("THE ID is : " + response)
        return response
    }

    override fun onError(error: String) : String{
        println("ERROR " + error)
        return error
    }
}