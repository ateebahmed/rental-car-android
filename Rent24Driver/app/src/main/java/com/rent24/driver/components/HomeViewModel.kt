package com.rent24.driver.components

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.repository.ApiManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val apiManager: ApiManager by lazy { ApiManager.getInstance(token) }
    private val status: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val token: String by lazy {
        PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
    }

    fun callStatusApi(status: Boolean) {
        apiManager.status(if (status) "online" else "offline", this)
    }

    fun status(response: StatusResponse) {
        status.value = response.success > 0
    }

    fun getStatus(): LiveData<Boolean> {
        return status
    }
}