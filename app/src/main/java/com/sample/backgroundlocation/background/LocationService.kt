package com.sample.backgroundlocation.background

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.sample.backgroundlocation.R
import com.sample.backgroundlocation.db.AppDatabase
import com.sample.backgroundlocation.db.LocationDao
import com.sample.backgroundlocation.db.LocationData
import com.sample.backgroundlocation.db.UserDao
import com.sample.backgroundlocation.ui.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationDao: LocationDao
    private lateinit var userDao: UserDao

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

        isRuning = true

        locationDao = AppDatabase.getDatabase(application).locationDao()
        userDao = AppDatabase.getDatabase(application).userDao()

        locationRequest = LocationRequest.create()
        locationRequest.interval = 180000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                locationResult.lastLocation

                GlobalScope.launch {
                    locationDao.insert(LocationData(
                        deviceId = userDao.getUUID(),
                        lat = locationResult.lastLocation.latitude,
                        lng = locationResult.lastLocation.longitude,
                        created_at = System.currentTimeMillis()
                    )
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val intent1 = Intent(this, MainActivity::class.java)
        val activity = PendingIntent.getActivity(this, 0, intent1, 0)
        val build = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Running in Background")
            .setContentText("Accessing Location")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(activity)
            .build()
        startForeground(1, build)


        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRuning = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "CHANNEL_ID",
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        var isRuning = false

    }

}