package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromocodeDetails(

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("minimum_booking")
    val minimumBooking: String? = null,

    @field:SerializedName("promocode_limit")
    val promocodeLimit: String? = null,

    @field:SerializedName("for_all_user")
    val forAllUser: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("promocode_remaining")
    val promocodeRemaining: String? = null,

    @field:SerializedName("promocode")
    val promocode: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("benefit")
    val benefit: String? = null,

    @field:SerializedName("promocode_type")
    val promocodeType: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("promocode_discount")
    val promocodeDiscount: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null
) : Parcelable

@Parcelize
data class InvoiceItemDetail(

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

    @field:SerializedName("arabic_name")
    val arabicName: String? = null,

    @field:SerializedName("item_image")
    val itemImage: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("item_description")
    val itemDescription: String? = null
) : Parcelable

@Parcelize
data class InvoiceItemDetailItem(

    @field:SerializedName("quantity")
    val quantity: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("item_addition_id")
    val itemAdditionId: String? = null,

    @field:SerializedName("size_detail")
    val sizeDetail: InvoiceSizeDetail? = null,

    @field:SerializedName("item_size_id")
    val itemSizeId: String? = null,

    @field:SerializedName("item_total")
    val itemTotal: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    @field:SerializedName("addition_detail")
    val additionDetail: List<InvoiceAdditionDetailItem?>? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("item_detail")
    val itemDetail: InvoiceItemDetail? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("order_id")
    val orderId: String? = null
) : Parcelable

@Parcelize
data class UserDetail(

    @field:SerializedName("firstname")
    val firstname: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("login_type")
    val loginType: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("avg_rate")
    val avgRate: String? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("stripe_customer_id")
    val stripeCustomerId: String? = null,

    @field:SerializedName("profile_image")
    val profileImage: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("reward_point")
    val rewardPoint: String? = null,

    @field:SerializedName("challange_duration")
    val challangeDuration: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("forgot_pass_time")
    val forgotPassTime: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("is_login")
    val isLogin: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("is_expier")
    val isExpier: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("wallet")
    val wallet: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("is_admin_approve")
    val isAdminApprove: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("refferal_code")
    val refferalCode: String? = null,

    @field:SerializedName("otp")
    val otp: String? = null,

    @field:SerializedName("setting_levels")
    val settingLevels: String? = null,

    @field:SerializedName("time_zone")
    val timeZone: String? = null,

    @field:SerializedName("is_verified")
    val isVerified: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("lastname")
    val lastname: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = null,

    @field:SerializedName("social_id")
    val socialId: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("device_token")
    val deviceToken: String? = null,

    @field:SerializedName("about_self")
    val aboutSelf: String? = null,

    @field:SerializedName("signup_step")
    val signupStep: String? = null,

    @field:SerializedName("notifications")
    val notifications: String? = null,

    @field:SerializedName("levels")
    val levels: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("forgot_password")
    val forgotPassword: String? = null
) : Parcelable

