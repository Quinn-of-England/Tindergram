package com.example.soen341

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(AndroidJUnit4::class)
class CleanUp() {

    @Test
    fun delete(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext

        RequestHandler.getInstance(applicationContext).deleteUser(applicationContext, testId1, object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"], response["error"].equals("0"))
            }
        })

        RequestHandler.getInstance(applicationContext).deleteUser(applicationContext,testId2,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"], response["error"].equals("0"))
            }
        })
    }
}
