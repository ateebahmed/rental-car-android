package com.rent24.driver.components.invoice

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.response.InvoiceEntry
import com.rent24.driver.api.login.response.InvoiceResponse
import com.rent24.driver.repository.ApiManager

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
        entries.value = if (response.success.isNotEmpty()) response.success else entries.value
        totalAmount.value = 0.0
        entries.value
            ?.forEach { totalAmount.value = totalAmount.value?.plus(it.amount) }
    }

    fun getEntries(): LiveData<List<InvoiceEntry>> {
        return entries
    }
}
