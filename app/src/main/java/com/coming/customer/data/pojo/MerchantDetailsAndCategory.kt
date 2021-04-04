package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantDetailsAndCategory(

    @field:SerializedName("category_data")
    val categoryData: ArrayList<CategoryDataItem>? = null,

    @field:SerializedName("item_detail")
    val itemDetail: ArrayList<ItemDetailItem>? = null,

    @field:SerializedName("merchant_detail")
    val merchantDetail: MerchantDetail? = null
) : Parcelable

@Parcelize
data class ItemDetailItem(

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_price")
    val itemPrice: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("is_out_stoke")
    val isOutStoke: String? = null,

    @field:SerializedName("calories")
    val calories: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("category_id")
    val categoryId: String? = null,

    @field:SerializedName("item_image")
    val itemImage: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("item_description")
    val itemDescription: String? = null
) : Parcelable

@Parcelize
data class MerchantDetail(

    @field:SerializedName("background_image")
    val backgroundImage: String? = null,

    @field:SerializedName("profile_image")
    val profileImage: String? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("distance")
    val distance: String? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class CategoryDataItem(

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
