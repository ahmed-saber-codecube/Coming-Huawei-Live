package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromoCode(

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("is_active")
    val isActive: Int? = null,

    @field:SerializedName("minimum_booking")
    val minimumBooking: Int? = null,

    @field:SerializedName("promocode_limit")
    var promocodeLimit: Int? = null,

    @field:SerializedName("for_all_user")
    val forAllUser: Int? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("promocode_remaining")
    val promocodeRemaining: Int? = null,

    @field:SerializedName("promocode")
    var promocode: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("benefit")
    val benefit: String? = null,

    @field:SerializedName("promocode_type")
    var promocodeType: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: Int? = null,

    @field:SerializedName("promocode_discount")
    var promocodeDiscount: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null
) : Parcelable
