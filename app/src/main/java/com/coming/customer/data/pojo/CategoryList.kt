package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryList(

    @field:SerializedName("category_description")
    val categoryDescription: String? = null,

    @field:SerializedName("category_image")
    val categoryImage: String? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_selected_category")
    var isSelectedCategory: Boolean = false,

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable
