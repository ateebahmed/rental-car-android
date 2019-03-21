package com.rent24.driver.components

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.repository.ApiManager

private val TAG = HomeViewModel::class.java.name

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val status: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val apiManager: ApiManager by lazy { ApiManager.getInstance(apiToken) }
    private val apiToken: String by lazy {
        PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
    }
    private val firebaseToken: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun callStatusApi(status: Boolean) {
        apiManager.status(if (status) "online" else "offline", this)
    }

    fun status(response: StatusResponse) {
        status.value = response.success > 0
    }

    fun getStatus(): LiveData<Boolean> {
        return status
    }

    fun updateNewToken(firebaseToken: String) {
        this.firebaseToken.value = firebaseToken
        apiManager.firebaseToken("android", this.firebaseToken.value!!)
    }

    fun updateFirebaseToken(completeListener: OnCompleteListener<InstanceIdResult>) {
        FirebaseInstanceId.getInstance()
            .instanceId
            .addOnCompleteListener(completeListener)
            .addOnCompleteListener { task -> if (task.isSuccessful) updateNewToken(task.result?.token ?: "") }
    }

    fun getFirebaseToken(): LiveData<String> {
        return firebaseToken
    }
}