package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class HomeMerchantSlider(

    @field:SerializedName("image")
    var backgroundImage: String? = null

//    @field:SerializedName("id")
//    var id: String? = null
) : Parcelable, Serializable
