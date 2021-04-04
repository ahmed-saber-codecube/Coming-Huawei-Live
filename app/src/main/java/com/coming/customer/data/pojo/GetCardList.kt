package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetCardList(

    @field:SerializedName("driver_id")
    val driverId: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("exp_month")
    val expMonth: String? = null,

    @field:SerializedName("exp_year")
    val expYear: String? = null,

    @field:SerializedName("cardno")
    val cardno: String? = null,

    @field:SerializedName("card_id")
    val cardId: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("stripe_customer_id")
    val stripeCustomerId: String? = null,

    @field:SerializedName("is_saved")
    val isSaved: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("card_country")
    val cardCountry: String? = null,

    @field:SerializedName("original_card")
    val originalCard: String? = null,

    @field:SerializedName("fingerprint")
    val fingerprint: String? = null,

    @field:SerializedName("cardholdername")
    val cardholdername: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("brand")
    val brand: String? = null
) : Parcelable
