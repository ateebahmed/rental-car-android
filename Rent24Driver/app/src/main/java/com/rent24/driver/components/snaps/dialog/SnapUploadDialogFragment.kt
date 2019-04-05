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
    private var tab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = arguments?.getParcelable("uri") as Uri
        tab = arguments?.getInt("tab", 0) ?: tab
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val layout = View.inflate(context, R.layout.snap_upload_dialog_fragment, null)
        dialog?.setContentView(layout)

        binding = SnapUploadDialogFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        val model = ViewModelProviders.of(this)
            .get(SnapUploadViewModel::class.java)
        model.createUploadFile(imageUri)
        model.updateTab(tab)
        binding.previewImage
            .setImageURI(imageUri)
        if (tab == 2) {
            binding.entryAmountLayout.visibility = View.VISIBLE
            binding.invoiceEntryLayout.visibility = View.VISIBLE
        }
        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.dialogContainer, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        binding.entry.onFocusChangeListener = model.onEntryFocusChangeListener
        binding.amount.onFocusChangeListener = model.onAmountFocusChangeListener
        binding.snapsUploadButton.setOnClickListener(model.onSnapUploadClickListener)

        val viewModel = ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)
        model.getUploadResult()
            .observe(this, Observer {
                if (it) {
                    viewModel.setUploadResult(it)
                }
                dialog?.dismiss()
            })
    }

    companion object {

        fun newInstance() = SnapUploadDialogFragment()
    }
}
