package com.rent24.driver.components.snaps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rent24.driver.R
import com.rent24.driver.components.snaps.adapter.SnapsFragmentPagerAdapter
import com.rent24.driver.components.snaps.dialog.SnapUploadDialogFragment
import com.rent24.driver.databinding.SnapsFragmentBinding

private const val PICK_IMAGE_REQUEST_CODE = 5

class SnapsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)
    }
    private lateinit var binding: SnapsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = inflater.inflate(R.layout.snaps_fragment, container, false)
        binding = SnapsFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getStartCameraActivity()
            .observe(this, Observer {
                if (it) {
                    startActivityForResult(Intent.createChooser(Intent().apply {
                        type = "image/*"
                        action = Intent.ACTION_GET_CONTENT
                    }, "Select Picture"), PICK_IMAGE_REQUEST_CODE)
                }
            })
        viewModel.getImageUri()
            .observe(this, Observer {
                showDialog(it, binding.snapsTabLayout.selectedTabPosition)
            })
        binding.snapsViewPager.adapter = SnapsFragmentPagerAdapter(childFragmentManager)
        binding.snapsTabLayout
            .setupWithViewPager(binding.snapsViewPager)
        binding.snapsAddButton
            .setOnClickListener(viewModel.onFabClickListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.onActivityResult(requestCode, resultCode, PICK_IMAGE_REQUEST_CODE, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDialog(uri: Uri, tab: Int) {
        val bundle = Bundle()

        bundle.putParcelable("uri", uri)
        bundle.putInt("tab", tab)
        val dialog = SnapUploadDialogFragment.newInstance()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, "imageUpload")
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
