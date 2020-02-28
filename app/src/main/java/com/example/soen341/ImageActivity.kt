package com.example.soen341

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_upload_image.*

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
                //open the permission window for this activity, specify the type of permission im asking for, and store the
                //result code in MY_PERMISSIONS_READ_EXTERNAL_STORE which is defined at the bottom
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),MY_PERMISSIONS_READ_EXTERNAL_STORAGE)

            }
            else{
                //if permission already granted(permissions will always be saved unless you forcefully reset them
                //thus from the time the permission was granted, this code will always happen from the on
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
        //this action for this intent will be a 'pick', it will by of type image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        //An image picker GUI will be automically genetared, and the result of the activity
        // will by written to IMAGE_PICK_CODE (defined below)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handler function is overriden to handle the result of this request from the image pick activity.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //check that the request was sucesful and we are responding to the image pick request by checking that the
        // generated request code is equal to the user defined request code (for now it doesnt matter since this is the only request being made)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //set the imageview to the data that is contained by this handler
            image_upload.setImageURI(data?.data)
        }
        //This is just here by default
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


