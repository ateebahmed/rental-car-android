package com.rent24.driver.components.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InvoiceViewModel : ViewModel() {

    private val totalAmount: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }

    fun getTotalAmount(): LiveData<Double> {
        return totalAmount
    }
}
