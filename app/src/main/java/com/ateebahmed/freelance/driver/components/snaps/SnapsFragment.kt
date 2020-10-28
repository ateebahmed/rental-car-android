package com.ateebahmed.freelance.driver.components.snaps

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.components.home.HomeViewModel
import com.ateebahmed.freelance.driver.components.home.STATUS_DROP_OFF
import com.ateebahmed.freelance.driver.components.home.STATUS_PICKUP
import com.ateebahmed.freelance.driver.components.snaps.adapter.SnapsFragmentPagerAdapter
import com.ateebahmed.freelance.driver.components.snaps.dialog.SnapUploadDialogFragment
import com.ateebahmed.freelance.driver.databinding.SnapsFragmentBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val PICK_IMAGE_REQUEST_CODE = 5
private const val STORAGE_REQUEST_CODE = 6

class SnapsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!)
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
    private lateinit var photoPath: String
    private lateinit var photoUri: Uri

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
        if (this::photoPath.isInitialized) {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(photoPath)
                mediaScanIntent.data = Uri.fromFile(f)
                activity!!.sendBroadcast(mediaScanIntent)
            }
        }
        viewModel.onActivityResult(requestCode, resultCode, PICK_IMAGE_REQUEST_CODE, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        viewModel.onRequestPermissionsResult(requestCode, grantResults, STORAGE_REQUEST_CODE)
    }

    private fun showDialog(uri: Uri) {
        val bundle = Bundle()

        bundle.putParcelable("uri", uri)
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
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                        pictureIntent.resolveActivity(activity!!.packageManager)?.also {
                            val photo: File? = try {
                                File.createTempFile("Rent24_${SimpleDateFormat("yyyyMMdddd_HHmmss",
                                    Locale.getDefault()).format(Date())}_", ".png",
                                    activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).apply {
                                    photoPath = absolutePath
                                }
                            } catch (e: IOException) {
                                Snackbar.make(binding.snapsCoordinatorLayout, "Error occurred when saving file",
                                    Snackbar.LENGTH_SHORT).show()
                                null
                            }
                            photo?.also {
                                photoUri = FileProvider.getUriForFile(activity!!,
                                    "com.ateebahmed.freelance.driver.fileprovider", it)
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            }
                            startActivityForResult(pictureIntent, PICK_IMAGE_REQUEST_CODE)
                        }
                    }
//                    for picking an image
//                    startActivityForResult(Intent.createChooser(Intent().apply {
//                        type = "image/*"
//                        action = Intent.ACTION_GET_CONTENT
//                    }, "Select Picture"), PICK_IMAGE_REQUEST_CODE)

                }
            })
        viewModel.getImageUri()
            .observe(this, Observer {
                if (null != it) {
                    showDialog(photoUri)
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
                if (it.isNotBlank()) {
                    Snackbar.make(binding.snapsCoordinatorLayout, it, Snackbar.LENGTH_SHORT)
                        .show()
                }
            })
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
