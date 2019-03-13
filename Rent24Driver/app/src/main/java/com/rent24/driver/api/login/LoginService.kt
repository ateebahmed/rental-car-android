package com.rent24.driver.api.login

import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("login")
    fun login(@Body request: LoginRequest) : Call<LoginResponse>
}
