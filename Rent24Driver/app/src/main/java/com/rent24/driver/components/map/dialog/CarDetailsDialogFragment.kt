package com.rent24.driver.components.map.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.home.STATUS_DROP_OFF
import com.rent24.driver.components.home.STATUS_PICKUP
import com.rent24.driver.components.job.list.ScheduledJobListViewModel
import com.rent24.driver.components.map.ParentMapViewModel
import com.rent24.driver.components.snaps.dialog.DROP_OFF
import com.rent24.driver.components.snaps.dialog.PICKUP
import com.rent24.driver.databinding.CarDetailsDialogFragmentBinding

class CarDetailsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: CarDetailsDialogFragmentBinding
    private var status = -1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = arguments?.getInt("status") ?: -1
        imageUri = arguments?.getParcelable("uri")
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        val layout = dialog?.layoutInflater?.inflate(R.layout.car_details_dialog_fragment, null)!!
        dialog.setContentView(layout)
        binding = CarDetailsDialogFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        binding.model = ViewModelProviders.of(this)
            .get(CarDetailsViewModel::class.java)
        val model = binding.model as CarDetailsViewModel
        model.createUploadFile(imageUri)
        binding.previewImage
            .setImageURI(imageUri)
        val homeViewModel = ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
        homeViewModel.getActiveJobId()
            .observe(this, Observer {
                model.jobId = it
            })
        model.status = when (status) {
            0 -> PICKUP
            1 -> DROP_OFF
            else -> ""
        }
        model.getSetFields()
            .observe(this, Observer {
                if (it) {
                    model.damage.value = binding.damage.text.toString()
                    model.fuelRange.value = binding.fuelRange.text.toString().toDouble()
                    model.notes.value = binding.notes.text.toString()
                    model.odometer.value = binding.odometer.text.toString().toDouble()
                    val location = ViewModelProviders.of(activity!!)
                        .get(ParentMapViewModel::class.java)
                        .getCurrentLocation()
                    model.submit(location.latitude, location.longitude)
                }
            })
        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.container, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        binding.conditionChipGroup.setOnCheckedChangeListener(model.onChipCheckedChangeListener)
        binding.submit.setOnClickListener(model.onButtonClickListener)
        val parentMapViewModel = ViewModelProviders.of(activity!!)
            .get(ParentMapViewModel::class.java)
        model.getUploadStatus()
            .observe(this, Observer {
                if (it) {
                    if (STATUS_PICKUP == status) {
                        parentMapViewModel.switchButtons(false)
                        parentMapViewModel.sendSnackbarMessage("Tap on Navigate to get navigation to drop off")
                    } else if (STATUS_DROP_OFF == status) {
                        ViewModelProviders.of(activity!!)
                            .get(ScheduledJobListViewModel::class.java)
                            .refreshTrips()
                        parentMapViewModel.switchButtons(true)
                        parentMapViewModel.sendSnackbarMessage("You have completed this job")
                    }
                    dismiss()
                }
            })
        model.getShowLoadingProgressBar()
            .observe(this, Observer {
                if (it) {
                    binding.progressCircular.show()
                } else {
                    binding.progressCircular.hide()
                }
            })
    }
}