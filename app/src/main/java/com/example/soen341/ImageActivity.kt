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
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.soen341.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_upload_image.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_home)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

   /*     upload_image_back_button.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } */
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
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){

            var uri = data?.data;
            val imageData : ByteArray? = CreateImageDataFromURI(uri)
            //SaveImageToServer(imageData)

            val image = ImageView(this)
            image.id = View.generateViewId()
            image.setImageURI(uri)

            val home_layout : ConstraintLayout = findViewById<ConstraintLayout>(R.id.root_layout)
            home_layout.addView(image,200,200)

            val set : ConstraintSet = ConstraintSet()
            set.clone(home_layout)
            set.connect(image.id,ConstraintSet.TOP,toolbar.id,ConstraintSet.TOP,100)
            set.connect(image.id,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,100)
            set.connect(image.id,ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,100)
            set.connect(image.id,ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,100)
            var user_id : String? = null

            Utilities.instance().getUserIDFromUsername("gorbachev",this, object : VolleyCallback{
                override fun onSucess(result: String) {
                    user_id = result
                }

                override fun onError(error: String) {
                    println(error)
                }
            })
           // set.createHorizontalChain(ConstraintSet.PARENT_ID,ConstraintSet.LEFT,ConstraintSet.PARENT_ID,
             //   ConstraintSet.RIGHT,images,null,ConstraintSet.CHAIN_SPREAD)
            set.applyTo(home_layout)

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


