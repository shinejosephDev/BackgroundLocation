package com.sample.backgroundlocation.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sample.backgroundlocation.R
import com.sample.backgroundlocation.databinding.ActivityMapsBinding
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var binding: ActivityMapsBinding

    private lateinit var mMap: GoogleMap


    var startDateInMillis: Long = 0
    var endDateInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        //show date picker
        binding.btnStartDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this@MapsActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear,
                            dayOfMonth, 0,
                            0] =
                        0
                    startDateInMillis = calendar.timeInMillis

                    val format = SimpleDateFormat("MMM-dd-yyyy")
                    binding.btnStartDate.text = format.format(calendar.time)
                },
                year,
                month,
                day)

            dpd.show()
        }

        //show date picker
        binding.btnEndDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this@MapsActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear,
                            dayOfMonth, 0,
                            0] =
                        0
                    endDateInMillis = calendar.timeInMillis

                    val format = SimpleDateFormat("MMM-dd-yyyy")
                    binding.btnEndDate.text = format.format(calendar.time)
                },
                year,
                month,
                day)

            dpd.show()
        }

        binding.btnApply.setOnClickListener {
            mMap.clear()
            mapViewModel.getLocationsTimeRange(startDateInMillis,endDateInMillis).observe(this,
                Observer {
                    for (locationData in it) {
                        val marker = LatLng(locationData.lat, locationData.lng)
                        mMap.addMarker(MarkerOptions().position(marker).title(locationData.uid.toString()))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 18f))
                    }
                })
        }

        binding.btnReset.setOnClickListener {
            mapViewModel.getLocations().observe(this, Observer {
                mMap.clear()
                for (locationData in it) {
                    val marker = LatLng(locationData.lat, locationData.lng)
                    mMap.addMarker(MarkerOptions().position(marker).title(locationData.uid.toString()))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 18f))
                }
            })
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //show all the coordinates in the db
        mapViewModel.getLocations().observe(this, Observer {
            for (locationData in it) {
                val marker = LatLng(locationData.lat, locationData.lng)
                mMap.addMarker(MarkerOptions().position(marker).title(locationData.uid.toString()))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 18f))
            }
        })

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
    }
}