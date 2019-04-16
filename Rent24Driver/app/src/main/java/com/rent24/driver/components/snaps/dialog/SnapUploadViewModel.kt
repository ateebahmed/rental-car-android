package com.rent24.driver.components.snaps.dialog

import android.app.Application
import android.net.Uri
import android.os.AsyncTask
import android.provider.OpenableColumns
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.rent24.driver.api.login.response.StatusBooleanResponse
import com.rent24.driver.repository.ApiManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

const val PICKUP = "pickup"
const val DROP_OFF = "dropoff"
private const val RECEIPT = "receipt"

class SnapUploadViewModel(application: Application) : AndroidViewModel(application) {

    val entry by lazy { MutableLiveData<String>() }
    val amount by lazy { MutableLiveData<Double>() }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private val apiManager by lazy { ApiManager.getInstance(application) }
    private val uploadResult by lazy { MutableLiveData<Boolean>() }
    val onEntryFocusChangeListener by lazy {
        View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (view is TextInputEditText) {
                    if (view.text?.isNotBlank() == true) {
                        entry.value = view.text.toString()
                    }
                }
            }
        }
    }
    val onAmountFocusChangeListener by lazy {
        View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (view is TextInputEditText) {
                    if (view.text?.isNotBlank() == true) {
                        amount.value = view.text.toString()
                            .toDouble()
                    }
                }
            }
        }
    }
    private lateinit var uploadImage: File
    val onSnapUploadClickListener by lazy {
        View.OnClickListener {
            val image = MultipartBody.Part
                .createFormData("snap[]", uploadImage.name, RequestBody.create(MediaType.parse("image/*"),
                    uploadImage))
            val mediaType = MediaType.parse("multipart/form-data")
            val status = RequestBody.create(mediaType, getStatus(tab))
            val jobId = RequestBody.create(mediaType, this.jobId.toString())
            when {
                validateFields() -> {
                    val title = RequestBody.create(mediaType, entry.value!!)
                    val amount = RequestBody.create(mediaType, this.amount.value.toString())

                    apiManager.uploadInvoiceEntry(image, status, title, amount, jobId, this)
                }
                else -> snackbarMessage.value = "One or more inputs are empty"
            }
        }
    }
    private var tab = 0
    var jobId = 0

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun snapUploadResult(status: StatusBooleanResponse) {
        if (status.success) {
            snackbarMessage.value = "Invoice created"
        } else {
            snackbarMessage.value = "Error occurred, try again!"
        }
        uploadResult.value = status.success
    }

    fun getUploadResult(): LiveData<Boolean> = uploadResult

    fun createUploadFile(imageUri: Uri) {
        BytesConversionTask().execute(imageUri)
    }

    fun updateTab(tab: Int) {
        this.tab = tab
    }

    private fun validateFields() = entry.value?.isNotBlank() ?: false && !(amount.value?.isNaN() ?: true)

    private fun getStatus(tab: Int): String {
        return when (tab) {
            0 -> PICKUP
            1 -> DROP_OFF
            2 -> RECEIPT
            else -> ""
        }
    }

    private inner class BytesConversionTask : AsyncTask<Uri, Void, File>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): File {
            val imageUri = params[0]!!
            val imageName = if (imageUri.scheme == "content") {
                getApplication<Application>().contentResolver?.query(imageUri, null, null,
                    null, null)
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
            val uploadFile = File.createTempFile("upload-image-tmp", imageName?.substring(imageName
                .lastIndexOf('.')))
            uploadFile.deleteOnExit()
            getApplication<Application>().contentResolver?.openInputStream(imageUri)
                .use {input ->
                    FileOutputStream(uploadFile).use {
                        input?.copyTo(it)
                    }
                }
            return uploadFile
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)
            uploadImage = result!!
        }

        private fun getFilename(uri: Uri): String? {
            val path = uri.path
            return path?.substring(path.lastIndexOf(File.separator) + 1)
        }

    }
}