package com.rent24.driver.components.snaps.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.repository.SnapsRepository

class SnapsTabViewModel(application: Application) : AndroidViewModel(application) {

    private val snapsRepository by lazy { SnapsRepository.getInstance(application) }
    private val pickupSnaps by lazy { snapsRepository.getPickupSnaps() }
    private val dropoffSnaps by lazy { snapsRepository.getDropoffSnaps() }
    private val receiptSnaps by lazy { snapsRepository.getReceiptSnaps() }

    fun getSnaps(key: String): LiveData<List<String>> = when (key) {
        "pickup" -> pickupSnaps
        "dropoff" -> dropoffSnaps
        "receipt" -> receiptSnaps
        else -> MutableLiveData<List<String>>()
    }

    fun updateSnaps(key: String) {
        when (key) {
            "pickup" -> snapsRepository.getPickupSnaps()
            "dropoff" -> snapsRepository.getDropoffSnaps()
            "receipt" -> snapsRepository.getReceiptSnaps()
        }
    }
}