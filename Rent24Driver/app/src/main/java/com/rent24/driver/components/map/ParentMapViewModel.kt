package com.rent24.driver.components.map

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.collection.SparseArrayCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.rent24.driver.R
import com.rent24.driver.components.home.STATUS_DROP_OFF
import com.rent24.driver.components.home.STATUS_PICKUP

private val TAG = ParentMapViewModel::class.java.name
private const val CURRENT_LOCATION = 0
const val PICKUP_LOCATION = 1
const val DROP_OFF_LOCATION = 2

class ParentMapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }
    private val lastLocation by lazy { MutableLiveData<Location>() }
    private val cameraUpdate by lazy { MutableLiveData<CameraUpdate>() }
    private val lastLocationOnCompleteListener by lazy {
        OnCompleteListener<Location> { t ->
            if (t.isSuccessful) {
                if (null != t.result) {
                    Log.d(TAG, "location detected ${t.result?.latitude} : ${t.result?.longitude}")
                    lastLocation.value = t.result ?: Location("")
                    val latLng = LatLng(lastLocation.value?.latitude ?: 0.0, lastLocation.value?.longitude ?: 0.0)
                    updateMarkerPosition(CURRENT_LOCATION, latLng)
                    updateCamera(latLng)
                }
                checkLocationSettings(locationSettingsRequest)
            } else {
                Log.e(TAG, "error occured in detecting location", t.exception)
                snackbarMessage.value = "Could not detect location"
            }
        }
    }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private var listenerAdded = false
    private val askForLocationPermission by lazy { MutableLiveData<Boolean>() }
    private val locationSettingsRequest by lazy { MutableLiveData<LocationSettingsRequest.Builder>() }
    private val locationRequest by lazy {
        LocationRequest.create()
            .apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }
    private val locationCallback by lazy {
        object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0 ?: return
                lateinit var latLng: LatLng
                for (location in p0.locations) {
                    latLng = LatLng(location.latitude, location.longitude)
                    updateMarkerPosition(CURRENT_LOCATION, latLng)
//                    updateCamera(latLng)
                }
            }
        }
    }
    private var detectLocation = false
    private val driverStatus by lazy { MutableLiveData<Int>() }
    val onButtonClickListener by lazy {
        View.OnClickListener {
            if (0 != jobId) {
                when (it.id) {
                    R.id.pickup_button -> driverStatus.value = STATUS_PICKUP
                    R.id.dropoff_button -> driverStatus.value = STATUS_DROP_OFF
                    R.id.trip_stop_button -> {}
                }
            } else {
                snackbarMessage.value = "You have no active job"
            }
        }
    }
    private val askForStoragePermission by lazy { MutableLiveData<Boolean>() }
    private val startCameraActivity by lazy { MutableLiveData<Boolean>() }
    private val imageUri by lazy { MutableLiveData<Uri?>() }
    private val route by lazy { MutableLiveData<String>() }
    private val dropOffLocation by lazy { MutableLiveData<LatLng?>() }
    var jobId = 0
    private val markerPositions by lazy {
        MutableLiveData<SparseArrayCompat<LatLng>>().also { it.value = SparseArrayCompat() }
    }
    private val switchButtons by lazy { MutableLiveData<Boolean>() }

    fun getCameraUpdate(): LiveData<CameraUpdate> = cameraUpdate

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun getLastLocation(): LiveData<Location> = lastLocation // can come handy later, do not remove

    fun askForLocationPermission(): LiveData<Boolean> = askForLocationPermission

    fun updateLastLocation() {
        if (!detectLocation) {
            detectLocation = true
            //        check for permission
            if (ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!listenerAdded) {
                    // request already granted
                    locationClient
                        .lastLocation
                        .addOnCompleteListener(lastLocationOnCompleteListener)
                    listenerAdded = true
                }
            } else {
                listenerAdded = false
                detectLocation = false
                askForLocationPermission.value = true
                askForLocationPermission.postValue(false)
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, expectedRequestCode: IntArray) {
        Log.d(TAG, "request code $requestCode")
        when (requestCode) {
            expectedRequestCode[0] -> {
                Log.d(TAG, "checking result for coarse location")
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    updateLastLocation()
                } else {
                    snackbarMessage.value = "Location detection permission denied"
                }
            }
            expectedRequestCode[1] -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity()
                } else {
                    snackbarMessage.value = "Storage access denied"
                }
            }
        }
    }

    fun locationSettingsRequest(): LiveData<LocationSettingsRequest.Builder> = locationSettingsRequest

    fun updateLocationRequest() {
        if (ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "fine location permission granted, updating location request")
            locationClient.requestLocationUpdates(locationRequest,locationCallback, null)
        } else {
            Log.d(TAG, "permission denied, need to ask permission for fine location")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, expectedRequestCode: IntArray, data: Intent?) {
        when (requestCode) {
            expectedRequestCode[0] -> {
                if (resultCode == Activity.RESULT_OK) {
                    updateLocationRequest()
                } else {
                    snackbarMessage.value = "Live location updates not allowed"
                }
            }
            expectedRequestCode[1] -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri.value = data?.data
                    driverStatus.postValue(-1)
                    imageUri.postValue(null)
                } else {
                    snackbarMessage.value = "Storage access not allowed"
                }
            }
        }
    }

    fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    fun getDriverStatus(): LiveData<Int> = driverStatus

    fun startCameraActivity() {
        if (ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startCameraActivity.value = true
            startCameraActivity.postValue(false)
        } else {
            askForStoragePermission.value = true
            askForStoragePermission.postValue(false)
        }
    }

    fun getAskForStoragePermission(): LiveData<Boolean> = askForStoragePermission

    fun getStartCameraActivity(): LiveData<Boolean> = startCameraActivity

    fun getImageUri(): LiveData<Uri?> = imageUri

    fun getDropOffLocation(): LiveData<LatLng?> = dropOffLocation

    fun sendSnackbarMessage(message: String) {
        snackbarMessage.postValue(message)
    }

    fun updateMarkerPosition(key: Int, position: LatLng) {
        markerPositions.value?.append(key, position)
        markerPositions.value = markerPositions.value
    }

    fun getMarkerPositions(): LiveData<SparseArrayCompat<LatLng>> = markerPositions

    fun switchButtons(switch: Boolean) {
        switchButtons.value = switch
    }

    fun getSwitchButtons(): LiveData<Boolean> = switchButtons

    private fun updateCamera(latLng: LatLng) {
        cameraUpdate.value = CameraUpdateFactory.newLatLngZoom(latLng, 18.0F)
    }

    private fun checkLocationSettings(locationSettingsRequestBuilder: MutableLiveData<LocationSettingsRequest.Builder>) {
        locationSettingsRequestBuilder.value = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
    }
}
