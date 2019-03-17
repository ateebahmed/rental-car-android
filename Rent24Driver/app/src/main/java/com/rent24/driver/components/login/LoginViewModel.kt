package com.rent24.driver.components.login

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val email: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val password: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getEmail(): LiveData<String> {
        return email
    }

    fun getPassword(): LiveData<String> {
        return password
    }

    fun callLoginApi(email: String, password: String) {
        LoginRepository().login(LoginRequest(email, password), this)
    }

    fun saveToken(response: LoginResponse) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
        with(preferences.edit()) {
            if (response.success
                    .token
                    .isNotEmpty()) {
                putString("token", response.success
                    .token)
            } else {
                putString("error", response.error
                    .error)
            }
            apply()
        }
    }
}
