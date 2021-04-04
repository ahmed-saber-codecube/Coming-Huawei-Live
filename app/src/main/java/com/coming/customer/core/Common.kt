package com.coming.customer.core

import android.Manifest
import com.coming.customer.BuildConfig

object Common {
    val PROVIDER_ID: String = "PROVIDER_ID"
    const val IS_LOGIN = "is_login"
    const val EVENT_DATA = "event_data"
    const val AES_KEY = "ojgoigjgjlfdgjlfgjfdngkjfgnghgfh"
    const val APPLANG = "AppLang"

    const val SERVER_TIMEZONE = "UTC"
    const val ACTIVITY_FIRST_PAGE = "FirstPage"
    const val FNAME = "firstname"
    const val SHARED_PREF_NAME = "app_preference"
    const val USERNAME = "username"
    const val IS_ON_OFF = "is_on_notification"
    const val BOOKING_ID = "booking_id"
    const val ORDER_REQ_ID = "order_request_id"
    const val FCM_PUSH_DATA = "fcm_push_data"
    const val SERVER_TIMEZONE_UTC = "UTC"
//    const val AWSACKEY = "aws_ac_key"
//    const val AWSSCKEY = "aws_sc_key"

    //Is Paymentez SDK DEV environment?
    const val PAYMENTEZ_IS_TEST_MODE = false
    const val PAYMENTEZ_CLIENT_APP_CODE = "YASTAA-EC-CLIENT"/*"YASTAA-EC-SERVER"*/
    const val PAYMENTEZ_CLIENT_APP_KEY = /*"2GFoJAyhIoLKEEufRg6jwNGwYu7k4p"*/"VCnTmvDcskQZ4LXCe5kqjBkM6Nz7Mg"


    const val CANCLE_REASON = "cancle_reason"
    const val PAGE_1 = 1
    const val CANCLE_BY = "cancle_by"
    const val LNAME = "lastname"
    const val BIRTHDATE = "birthdate"
    const val GENDER = "gender"
    const val EMAIL = "email"
    const val USER_JSON = "UserJSON"
    const val USER_STYLIST_JSON = "StylistJSON"
    const val PROFILE_IMAGE = "profile_image"
    const val ABOUT_SERVICE = "about_service"
    const val PASSWORD = "password"
    const val CPASSWORD = "confirm_pwd"
    const val MOBILE_NUM = "phone"
    const val COUNTRY_CODE = "country_code"
    const val OTP = "otp"
    const val DEVICE_TOKEN = "device_token"
    const val SKIP_CUSTOMER = "isSkipped"
    const val LOGIN_TYPE = "login_type"
    const val DEVICE_TYPE = "device_type"
    const val ROLE = "role"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val SOCIAL_ID = "social_id"
    const val USER_ID = "user_id"
    const val REJECT_REASON = "reject_reason"
    const val STYLIST_ID = "stylist_id"
    var ACCEPT = "accept"
    const val PAGE = "page"
    const val TIMEZONE = "time_zone"
    const val SEARCH_TEXT = "search_text"
    const val OLD_PASS = "oldpassword"
    const val NEW_PASS = "newpassword"
    const val CONFIRM_PASS = "confirm_pwd"
    const val ROLE_CUSTOMER = "U"
    const val ROLE_STYLIST = "S"
    const val REQUEST_PERMISSION = 1
    const val CHANNEL_ID = BuildConfig.APPLICATION_ID
    const val SHARED_PREF_REFERRAL_NAME = "app_preference_referral"
    val REQUEST_CALL_PERMISSION = 1014
    const val REQUEST_CAMERA_PERMISSION = 1
    const val REQUEST_GALLERY_PERMISSION = 2
    const val REGEX_ALPHANUMERIC_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{2,}$"


