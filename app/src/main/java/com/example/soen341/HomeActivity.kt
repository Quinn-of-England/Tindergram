package com.example.soen341


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        UploadImageActivityButton.setOnClickListener {
            val intent = Intent(this,ImageActivity::class.java);
            startActivity(intent);
        }
    }
}
