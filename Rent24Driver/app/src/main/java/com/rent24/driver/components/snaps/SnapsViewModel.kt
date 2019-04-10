package com.rent24.driver.components.snaps

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SnapsViewModel(application: Application) : AndroidViewModel(application) {

    val onFabClickListener by lazy {
        View.OnClickListener {
            startCameraActivity()
        }
    }
    private val startCameraActivity by lazy { MutableLiveData<Boolean>() }
    private val imageUri by lazy { MutableLiveData<Uri>() }
    private val uploadResult by lazy { MutableLiveData<Boolean>() }
    private val askForStoragePermission by lazy { MutableLiveData<Boolean>() }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    var activeJobId = 0

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

    fun setUploadResult(result: Boolean) {
        uploadResult.value = result
    }

    fun getUploadResult(): LiveData<Boolean> = uploadResult

    fun getAskForStoragePermission(): LiveData<Boolean> = askForStoragePermission

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, expectedRequestCode: Int) {
        when (requestCode) {
            expectedRequestCode -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity()
                } else {
                    snackbarMessage.value = "Storage access denied"
                }
            }
        }
    }

    private fun startCameraActivity() {
        if (activeJobId != 0) {
            if (ContextCompat.checkSelfPermission(
                    getApplication<Application>().applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraActivity.value = true
                startCameraActivity.postValue(false)
            } else {
                askForStoragePermission.value = true
                askForStoragePermission.postValue(false)
            }
        } else {
            snackbarMessage.value = "You currently have no active job"
        }
    }
}
