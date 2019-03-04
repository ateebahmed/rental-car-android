package com.rent24.driver.components.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val email : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val password : MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getEmail(): LiveData<String> {
        return email
    }

    fun getPassword(): LiveData<String> {
        return password
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun setPassword(password: String) {
        this.password.value = password
    }
}
