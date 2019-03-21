package com.rent24.driver

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.rent24.driver.repository.ApiManager

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseToken: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val apiManager: ApiManager by lazy { ApiManager.getInstance(apiToken) }
    private val apiToken: String by lazy {
        PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
    }

    fun updateFirebaseToken(completeListener: OnCompleteListener<InstanceIdResult>) {
        FirebaseInstanceId.getInstance()
            .instanceId
            .addOnCompleteListener(completeListener)
            .addOnCompleteListener { task -> if (task.isSuccessful) updateNewToken(task.result?.token ?: "") }
    }

    private fun updateNewToken(firebaseToken: String) {
        this.firebaseToken.value = firebaseToken
        apiManager.firebaseToken("android", this.firebaseToken.value!!)
    }
}