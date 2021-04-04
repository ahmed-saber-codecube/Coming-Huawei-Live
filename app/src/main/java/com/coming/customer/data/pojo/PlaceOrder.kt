package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceOrder(
    @field:SerializedName("order_id")
    val orderId: String? = null,
    @field:SerializedName("seconds")
    val seconds: String? = null
) : Parcelable
