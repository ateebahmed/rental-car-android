package com.rent24.driver.components.login

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.repository.ApiManager

private val TAG = LoginViewModel::class.java.name

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val email by lazy { MutableLiveData<String>() }
    val password by lazy { MutableLiveData<String>() }
    private val apiManager by lazy { ApiManager.getInstance(application.applicationContext) }
    val signInButtonClickListener by lazy {
        View.OnClickListener {
            if (validateFields()) {
                showLoadingProgressBar.value = true
                callLoginApi(email.value ?: "", password.value ?: "")
            } else {
                snackbarMessage.value = "One or more inputs are empty"
            }
        }
    }
    private val showLoadingProgressBar by lazy { MutableLiveData<Boolean>() }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    val onSharedPreferenceChangeListener by lazy {
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            showLoadingProgressBar.value = false
            key ?: return@OnSharedPreferenceChangeListener
            when (key) {
                "token" -> {
                    Log.i(TAG, "login successful")
                    activityResult.value = Activity.RESULT_OK
                }
                "error" -> snackbarMessage.value =
                    sharedPreferences?.getString(key, "Error occured while processing")!!
                else -> snackbarMessage.value = "Wrong credentials, Try again"
            }
        }
    }
    private val activityResult by lazy { MutableLiveData<Int>() }

    private fun callLoginApi(email: String, password: String) {
        apiManager.login(LoginRequest(email, password), this)
    }

    fun saveToken(response: LoginResponse) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
        if ((null != response.success) && !(response.success.token.isNullOrBlank())) {
            with(sharedPreferences.edit()) {
                putString("token", response.success
                    .token)
                apply()
            }
        } else if ((null != response.error) && !(response.error.error.isNullOrBlank())) {
            with(sharedPreferences.edit()) {
                putString("error", response.error
                    .error)
                apply()
            }
        }
    }

    private fun validateFields(): Boolean {
        return (email.value
                ?.isNotBlank() == true && password.value
                ?.isNotBlank() == true)
    }

    fun shouldShowLoadingProgressBar(): LiveData<Boolean> {
        return showLoadingProgressBar
    }

    fun getSnackbarMessage(): LiveData<String> {
        return snackbarMessage
    }

    fun getActivityResult(): LiveData<Int> {
        return activityResult
    }

    fun onActivityBackPressed() {
        showLoadingProgressBar.value = false
        activityResult.value = Activity.RESULT_CANCELED
    }
}
