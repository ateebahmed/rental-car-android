package com.rent24.driver.components.map.dialog

import android.app.Application
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.rent24.driver.api.login.response.StatusBooleanResponse
import com.rent24.driver.repository.ApiManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CarDetailsViewModel(application: Application) : AndroidViewModel(application) {

    val fuelRange by lazy { MutableLiveData<Double>() }
    val odometer by lazy { MutableLiveData<Double>() }
    val condition by lazy { MutableLiveData<String>() }
    val damage by lazy { MutableLiveData<String>() }
    val notes by lazy { MutableLiveData<String>() }
    val onButtonClickListener by lazy {
        View.OnClickListener {
            setFields.value = true
            setFields.postValue(false)
        }
    }
    private val setFields by lazy { MutableLiveData<Boolean>() }
    val onChipCheckedChangeListener by lazy {
        ChipGroup.OnCheckedChangeListener { chipGroup, i ->
            condition.value = chipGroup.findViewById<Chip>(i).text.toString()
        }
    }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private lateinit var uploadImage: File
    private val apiManager by lazy { ApiManager.getInstance(application) }
    lateinit var status: String
    var jobId = 0
    private val uploadStatus by lazy { MutableLiveData<Boolean>() }
    private val showLoadingProgressBar by lazy { MutableLiveData<Boolean>() }

    fun getSetFields(): LiveData<Boolean> = setFields

    fun submit(latitude: Double, longitude: Double) {
        if (validate() && jobId != 0) {
            showLoadingProgressBar.value = true
            val image = MultipartBody.Part
                .createFormData("snap[]", uploadImage.name, RequestBody.create(MediaType.parse("image/*"),
                    uploadImage))
            val mediaType = MediaType.parse("multipart/form-data")
            val status = RequestBody.create(mediaType, status)
            val fuelRange = RequestBody.create(mediaType, this.fuelRange.value.toString())
            val odometer = RequestBody.create(mediaType, this.odometer.value.toString())
            val damage = RequestBody.create(mediaType, this.damage.value.toString())
            val condition = RequestBody.create(mediaType, this.condition.value!!)
            val notes = RequestBody.create(mediaType, this.notes.value!!)
            val jobId = RequestBody.create(mediaType, this.jobId.toString())
            val latitudeBody = RequestBody.create(mediaType, latitude.toString())
            val longitudeBody = RequestBody.create(mediaType, longitude.toString())
            apiManager.updateJobStatus(image, status, fuelRange, odometer, damage, condition, notes, jobId,
                latitudeBody, longitudeBody, this)
        } else {
            snackbarMessage.value = "One or more inputs are empty or not selected!"
        }
    }

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun createUploadFile(imageUri: Uri?) {
        BytesConversionTask().execute(imageUri)
    }

    fun updateStatus(status: StatusBooleanResponse) {
        showLoadingProgressBar.value = false
        if (null != status.success && !status.success) {
            snackbarMessage.value = "Error occured, Try again!"
        } else {
            apiManager.updateJobStatus(mapOf(Pair("jobid", jobId.toString()), Pair("status", "finish")))
            uploadStatus.value = true
        }
        uploadStatus.postValue(false)
    }

    fun getUploadStatus(): LiveData<Boolean> = uploadStatus

    fun getShowLoadingProgressBar(): LiveData<Boolean> = showLoadingProgressBar

    private fun validate(): Boolean = (fuelRange.value?.isNaN() == false) && (odometer.value?.isNaN() == false) &&
            (condition.value?.isNotBlank() == true) && (damage.value?.isNotBlank() == true) &&
            (notes.value?.isNotBlank() == true) && ::uploadImage.isInitialized && ::status.isInitialized

    private inner class BytesConversionTask : AsyncTask<Uri, Void, File>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): File {
            if (!params.isNullOrEmpty()) {
                val imageUri = params[0]!!
                val imageName = if (imageUri.scheme == "content") {
                    getApplication<Application>().contentResolver?.query(
                        imageUri, null, null,
                        null, null
                    )
                        .use {
                            return@use if (it?.moveToFirst() == true) {
                                it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            } else {
                                getFilename(imageUri)
                            }
                        }
                } else {
                    getFilename(imageUri)
                }
                val uploadFile = File.createTempFile(
                    "upload-image-tmp", imageName?.substring(
                        imageName
                            .lastIndexOf('.')
                    )
                )
                uploadFile.deleteOnExit()
                getApplication<Application>().contentResolver?.openInputStream(imageUri)
                    .use { input ->
                        FileOutputStream(uploadFile).use {
                            input?.copyTo(it)
                        }
                    }
                return uploadFile
            }
            return File("")
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)
            if (null != result) {
                uploadImage = result
            }
        }

        private fun getFilename(uri: Uri): String? {
            val path = uri.path
            return path?.substring(path.lastIndexOf(File.separator) + 1)
        }

    }
}