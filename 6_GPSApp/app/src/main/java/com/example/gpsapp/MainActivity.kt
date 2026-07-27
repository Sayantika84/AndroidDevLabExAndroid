package com.example.gpsapp // Ensure this matches your actual package name

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var tvLocation: TextView
    private lateinit var btnGetLocation: Button
    private lateinit var btnShowMap: Button

    private lateinit var locationManager: LocationManager
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    private val LOCATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setupUI()
    }

    private fun setupUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        tvLocation = TextView(this).apply {
            text = "Location not available.\nPress 'Get Location' to start."
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 80)
        }

        btnGetLocation = Button(this).apply {
            text = "Get Current GPS Location"
            setOnClickListener { fetchLocation() }
        }

        btnShowMap = Button(this).apply {
            text = "Show on Google Maps"
            isEnabled = false // Disabled until we have a location
            setOnClickListener { openGoogleMaps() }
        }

        layout.addView(tvLocation)
        layout.addView(btnGetLocation)
        layout.addView(btnShowMap)

        setContentView(layout)
    }

    private fun fetchLocation() {
        // 1. Check if we have permission to use GPS
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if we don't have it
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
            return
        }

        tvLocation.text = "Fetching location...\n(Ensure GPS is turned on)"

        // 2. Request updates from the GPS provider
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L, // Minimum time interval between updates (ms)
                5f,    // Minimum distance between updates (meters)
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // 3. Define the listener that reacts when GPS gets a fix
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLat = location.latitude
            currentLng = location.longitude

            // Update UI
            tvLocation.text = "Latitude: $currentLat\nLongitude: $currentLng"
            btnShowMap.isEnabled = true

            // Stop requesting updates to save battery once we have the location
            locationManager.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            Toast.makeText(this@MainActivity, "Please enable GPS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGoogleMaps() {
        if (currentLat != null && currentLng != null) {
            // Create a Uniform Resource Identifier (URI) for Google Maps
            // The 'q' parameter adds a marker at the coordinates
            val gmmIntentUri = Uri.parse("geo:$currentLat,$currentLng?q=$currentLat,$currentLng(My+Current+Location)")

            // Create an implicit intent to view a map
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Target Google Maps specifically

            // Attempt to start the activity
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation() // Try fetching again now that permission is granted
            } else {
                Toast.makeText(this, "Permission Denied. Cannot access GPS.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}