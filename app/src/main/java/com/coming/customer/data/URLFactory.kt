package com.coming.customer.data

import com.coming.customer.BuildConfig
import okhttp3.HttpUrl

/**
 * Created by hlink21 on 11/5/17.
 */

object URLFactory {

    // server details
    const val IS_LOCAL = false
    private const val SCHEME = "http"

    //production: 18.198.109.207
    //BuildConfig.BASE_URL
    //test: 18.184.119.36
    //sha : 192.168.1.50
    private val HOST = BuildConfig.BASE_URL

    //private val HOST = if (IS_LOCAL) "18.159.245.2" else "18.159.245.2" /*
    private val PORT = 8507
    private val API_PATH = "v1/"

    fun provideHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .port(PORT)
            .addPathSegments(API_PATH)
            .build()
    }

    // API Methods
    object Method {
        object Customer {
            private const val CUSTOMER = "customer/"
            private const val COMBINE = "combine/"
            private const val PAYMENT = "payment/"

            //28082020
            const val SIGNUP = CUSTOMER + "signup"
            const val SKIP_STEP = CUSTOMER + "skipStep"
            const val VERIFY_OTP = CUSTOMER + "verifyOTP"
            const val RE_SEND_OTP = CUSTOMER + "resendOTP"
            const val GET_MERCHANT_LIST = CUSTOMER + "getMerchantList"

            //31082020
            const val GET_MERCHANT_DETAILS = CUSTOMER + "getMerchantDetail"

            //01092020
            const val ITEM_DETAILS = CUSTOMER + "itemDetail"
            const val ADD_TO_CART = CUSTOMER + "addToCart"
            const val VIEW_CART = CUSTOMER + "viewCart"

            //04092020
            const val REMOVE_ITEM_FROM_CART = CUSTOMER + "removeItemFromCart"
            const val CHECK_PROMO_CODE = CUSTOMER + "checkPromocode"

            //05092020
            const val PLACE_ORDER = CUSTOMER + "placeOrder"
            const val ADD_SPECIAL_ORDER = CUSTOMER + "addSpecialOrder"
            const val GET_OFFER_LIST = CUSTOMER + "getOfferList"
            const val SAVE_CARD = CUSTOMER + "saveCard"
            const val GET_CARDS = CUSTOMER + "getCards"
            const val DELETE_CARD = CUSTOMER + "deleteCard"
            const val GET_MY_ORDER = CUSTOMER + "getMyOrder"

            //29082020
            const val GET_PROFILE = CUSTOMER + "getProfile"
            const val EDIT_PROFILE = CUSTOMER + "editProfile"
            const val LOGOUT = CUSTOMER + "logout"

            //05102020
            const val GET_INVOICE_LIST = CUSTOMER + "getInvoiceList"

            //28082020
            const val GET_CATEGORY = COMBINE + "getCategory"
            const val CHANGE_NOTIFICATION_STATUS = COMBINE + "changeNotificationStatus"

            //06102020
            const val CREATE_PAYMENT_TOKEN = PAYMENT + "create_payment_token"
            const val CHECK_PAYMENT_STATUS = PAYMENT + "check_payment_status"

            //12102020
            const val GET_WALLET_TRANSACTION = CUSTOMER + "getWalletTransaction"

            //13102020
            const val SET_RATE_REVIEW = COMBINE + "setRateReview"

            //14102020
            const val GET_TOP_SLIDER = COMBINE + "getCarouselImages"
            const val GET_MERCHANT_CATEGORY_LIST = CUSTOMER + "getMerchantCategoryList"

            //16102020
            const val UPDATE_PAYMENT_TYPE = CUSTOMER + "updatePaymentType"
            const val GET_ORDER_DETAILS = CUSTOMER + "getOrderDetails"

            //23102020
            const val GET_ALL_LOCATION = COMBINE + "getAllLocation"

            //remaining api
            const val FORGOT_PASSWORD = CUSTOMER + "forgotpassword" //ignore right now
            const val ADD_TO_WALLET = CUSTOMER + "addToWallet"
            const val REFUND_PAYMENT = CUSTOMER + "refund_payment"


            //combine
            const val GET_CUSTOMER_DRIVER_CURRENT_ORDER = CUSTOMER + "getCustomerDriverCurrentOrder"
            const val CHANGE_LANGUAGE = CUSTOMER + "changeLanguage"

            // clean cart
            const val CLEAN_CART = CUSTOMER + "clearCart"

            //increase and decrease
            const val INCREASE_ITEM = CUSTOMER + "increaseCartItem"
            const val DECREASE_ITEM = CUSTOMER + "decreaseCartItem"

            // get customer service details
            const val GET_CUSTOMER_SERVICE_DETAILS = COMBINE + "getCSdetails"

            // const val UPDATE_PAID_AMOUNT = CUSTOMER +"updatePaidAmount"
            // deduct from customer wallet
            // const val DEDUCT_FROM_WALLET = CUSTOMER + "deductFromWallet"

            //getVersionCode
            const val VERSION_CODE = COMBINE + "getVersionsDetails"

        }
    }

    fun aboutUsEApp(): String {
        return "http://" + HOST + "/coming/home/about_us_app"
    }

    fun termsEApp(): String {
        return "http://" + HOST + "/coming/home/terms_app"
    }

    fun privacyEPolicy(): String {

        return "https://coming.delivery/privacy.html"
    }

    fun faqE(): String {
        return "http://" + HOST + "/coming/home/faq_app"
    }


}
