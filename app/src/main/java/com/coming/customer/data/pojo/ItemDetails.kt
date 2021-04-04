package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemDetails(

//	@field:SerializedName("cart_detail")
//	val cartDetail: CartDetail? = null,

    @field:SerializedName("item_detail")
    val itemDetail: ItemDetail? = null
) : Parcelable

@Parcelize
data class SizeDetailItem(

    @field:SerializedName("size_name")
    val sizeName: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("size_price")
    val sizePrice: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_selected")
    var isSelected: Boolean = false,

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable

@Parcelize
data class AdditionDetailItem(
    @field:SerializedName("id")
    val itemId: String? = null,
    @field:SerializedName("category_name")
    val categoryName: String? = null,
    @field:SerializedName("arabic_category_name")
    val arabicCategoryName: String? = null,
    @field:SerializedName("merchant_id")
    val merchantId: String? = null,
    @field:SerializedName("is_optional")
    val isOptional: String? = null,
    @field:SerializedName("sorting")
    val sorting: String? = null,
    @field:SerializedName("max_count")
    val maxCount: String? = null,
    @field:SerializedName("is_active")
    val isActive: String? = null,
    @field:SerializedName("is_delete")
    val isDelete: String? = null,
    @field:SerializedName("inserted_date")
    val insertedDate: String? = null,
    @field:SerializedName("items")
    val items: ArrayList<Element>? = null
) : Parcelable

@Parcelize
data class Element(
    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("addition_price")
    val additionPrice: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("extra_category_id")
    val extraCategoryId: String? = null,

    @field:SerializedName("addition_name")
    val additionName: String? = null,

    @field:SerializedName("arabic_addition_name")
    val arabicAdditionName: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,
    //add for selection
    @field:SerializedName("is_selected")
    var isSelected: Boolean = false,

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable

//@Parcelize
//data class CartDetail(
//	val any: Any? = null
//) : Parcelable

@Parcelize
data class ItemDetail(

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_price")
    val itemPrice: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("size_detail")
    val sizeDetail: ArrayList<SizeDetailItem>? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("is_out_stoke")
    val isOutStoke: String? = null,

    @field:SerializedName("calories")
    val calories: String? = null,

    @field:SerializedName("addition_detail")
    val additionDetail: ArrayList<AdditionDetailItem>? = null,

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
