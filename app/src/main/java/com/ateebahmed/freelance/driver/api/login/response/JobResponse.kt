package com.ateebahmed.freelance.driver.api.login.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobResponse(val success: List<JobTrip>?)