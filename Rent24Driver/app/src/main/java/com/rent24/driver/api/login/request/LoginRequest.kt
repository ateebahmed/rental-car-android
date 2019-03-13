package com.rent24.driver.api.login.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String,
                        @Json(name = "device_token") val deviceToken: String = "karachipakistan")
