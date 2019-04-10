package com.rent24.driver.service

import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.request.PositionRequest
import com.rent24.driver.api.login.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Part

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

        @GET("job/getinvoice")
        fun invoice(@Query("jobid") id: Int): Call<InvoiceResponse>

        @GET("user/token")
        fun token(@Query("device_type") deviceType: String, @Query("device_token") deviceToken: String):
                Call<StatusResponse>

        @POST("user/position")
        fun position(@Body position: PositionRequest): Call<StatusResponse>

        @Multipart
        @POST("job/status")
        fun uploadInvoiceEntry(@Part image: MultipartBody.Part, @Part("status") status: RequestBody,
                               @Part("title") title: RequestBody, @Part("amount") amount: RequestBody):
                Call<StatusBooleanResponse>

        @Multipart
        @POST("job/status")
        fun uploadInvoiceEntry(@Part image: MultipartBody.Part, @Part("status") status: RequestBody):
                Call<StatusBooleanResponse>

        @GET("job/snaps")
        fun snaps(@Query("jobid") jobId: Int): Call<SnapsResponse>

        @POST("job/status")
        fun jobStatus(@Body status: Map<String, String>): Call<StatusBooleanResponse>

        @GET("details")
        fun userProfile(): Call<ProfileResponse>
    }

    interface NonAuthApis {

        @POST("login")
        fun login(@Body request: LoginRequest): Call<LoginResponse>
    }

}
