package com.coming.customer.core


import android.content.Context
import android.provider.Settings
import com.coming.customer.data.pojo.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by hlink21 on 11/7/16.
 */
@Singleton
class AppSession @Inject
constructor(private val appPreferences: AppPreferences, @ApplicationContext private val context: Context) : Session {

    private val gson: Gson = Gson()

    override var user: User? = null
        get() {
            if (field == null) {
                val userJSON = appPreferences.getString(USER_JSON)
                field = gson.fromJson(userJSON, User::class.java)
            }
            return field
        }
        set(value) {
            field = value
            val userJson = gson.toJson(value)
            if (userJson != null)
                appPreferences.putString(USER_JSON, userJson)
        }


    override var userSession: String
        get() = appPreferences.getString(Session.AUTHORIZATION)
        set(userSession) = appPreferences.putString(Session.AUTHORIZATION, userSession)

    override var apiKey: String
        get() = appPreferences.getString(Session.API_KEY)
        set(key) = appPreferences.putString(Session.API_KEY, key)

    override var userId: String
        get() = appPreferences.getString(Session.USER_ID)
        set(userId) = appPreferences.putString(Session.USER_ID, userId)

    override/* open below comment after Firebase integration *///token = FirebaseInstanceId.getInstance().getToken();
    val deviceId: String
        get() {
//            var token = FirebaseInstanceId.getInstance().getToken()
            var token = appPreferences.getString(AppCommon.DEVICE_TOKEN)

            return if (token.isNullOrEmpty()) {
                token = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                token
            } else {
                token
            }

        }

//    override//  return StringUtils.equalsIgnoreCase(appPreferences.getString(Common.LANGUAGE), "ar") ? LANGUAGE_ARABIC : LANGUAGE_ENGLISH;
    /*val language: String
        get() = "en"*/

    override
    val language: String
        get() = appPreferences.getString(AppCommon.LANGUAGE)


    override fun clearSession() {
        appPreferences.clearAll()
    }


    companion object {
        const val USER_JSON = "user_json"
        const val COUNTRY_LIST = "country_list"
        const val PROPERTY_DETAILS = "property_details"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val REQUEST_DATA = "request_data"
    }
}