    const val IS_FIRST_TIME: String = "is_first_time"
    const val POSITION: String = "position"
    const val IS_PAYMENT_DONE: String = "payment_done"
    const val IS_WALK_THROUGH_COME: String = "walk_through"
    const val IS_LOGIN_PROVIDER: String = "is_provider_login"
    const val REQUEST_SERVICES_EDIT: Int = 7
    const val APP_DIRECTORY = "Yastaa"
    const val NOTIFICATION = "notification"
    const val MESSAGE = "message"

    val km: String = "km"

    object ProviderType {
        const val DRIVER = "driver"
        const val SERVICE_PROVIDER = "service_provider"
        const val RESERVATION = "reservation"
        const val EVENT = "event"
        const val payment_req = "Payment_request"
    }

    object RequestCode {

        val REQUEST_TAKE_PHOTO = 1
        val RESULT_LOAD_IMAGE = 2
        val REQUEST_IMAGE_AND_VIDEO = 3
        val REQUEST_FROM_CAMERA = 4
        val REQUEST_TO_FINISH = 5
        val REQUEST = 10
        val REQUEST_TRIM_VIDEO = 11
        val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203

        val PLACE_AUTOCOMPLETE_REQUEST_CODE = 123
    }

    object NotificationTag {

        /**
         * Customer Side Push
         */

        val CUSTOMER_RIDE_REQ_ACCEPT = "ride_request_accept"
        val CUSTOMER_NO_DRIVER_FOUND = "no_driver_found"
        val CUSTOMER_CANCEL_RIDE_BY_DRIVER = "cancel_ride_by_driver"
        val CUSTOMER_DRIVER_ARRIVED = "driver_arrived"
        val CUSTOMER_DRIVER_WITH_CUSTOMER = "driver_with_customer"
        val CUSTOMER_RIDE_COMPLETE = "ride_request_complete"
        val CUSTOMER_PAYMENT_RIDE = "payment_for_ride"
        val CUSTOMER_SERVICE_CANCEL_BY_PROVIDER = "service_request_cancel_by_provider"
        val CUSTOMER_SERVICE_REJECT = "service_request_reject"
        val CUSTOMER_SERVICE_ACCEPT = "service_request_accept"
        val CUSTOMER_SERVICE_COMPLETE = "service_request_complete"

        val RESERVATION_ACCEPT = "reservation_request_accept"
        val RESERVATION_REJECT = "reservation_request_reject"
        val RESERVATION_COMPLETE = "reservation_request_complete"
        val RESERVATION_CANCEL_BY_PROVIDER = "reservation_cancel_by_provider"

        val EVENT_COMPLETE = "event_complete"

        val FCM_FIRESTORE_PUSH = "fcm_message"
        val FAMILY_INVITATION = "family_invitation"


    }

    object ApiBookingStatus {

        const val completed = "Complete"
        const val cancel = "Cancel"
        const val rejected = "Reject"
        const val pending = "Pending"
        const val arrived = "Arrived"
        const val paid = "Paid"
        const val pickup = "Pickup"
        const val with_customer = "With_customer"
        const val accept = "Accept"
        const val reject = "Reject"

    }

    /*  object BookingStatusDisplay {

          var completed = BaseFragment.context_?.getString(R.string.Completed)
          var cancel = BaseFragment.context_?.getString(R.string.Canceled)
          var pending = BaseFragment.context_?.getString(R.string.Request)
          var arrived = "Arrived"
          var paid = "Paid"
          var pickup = "Pickup"
          var with_customer = "With_customer"
          var accept = BaseFragment.context_?.getString(R.string.Accepted)
          var reject = BaseFragment.context_?.getString(R.string.Rejected)

      }*/

    object TwilioParam {

        const val T_USER_ID = "t_user_id"
        const val T_USER_NAME = "t_user_name"
        const val T_USER_IMG = "t_user_img"

    }

    object MsgAttr {

        const val RECEIVER_ID = "receiver_id"
        const val SENDER_ID = "sender_id"
        const val SENDER_NAME = "sender_name"
        const val RECEIVER_NAME = "receiver_name"
        const val SENDER_IMG = "sender_img"
        const val RECEIVER_IMG = "receiver_img"

    }


