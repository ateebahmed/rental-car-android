package com.rent24.driver.components.home

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.repository.ApiManager

private val TAG = HomeViewModel::class.java.name

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val status by lazy { MutableLiveData<Boolean>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    val statusOnCheckedChangeListener by lazy {
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            switchState = isChecked
            status.value = true
            callStatusApi(isChecked)
        }
    }
    private var switchState = false
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private val startBackgroundService by lazy { MutableLiveData<Boolean>() }

    fun status(response: StatusResponse) {
        status.value = response.success > 0
        updateSnackbarMessage(status.value ?: false)
        manageBackgroundService(status.value ?: false)
    }

    fun getStatus(): LiveData<Boolean> = status

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun getStartBackgroundService(): LiveData<Boolean> = startBackgroundService

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