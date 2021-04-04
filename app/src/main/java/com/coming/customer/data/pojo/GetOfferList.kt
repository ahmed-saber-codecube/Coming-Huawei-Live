package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetOfferList(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("discount")
    val discount: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("offer_remaining")
    val offerRemaining: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("updatedate")
    val updatedate: String? = null,

    @field:SerializedName("offer_limit")
    val offerLimit: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable
