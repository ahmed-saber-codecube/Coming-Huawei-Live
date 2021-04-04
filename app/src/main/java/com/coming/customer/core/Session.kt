package com.coming.customer.core


import com.coming.customer.data.pojo.User


/**
 * Created by hlink21 on 11/7/16.
 */
public interface Session {

    var apiKey: String

    var userSession: String

    var userId: String

    val deviceId: String

    var user: User?

    val language: String

    fun clearSession()

    companion object {
        const val API_KEY = "api_key"
        const val API_KEY_VALUE = "coming"
        const val USER_ID = "USER_ID"
        const val DEVICE_TYPE = "A"

        const val SING_UP_TYPE_S = "S"
        const val ACCEPT_LANGUAGE = "accept_language"
        const val AUTHORIZATION = "authorization"
        const val APP = "app"
        const val APP_VALUE = "U"

        const val DATA = "data"
        const val MERCHANT_ID = "merchant_id"

        const val WEB_URL_KEY = "web_url_key"
        const val TOOL_BAR_TITLE = "tool_bar_title"
        const val WEB_URL = "web_url"
    }
}
