package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TransactionHistory(

    @field:SerializedName("wallet")
    val wallet: String? = null,

    @field:SerializedName("transaction")
    val transaction: ArrayList<TransactionItem>? = null
) : Parcelable

@Parcelize
data class TransactionItem(

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("updatedate")
    val updatedate: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("tag")
    val tag: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable
