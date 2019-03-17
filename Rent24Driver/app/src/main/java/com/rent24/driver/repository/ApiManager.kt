package com.rent24.driver.repository

import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.components.login.LoginViewModel
import com.rent24.driver.service.RestService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiManager {

    fun login(request: LoginRequest, viewModel: LoginViewModel) {
        retrofit.create(RestService::class.java)
            .login(request)
            .enqueue(repository.loginCallback(viewModel))
    }

    companion object {
        @Volatile private var instance: ApiManager? = null
        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://www.technidersolutions.com/sandbox/rmc/public/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        }

        private val repository: NetworkRepository by lazy { NetworkRepository.getInstance() }

        fun getInstance(): ApiManager = instance ?: synchronized(this) { instance ?: ApiManager() }
    }
}