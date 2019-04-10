package com.rent24.driver.repository

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.request.PositionRequest
import com.rent24.driver.api.login.response.*
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.invoice.InvoiceViewModel
import com.rent24.driver.components.job.list.CompletedJobListViewModel
import com.rent24.driver.components.job.list.ScheduledJobListViewModel
import com.rent24.driver.components.job.list.item.JobItemViewModel
import com.rent24.driver.components.login.LoginViewModel
import com.rent24.driver.components.profile.ProfileViewModel
import com.rent24.driver.components.snaps.dialog.SnapUploadViewModel
import com.rent24.driver.service.RestService
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Collections

private const val BASE_URL = "http://www.technidersolutions.com/sandbox/rmc/public/api/"
private var TAG = ApiManager::class.java.name

class ApiManager private constructor(context: Context) {

    private val builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
    }
    private val retrofit by lazy {
        builder.client(OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .build())
            .build()
    }
    private val authenticationInterceptor by lazy {
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

    fun scheduledTrips(viewModel: ScheduledJobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .schedule()
            .enqueue(jobListCallback(viewModel))
    }

    fun completedTrips(viewModel: CompletedJobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .history()
            .enqueue(jobListCallback(viewModel))
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

    fun uploadInvoiceEntry(image: MultipartBody.Part, status: RequestBody, title: RequestBody, amount: RequestBody,
                           viewModel: SnapUploadViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .uploadInvoiceEntry(image, status, title, amount)
            .enqueue(uploadInvoiceEntryCallback(viewModel))
    }

    fun uploadInvoiceEntry(image: MultipartBody.Part, status: RequestBody, viewModel: SnapUploadViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .uploadInvoiceEntry(image, status)
            .enqueue(uploadInvoiceEntryCallback(viewModel))
    }

    fun getSnaps(repository: SnapsRepository, jobId: Int) {
        retrofit.create(RestService.AuthApis::class.java)
            .snaps(jobId)
            .enqueue(snapsCallback(repository))
    }

    fun updateJobStatus(status: Map<String, String>, viewModel: ScheduledJobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .jobStatus(status)
            .enqueue(jobStatusCallback(viewModel))
    }

    fun userProfile(viewModel: ProfileViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .userProfile()
            .enqueue(profileCallback(viewModel))
    }

    private fun getToken(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
        ?.getString("token", "") ?: ""

    private fun loginCallback(viewModel: LoginViewModel) = object: Callback<LoginResponse> {
        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Log.e(TAG, "loginCallback ${t.message}", t)
            viewModel.saveToken(LoginResponse(LoginSuccess(""), LoginError("No network available")))
        }

        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            Log.d(TAG, "loginCallback ${response.code()} ${response.message()}")
            viewModel.saveToken(response.body() ?: LoginResponse(LoginSuccess(""),
                LoginError("Invalid credentials")))
        }
    }

    private fun statusCallback(viewModel: HomeViewModel) = object: Callback<StatusResponse> {
        override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
            Log.e(TAG, "statusCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
            Log.d(TAG, "statusCallback ${response.code()} ${response.message()}")
            viewModel.status(response.body() ?: StatusResponse(-1))
        }
    }

    private fun jobListCallback(viewModel: CompletedJobListViewModel) = object: Callback<JobResponse> {
        override fun onFailure(call: Call<JobResponse>, t: Throwable) {
            Log.e(TAG, "jobListCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
            Log.d(TAG, "jobListCallback ${response.code()} ${response.message()}")
            val data = response.body() ?: JobResponse(Collections.emptyList())
            viewModel.updateTrips(data)
        }
    }

    private fun jobDetailCallback(viewModel: JobItemViewModel) = object: Callback<JobResponse> {
        override fun onFailure(call: Call<JobResponse>, t: Throwable) {
            Log.e(TAG, "jobDetailCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
            Log.d(TAG, "jobDetailCallback ${response.code()} ${response.message()}")
            viewModel.updateModel(response.body() ?: JobResponse(Collections.emptyList()))
        }
    }

    private fun invoiceCallback(viewModel: InvoiceViewModel) = object: Callback<InvoiceResponse> {
        override fun onFailure(call: Call<InvoiceResponse>, t: Throwable) {
            Log.e(TAG, "invoiceCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<InvoiceResponse>, response: Response<InvoiceResponse>) {
            Log.d(TAG, "invoiceCallback ${response.code()} ${response.message()}")
            viewModel.updateInvoice(response.body() ?: InvoiceResponse(Collections.emptyList()))
        }
    }

    private fun tokenCallback() = object: Callback<StatusResponse> {
        override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
            Log.e(TAG, "tokenCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
            Log.d(TAG, "tokenCallback ${response.code()} ${response.message()}")
        }
    }

    private fun positionCallback() = object: Callback<StatusResponse> {
        override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
            Log.e(TAG, "positionCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
            Log.d(TAG, "positionCallback ${response.code()} ${response.message()}")
        }
    }

    private fun uploadInvoiceEntryCallback(viewModel: SnapUploadViewModel) = object: Callback<StatusBooleanResponse> {
        override fun onFailure(call: Call<StatusBooleanResponse>, t: Throwable) {
            Log.e(TAG, "uploadInvoiceEntryCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<StatusBooleanResponse>, response: Response<StatusBooleanResponse>) {
            Log.d(TAG, "uploadInvoiceEntryCallback ${response.code()} ${response.message()}")
            viewModel.snapUploadResult(response.body() ?: StatusBooleanResponse(false))
        }
    }

    private fun snapsCallback(repository: SnapsRepository) = object: Callback<SnapsResponse> {
        override fun onFailure(call: Call<SnapsResponse>, t: Throwable) {
            Log.e(TAG, "snapsCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<SnapsResponse>, response: Response<SnapsResponse>) {
            Log.d(TAG, "snapsCallback ${response.code()} ${response.message()}")
            repository.updateSnaps(response.body() ?: SnapsResponse(SnapsSucess(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList())))
        }
    }

    private fun jobStatusCallback(viewModel: ScheduledJobListViewModel) = object: Callback<StatusBooleanResponse> {
        override fun onFailure(call: Call<StatusBooleanResponse>, t: Throwable) {
            Log.e(TAG, "jobStatusCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<StatusBooleanResponse>, response: Response<StatusBooleanResponse>) {
            Log.d(TAG, "jobStatusCallback ${response.code()} ${response.message()}")
            viewModel.updateTrips(response.body() ?: StatusBooleanResponse(false))
        }
    }

    private fun profileCallback(viewModel: ProfileViewModel) = object: Callback<ProfileResponse> {
        override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
            Log.e(TAG, "profileCallback ${t.message}", t)
        }

        override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
            Log.d(TAG, "profileCallback ${response.code()} ${response.message()}")
            viewModel.updateProfile(response.body() ?:
            ProfileResponse(UserInformation(0, "", "", "")))
        }
    }

    companion object {
        @Volatile private var instance: ApiManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: ApiManager(context)
        }
    }
}