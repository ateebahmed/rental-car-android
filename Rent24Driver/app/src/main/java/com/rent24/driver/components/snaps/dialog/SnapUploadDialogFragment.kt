package com.rent24.driver.components.snaps.dialog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rent24.driver.R
import com.rent24.driver.databinding.SnapUploadDialogFragmentBinding

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.snap_upload_dialog_fragment, container, false)
        binding = SnapUploadDialogFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        binding.previewImage
            .setImageURI(imageUri)
        if (tab == 2) {
            binding.entryAmountLayout.visibility = View.VISIBLE
            binding.invoiceEntryLayout.visibility = View.VISIBLE
        }
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {

        fun newInstance() = SnapUploadDialogFragment()
    }
}