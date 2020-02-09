package com.example.soen341
import android.app.Activity
import android.content.Intent
import android.widget.Button;
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        upload_image_button.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                println("Permission denied");
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),MY_PERMISSIONS_READ_EXTERNAL_STORAGE)

            }
            else{
                println("Permission already granted")
                pickImageFromGallery()
            }
        }
    }
    private fun pickImageFromGallery(){
        //this action for this intent will be a pick
        val intent = Intent(Intent.ACTION_PICK)
         intent.type = "image/*"

        //handler function is overriden to handle the result of this request
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //check that we are responding to the image pick request and the request was sucesful
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_upload.setImageURI(data?.data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSIONS_READ_EXTERNAL_STORAGE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                } else{
                    Toast.makeText(this,"Permisson denied loser",Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    companion object{
        private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
        private val IMAGE_PICK_CODE = 0;
    }
}
