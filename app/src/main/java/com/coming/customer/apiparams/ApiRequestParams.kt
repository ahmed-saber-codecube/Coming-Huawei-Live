package com.coming.customer.apiparams

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
class ApiRequestParams(
    @SerializedName("signup_type")
    var signup_type: String? = null,
    @SerializedName("country_code")
    var country_code: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("booking_id")
    var booking_id: String? = null,
    @SerializedName("category_id")
    var category_id: String? = null,
    @SerializedName("merchant_category_id")
    var merchant_category_id: String? = null,
    @SerializedName("subcategory_id")
    var subcategory_id: String? = null,
    @SerializedName("product_id")
    var product_id: String? = null,
    @SerializedName("is_favorite")
    var is_favorite: String? = null,
    @SerializedName("mobile_number")
    var mobile_number: String? = null,
    @SerializedName("device_type")
    var device_type: String? = null,
    @SerializedName("page")
    var page: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("store_id")
    var store_id: String? = null,
    @SerializedName("device_token")
    var device_token: String? = null,
    @SerializedName("ip_address")
    var ip_address: String? = null,
    @SerializedName("latitude")
    var latitude: String? = null,
    @SerializedName("longitude")
    var longitude: String? = null,
    @SerializedName("login_type")
    var login_type: String? = null,
    @SerializedName("customer_id")
    var customer_id: String? = null,
    @SerializedName("old_password")
    var old_password: String? = null,
    @SerializedName("new_password")
    var new_password: String? = null,
    @SerializedName("role")
    var role: String? = null,
    @SerializedName("notification_id")
    var notification_id: String? = null,
    @SerializedName("user_id")
    var user_id: String? = null,
    @SerializedName("exp_month")
    var exp_month: String? = null,
    @SerializedName("exp_year")
    var exp_year: String? = null,
    @SerializedName("cardno")
    var cardno: String? = null,
    @SerializedName("cardholdername")
    var cardholdername: String? = null,
    @SerializedName("store_lat")
    var store_lat: String? = null,
    @SerializedName("store_lng")
    var store_lng: String? = null,
    @SerializedName("store_name")
    var store_name: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("order_description")
    var order_description: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("registrations")
    var registrations: String? = null,
    @SerializedName("cardtype")
    var cardtype: String? = null,
    @SerializedName("flag")
    var flag: String? = null,
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("card_number")
    var card_number: String? = null,
    @SerializedName("card_holder_name")
    var card_holder_name: String? = null,
    @SerializedName("cvv")
    var cvv: String? = null,
    @SerializedName("expiry_date")
    var expiry_date: String? = null,
    @SerializedName("stylist_id")
    var stylist_id: String? = null,
    @SerializedName("service_id")
    var service_id: String? = null,
    @SerializedName("lng")
    var lng: String? = null,
    @SerializedName("merchant_id")
    var merchant_id: String? = null,
    @SerializedName("driver_id")
    var driver_id: String? = null,
    @SerializedName("driver_rating")
    var driver_rating: String? = null,
    @SerializedName("merchant_rating")
    var merchant_rating: String? = null,
    @SerializedName("who_given")
    var who_given: String? = null,
    @SerializedName("notifications")
    var notifications: String? = null,
    @SerializedName("lat")
    var lat: String? = null,
    @SerializedName("order_id")
    var order_id: String? = null,
    @SerializedName("payment_method")
    var payment_method: String? = null,
    @SerializedName("card_amount")
    var card_amount: String? = null,
    @SerializedName("wallet_amount")
    var wallet_amount: String? = null,
    @SerializedName("booking_date")
    var booking_date: String? = null,
    @SerializedName("item_id")
    var item_id: String? = null,
    @SerializedName("quantity")
    var quantity: String? = null,
    @SerializedName("item_addition_id")
    var addition_detail: String? = null,
    @SerializedName("item_size_id")
    var item_size_id: String? = null,
    @SerializedName("booking_time")
    var booking_time: String? = null,
    @SerializedName("payment_type")
    var payment_type: String? = null,
    @SerializedName("card_id")
    var card_id: String? = null,
    @SerializedName("delivery_type")
    var delivery_type: String? = null,
    @SerializedName("event_date")
    var event_date: String? = null,
    @SerializedName("event_time")
    var event_time: String? = null,
    @SerializedName("delivery_date")
    var delivery_date: String? = null,
    @SerializedName("delivery_address")
    var delivery_address: String? = null,
    @SerializedName("address_lat")
    var address_lat: String? = null,
    @SerializedName("address_lng")
    var address_lng: String? = null,
    @SerializedName("delivery_instruction")
    var delivery_instruction: String? = null,
    @SerializedName("notes")
    var notes: String? = null,
    @SerializedName("delivery_lat")
    var delivery_lat: String? = null,
    @SerializedName("delivery_lng")
    var delivery_lng: String? = null,
    @SerializedName("sub_total")
    var sub_total: String? = null,
    @SerializedName("discount_amount")
    var discount_amount: String? = null,
    @SerializedName("tax_amount")
    var tax_amount: String? = null,
    @SerializedName("total_amount")
    var total_amount: String? = null,
    @SerializedName("total_items")
    var total_items: String? = null,
    @SerializedName("note")
    var note: String? = null,
    @SerializedName("payment_details")
    var payment_details: String? = null,
    @SerializedName("address_id")
    var address_id: String? = null,
    @SerializedName("promocode")
    var promocode: String? = null,
    @SerializedName("cart_id")
    var cart_id: String? = null,
    @SerializedName("qty")
    var qty: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("rating")
    var rating: String? = null,
    @SerializedName("review")
    var review: String? = null,
    @SerializedName("max_price")
    var max_price: String? = null,
    @SerializedName("min_price")
    var min_price: String? = null,
    @SerializedName("social_id")
    var social_id: String? = null,
    @SerializedName("city")
    var city: String? = null,
    @SerializedName("state")
    var state: String? = null,
    @SerializedName("area")
    var area: String? = null,
    @SerializedName("building_name")
    var building_name: String? = null,
    @SerializedName("landmark")
    var landmark: String? = null,
    @SerializedName("postal_code")
    var postal_code: String? = null,
    @SerializedName("postcode")
    var postcode: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("time_zone")
    var time_zone: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("mall_id")
    var mall_id: String? = null,
    /*
    * pass arrayList example
    * */
    @SerializedName("demo_details")
    var demo_details: ArrayList<DemoListParams>? = null,
    @SerializedName("profile_image")
    var profile_image: String? = null,
    @SerializedName("use_wallet")
    var isUseWallet: Boolean? = null,
    @SerializedName("branch_id")
    var branch_id: String? = null
) : Parcelable