package com.sample.backgroundlocation.ui

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.sample.backgroundlocation.background.LocationService
import com.sample.backgroundlocation.databinding.ActivityMainBinding
import com.sample.backgroundlocation.db.User
import java.util.*


class MainActivity : AppCompatActivity() {
    private var googleApiClient: GoogleApiClient? = null
    private val REQUESTLOCATION = 199

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //hide data entry fields if service is already running in background
        if (LocationService.isRuning) {
            binding.buttonTracking.visibility = View.GONE
            binding.editTextTextPersonName.visibility = View.GONE
            binding.editTextFreq.visibility = View.GONE
        }

        //check for permission and gps, then add the entered fields and start background tracking service
        binding.buttonTracking.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this,
                    ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled(this)) {
                    if (binding.editTextTextPersonName.text.isEmpty()) {
                        Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (binding.editTextFreq.text.isEmpty()) {
                        Toast.makeText(this, "Enter interval", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    //add date to db
                    mainViewModel.insert(User(UUID.randomUUID().toString(),
                        binding.editTextTextPersonName.text.trim().toString(),binding.editTextFreq.text.toString().toInt()))

                    val intent = Intent(this, LocationService::class.java)
                    ContextCompat.startForegroundService(this, intent)

                    binding.buttonTracking.visibility = View.GONE
                    binding.editTextTextPersonName.visibility = View.GONE
                    binding.editTextFreq.visibility = View.GONE
                } else {
                    enableLoc()
                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 123)
            }
        }

        binding.buttonMap.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }

        binding.buttonStop.setOnClickListener {
            stopService(Intent(this@MainActivity, LocationService::class.java))
        }


    }

    //show gps on popup
    private fun enableLoc() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {
                    googleApiClient?.connect()
                }
            })
            .addOnConnectionFailedListener {
            }.build()
        googleApiClient?.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        this@MainActivity,
                        REQUESTLOCATION
                    )
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }
    }

    //check gps enabled or not
    fun isGPSEnabled(mContext: Context): Boolean {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}