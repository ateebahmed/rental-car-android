package com.rent24.driver.components.snaps.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.map.ParentMapViewModel
import com.rent24.driver.components.snaps.SnapsViewModel
import com.rent24.driver.databinding.SnapUploadDialogFragmentBinding

private val TAG = SnapUploadDialogFragment::class.java
/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    SnapUploadDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [SnapUploadDialogFragment.Listener].
 */
class SnapUploadDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: SnapUploadDialogFragmentBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = arguments?.getParcelable("uri") as Uri
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val layout = View.inflate(context, R.layout.snap_upload_dialog_fragment, null)
        dialog?.setContentView(layout)

        binding = SnapUploadDialogFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        val model = ViewModelProviders.of(this)
            .get(SnapUploadViewModel::class.java)
        model.createUploadFile(imageUri)
        binding.previewImage
            .setImageURI(imageUri)

        model.jobId = ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
            .getActiveJobId()
            .value ?: 0

        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.dialogContainer, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        model.getSetFields()
            .observe(this, Observer {
                if (it) {
                    model.entry.value = binding.entry.text.toString()
                    model.amount.value = binding.amount.text.toString().toDouble()
                    val location = ViewModelProviders.of(activity!!)
                        .get(ParentMapViewModel::class.java)
                        .getCurrentLocation()
                    model.submit(location.latitude, location.longitude)
                }
            })
        binding.snapsUploadButton.setOnClickListener(model.onSnapUploadClickListener)

        model.getUploadResult()
            .observe(this, Observer {
                if (it) {
                    ViewModelProviders.of(activity!!)
                        .get(SnapsViewModel::class.java)
                        .setUploadResult(it)
                }
                dialog?.dismiss()
            })
    }

    companion object {

        fun newInstance() = SnapUploadDialogFragment()
    }
}
