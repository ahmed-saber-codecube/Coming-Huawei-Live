package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerServiceDetails(

    @field:SerializedName("CS_licence")
    val customerServiceLicense: String? = null,

    @field:SerializedName("CS_users_group_id")
    val customerGroupID: String? = null,

    @field:SerializedName("CS_support_number")
    var customerServiceSupportNumber: String? = null

) : Parcelable