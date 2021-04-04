package com.coming.customer.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(

    @field:SerializedName("firstname")
    val firstname: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("login_type")
    val loginType: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("notifications")
    val notifications: String? = null,

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

    @field:SerializedName("cart_count")
    val cartCount: String? = null,

    @field:SerializedName("levels")
    val levels: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("forgot_password")
    val forgotPassword: String? = null
) : Parcelable
