package com.rent24.driver.service

import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.JobResponse
import com.rent24.driver.api.login.response.StatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestService {

    interface AuthApis {

        @GET("user/status")
        fun status(@Query("status") status: String): Call<StatusResponse>

        @GET("job/schedule")
        fun schedule(): Call<JobResponse>

        @GET("job/history")
        fun history(): Call<JobResponse>

        @GET("job/detail")
        fun detail(@Query("jobid") id: Int): Call<JobResponse>
    }

    interface NonAuthApis {

        @POST("login")
        fun login(@Body request: LoginRequest): Call<LoginResponse>
    }

}
