package com.rent24.driver.components

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.repository.ApiManager

private val TAG = HomeViewModel::class.java.name

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val status by lazy { MutableLiveData<Boolean>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    val statusOnCheckedChangeListener by lazy {
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            switchState = isChecked
            callStatusApi(isChecked)
        }
    }
    private var switchState = false
    private val snackbarMessage by lazy { MutableLiveData<String>() }

    fun status(response: StatusResponse) {
        status.value = response.success > 0
        updateSnackbarMessage(status.value ?: false)
    }

    fun getStatus(): LiveData<Boolean> = status

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    private fun callStatusApi(status: Boolean) {
        apiManager.status(if (status) "online" else "offline", this)
    }

    private fun updateSnackbarMessage(status: Boolean) {
        if (status && switchState) {
            snackbarMessage.value = "You are online"
        } else if(status && !switchState) {
            snackbarMessage.value = "You are offline"
        } else {
            snackbarMessage.value = "Error occurred, please try again"
        }
    }
}