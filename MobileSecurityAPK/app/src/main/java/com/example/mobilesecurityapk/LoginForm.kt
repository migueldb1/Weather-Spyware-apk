package com.example.mobilesecurityapk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.mobilesecurityapk.databinding.ActivityLoginFormBinding
import java.security.MessageDigest

class LoginForm : AppCompatActivity() {

    private lateinit var bind: ActivityLoginFormBinding

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginFormBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val dbhelp = DBClass(applicationContext)
        val db = dbhelp.readableDatabase

        bind.btnlogin.setOnClickListener {
            val username = bind.logtxt.text.toString()
            val password = bind.ed3.text.toString()

            // Hash the password using SHA-256
            val md = MessageDigest.getInstance("SHA-256")
            val hashedPassword =
                md.digest(password.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }

            val query = "SELECT * FROM user WHERE username=? AND pswd=?"
            val rs = db.rawQuery(query, arrayOf(username, hashedPassword))

            if (rs.moveToFirst()) {
                val name = rs.getString(rs.getColumnIndex("name"))
                rs.close()
                startActivity(Intent(this, WeatherActivity::class.java).putExtra("name", name))
            } else {
                val ad = AlertDialog.Builder(this)
                ad.setTitle("Message")
                ad.setMessage("Username or password is incorrect!")
                ad.setPositiveButton("Ok", null)
                ad.show()
            }
        }
        bind.regisLink.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
