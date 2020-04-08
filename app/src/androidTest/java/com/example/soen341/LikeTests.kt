
package com.example.soen341

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LikeTests{

    /*@Test
    fun canUserLikeImage(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).likeImage("3","120",applicationContext,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
                canUserLikeTwice()
            }
        })
    }
    */
    /*@Test
    fun canUserLikeTwice(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).likeImage("3","120",applicationContext,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertFalse(response!!["message"],response!!["error"].equals("0"))

            }
        })
    }*/
}