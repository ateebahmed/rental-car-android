package com.rent24.driver.components.snaps

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SnapsViewModel(application: Application) : AndroidViewModel(application) {

    val onFabClickListener by lazy {
        View.OnClickListener {
            startCameraActivity.value = true
            startCameraActivity.postValue(false)
        }
    }
    private val startCameraActivity by lazy { MutableLiveData<Boolean>() }
    private val imageUri by lazy { MutableLiveData<Uri>() }

    fun getStartCameraActivity(): LiveData<Boolean> = startCameraActivity

    fun onActivityResult(requestCode: Int, resultCode: Int, expectedRequestCode: Int, data: Intent?) {
        when (requestCode) {
            expectedRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri.value = data?.data
                }
            }
        }
    }

    fun getImageUri(): LiveData<Uri> = imageUri
}
