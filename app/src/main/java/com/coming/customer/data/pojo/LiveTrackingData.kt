package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveTrackingData(

    @field:SerializedName("user_lng")
    val userLng: String? = null,

    @field:SerializedName("user_lat")
    val userLat: String? = null,

    @field:SerializedName("merchant_lng")
    val merchantLng: String? = null,

    @field:SerializedName("merchant_lat")
    val merchantLat: String? = null,

    @field:SerializedName("driver_lng")
    val driverLng: String? = null,

    @field:SerializedName("driver_lat")
    val driverLat: String? = null
) : Parcelable
