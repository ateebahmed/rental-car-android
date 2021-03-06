package com.ateebahmed.freelance.driver.components.invoice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ateebahmed.freelance.driver.api.login.response.InvoiceEntry
import com.ateebahmed.freelance.driver.api.login.response.InvoiceResponse
import com.ateebahmed.freelance.driver.repository.ApiManager

class InvoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val totalAmount: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    private val entries: MutableLiveData<List<InvoiceEntry>> by lazy { MutableLiveData<List<InvoiceEntry>>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }

    fun getTotalAmount(): LiveData<Double> {
        return totalAmount
    }

    fun callInvoiceApi(id: Int) {
        apiManager.invoiceDetail(this, id)
    }

    fun updateInvoice(response: InvoiceResponse) {
        entries.value = response.success
        totalAmount.value = 0.0
        entries.value
            ?.forEach { totalAmount.value = totalAmount.value?.plus(it.amount ?: 0F) }
    }

    fun getEntries(): LiveData<List<InvoiceEntry>> {
        return entries
    }
}
