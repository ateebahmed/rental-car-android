package com.rent24.driver.components.home

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.repository.ApiManager

private val TAG = HomeViewModel::class.java.name

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val status by lazy { MutableLiveData<Boolean>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    val statusOnCheckedChangeListener by lazy {
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            switchState = isChecked
            showLoadingProgressBar.value = true
            status.value = true
            callStatusApi(isChecked)
        }
    }
    private var switchState = false
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private val startBackgroundService by lazy { MutableLiveData<Boolean>() }
    private val showLoadingProgressBar by lazy { MutableLiveData<Boolean>() }
    private val pickupLocation by lazy { MutableLiveData<LatLng>() }
    private val activeJobId by lazy { MutableLiveData<Int>() }
    private val driverStatus by lazy { MutableLiveData<Int>() }
    private val dropOffLocation by lazy { MutableLiveData<LatLng>() }
    private val triggerNextJob by lazy { MutableLiveData<Boolean>() }

    fun status(response: StatusResponse) {
        status.value = response.success > 0
        showLoadingProgressBar.value = false
        updateSnackbarMessage(status.value ?: false)
        manageBackgroundService(status.value ?: false)
    }

    fun getStatus(): LiveData<Boolean> = status

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun getStartBackgroundService(): LiveData<Boolean> = startBackgroundService

    fun getShowLoadingProgressBar(): LiveData<Boolean> = showLoadingProgressBar

    fun showLoadingProgressBar(status: Boolean) {
        showLoadingProgressBar.value = status
    }

    fun setPickupLocation(location: LatLng) {
        pickupLocation.value = location
    }

    fun getPickupLocation(): LiveData<LatLng> = pickupLocation

    fun setActiveJobId(id: Int) {
        activeJobId.value = id
    }

    fun getActiveJobId(): LiveData<Int> = activeJobId

    fun updateDriverStatus(status: Int) {
        driverStatus.value = status
    }

    fun getDriverStatus(): LiveData<Int> = driverStatus

    fun updateDropOffLocation(dropOff: LatLng) {
        dropOffLocation.value = dropOff
    }

    fun getDropOffLocation(): LiveData<LatLng> = dropOffLocation

    fun triggerNextJob() {
        triggerNextJob.value = true
        triggerNextJob.postValue(false)
    }

    fun getTriggerNextJob(): LiveData<Boolean> = triggerNextJob

    private fun manageBackgroundService(value: Boolean) {
        startBackgroundService.value = value && switchState
    }

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