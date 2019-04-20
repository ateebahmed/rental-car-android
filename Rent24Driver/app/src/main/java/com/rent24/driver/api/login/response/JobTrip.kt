package com.rent24.driver.api.login.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class JobTrip(val id: Int?, @Json(name = "rmc_id") val rcmId: String?,
                   @Json(name = "pickup_location") val pickupLocation: String?,
                   @Json(name = "dropoff_location") val dropoffLocation: String?,
                   @Json(name = "start_time") val startTime: String?,
                   @Json(name = "job_type") val jobType: String?, val task: String?, val stops: List<TripStop>?,
                   @Json(name = "pickup_lat") val pickupLatitude: Double?,
                   @Json(name = "pickup_long") val pickupLongitude: Double?,
                   @Json(name = "dropoff_lat") val dropoffLatitude: Double?,
                   @Json(name = "dropoff_long") val dropoffLongitude: Double?, var status: String?, val route: String?) {
    val startTimeConverted: String
            get() {
                return if (startTime.isNullOrBlank()) "" else SimpleDateFormat("dd-MM-yy hh:mm aa",
                    Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(startTime)
                    .time)
            }
}