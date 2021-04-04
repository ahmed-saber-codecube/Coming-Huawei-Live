package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class GetVersionCode(
    @field:SerializedName("android_customer_version")
    val androidCustomerVersion: String? = null
) : Parcelable, Serializable