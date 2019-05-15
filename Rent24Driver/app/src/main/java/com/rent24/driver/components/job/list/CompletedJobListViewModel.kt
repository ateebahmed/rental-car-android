package com.rent24.driver.components.job.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.repository.ApiManager

open class CompletedJobListViewModel(application: Application) : AndroidViewModel(application) {

    protected val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    protected val trips: MutableLiveData<List<JobTrip>> by lazy {
        MutableLiveData<List<JobTrip>>()
    }

    fun getTrips(): LiveData<List<JobTrip>> {
        updateTrips()
        return trips
    }

    protected open fun updateTrips() {
        apiManager.completedTrips(this)
    }

    open fun updateTrips(response: JobResponse) {
        trips.value = response.success
    }
}