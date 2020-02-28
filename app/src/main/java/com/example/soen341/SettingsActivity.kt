package com.example.soen341

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // If not logged in, go back to login page
        if (!SharedPrefManager.getInstance(applicationContext).isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle Buttons
        val btnUser = findViewById<Button>(R.id.change_user)
        btnUser.setOnClickListener{
            val intent = Intent(this, ChangeUsernameActivity::class.java)
            startActivity(intent)
        }
        val btnEmail = findViewById<Button>(R.id.change_email)
        btnEmail.setOnClickListener {
            val intent = Intent(this, ChangeEmailActivity::class.java)
            startActivity(intent)
        }
        val btnPass = findViewById<Button>(R.id.change_pass)
        btnPass.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // Add back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Show current username
        val user = findViewById<TextView>(R.id.show_username)
        user.text = SharedPrefManager.getInstance(applicationContext).getUserUsername()

        // Show current email address
        val email = findViewById<TextView>(R.id.show_email)
        email.text = SharedPrefManager.getInstance(applicationContext).getUserEmail()
    }

    // Adding in main menu in top right
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    // Handling main menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_log_out -> {
                SharedPrefManager.getInstance(applicationContext).userLogoutPref()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
