package com.rent24.driver.components.job.list

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.repository.ApiManager

class JobListViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduledTrips: MutableLiveData<List<JobTrip>> by lazy {
        MutableLiveData<List<JobTrip>>()
    }
    private val completedTrips: MutableLiveData<List<JobTrip>> by lazy {
        MutableLiveData<List<JobTrip>>()
    }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }

    fun getScheduledTrips(): LiveData<List<JobTrip>> {
        return scheduledTrips
    }

    fun getCompletedTrips(): LiveData<List<JobTrip>> {
        return completedTrips
    }

    fun updateScheduledTrips() {
        if (scheduledTrips.value.isNullOrEmpty()) apiManager.scheduledTrips(this)
        else scheduledTrips.postValue(scheduledTrips.value)
    }

    fun updateScheduledTrips(response: JobResponse) {
        scheduledTrips.value = response.success
    }

    fun updateCompletedTrips() {
        if (completedTrips.value.isNullOrEmpty()) apiManager.completedTrips(this)
        else completedTrips.postValue(completedTrips.value)
    }

    fun updateCompletedTrips(response: JobResponse) {
        completedTrips.value = response.success
    }
}
