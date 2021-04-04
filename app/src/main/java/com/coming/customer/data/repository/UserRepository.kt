package com.coming.customer.data.repository

import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.*
import com.coming.customer.ui.payment.pojo.CheckPaymentStatus
import com.coming.customer.ui.payment.pojo.CreatePaymentToken
import io.reactivex.Single

interface UserRepository {

    fun login(param: ApiRequestParams): Single<DataWrapper<User>>

    fun getProfile(param: ApiRequestParams): Single<DataWrapper<User>>

    fun editProfile(param: ApiRequestParams): Single<DataWrapper<User>>

    fun logout(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun skipStep(param: ApiRequestParams): Single<DataWrapper<User>>

    fun verifyOTP(param: ApiRequestParams): Single<DataWrapper<User>>

    fun resendOTP(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getOrderDetails(param: ApiRequestParams): Single<DataWrapper<GetOrderDetails>>

    fun getAllLocation(param: ApiRequestParams): Single<DataWrapper<LiveTrackingData>>

    fun getCategory(param: ApiRequestParams): Single<DataWrapper<ArrayList<CategoryList>>>

    fun getMerchantList(param: ApiRequestParams): Single<DataWrapper<ArrayList<Store>>>

    fun getMerchantDetail(param: ApiRequestParams): Single<DataWrapper<MerchantDetailsAndCategory>>

    fun itemDetail(param: ApiRequestParams): Single<DataWrapper<ItemDetails>>

    fun addToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>>

    fun viewCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>>

    fun removeItemFromCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>>

    fun checkPromocode(param: ApiRequestParams): Single<DataWrapper<PromoCode>>

    fun placeOrder(param: ApiRequestParams): Single<DataWrapper<PlaceOrder>>

    fun addSpecialOrder(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getOfferList(param: ApiRequestParams): Single<DataWrapper<ArrayList<GetOfferList>>>

    fun saveCard(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getCards(param: ApiRequestParams): Single<DataWrapper<ArrayList<GetCardList>>>

    fun deleteCard(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getWalletTransaction(param: ApiRequestParams): Single<DataWrapper<TransactionHistory>>

    fun setRateReview(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getTopSlider(): Single<DataWrapper<ArrayList<HomeMerchantSlider>>>

    fun getMerchantCategoryList(param: ApiRequestParams): Single<DataWrapper<ArrayList<MerchantCategory>>>

    fun updatePaymentType(param: ApiRequestParams): Single<DataWrapper<Any>>

    //fun updatePaidAmount(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getMyOrder(param: ApiRequestParams): Single<DataWrapper<ArrayList<OrderList>>>

    fun getInvoiceList(param: ApiRequestParams): Single<DataWrapper<ArrayList<InvoiceList>>>

    fun changeNotificationStatus(param: ApiRequestParams): Single<DataWrapper<User>>

    fun createPaymentToken(param: ApiRequestParams): Single<DataWrapper<CreatePaymentToken>>

    fun checkPaymentStatus(param: ApiRequestParams): Single<DataWrapper<CheckPaymentStatus>>

    fun cleanCart(): Single<DataWrapper<CartDetails>>

    fun increaseItemToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>>

    fun decreaseItemToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>>

    fun getCustomerServiceDetails(): Single<DataWrapper<CustomerServiceDetails>>

    // fun deductFromWallet(param: ApiRequestParams): Single<DataWrapper<Any>>

    fun getVersionCode(): Single<DataWrapper<GetVersionCode>>


}