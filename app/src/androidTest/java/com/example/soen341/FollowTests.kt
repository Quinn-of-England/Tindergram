package com.example.soen341

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class FollowTests {
    @Test
    fun can_user_follow_another_user(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
            RequestHandler.getInstance(applicationContext).followUser(applicationContext,"testAccountChange","vladputin", object :VolleyCallback{
                override fun onResponse(response: MutableMap<String, String>?) {
                    Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
                }
            })
    }
    @Test
    fun can_user_follow_another_user_twice(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).followUser(applicationContext,"testAccountChange","vladputin", object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertFalse(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }
}