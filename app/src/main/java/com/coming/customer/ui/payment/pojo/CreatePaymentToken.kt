package com.coming.customer.ui.payment.pojo

import com.google.gson.annotations.SerializedName

data class Result(

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("description")
    val description: String? = null
)

data class CreatePaymentToken(

    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("ndc")
    val ndc: String? = null,

    @field:SerializedName("buildNumber")
    val buildNumber: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,
    @field:SerializedName("total_amount")
    val totalAmount: String? = null
)
