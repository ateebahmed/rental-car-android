package com.rent24.driver.api.login.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobResponse(val success: List<JobTrip>?)