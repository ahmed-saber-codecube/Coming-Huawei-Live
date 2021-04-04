package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class OrderList(

    @field:SerializedName("order_id")
    val orderId: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("grand_total")
    val grandTotal: String? = null,

    @field:SerializedName("profile_image")
    val profileImage: String? = null,

    @field:SerializedName("merchant_name")
    val merchantName: String? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("driver_id")
    val driverId: String? = null,
    @field:SerializedName("branch_id")
    val branch_id: String? = null

) : Parcelable, Serializable
