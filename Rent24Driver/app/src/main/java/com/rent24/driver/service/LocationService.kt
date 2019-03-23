package com.rent24.driver.service

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationService private constructor() {

    private lateinit var locationClient: FusedLocationProviderClient

    fun getLocationClient(): FusedLocationProviderClient {
        return locationClient
    }

    companion object {

        @Volatile
        private var instance: LocationService? = null

        fun getInstance(context: Context): LocationService {
            return (instance ?: synchronized(this) { instance ?: LocationService() })
                .also { locationService ->
                    locationService.locationClient = LocationServices.getFusedLocationProviderClient(context)
                }
        }
    }
}