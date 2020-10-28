package com.ateebahmed.freelance.driver.components.job.list.item

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ateebahmed.freelance.driver.api.login.response.JobResponse
import com.ateebahmed.freelance.driver.api.login.response.JobTrip
import com.ateebahmed.freelance.driver.repository.ApiManager

class JobItemViewModel(application: Application) : AndroidViewModel(application) {

    private val liveModel: MutableLiveData<JobTrip> by lazy { MutableLiveData<JobTrip>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }

    fun updateModel(id: Int) {
        apiManager.getJobDetail(this, id)
    }

    fun updateModel(response: JobResponse) {
        liveModel.value = if (!(response.success.isNullOrEmpty())) response.success[0] else liveModel.value
    }

    fun getModel(): LiveData<JobTrip> {
        return liveModel
    }
}
