package com.rent24.driver.components.snaps

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.rent24.driver.R
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.home.STATUS_DROP_OFF
import com.rent24.driver.components.home.STATUS_PICKUP
import com.rent24.driver.components.snaps.adapter.SnapsFragmentPagerAdapter
import com.rent24.driver.components.snaps.dialog.SnapUploadDialogFragment
import com.rent24.driver.databinding.SnapsFragmentBinding

const val PICK_IMAGE_REQUEST_CODE = 5
const val STORAGE_REQUEST_CODE = 6

class SnapsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)
    }
    private lateinit var binding: SnapsFragmentBinding
    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
    }
    private var status: Int? = null
    private val onTabSelectedListener by lazy {
        object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    2 -> binding.snapsAddButton.visibility = View.VISIBLE
                    else -> binding.snapsAddButton.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = arguments?.getInt("status")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = inflater.inflate(R.layout.snaps_fragment, container, false)
        binding = SnapsFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupObservers()
        homeViewModel.getActiveJobId()
            .observe(this, Observer { viewModel.activeJobId = it })
        binding.snapsViewPager.adapter = SnapsFragmentPagerAdapter(childFragmentManager)
        binding.snapsTabLayout
            .setupWithViewPager(binding.snapsViewPager)
        binding.snapsTabLayout.addOnTabSelectedListener(onTabSelectedListener)
        binding.snapsAddButton
            .setOnClickListener(viewModel.onFabClickListener)
        if (null != status) {
            binding.snapsViewPager.currentItem = when(status) {
                STATUS_PICKUP -> 0
                STATUS_DROP_OFF -> 1
                else -> 0
            }
            viewModel.startCameraActivity()
            status = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.onActivityResult(requestCode, resultCode, PICK_IMAGE_REQUEST_CODE, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        viewModel.onRequestPermissionsResult(requestCode, grantResults, STORAGE_REQUEST_CODE)
    }

    private fun showDialog(uri: Uri, tab: Int) {
        val bundle = Bundle()

        bundle.putParcelable("uri", uri)
        bundle.putInt("tab", tab)
        val dialog = SnapUploadDialogFragment.newInstance()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun askForStoragePermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val snackbar = Snackbar.make(binding.snapsCoordinatorLayout, "Need to access storage to upload photo",
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") {
                requestPermissions(permissions, STORAGE_REQUEST_CODE)
                snackbar.dismiss()
            }
            snackbar.show()
        } else {
            requestPermissions(permissions, STORAGE_REQUEST_CODE)
        }
    }

    private fun setupObservers() {
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
                if (null != it) {
                    showDialog(it, binding.snapsTabLayout.selectedTabPosition)
                }
            })
        viewModel.getUploadResult()
            .observe(this, Observer {
                if (it) {
                    (binding.snapsViewPager.adapter as SnapsFragmentPagerAdapter)
                        .updateTab(binding.snapsTabLayout.selectedTabPosition)
                }
            })
        viewModel.getAskForStoragePermission()
            .observe(this, Observer {
                if(it) {
                    askForStoragePermission()
                }
            })
        viewModel.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.snapsCoordinatorLayout, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
