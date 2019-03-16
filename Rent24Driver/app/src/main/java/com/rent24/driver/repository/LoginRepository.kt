package com.rent24.driver.repository

import com.rent24.driver.api.login.LoginService
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginError
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.LoginSuccess
import com.rent24.driver.components.login.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginRepository {

    fun login(request: LoginRequest, viewModel: LoginViewModel) {
        Retrofit.Builder()
            .baseUrl("http://www.technidersolutions.com/sandbox/rmc/public/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LoginService::class.java)
            .login(request)
            .enqueue(loginCallback(viewModel))
    }

    private fun loginCallback(viewModel: LoginViewModel): Callback<LoginResponse> {
        return object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) = t.printStackTrace()
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                viewModel.saveToken(response.body() ?: LoginResponse(LoginSuccess(""),
                    LoginError(response.errorBody().toString())))
            }
        }
    }
}