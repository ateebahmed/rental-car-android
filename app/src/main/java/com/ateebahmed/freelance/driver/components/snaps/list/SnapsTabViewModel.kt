package com.ateebahmed.freelance.driver.components.snaps.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ateebahmed.freelance.driver.repository.SnapsRepository

class SnapsTabViewModel(application: Application) : AndroidViewModel(application) {

    private val snapsRepository by lazy { SnapsRepository.getInstance(application) }

    fun getSnaps(key: String, jobId: Int): LiveData<List<String>> = when (key) {
        "pickup" -> snapsRepository.getPickupSnaps(jobId)
        "dropoff" -> snapsRepository.getDropoffSnaps(jobId)
        "receipt" -> snapsRepository.getReceiptSnaps(jobId)
        else -> MutableLiveData<List<String>>()
    }

    fun updateSnaps(key: String, jobId: Int) {
        when (key) {
            "pickup" -> snapsRepository.getPickupSnaps(jobId)
            "dropoff" -> snapsRepository.getDropoffSnaps(jobId)
            "receipt" -> snapsRepository.getReceiptSnaps(jobId)
        }
    }
}