package com.rent24.driver.components.login

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rent24.driver.api.login.request.LoginRequest
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.repository.ApiManager

private val TAG = LoginViewModel::class.java.name

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val email: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val password: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val apiManager: ApiManager by lazy { ApiManager.getInstance(token) }
    private val token: String by lazy {
        PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
            .getString("token", "")
    }
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
    val onEmailFocusChangeListener by lazy {
        View.OnFocusChangeListener { v, focus ->
            val editText = (v as AppCompatAutoCompleteTextView)
            if (!focus && editText.text?.isNotEmpty() == true) {
                email.value = editText.text.toString()
            }
        }
    }
    val onPasswordFocusChangeListener by lazy {
        View.OnFocusChangeListener { v, focus ->
            val editText = (v as AppCompatEditText)
            if (!focus && editText.text?.isNotEmpty() == true) {
                password.value = editText.text.toString()
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

    private fun validateFields(): Boolean {
        return (email.value
                ?.isNotEmpty() == true && password.value
                ?.isNotEmpty() == true)
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
