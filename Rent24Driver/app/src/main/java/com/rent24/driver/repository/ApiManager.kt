package com.rent24.driver.repository

import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.components.HomeViewModel
import com.rent24.driver.components.job.list.JobListViewModel
import com.rent24.driver.components.login.LoginViewModel
import com.rent24.driver.service.RestService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiManager private constructor() {

    fun login(request: LoginRequest, viewModel: LoginViewModel) {
        builder.build()
            .create(RestService.NonAuthApis::class.java)
            .login(request)
            .enqueue(repository.loginCallback(viewModel))
    }

    fun status(status: String, viewModel: HomeViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .status(status)
            .enqueue(repository.statusCallback(viewModel))
    }

    fun scheduledTrips(viewModel: JobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .schedule()
            .enqueue(repository.jobListCallback(viewModel, 0))
    }

    fun completedTrips(viewModel: JobListViewModel) {
        retrofit.create(RestService.AuthApis::class.java)
            .history()
            .enqueue(repository.jobListCallback(viewModel, 1))
    }

    companion object {

        private const val baseUrl = "http://www.technidersolutions.com/sandbox/rmc/public/api/"
        private val builder: Retrofit.Builder by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
        }
        @Volatile private var instance: ApiManager? = null
        private val retrofit: Retrofit by lazy {
            builder.client(OkHttpClient.Builder()
                .addInterceptor(authenticationInterceptor)
                .build())
                .build()
        }
        private lateinit var token: String
        private val authenticationInterceptor: Interceptor by lazy {
            Interceptor {
                it.proceed(it.request()
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build())
            }
        }
        private val repository: NetworkRepository by lazy { NetworkRepository.getInstance() }

        fun getInstance(token: String): ApiManager {
            this.token = token
            return instance ?: synchronized(this) { instance ?: ApiManager() }
        }
    }
}