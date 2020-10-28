package com.ateebahmed.freelance.driver.components.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ateebahmed.freelance.driver.api.login.response.ProfileResponse
import com.ateebahmed.freelance.driver.api.login.response.UserInformation
import com.ateebahmed.freelance.driver.repository.ApiManager

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profile by lazy { MutableLiveData<UserInformation>() }
    private val apiManager by lazy { ApiManager.getInstance(application) }

    fun getProfile(): LiveData<UserInformation> {
        if (null == profile.value) {
            apiManager.userProfile(this)
        }
        return profile
    }

    fun updateProfile(response: ProfileResponse) {
        profile.value = response.success
    }
}
