package com.rent24.driver.components.map.dialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CarDetails(val fuelRange: Double, val odometer: Double, val condition: String, val damage: String,
                      val notes: String) : Parcelable