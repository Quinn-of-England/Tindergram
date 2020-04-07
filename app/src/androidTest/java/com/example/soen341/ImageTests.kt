package com.example.soen341

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageTests{
    @Test
    fun canUserUploadImage(){

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sampleImage = Constants.SAMPLE_IMAGE_BYTES
        var sampleImageData : ByteArray = ByteArray(sampleImage.length)
        sampleImage.forEach {
            sampleImageData += it.toByte()
        }
        RequestHandler.getInstance(applicationContext).saveImageToServer(sampleImageData,"test",applicationContext,object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }
    @Test
    fun canUserViewImages(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).updateImageList(applicationContext, object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"], response!!["error"].equals("0"))
            }
        })
    }
}
