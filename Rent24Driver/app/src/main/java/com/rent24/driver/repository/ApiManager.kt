package com.rent24.driver.repository

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.request.PositionRequest
import com.rent24.driver.api.login.response.LoginError
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.LoginSuccess
import com.rent24.driver.api.login.response.StatusResponse
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.InvoiceResponse
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.invoice.InvoiceViewModel
import com.rent24.driver.components.job.list.JobListViewModel
import com.rent24.driver.components.job.list.item.JobItemViewModel
import com.rent24.driver.components.login.LoginViewModel
import com.rent24.driver.service.RestService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Collections

private const val BASE_URL = "http://www.technidersolutions.com/sandbox/rmc/public/api/"
private var TAG = ApiManager::class.java.name

class ApiManager private constructor(context: Context) {

    private val builder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
    }
    private val retrofit: Retrofit by lazy {
        builder.client(OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .build())
            .build()
    }
    private val authenticationInterceptor: Interceptor by lazy {
        Interceptor {
            it.proceed(it.request()
                .newBuilder()
                .header("Authorization", "Bearer ${getToken(context)}")
                .build())
        }
    }

    fun login(request: LoginRequest, viewModel: LoginViewModel) {
        builder.build()
            .create(RestService.NonAuthApis::class.java)
            .login(request)
            .enqueue(loginCallback(viewModel))
    }

    fun status(status: String, viewModel: HomeViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .status(status)
            .enqueue(statusCallback(viewModel))
    }

    fun scheduledTrips(viewModel: JobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .schedule()
            .enqueue(jobListCallback(viewModel, 0))
    }

    fun completedTrips(viewModel: JobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .history()
            .enqueue(jobListCallback(viewModel, 1))
    }

    fun getJobDetail(viewModel: JobItemViewModel, id: Int) {
        retrofit.create(RestService.AuthApis::class.java)
            .detail(id)
            .enqueue(jobDetailCallback(viewModel))
    }

    fun invoiceDetail(viewModel: InvoiceViewModel, id: Int) {
        retrofit.create(RestService.AuthApis::class.java)
            .invoice(id)
            .enqueue(invoiceCallback(viewModel))
    }

    fun firebaseToken(deviceType: String, token: String) {
        retrofit.create(RestService.AuthApis::class.java)
            .token(deviceType, token)
            .enqueue(tokenCallback())
    }

    fun updatePosition(positionRequest: PositionRequest) {
        retrofit.create(RestService.AuthApis::class.java)
            .position(positionRequest)
            .enqueue(positionCallback())
    }

    private fun getToken(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            ?.getString("token", "") ?: ""
    }

    private fun loginCallback(viewModel: LoginViewModel): Callback<LoginResponse> {
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

    private fun statusCallback(viewModel: HomeViewModel): Callback<StatusResponse> {
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

    private fun jobListCallback(viewModel: JobListViewModel, api: Int): Callback<JobResponse> {
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

    private fun jobDetailCallback(viewModel: JobItemViewModel): Callback<JobResponse> {
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

    private fun invoiceCallback(viewModel: InvoiceViewModel): Callback<InvoiceResponse> {
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

    private fun tokenCallback(): Callback<StatusResponse> {
        return object: Callback<StatusResponse> {
            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
            }
        }
    }

    private fun positionCallback(): Callback<StatusResponse> {
        return object: Callback<StatusResponse> {
            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                Log.e(TAG, t.message, t)
            }

            override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                Log.d(TAG, "${response.code()} ${response.message()}")
            }
        }
    }

    companion object {
        @Volatile private var instance: ApiManager? = null

        fun getInstance(context: Context): ApiManager {
            return instance ?: synchronized(this) {
                instance ?: ApiManager(context)
            }
        }
    }
}