package com.coming.customer.data.pojo

import com.google.gson.annotations.SerializedName

data class ResponseBody<T>(
    @SerializedName("code") val responseCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
) {

}