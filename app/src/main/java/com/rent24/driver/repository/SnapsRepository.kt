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
            return@map if (null != snapsResponse.success) {
                snapsResponse.success.pickup
            } else {
                emptyList()
            }
        }
    }
    private val dropoffSnapsLiveData by lazy {
        Transformations.map(snapsResponseLiveData) { snapsResponse ->
            return@map if (null != snapsResponse.success) {
                snapsResponse.success.dropoff
            } else {
                emptyList()
            }
        }
    }
    private val receiptSnapsLiveData by lazy {
        Transformations.map(snapsResponseLiveData) {
            return@map if (null != it.success) {
                it.success.receipt
            } else {
                emptyList()
            }
        }
    }

    fun getPickupSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || pickupSnapsLiveData.value.isNullOrEmpty()) {
            apiManager.getSnaps(this, jobId)
        }
        return getValue(pickupSnapsLiveData)
    }

    fun updateSnaps(response: SnapsResponse) {
        snapsResponseLiveData.value = response
    }

    fun getDropoffSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || dropoffSnapsLiveData.value.isNullOrEmpty()) {
            apiManager.getSnaps(this, jobId)
        }
        return getValue(dropoffSnapsLiveData)
    }

    fun getReceiptSnaps(jobId: Int): LiveData<List<String>> {
        if (null == snapsResponseLiveData.value || receiptSnapsLiveData.value.isNullOrEmpty()) {
            apiManager.getSnaps(this, jobId)
        }
        return getValue(receiptSnapsLiveData)
    }

    private fun getValue(data: LiveData<List<String>?>): LiveData<List<String>> {
        return MutableLiveData<List<String>>().also {
            if (data.value.isNullOrEmpty()) {
                it.value = emptyList()
            } else {
                it.value = pickupSnapsLiveData.value
            }
        }
    }

    companion object {

        @Volatile private var instance: SnapsRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: SnapsRepository(context)
        }
    }
}