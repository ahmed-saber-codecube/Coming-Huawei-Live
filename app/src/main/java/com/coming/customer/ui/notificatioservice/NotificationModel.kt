package com.coming.customer.ui.notificatioservice


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
@Parcelize
data class Message(
    @SerializedName("Message")
    var message: String = "",
    @SerializedName("Title")
    var title: String = ""
) : Parcelable
*/


@Parcelize
data class NotificationModel(
    @SerializedName("status")
    var status: String = "",
    @SerializedName("tag")
    var tag: String = "",
    @SerializedName("body")
    var body: String = "",
    @SerializedName("sender_id")
    var sender_id: String = "",
    @SerializedName("request_id")
    var request_id: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("order_id")
    var order_id: String = ""
) : Parcelable
