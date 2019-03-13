package com.rent24.driver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.LoginService
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginError
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.LoginSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginRepository {

    fun login(request: LoginRequest): LiveData<LoginResponse> {
        val response = MutableLiveData<LoginResponse>()

        Retrofit.Builder()
            .baseUrl("http://www.technidersolutions.com/realestate/public/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LoginService::class.java)
            .login(request)
            .enqueue(loginCallback(response))

        return response
    }

    private fun loginCallback(data: MutableLiveData<LoginResponse>): Callback<LoginResponse> {
        return object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) = t.printStackTrace()
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                data.value = response.body() ?: LoginResponse(LoginSuccess(""), LoginError(response.errorBody()
                    .toString()))
            }
        }
    }
}