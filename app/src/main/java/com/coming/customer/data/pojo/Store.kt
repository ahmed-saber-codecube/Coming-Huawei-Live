package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Store(

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
    val status: String? = null,

    @field:SerializedName("branch_id")
    val branchId: String? = null

) : Parcelable, Serializable