@Parcelize
data class OrderDetail(

    @field:SerializedName("driver_id")
    val driverId: String? = null,

    @field:SerializedName("delivery_address")
    val deliveryAddress: String? = null,

    @field:SerializedName("notes")
    val notes: String? = null,

    @field:SerializedName("pick_up_address")
    val pickUpAddress: String? = null,

    @field:SerializedName("merchant_id")
    val merchantId: String? = null,

    /*@field:SerializedName("type")
    val type: Any? = null,*/

    @field:SerializedName("admin_earning")
    val adminEarning: String? = null,

    @field:SerializedName("delivery_instruction")
    val deliveryInstruction: String? = null,

    @field:SerializedName("delivery_cost")
    val deliveryCost: String? = null,

    @field:SerializedName("updatedate")
    val updatedate: String? = null,

    @field:SerializedName("promocode_discount")
    val promocodeDiscount: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("grand_total")
    val grandTotal: String? = null,

    @field:SerializedName("payment_method")
    val paymentMethod: String? = null,

    @field:SerializedName("promocode_name")
    val promocodeName: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("card_id")
    val cardId: String? = null,

    @field:SerializedName("promocode_type")
    val promocodeType: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("admin_commision")
    val adminCommision: String? = null,

    @field:SerializedName("item_detail")
    val itemDetail: ArrayList<InvoiceItemDetailItem>? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("address_lng")
    val addressLng: String? = null,

    @field:SerializedName("merchant_earning")
    val merchantEarning: String? = null,

    @field:SerializedName("sub_total")
    val subTotal: String? = null,

    @field:SerializedName("address_lat")
    val addressLat: String? = null,

    @field:SerializedName("insertdate")
    val insertdate: String? = null,

    @field:SerializedName("customer_name")
    val customerName: String? = null,

    @field:SerializedName("order_id")
    val orderId: String? = null,

    @field:SerializedName("promocode_amount")
    val promocodeAmount: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class InvoiceSizeDetail(

    @field:SerializedName("size_name")
    val sizeName: String? = null,

    @field:SerializedName("arabic_size_name")
    val arabicSizeName: String? = null,

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
data class InvoiceAdditionDetailItem(

    @field:SerializedName("extra_category_id")
    val extraCategoryId: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("arabic_addition_name")
    val arabicAdditionName: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("addition_price")
    val additionPrice: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("addition_name")
    val additionName: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null
) : Parcelable

@Parcelize
data class DriverDetail(

    @field:SerializedName("iban_number")
    val ibanNumber: String? = null,

    @field:SerializedName("passwor d")
    val passworD: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("login_type")
    val loginType: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("avg_rate")
    val avgRate: String? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("stripe_customer_id")
    val stripeCustomerId: String? = null,

    @field:SerializedName("reward_point")
    val rewardPoint: String? = null,

    @field:SerializedName("bank_name")
    val bankName: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("is_online")
    val isOnline: String? = null,

    @field:SerializedName("forgot_pass_time")
    val forgotPassTime: String? = null,

    @field:SerializedName("driver_image")
    val driverImage: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("is_login")
    val isLogin: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("is_expier")
    val isExpier: String? = null,

    @field:SerializedName("is_order_assign")
    val isOrderAssign: String? = null,

    @field:SerializedName("id_number")
    val idNumber: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("is_admin_approve")
    val isAdminApprove: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("expiry_date")
    val expiryDate: String? = null,

    @field:SerializedName("refferal_code")
    val refferalCode: String? = null,

    @field:SerializedName("otp")
    val otp: String? = null,

    @field:SerializedName("time_zone")
    val timeZone: String? = null,

    @field:SerializedName("is_verified")
    val isVerified: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = null,

    @field:SerializedName("social_id")
    val socialId: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("device_token")
    val deviceToken: String? = null,

    @field:SerializedName("signup_step")
    val signupStep: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("forgot_password")
    val forgotPassword: String? = null,

    @field:SerializedName("password")
    val password: String? = null
) : Parcelable

@Parcelize
data class InvoiceMerchantDetail(

    @field:SerializedName("firstname")
    val firstname: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("login_type")
    val loginType: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("avg_rate")
    val avgRate: String? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("stripe_customer_id")
    val stripeCustomerId: String? = null,

    @field:SerializedName("profile_image")
    val profileImage: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("reward_point")
    val rewardPoint: String? = null,

    @field:SerializedName("video_limit")
    val videoLimit: String? = null,

    @field:SerializedName("challange_duration")
    val challangeDuration: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("forgot_pass_time")
    val forgotPassTime: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("is_login")
    val isLogin: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("is_expier")
    val isExpier: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("is_active")
    val isActive: String? = null,

    @field:SerializedName("is_admin_approve")
    val isAdminApprove: String? = null,

    @field:SerializedName("insert_date")
    val insertDate: String? = null,

    @field:SerializedName("refferal_code")
    val refferalCode: String? = null,

    @field:SerializedName("otp")
    val otp: String? = null,

    @field:SerializedName("setting_levels")
    val settingLevels: String? = null,

    @field:SerializedName("time_zone")
    val timeZone: String? = null,

    @field:SerializedName("is_verified")
    val isVerified: String? = null,

    @field:SerializedName("update_date")
    val updateDate: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("lastname")
    val lastname: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("background_image")
    val backgroundImage: String? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = null,

    @field:SerializedName("social_id")
    val socialId: String? = null,

    @field:SerializedName("admin_commision")
    val adminCommision: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("device_token")
    val deviceToken: String? = null,

    @field:SerializedName("about_self")
    val aboutSelf: String? = null,

    @field:SerializedName("signup_step")
    val signupStep: String? = null,

    @field:SerializedName("notifications")
    val notifications: String? = null,

    @field:SerializedName("levels")
    val levels: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("forgot_password")
    val forgotPassword: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable

@Parcelize
data class InvoiceList(

    @field:SerializedName("total_item")
    val totalItem: String? = null,

    @field:SerializedName("delivery_cost")
    val deliveryCost: String? = null,

    @field:SerializedName("user_detail")
    val userDetail: UserDetail? = null,

    @field:SerializedName("merchant_detail")
    val merchantDetail: InvoiceMerchantDetail? = null,

    @field:SerializedName("sub_total")
    val subTotal: String? = null,

    @field:SerializedName("driver_detail")
    val driverDetail: DriverDetail? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("grand_total")
    val grandTotal: String? = null,

    @field:SerializedName("order_detail")
    val orderDetail: OrderDetail? = null,

    @field:SerializedName("promocode_details")
    val promocodeDetails: PromocodeDetails? = null,

    @field:SerializedName("promocode_amount")
    val promocodeAmount: String? = null
) : Parcelable
