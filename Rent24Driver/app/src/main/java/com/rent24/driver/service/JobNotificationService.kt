package com.rent24.driver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rent24.driver.R
import com.rent24.driver.repository.ApiManager

private val TAG = JobNotificationService::class.java.name
private const val CHANNEL_ID = "Firebase Notification"

class JobNotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        Log.d(TAG, "firebase token: $p0")

        with(PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()) {
            putString("firebase-token", p0)
            apply()
        }
        ApiManager.getInstance(applicationContext)
            .firebaseToken("android", p0 ?: "")
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        p0?.data?.isNotEmpty()
            .let {
                Log.d(TAG, "Message data ${p0?.data}")
            }

        p0?.notification?.let {
            Log.d(TAG, "Message notification ${it.body}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "creating notification channel")
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(NotificationChannel(CHANNEL_ID, "Firebase Notification Service",
                    NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Rent24 Job Alert"
                })
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_toolbar_logo)
            .setContentTitle("Job Alert")
            .setContentText(p0?.data?.toString() ?: "Data comes here")
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }
    }
}