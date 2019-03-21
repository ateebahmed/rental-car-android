package com.rent24.driver.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.rent24.driver.components.HomeViewModel

private val TAG = JobNotificationService::class.java.name

class JobNotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        Log.d(TAG, "firebase token: $p0")

        HomeViewModel(application).updateNewToken(p0 ?: "")
    }
}