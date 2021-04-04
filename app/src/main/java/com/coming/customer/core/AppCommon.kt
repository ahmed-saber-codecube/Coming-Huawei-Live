package com.coming.customer.core

import android.Manifest

import java.io.File

object AppCommon {
    val IS_PROTOTYPE = false

    var IS_ACTIVE = false

    const val SERVER_TIMEZONE = "UTC"

    val IMAGE_URI = "image_uri"
    val VIDEO_URI = "video_uri"
    const val SHARED_PREF_NAME = "app_preference"
    const val CHECKOUT_ID = "checkoutId"
    val ACTIVITY_FIRST_PAGE = "FirstPage"
    val CURRENT_USER = "current_user"
    val USER_BUNDLE = "UserBundle"
    val USER_JSON = "UserJSON"
    val DRIVER_JSON = "DriverJSON"
    val SOCIAL_DATA = "soicalData"
    val DATA = "data"
    val DATA1 = "data1"
    val ORDER_DETAILS = "order_details"
    val DRIVER_ID = "driver_id"
    val DRIVER_USER_NAME = "username"
    val DRIVER_IMAGE = "profile_image"

    val DISABLE_DAY_NAME = "wed"

    val DELIVERY_TYPE = "ASAP"
    val DELIVERY_MESSAGE = "Delivery as soon as possible"

    val BOOKING_STATUS_CANCELLED = "Cancelled"
    val BOOKING_STATUS_REJECTED = "Rejected"
    val BOOKING_STATUS_DELIVERED = "Delivered"

    val LANGUAGE = "Locale.Helper.Selected.Language"
    val EN = "en"
    val AR = "ar"

    val CURRUNT_ORDER_DETAILS_DATA = "CurruntOrderDetailsData"
    val PAST_ORDER_DETAILS_DATA = "PastOrderDetailsData"

    const val DEVICE_TOKEN = "device_token"

    val APP_DIRECTORY = "BidToStay"
    val SNAP_DIRECTORY = APP_DIRECTORY + File.separator + "Snap"

    val REQUEST_PERMISSION = 1
    val RC_SELECT_IMAGE = 1
    val REQUEST_CAMERA_PERMISSION = 1
    val REQUEST_GALLERY_PERMISSION = 2
    val REQUEST_CROP_PROFILE = 69
    val REQUEST_CROP_BANNER = 70
    val REQUEST_COUNTRY_CODE = 50
    val REQUEAST_BED_TYPE = 8
    val REQUEST_SERCH_PLACE = 150
    val REQUEST_FILTER_DATA = 22

    val REQUEST_PROMOCODE_DATA = 47
    val PROMO_CODE = "promo_code"

    val LOGIN_WITH_FACEBOOK = "F"
    val LOGIN_WITH_GOOGLE = "G"

    val LOGIN_TYPE = "login_type"

    val FACEBOOK_ID = "facebook_id"
    val FACEBOOK_NAME = "facebook_name"
    val FACEBOOK_EMAIL = "facebook_email"

    val GOOGLE_ID = "google_id"
    val GOOGLE_NAME = "google_name"
    val GOOGLE_EMAIL = "google_email"

    val PERMISSIONS_GALLERY = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val PERMISSIONS_RECORD_VIDEO = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    val IS_LOGIN = "is_login"
    val IS_FIRST_TIME = "is_first_time"
    val IS_HOST_LOGIN = "is_host_login"

    val TAP_TO_LOAD_LIMIT = 4
    val MAX_IMAGE_LOAD_LIMIT = 15
    //    public static final int MAX_IMAGE_LOAD_LIMIT = 20;

    val MESSAGE = "message"
    val NOTIFICATION_COUNT = "notification_count"
    val DISTANCE = "Distance"
    val THEME_COLOR = "theme_color"
    var IS_ON_SERVICE = "is_on_service"
    var IS_CHANNEL_CREATED = "is_channel_created"
    val DIRECTORY = "/BidToStay"


    val ORDER_ID = "order_id"
    val UPDATE_IRDER_DETAILS = "updateOrderDetails"

    val CHANNEL_ID: String = "com.fourshop.driver.ui.module.notificatioservice"
    val CHANNEL_NAME: String = "com.fourshop.driver.notification_channel_01"

    object RequestCode {
        val REQUEST_TAKE_PHOTO = 1
        val RESULT_LOAD_IMAGE = 2
        val REQUEST_IMAGE_AND_VIDEO = 3
        val REQUEST_FROM_CAMERA = 4
        val REQUEST_TO_FINISH = 5
        val REQUEST = 10
        val REQUEST_TRIM_VIDEO = 11
        val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203
        val REQUEST_CONTACT_NUMBER = 6
        val REQUEST_LOAD_VIDEO = 7
        val PLACE_AUTOCOMPLETE_REQUEST_CODE = 100
        val ADD_ADDRESS = 105
        val ADD_SERVICE = 202
        val SELECT_PROPERTY = 20
    }

    object ResultCode {
        val IMAGE_RESULT = 1
        val VIDEO_RESULT = 2
        val FINISH_ME = 444
        val MESSAGE = 10
    }

