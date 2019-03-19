package com.rent24.driver.api.login.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TripStop(val id: Int, @Json(name = "job_id") val jobId: String, val address: String, val latitude: Double?,
                    val longitude: Double?, @Json(name = "created_at") val createdAt: String?, val status: Int)