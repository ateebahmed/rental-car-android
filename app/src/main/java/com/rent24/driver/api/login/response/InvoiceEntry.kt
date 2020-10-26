package com.rent24.driver.api.login.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Locale

@JsonClass(generateAdapter = true)
data class InvoiceEntry(val id: Int?, @Json(name = "user_id") val userId: Int?, @Json(name = "job_id") val jobId: Int?,
                        val amount: Float?, val description: String?, val images: String?,
                        @Json(name = "created_at") val createdAt: String?,
                        @Json(name = "updated_at") val updatedAt: String?, val status: String?) {
    val createdAtConverted: String
        get() {
            return if (createdAt.isNullOrBlank()) "" else SimpleDateFormat("dd-MM-yy hh:mm aa",
                Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(createdAt)
                .time)
        }
    val updatedAtConverted: String
        get() {
            return if (updatedAt.isNullOrBlank()) "" else SimpleDateFormat("dd-MM-yy hh:mm aa",
                Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(updatedAt)
                .time)
        }
    val amountString: String
        get() = amount?.toString() ?: "0.0"
}
