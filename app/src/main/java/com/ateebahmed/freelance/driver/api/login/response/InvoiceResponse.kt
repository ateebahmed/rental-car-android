package com.ateebahmed.freelance.driver.api.login.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvoiceResponse(val success: List<InvoiceEntry>?)