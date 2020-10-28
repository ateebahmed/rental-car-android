package com.ateebahmed.freelance.driver.components.map.dialog

import android.app.Application
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ateebahmed.freelance.driver.api.login.response.StatusBooleanResponse
import com.ateebahmed.freelance.driver.components.snaps.dialog.DROP_OFF
import com.ateebahmed.freelance.driver.repository.ApiManager
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
    private lateinit var uploadImages: MutableList<File>
    private val apiManager by lazy { ApiManager.getInstance(application) }
    lateinit var status: String
    var jobId = 0
    private val uploadStatus by lazy { MutableLiveData<Boolean>() }
    private val showLoadingProgressBar by lazy { MutableLiveData<Boolean>() }

    fun getSetFields(): LiveData<Boolean> = setFields

    fun submit(latitude: Double, longitude: Double) {
        if (validate() && jobId != 0) {
            showLoadingProgressBar.value = true
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            uploadImages.map {
                builder.addFormDataPart("snap[]", it.name,
                    RequestBody.create(MediaType.parse("multipart/formdata"), it))
            }
            builder.addFormDataPart("status", status)
                .addFormDataPart("fuelrange", this.fuelRange.value.toString())
                .addFormDataPart("odometer", this.odometer.value.toString())
                .addFormDataPart("damage", this.damage.value.toString())
                .addFormDataPart("condition", this.damage.value.toString())
                .addFormDataPart("notes", this.notes.value!!)
                .addFormDataPart("jobid", this.jobId.toString())
                .addFormDataPart("latitude", latitude.toString())
                .addFormDataPart("longitude", longitude.toString())
            apiManager.updateJobStatus(builder, this)
        } else {
            snackbarMessage.value = "One or more inputs are empty or not selected!"
        }
    }

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun createUploadFile(imageUris: List<Uri?>) {
        BytesConversionTask().execute(imageUris)
    }

    fun updateStatus(status: StatusBooleanResponse) {
        showLoadingProgressBar.value = false
        if (null != status.success && !status.success) {
            snackbarMessage.value = "Error occured, Try again!"
        } else {
            if (DROP_OFF == this.status) {
                apiManager.updateJobStatus(mapOf(Pair("jobid", jobId.toString()), Pair("status", "finish")))
            }
            uploadStatus.value = true
        }
        uploadStatus.postValue(false)
    }

    fun getUploadStatus(): LiveData<Boolean> = uploadStatus

    fun getShowLoadingProgressBar(): LiveData<Boolean> = showLoadingProgressBar

    private fun validate(): Boolean = (fuelRange.value?.isNaN() == false) && (odometer.value?.isNaN() == false) &&
            (condition.value?.isNotBlank() == true) && (damage.value?.isNotBlank() == true) &&
            (notes.value?.isNotBlank() == true) && ::uploadImages.isInitialized && ::status.isInitialized

    private inner class BytesConversionTask : AsyncTask<List<Uri?>, Void, List<File>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: List<Uri?>?): List<File> {
            if (!params.isNullOrEmpty()) {
                val imageUris = params[0]!!
                return imageUris.map { uri ->
                    uri ?: return@map ""
                    return@map if (uri.scheme == "content") {
                        getApplication<Application>().contentResolver?.query(uri, null, null,
                            null, null)
                            .use {
                                return@use if (it?.moveToFirst() == true) {
                                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                } else {
                                    getFilename(uri)
                                }
                            }
                    } else {
                        getFilename(uri)
                    }
                }.map {
                    if (it == "") return@map File("")
                    return@map File.createTempFile("upload-image-tmp", it?.substring(it.lastIndexOf('.')))
                }.map { file ->
                    file.deleteOnExit()
                    getApplication<Application>().contentResolver?.openInputStream(file.toUri())
                        .use { input ->
                            FileOutputStream(file).use { input?.copyTo(it) }
                        }
                    return@map file
                }
            }
            return emptyList()
        }

        override fun onPostExecute(result: List<File>?) {
            super.onPostExecute(result)
            if (null != result) {
                uploadImages = result.toMutableList()
            }
        }

        private fun getFilename(uri: Uri): String? {
            val path = uri.path
            return path?.substring(path.lastIndexOf(File.separator) + 1)
        }

    }
}