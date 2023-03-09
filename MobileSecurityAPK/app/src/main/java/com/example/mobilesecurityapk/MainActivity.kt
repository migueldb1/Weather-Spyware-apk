package com.example.mobilesecurityapk

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mobilesecurityapk.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database
        val dbhelper = DBClass(applicationContext)
        db = dbhelper.writableDatabase

        // Set up button click listener
        binding.btnrgs.setOnClickListener {
            registerAccount()
        }

        // Set up login link click listener
        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginForm::class.java))
        }
    }

    private fun registerAccount() {
        val name = binding.ed1.text.toString()
        val username = binding.ed2.text.toString()
        val password = binding.ed3.text.toString()

        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            return
        }

        // Hash the password using SHA-256
        val md = MessageDigest.getInstance("SHA-256")
        val hashedPassword =
            md.digest(password.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }

        val data = ContentValues().apply {
            put("name", name)
            put("username", username)
            put("pswd", hashedPassword)
        }

        val result = db.insert("user", null, data)

        if (result == -1L) {
            showAlertDialog("Record not added")
        } else {
            showAlertDialog("Account registered successfully")
            clearFields()
        }
    }

    private fun clearFields() {
        binding.ed1.text.clear()
        binding.ed2.text.clear()
        binding.ed3.text.clear()
    }

    private fun showAlertDialog(message: String) {
        val ad = AlertDialog.Builder(this)
        ad.setTitle("Message")
        ad.setMessage(message)
        ad.setPositiveButton("Ok", null)
        ad.show()
    }
}
