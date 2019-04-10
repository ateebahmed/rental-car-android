package com.rent24.driver.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.rent24.driver.api.login.response.SnapsResponse

class SnapsRepository private constructor(context: Context) {

    private val snapsResponseLiveData by lazy { MutableLiveData<SnapsResponse>() }
    private val apiManager by lazy { ApiManager.getInstance(context) }
    private val pickupSnapsLiveData by lazy {
        Transformations.map(snapsResponseLiveData) { snapsResponse ->
            snapsResponse.success.pickup
        }
    }
    private val dropoffSnapsLiveData by lazy {
        Transformations.map(snapsResponseLiveData) { snapsResponse ->
            snapsResponse.success.dropoff
        }
    }
    private val receiptSnapsLiveData by lazy {
        Transformations.map(snapsResponseLiveData) { snapsResponse ->
            snapsResponse.success.receipt
        }
    }

    fun getPickupSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || (pickupSnapsLiveData.value?.isEmpty() == true)) {
            apiManager.getSnaps(this, jobId)
        }
        return pickupSnapsLiveData
    }

    fun updateSnaps(response: SnapsResponse) {
        snapsResponseLiveData.value = response
    }

    fun getDropoffSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || (dropoffSnapsLiveData.value?.isEmpty() == true)) {
            apiManager.getSnaps(this, jobId)
        }
        return dropoffSnapsLiveData
    }

    fun getReceiptSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || (receiptSnapsLiveData.value?.isEmpty() == true)) {
            apiManager.getSnaps(this, jobId)
        }
        return receiptSnapsLiveData
    }

    companion object {

        @Volatile private var instance: SnapsRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: SnapsRepository(context)
        }
    }
}