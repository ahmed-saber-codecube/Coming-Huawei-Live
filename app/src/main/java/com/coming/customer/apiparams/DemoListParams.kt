package com.coming.customer.apiparams

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DemoListParams(

    @field:SerializedName("service_id")
    val serviceId: String? = null,

    @field:SerializedName("quantity")
    val quantity: String? = null
) : Parcelable
