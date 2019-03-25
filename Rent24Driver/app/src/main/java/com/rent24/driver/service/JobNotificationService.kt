package com.rent24.driver.service

import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.rent24.driver.repository.ApiManager

private val TAG = JobNotificationService::class.java.name

class JobNotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        Log.d(TAG, "firebase token: $p0")

        with(PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()) {
            putString("token", p0)
            apply()
        }
        ApiManager.getInstance(applicationContext)
            .firebaseToken("android", p0 ?: "")
    }
}