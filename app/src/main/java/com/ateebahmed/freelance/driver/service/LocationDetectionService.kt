package com.ateebahmed.freelance.driver.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.api.login.request.PositionRequest
import com.ateebahmed.freelance.driver.repository.ApiManager

private const val CHANNEL_ID = "Service"
private val TAG = LocationDetectionService::class.java.name

class LocationDetectionService : LifecycleService() {

    private var looper: Looper? = null
    private var handler: ServiceHandler? = null
    private val locationCallback by lazy {
        object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0 ?: return
                lateinit var latLng: LatLng
                for (location in p0.locations) {
                    latLng = LatLng(location.latitude, location.longitude)
                }
                Log.d(TAG, "${latLng.latitude} : ${latLng.longitude}")
                apiManager.updatePosition(PositionRequest(latLng.latitude, latLng.longitude))
            }
        }
    }
    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            this@LocationDetectionService.looper = this@apply.looper
            this@LocationDetectionService.handler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "Background service starting")

        handler?.obtainMessage()?.also {
            it.arg1 = startId
            handler?.sendMessage(it)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        locationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        super.onDestroy()
    }

    /*
    * This is handler class for service tasks
    * Responsible for handling background tasks
    * We use it to detect location of device
    */
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        private val locationRequest by lazy {
            LocationRequest.create()
                .apply {
                    interval = 30000
                    fastestInterval = 15000
                    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                }
        }

        override fun handleMessage(msg: Message) {
            Log.d(TAG, "Message Handler")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "creating notification channel")
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(NotificationChannel(
                        CHANNEL_ID, "LocatonDetectionService",
                        NotificationManager.IMPORTANCE_LOW).apply {
                        description = "Rent24 Location Service Notification"
                    })
            }

            val builder = NotificationCompat.Builder(this@LocationDetectionService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_toolbar_logo)
                .setContentTitle("Location Detection Service")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            startForeground(msg.arg1, builder)

            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "fine location permission granted, updating location request")
                locationClient.requestLocationUpdates(locationRequest,locationCallback, looper)
            } else {
                Log.d(TAG, "fine location permission not allowed, stopping service")
                stopForeground(true)
                stopSelfResult(msg.arg1)
            }
        }
    }
}
