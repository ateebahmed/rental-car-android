package com.rent24.driver.repository

import android.util.Log
import com.rent24.driver.api.login.response.LoginError
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.LoginSuccess
import com.rent24.driver.components.login.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository {

    private val TAG = NetworkRepository::class.java.name

    fun loginCallback(viewModel: LoginViewModel): Callback<LoginResponse> {
        return object : Callback<LoginResponse> {
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

    companion object {

        @Volatile private var instance: NetworkRepository? = null

        fun getInstance(): NetworkRepository = instance ?: synchronized(this) {
            instance ?: buildRepository().also { instance = it}
        }

        private fun buildRepository(): NetworkRepository = NetworkRepository()
    }
}