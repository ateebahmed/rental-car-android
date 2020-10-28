package com.ateebahmed.freelance.driver

import android.app.Activity
import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.ateebahmed.freelance.driver.repository.ApiManager
import java.io.IOException

private val TAG = SplashViewModel::class.java.name

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var firebaseToken: String
    private val apiManager: ApiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    private val activitySwitcher by lazy { MutableLiveData<Boolean>() }
    private val showHomeActivity by lazy { MutableLiveData<Boolean>() }
    private val networkAvailable by lazy { MutableLiveData<Boolean>() }
    private var listenerAdded = false

    fun updateFirebaseToken() {
        if (!listenerAdded) {
            FirebaseInstanceId.getInstance()
                .instanceId
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateTokenOnServer(task.result?.token ?: "")
                    } else {
                        Log.e(TAG, "Error occured while getting firebase token", task.exception)
                    }
                }
            listenerAdded = true
        }
    }

    fun getActivitySwitcher(): LiveData<Boolean> = activitySwitcher

    fun checkSession() {
        val token = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
        activitySwitcher.value = token!!.isBlank()
    }

    fun getShowHomeActivity(): LiveData<Boolean> = showHomeActivity

    fun onActivityResult(requestCode: Int, resultCode: Int, expectedRequestCode: Int) {
        when(requestCode) {
            expectedRequestCode -> {
                when(resultCode) {
                    Activity.RESULT_OK -> showHomeActivity.value = true
                    Activity.RESULT_CANCELED -> showHomeActivity.value = false
                }
            }
        }
    }

    fun checkNetwork(runtime: Runtime, connectivityManager: ConnectivityManager) {
        networkAvailable.value = canConnectToInternet(runtime) && isNetworkAvailable(connectivityManager)
    }

    fun getNetworkStatus(): LiveData<Boolean> = networkAvailable

    private fun updateTokenOnServer(firebaseToken: String) {
        if (!this::firebaseToken.isInitialized) {
            this.firebaseToken = ""
        }
        if (this.firebaseToken != firebaseToken) {
            this.firebaseToken = firebaseToken
            apiManager.firebaseToken("android", firebaseToken)
        }
    }

    private fun canConnectToInternet(runtime: Runtime): Boolean {
        try {
            val process = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            return process.waitFor() == 0
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message, e)
        }
        return false
    }

    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val info: NetworkInfo? = connectivityManager.activeNetworkInfo
        return (null != info) && info.isConnected
    }
}
