package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantCategory(

    @field:SerializedName("merchant_category_name")
    val merchantCategoryName: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("updatedate")
    val updatedate: String? = null,

    @field:SerializedName("merchant_category_arabic_name")
    val merchantCategoryArabicName: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,
    @field:SerializedName("image")
    val image: String? = null,
    @field:SerializedName("background_color")
    val background_color: String? = null,
    @field:SerializedName("font_color")
    val font_color: String? = null
) : Parcelable
