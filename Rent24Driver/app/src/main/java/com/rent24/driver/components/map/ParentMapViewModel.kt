package com.rent24.driver.components.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener

private val TAG = ParentMapViewModel::class.java.name

class ParentMapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }
    private val lastLocation by lazy { MutableLiveData<Location>() }
    private val lastLocationMarker by lazy { MutableLiveData<MarkerOptions>() }
    private val cameraUpdate by lazy { MutableLiveData<CameraUpdate>() }
    private val lastLocationOnCompleteListener by lazy {
        OnCompleteListener<Location> { t ->
            if (t.isSuccessful) {
                Log.d(TAG, "location detected ${t.result?.latitude} : ${t.result?.longitude}")
                lastLocation.value = t.result ?: Location("")
                val latLng = LatLng(lastLocation.value?.latitude ?: 0.0, lastLocation.value?.longitude ?: 0.0)
                updateMarker(latLng)
                updateCamera(latLng)
            } else {
                Log.e(TAG, "error occured in detecting location", t.exception)
                snackbarMessage.value = "Could not detect location"
            }
        }
    }
    private val snackbarMessage by lazy { MutableLiveData<String>() }
    private var listenerAdded = false
    private val askForLocationPermission by lazy { MutableLiveData<Boolean>() }

    fun getLastLocationMarker(): LiveData<MarkerOptions> = lastLocationMarker

    fun getCameraUpdate(): LiveData<CameraUpdate> = cameraUpdate

    fun getSnackbarMessage(): LiveData<String> = snackbarMessage

    fun getLastLocation(): LiveData<Location> = lastLocation // can come handy later, do not remove

    fun askForLocationPermission(): LiveData<Boolean> = askForLocationPermission

    fun onMapReady() {
        if (ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = locationClient.lastLocation
            if (location.isSuccessful) {
                lastLocation.value = location.result
                val latLng = LatLng(lastLocation.value
                    ?.latitude ?: 0.0, lastLocation.value
                    ?.longitude ?: 0.0)
                updateMarker(latLng)
                updateCamera(latLng)
            }
        }
    }

    fun updateLastLocation() {
        //        check for permission
        if (ContextCompat.checkSelfPermission(getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!listenerAdded) {
                // request already granted
                locationClient
                    .lastLocation
                    .addOnCompleteListener(lastLocationOnCompleteListener)
                listenerAdded = true
            }
        } else {
            listenerAdded = false
            askForLocationPermission.value = true
            askForLocationPermission.postValue(false)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, resultCode: Int) {
        when (requestCode) {
            resultCode -> {
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    updateLastLocation()
                } else {
                    snackbarMessage.value = "Location detection permission denied"
                }
            }
        }
    }

    private fun updateMarker(latLng: LatLng) {
        lastLocationMarker.value = MarkerOptions().position(latLng)
            .title("Last Location")
    }

    private fun updateCamera(latLng: LatLng) {
        cameraUpdate.value = CameraUpdateFactory.newLatLng(latLng)
    }
}
