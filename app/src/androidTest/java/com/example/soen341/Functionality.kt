package com.example.soen341

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class Functionality{
    private val testAccount1 : MutableMap<String,String> = RegisterLogin.data()[0]
    private val testAccount2 : MutableMap<String,String> = RegisterLogin.data()[1]

    @Test
    fun a_canUserFollow(){
         val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).followUser(applicationContext,
            testAccount1.getValue("username"), testAccount2.getValue("username"), object :VolleyCallback{
                override fun onResponse(response: MutableMap<String, String>?) {
                    Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
                }
            })
    }
    @Test
    fun b_canUserPostImage(){

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sampleImage = Constants.SAMPLE_IMAGE_BYTES
        var sampleImageData : ByteArray = ByteArray(sampleImage.length)
        sampleImage.forEach {
            sampleImageData += it.toByte()
        }
        RequestHandler.getInstance(applicationContext).saveImageToServer(sampleImageData, testId2,"test",applicationContext,object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }
   @Test
   fun c_canUserLikeImage(){
       val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
       RequestHandler.getInstance(applicationContext).likeImage(testId2,"120",applicationContext,object :VolleyCallback{
           override fun onResponse(response: MutableMap<String, String>?) {
               Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))

           }
       })
   }
    @Test
    fun d_canUserLikeImageTwice(){

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).likeImage(testId2,"120",applicationContext,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertFalse(response!!["message"],response!!["error"].equals("0"))

            }
        })
    }
    @Test
    fun e_canUserViewFollowerImages(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).updateImageList(applicationContext, testId1 , object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"], response!!["error"].equals("0"))
            }
        })
    }
    @Test
    fun f_canUserPostComment(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).postComment("testing comment", testId2, 120 , object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"], response!!["error"].equals("0"))
            }
        })
    }
}
