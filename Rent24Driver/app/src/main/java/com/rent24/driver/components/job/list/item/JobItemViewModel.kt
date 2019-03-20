package com.rent24.driver.components.job.list.item

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.repository.ApiManager

class JobItemViewModel(application: Application) : AndroidViewModel(application) {

    private val liveModel: MutableLiveData<JobTrip> by lazy { MutableLiveData<JobTrip>() }
    private val apiManager: ApiManager by lazy { ApiManager.getInstance(token) }
    private val token: String by lazy {
        PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
    }

    fun updateModel(id: Int) {
        apiManager.getJobDetail(this, id)
    }

    fun updateModel(response: JobResponse) {
        liveModel.value = if (response.success.isNotEmpty()) response.success[0] else liveModel.value
    }

    fun getModel(): LiveData<JobTrip> {
        return liveModel
    }
}