    object ChannelAttr {

        const val CHANNEL_NAME = "channelName"
        const val CHANNEL_IMG = "channelImg"
        const val CHANNEL_TYPE = "channelType"


    }

    object NotificationPayloadParam {

        const val tag = "tag"
        const val body = "body"
        const val id = "id"
        const val title = "title"
        const val provider_name = "provider_name"
        const val provider_image = "provider_image"
        const val provider_id = "provider_id"
        const val booking_id = "booking_id"
        const val order_request_id = "order_request_id"
        const val FIELD_SENDER_ID = "sender_id"
        const val FIELD_SENDER_IMAGE = "sender_image"
        const val FIELD_SENDER_NAME = "sender_name"
        const val FIELD_SENDER_PROVIDER_TYPE = "sender_provider_type"


    }

    object TwilioPayloadParam {

        const val channel_id = "channel_id"
        const val message_id = "message_id"
        const val author = "author"
        const val message_index = "message_index"
        const val message_sid = "message_sid"
        const val twi_sound = "twi_sound"
        const val twi_message_type = "twi_message_type"
        const val channel_sid = "channel_sid"
        const val twi_message_id = "twi_message_id"
        const val twi_body = "twi_body"
        const val channel_title = "channel_title"

    }

    object MediaType {
        const val IMAGE = "image/*"
        const val VIDEO = "video/*"
    }

    public val PERMISSIONS_GALLERY = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    public val PERMISSIONS_CAMERA = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    public val PERMISSIONS_RECORD_VIDEO = arrayOf<String>(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    public val PERMISSIONS_RECORD_CONTACTS = arrayOf<String>(Manifest.permission.READ_CONTACTS)

    const val REQUEST_CAMERA_VIDEO_PERMISSION = 3
    const val REQUEST_GALLERY_VIDEO_PERMISSION = 4

    object Crop {
        const val IMAGE = "image"
        const val REQUEST_CODE = "request_code"
        const val TITLE = "title"
    }

    object WebPageURL {
        val TERMS = "http://18.231.76.113/home/terms_app"
        val TERMS_SPANISH = "http://18.231.76.113/home/terms_spanish_app"

    }


    /*Firebase chat */

    const val FIREBASE_AUTH_PASSWORD = "123456"

    object Chat {
        val CHAT_COLLECTION_NM = "chat"
        val CHAT_READ = "chat_read"
        val CHAT_TIME = "chat_time"
        val MESSAGE = "message"
        val MESSAGE_TYPE = "message_type"
        val CHAT_IMAGE = "chat_image"
        val RECEIVER_ID = "receiver_id"
        val RECENT_CHAT_ID = "recent_chat_id"
        val SENDER_ID = "sender_id"
        val SERVER_TIME = "server_time"
    }

    object RecentChat {
        val RECENT_CHAT_COLLECTION_NM = "recent_chat"
        val ANONYMOUS_ID = "anonymous_id"
        val CHAT_COUNT = "chat_count"
        val CHAT_TIME = "chat_time"
        val MESSAGE = "message"
        val MESSAGE_TYPE = "message_type"
        val RECEIVER_ID = "receiver_id"
        val RECEIVER_IMAGE = "receiver_image"
        val RECEIVER_NAME = "receiver_name"
        val SENDER_ID = "sender_id"
        val CHAT_IMAGE = "chat_image"
        val SENDER_IMAGE = "sender_image"
        val SENDER_NAME = "sender_name"
        val IS_BLOCKED = "is_blocked"
    }

    object User {
        val USER_COLLECTION_NM = "user"
        val DEVICE_TOKEN = "device_token"
        val DEVICE_TYPE = "device_type"
        val EMAIL = "email"
        val IS_ONLINE = "is_online"
        val PHONE = "phone"
        val USER_IMAGE = "user_image"
        val USER_NAME = "user_name"
    }

}