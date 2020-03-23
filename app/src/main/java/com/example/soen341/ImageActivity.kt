package com.exampl

import FileDataPart
import VolleyImageRequest
import com.android.volley.NetworkResponse
import javax.xml.transform.ErrorListener



import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.soen341.*
import kotlinx.android.synthetic.main.activity_upload_image.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upload_image)

        upload_image_button.setOnClickListener {

            //if permission is set to denied(by default it is), open a small gui to ask user for permissions
            //When the user specifies permissions, a handler function which is overriden below will automically be called to
            //handle this request

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                println("Permission denied")
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),MY_PERMISSIONS_READ_EXTERNAL_STORAGE)

            }
            else{
                println("Permission already granted")
                pickImageFromGallery()
            }
        }
        upload_image_back_button.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    fun SaveImageToServer(imageData:ByteArray?){
        val req = object: VolleyImageRequest(Request.Method.POST, Constants.IMAGE_URL , Response.Listener {
                response -> try{
            println("Network Response")
            println(response)
        }
        catch (e : Exception){
            e.printStackTrace()
        }
        },
            Response.ErrorListener {
                    error ->
                error("Failure")
                println(error)
            }) {
            override fun getByteData(): MutableMap<String, FileDataPart>? {
                var params = HashMap<String,FileDataPart>()
                params["imageFile"] = FileDataPart("supremeleader.jpg",imageData!!,"jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String,String>()
                params.put("likes","1337")
                params.put("comments","Trump buddy")
                params.put("authorId","Rocketman")
                return params
            }
        }
        RequestHandler.getInstance(applicationContext).addToRequestQueue(req)
    }
    fun CreateImageDataFromURI(uri : Uri?) : ByteArray?{
        var imageData : ByteArray? = null;
        val inputStream = contentResolver.openInputStream(uri!!)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
            println("Image in bytes is : " + imageData)
        }
        return imageData
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){

            var uri = data?.data;
            val imageData : ByteArray? = CreateImageDataFromURI(uri)
            SaveImageToServer(imageData)
            image_upload.setImageURI(uri)
         }

        super.onActivityResult(requestCode, resultCode, data)
    }

    //this will take care of the case when the user is prompted for permission. In essence, it will really only
    //happen once unless permissions are explicitely reset
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //this is Kotlin fancy way of doing switch statements. For now we are only checking if the generated request code
        //from this handler function is equal to the user defined request code we wrote into earlier
        when(requestCode){
            MY_PERMISSIONS_READ_EXTERNAL_STORAGE -> {
                //if grant results is empty, it means that the permission request was cancelled.
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                } else{
                    //Toast just open a little window on the app where you can write stuff
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Here by default
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object{
        private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0
        private val IMAGE_PICK_CODE = 0
    }


}


