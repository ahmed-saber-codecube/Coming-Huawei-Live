package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartDetails(

    @field:SerializedName("cart_detail")
    val cartDetail: ArrayList<CartDetailItem>? = null,

    @field:SerializedName("total_item")
    val totalItem: String? = null,

    @field:SerializedName("delivery_cost")
    var deliveryCost: String? = null,

    @field:SerializedName("sub_total")
    var subTotal: String? = null,

    @field:SerializedName("tax")
    var tax: String? = null,

    @field:SerializedName("promocode_amount")
    var promocodeAmount: String? = null,

    @field:SerializedName("grand_total")
    var grandTotal: String? = null,
    @field:SerializedName("showCheckout")
    var showCheckout: String? = null
) : Parcelable

@Parcelize
data class CartDetailItem(

    @field:SerializedName("quantity")
    var quantity: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("item_addition_id")
    val itemAdditionId: String? = null,

    @field:SerializedName("size_detail")
    val sizeDetail: SizeDetail? = null,

    @field:SerializedName("item_size_id")
    val itemSizeId: String? = null,

    @field:SerializedName("item_total")
    val itemTotal: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("addition_detail")
    val additionDetail: ArrayList<Element>? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("item_detail")
    val itemDetail: CartItemDetail? = null,


    @field:SerializedName("updatedate")
    val updatedate: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable

@Parcelize
data class SizeDetail(

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

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable

@Parcelize
data class CartItemDetail(

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
