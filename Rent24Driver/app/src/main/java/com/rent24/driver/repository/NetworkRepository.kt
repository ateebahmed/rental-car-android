package com.rent24.driver.repository

import android.util.Log
import com.rent24.driver.api.login.response.*
import com.rent24.driver.components.HomeViewModel
import com.rent24.driver.components.invoice.InvoiceViewModel
import com.rent24.driver.components.job.list.JobListViewModel
import com.rent24.driver.components.job.list.item.JobItemViewModel
import com.rent24.driver.components.login.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class NetworkRepository {

    private val TAG = NetworkRepository::class.java.name

    fun loginCallback(viewModel: LoginViewModel): Callback<LoginResponse> {
        return object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
                viewModel.saveToken(LoginResponse(LoginSuccess(""), LoginError("No network available")))
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                viewModel.saveToken(response.body() ?: LoginResponse(LoginSuccess(""),
                    LoginError("Invalid credentials")))
            }
        }
    }

    fun statusCallback(viewModel: HomeViewModel): Callback<StatusResponse> {
        return object: Callback<StatusResponse> {
            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
                viewModel.status(response.body() ?: StatusResponse(-1))
            }
        }
    }

    fun jobListCallback(viewModel: JobListViewModel, api: Int): Callback<JobResponse> {
        return object: Callback<JobResponse> {
            override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
                val data = response.body() ?: JobResponse(Collections.emptyList())
                if (api == 0) viewModel.updateScheduledTrips(data) else viewModel.updateCompletedTrips(data)
            }
        }
    }

    fun jobDetailCallback(viewModel: JobItemViewModel): Callback<JobResponse> {
        return object: Callback<JobResponse> {
            override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
                viewModel.updateModel(response.body() ?: JobResponse(Collections.emptyList()))
            }

        }
    }

    fun invoiceCallback(viewModel: InvoiceViewModel): Callback<InvoiceResponse> {
        return object: Callback<InvoiceResponse> {
            override fun onFailure(call: Call<InvoiceResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<InvoiceResponse>, response: Response<InvoiceResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
                viewModel.updateInvoice(response.body() ?: InvoiceResponse(Collections.emptyList()))
            }

        }
    }

    companion object {

        @Volatile private var instance: NetworkRepository? = null

        fun getInstance(): NetworkRepository = instance ?: synchronized(this) {
            instance ?: buildRepository().also { instance = it}
        }

        private fun buildRepository(): NetworkRepository = NetworkRepository()
    }
}