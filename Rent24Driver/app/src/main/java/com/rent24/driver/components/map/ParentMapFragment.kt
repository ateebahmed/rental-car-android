package com.rent24.driver.components.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.home.STATUS_DROP_OFF
import com.rent24.driver.components.home.STATUS_PICKUP
import com.rent24.driver.components.map.dialog.CarDetailsDialogFragment
import com.rent24.driver.databinding.MapFragmentBinding
import com.rent24.driver.databinding.ParentFragmentBinding

private const val LOCATION_REQUEST_CODE = 2
private val TAG = ParentMapFragment::class.java.name
private const val LOCATION_SETTINGS_REQUEST_CODE = 3
private const val PICK_IMAGE_REQUEST_CODE = 7
private const val STORAGE_REQUEST_CODE = 8

class ParentMapFragment : Fragment(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(ParentMapViewModel::class.java)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ParentFragmentBinding
    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
    }
    private lateinit var mapBinding: MapFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.parent_fragment, container, false)
        val mapLayout = inflater.inflate(R.layout.map_fragment, container, false)
        mapBinding = MapFragmentBinding.bind(mapLayout)
        layout.findViewById<FrameLayout>(R.id.frame_layout)
            .addView(mapLayout)
        binding = ParentFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupModelObservers(viewModel)
        val map = SupportMapFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_frame, map)
            .commitNow()
        map.getMapAsync(this)
        mapBinding.pickupButton
            .setOnClickListener(viewModel.onButtonClickListener)
        mapBinding.dropoffButton
            .setOnClickListener(viewModel.onButtonClickListener)
        mapBinding.tripStopButton
            .setOnClickListener(viewModel.onButtonClickListener)
        homeViewModel.getPickupLocation()
            .observe(this, Observer {
                viewModel.updateMarkerPosition(PICKUP_LOCATION, it)
            })
        homeViewModel.getDropOffLocation()
            .observe(this, Observer {
                viewModel.updateMarkerPosition(DROP_OFF_LOCATION, it)
            })
        mapBinding.dropOffNavigate
            .setOnClickListener {
                val location = viewModel.getMarkerPositions().value
                    ?.get(DROP_OFF_LOCATION)
                if (null != location) {
                    startMapsApplication(location)
                } else {
                    Snackbar.make(binding.container, "No dropoff point specified", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        homeViewModel.getActiveJobId()
            .observe(this, Observer {
                viewModel.jobId = it
            })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getMarkerPositions()
            .observe(this, Observer {
                mMap.clear()
                val size = it.size()
                for (i in 0 until size) {
                    if (it.containsKey(i) && it.containsValue(it.valueAt(i))) {
                        mMap.addMarker(MarkerOptions().position(it[i]!!))
                    }
                }
            })
        viewModel.getCameraUpdate()
            .observe(this, Observer {
                mMap.animateCamera(it)
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        viewModel.onRequestPermissionsResult(requestCode, grantResults, intArrayOf(LOCATION_REQUEST_CODE,
            STORAGE_REQUEST_CODE))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "result code $resultCode, request code $requestCode")
        viewModel.onActivityResult(requestCode, resultCode, intArrayOf(LOCATION_SETTINGS_REQUEST_CODE,
                PICK_IMAGE_REQUEST_CODE), data)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateLastLocation()
    }

    override fun onPause() {
        viewModel.stopLocationUpdates()
        super.onPause()
    }

    private fun setupModelObservers(model: ParentMapViewModel) {
        model.askForLocationPermission()
            .observe(this, Observer {
                if (it) {
                    askForLocationPermission()
                }
            })
        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.container, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        model.locationSettingsRequest()
            .observe(this, Observer {
                requestLocationSettings(it)
            })
        model.getDriverStatus()
            .observe(this, Observer {
                if (it in STATUS_PICKUP..STATUS_DROP_OFF) {
                    model.startCameraActivity()
                }
            })
        model.getStartCameraActivity()
            .observe(this, Observer {
                if (it) {
                    startActivityForResult(Intent.createChooser(Intent().apply {
                        type = "image/*"
                        action = Intent.ACTION_GET_CONTENT
                    }, "Select Picture"), PICK_IMAGE_REQUEST_CODE)
                }
            })
        model.getImageUri()
            .observe(this, Observer {
                if (null != it) {
                    val bundle = Bundle()
                    bundle.putInt("status", model.getDriverStatus().value!!)
                    bundle.putParcelable("uri", it)
                    val dialog = CarDetailsDialogFragment()
                    dialog.arguments = bundle
                    dialog.show(childFragmentManager, dialog.tag)
                }
            })
        model.getAskForStoragePermission()
            .observe(this, Observer {
                if (it) {
                    askForStoragePermission()
                }
            })
        model.getSwitchButtons()
            .observe(this, Observer {
                if (it) {
                    mapBinding.dropoffButton.visibility = View.GONE
                    mapBinding.tripStopButton.visibility = View.GONE
                    mapBinding.pickupButton.visibility = View.VISIBLE
                } else {
                    mapBinding.pickupButton.visibility = View.GONE
                    mapBinding.dropoffButton.visibility = View.VISIBLE
                    mapBinding.tripStopButton.visibility = View.VISIBLE
                }
            })
    }

    private fun askForLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        //            if we need to show user additional information
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val snackbar = Snackbar.make(binding.container, "Location permission needed to show location on map",
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(android.R.string.ok) {
                requestPermissions(permissions, LOCATION_REQUEST_CODE)
                snackbar.dismiss()
            }
            snackbar.show()
            Log.d(TAG, "showing extra information Location permission needed to show location on map")
        } else {
            Log.d(TAG, "no need to show anything, just show the dialog")
            requestPermissions(permissions, LOCATION_REQUEST_CODE)
        }
    }

    private fun requestLocationSettings(builder: LocationSettingsRequest.Builder) {
        LocationServices.getSettingsClient(this.activity!!)
            .checkLocationSettings(builder.build())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "settings are complete")
                    viewModel.updateLocationRequest()
                } else {
                    val exception = it.exception
                    Log.d(TAG, "exception occurred", exception)
                    if (exception is ResolvableApiException) {
                        Log.d(TAG, "exception resoultion starting", exception)
                        startIntentSenderForResult(exception.resolution.intentSender, LOCATION_SETTINGS_REQUEST_CODE,
                            Intent(), 0, 0, 0, Bundle())
                    }
                }
            }
    }

    private fun askForStoragePermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val snackbar = Snackbar.make(binding.container, "Need to access storage to upload photo",
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

    private fun startMapsApplication(dropOff: LatLng) {
        val mapIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=${dropOff.latitude},${dropOff.longitude}"))
        mapIntent.`package` = "com.google.android.apps.maps"
        if (null != mapIntent.resolveActivity(activity!!.packageManager)) {
            startActivity(mapIntent)
        } else {
            Snackbar.make(binding.container, "Install Google Maps to perform this action", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        fun newInstance() = ParentMapFragment()
    }
}
