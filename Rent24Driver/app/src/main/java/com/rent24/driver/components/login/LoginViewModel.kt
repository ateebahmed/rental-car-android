package com.rent24.driver.components.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginError
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.api.login.response.LoginSuccess
import com.rent24.driver.repository.LoginRepository

class LoginViewModel : ViewModel() {

    private val email: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val password: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val response = MutableLiveData<LoginResponse>()

    fun getEmail(): LiveData<String> {
        return email
    }

    fun getPassword(): LiveData<String> {
        return password
    }

    fun callLoginApi(email: String, password: String, deviceToken: String) {
        val res = Transformations.switchMap(response) {
            val data = MutableLiveData<LoginResponse>()
            data.value = LoginResponse(LoginSuccess("token"), LoginError("error"))
            if (null == data.value) {
                return@switchMap response
            }
            return@switchMap data
        }
        response.value = res.value
    }

    fun getToken(): LiveData<LoginResponse> {
        return response
    }
}
