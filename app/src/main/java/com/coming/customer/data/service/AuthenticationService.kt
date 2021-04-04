package com.coming.customer.data.service

import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.URLFactory
import com.coming.customer.data.pojo.*
import com.coming.customer.ui.payment.pojo.CheckPaymentStatus
import com.coming.customer.ui.payment.pojo.CreatePaymentToken
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationService {


    @POST(URLFactory.Method.Customer.SIGNUP)
    fun login(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    @POST(URLFactory.Method.Customer.GET_PROFILE)
    fun getProfile(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    @POST(URLFactory.Method.Customer.EDIT_PROFILE)
    fun editProfile(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    @POST(URLFactory.Method.Customer.LOGOUT)
    fun logout(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.SKIP_STEP)
    fun skipStep(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    @POST(URLFactory.Method.Customer.VERIFY_OTP)
    fun verifyOTP(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    @POST(URLFactory.Method.Customer.RE_SEND_OTP)
    fun resendOTP(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.GET_ORDER_DETAILS)
    fun getOrderDetails(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<GetOrderDetails>>

    @POST(URLFactory.Method.Customer.GET_ALL_LOCATION)
    fun getAllLocation(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<LiveTrackingData>>

    @POST(URLFactory.Method.Customer.GET_CATEGORY)
    fun getCategory(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<CategoryList>>>

    @POST(URLFactory.Method.Customer.GET_MERCHANT_LIST)

    fun getMerchantList(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<Store>>>

    @POST(URLFactory.Method.Customer.GET_MERCHANT_DETAILS)
    fun getMerchantDetail(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<MerchantDetailsAndCategory>>

    @POST(URLFactory.Method.Customer.ITEM_DETAILS)
    fun itemDetail(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ItemDetails>>

    @POST(URLFactory.Method.Customer.ADD_TO_CART)
    fun addToCart(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CartDetails>>

    @POST(URLFactory.Method.Customer.VIEW_CART)
    fun viewCart(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CartDetails>>

    @POST(URLFactory.Method.Customer.REMOVE_ITEM_FROM_CART)
    fun removeItemFromCart(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CartDetails>>

    @POST(URLFactory.Method.Customer.CHECK_PROMO_CODE)
    fun checkPromocode(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<PromoCode>>

    @POST(URLFactory.Method.Customer.PLACE_ORDER)
    fun placeOrder(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<PlaceOrder>>

    @POST(URLFactory.Method.Customer.ADD_SPECIAL_ORDER)
    fun addSpecialOrder(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.GET_OFFER_LIST)
    fun getOfferList(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<GetOfferList>>>

    @POST(URLFactory.Method.Customer.SAVE_CARD)
    fun saveCard(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.GET_CARDS)
    fun getCards(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<GetCardList>>>

    @POST(URLFactory.Method.Customer.DELETE_CARD)
    fun deleteCard(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.GET_WALLET_TRANSACTION)
    fun getWalletTransaction(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<TransactionHistory>>

    @POST(URLFactory.Method.Customer.SET_RATE_REVIEW)
    fun setRateReview(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @GET(URLFactory.Method.Customer.GET_TOP_SLIDER)
    fun getTopSlider(): Single<ResponseBody<ArrayList<HomeMerchantSlider>>>

    @POST(URLFactory.Method.Customer.GET_MERCHANT_CATEGORY_LIST)
    fun getMerchantCategoryList(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<MerchantCategory>>>

    @POST(URLFactory.Method.Customer.UPDATE_PAYMENT_TYPE)
    fun updatePaymentType(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @POST(URLFactory.Method.Customer.GET_MY_ORDER)
    fun getMyOrder(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<OrderList>>>

    @POST(URLFactory.Method.Customer.GET_INVOICE_LIST)
    fun getInvoiceList(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<ArrayList<InvoiceList>>>

    @POST(URLFactory.Method.Customer.CHANGE_NOTIFICATION_STATUS)
    fun changeNotificationStatus(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<User>>

    //payment getaway
    @POST(URLFactory.Method.Customer.CREATE_PAYMENT_TOKEN)
    fun createPaymentToken(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CreatePaymentToken>>

    @POST(URLFactory.Method.Customer.CHECK_PAYMENT_STATUS)
    fun checkPaymentStatus(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CheckPaymentStatus>>

    @POST(URLFactory.Method.Customer.CLEAN_CART)
    fun cleanCart(): Single<ResponseBody<CartDetails>>

    @POST(URLFactory.Method.Customer.INCREASE_ITEM)
    fun increaseItemToCart(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CartDetails>>

    @POST(URLFactory.Method.Customer.DECREASE_ITEM)
    fun decreaseItemToCart(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<CartDetails>>

    @GET(URLFactory.Method.Customer.GET_CUSTOMER_SERVICE_DETAILS)
    fun getCustomerServiceDetails(): Single<ResponseBody<CustomerServiceDetails>>
//    @POST(URLFactory.Method.Customer.UPDATE_PAID_AMOUNT)
//    fun updatePaidAmount(@Body apiRequestParams: ApiRequestParams) : Single<ResponseBody<Any>>

//    @POST(URLFactory.Method.Customer.DEDUCT_FROM_WALLET)
//    fun deductFromWallet(@Body apiRequestParams: ApiRequestParams): Single<ResponseBody<Any>>

    @GET(URLFactory.Method.Customer.VERSION_CODE)
    fun getVersionCode(): Single<ResponseBody<GetVersionCode>>

}