    object PaymentMethod {
        val VISA = "visa"
        val MADA = "mada"
        val STC_PAY = "stcpay"
        val CASH = "cash"
        val WALLET = "wallet"

    }

    object OrderStatus {
        val PENDING = "Pending"
        val ACCEPT = "Accept"
        val REJECT = "Reject"
        val CACNEL = "Cancel"
        val COMPLETE = "Complete"
        val ON_THE_WAY = "On the way"
        val READY = "Ready"
        val DELIVERED = "Delivered"
        val DRIVER_ACCEPT = "Driver Accept"
        val ARRIVED = "Arrived"
    }

    object DateFormat {
        const val DD_MM_YYYY = "dd-MM-yyyy"
        const val YYYY_MM_DD = "yyyy-MM-dd"
        const val DD_MMM_YYYY = "dd MMM, yyyy"
        const val DD_MMMM_YYYY = "dd MMMM, yyyy"
        const val FULL_DATE = "yyyy-MM-dd HH:mm:ss"
        const val FULL_TIME = "HH:mm:ss"
        const val DD_MMM_YYYY_HH_MM = "dd MMM, yyyy-hh:mm a"
        const val DAY = "EEEE"
        const val UTCFormatNode = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    object Crop {
        const val IMAGE = "image"
        const val REQUEST_CODE = "request_code"
        const val TITLE = "title"
    }

    object RegX {
        val USERNAME = "^[A-Za-z0-9_-]{3,25}$"
        val FULL_NAME = "^(?!\\s)$"
        val EMAIL_ID = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-zA-Z]{2,})$"

    }

    object Location {
        val LATLONG = "latlong"
        val LATITUDE = "latitude"
        val LONGITUDE = "longitude"
        val ADDRESS = "address"
    }

    enum class SelectionModes {
        NONE, SINGLE, MULTI
    }

    object Gender {
        val MALE = "Male"
        val FEMALE = "Female"
    }

    object orderType {
        val ORDER_TYPE = "orderType"

        val REUQUEST = "request"
        val CONFIRMED = "confirmed"
        val INPROGRESS = "inProgress"
        val PACKED = "packed"
        val PICKEDUP = "pickedUp"
        val DELIVERED = "delivered"
    }

    object DeliveryOption {
        val SELF_PICKUP = "Self Pickup"
        val DELIVERY = "Delivery"
        val SEND_SOFT_COPY = "Send Soft Copy"
        val MEET_IN_PERSON = "Meet In Person"
        val DELIVER_HARD_COPY = "Deliver Hard Copy"
    }

    object ProviderType {
        //add/true
        val MAKE_UP_ARTISTS = "Make up artists"
        val EVENT_ORGANIZERS = "Event organizers"
        val PHOTOGRAPHERS = "Photographers"
        val MODELS = "Models"
        val INTERIOR_DECORATION = "Interior decoration"
        val GRAPHIC_DESIGN = "Graphic design"

        //2x2 +/-
        val CAKE_ARTISTS = "Cake artists"
        val FLOWER_ARRANGERS = "Flower arrangers"

        //horizontal listing +/-
        val FOOD_AND_CATERING_PROVIDERS = "Food and catering providers"
    }

    object bookingType {
        val BOOKING_TYPE = "booking_type"


        val ON_GOING = "Ongoing"
        val CONFIRMED = "Confirmed"
        val PENDING_CONFIRMATION = "Pending Confirmation"
        val ON_THE_WAY = "On The Way"
    }

    object NotificationTag {
        const val PLACE_ORDER = "product_delivery_request"//"Order Confirmed"
        const val ACCEPT_ORDER = "accept_order"//"accept order"
        const val ORDER_ACCEPT_BY_DRIVER = "order_accepted_by_driver"//"accept order"


        const val CANCEL_ORDER = "4"//"Order Cancelled"
        const val ORDER_DELIVERED = "5"//"Order Delivered"
        const val TRACKING_NOTIFICATION = "6"//"Tracking Notification"
        const val DISCOUNT = "7"//"Discount"
        const val COMBO = "8"//"Combo"
        const val REVIEW_REQUEST = "9"//"Review Request "
        const val RETURN_REQUEST = "10"//"return Request"
        const val ITEM_NOT_AVAILABLE = "11"//"itemnotavaliable"
        const val CONTACT_US = "12"//"contactus"
        const val CHAT = "chat"

    }

    object NotificationParam {

        /*  const val NOTIFICATION_ID = "NotificationId"
          const val BODY = "body"

          const val DATA = "data"
          const val SENDER_ID = "sender_id"
          const val SENDER_IMAGE = "sender_image"
          const val SENDER_NAME = "sender_name"
          const val post_activity_id = "post_activity_id"
          const val role = "role"*/

//        const val TIME = "time"

        const val TAG = "tag"
        const val ORDER_SUMMARY_ID = "order_summary_id"
        const val TITLE = "title"
        const val MESSAGE = "message"
        const val ORDER_ID = "order_id"

    }
}
