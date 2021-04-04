package com.coming.customer.ui.viewmodel

import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.ui.base.APILiveData
import com.coming.customer.ui.base.BaseViewModel
import com.coming.customer.ui.payment.pojo.CheckPaymentStatus
import com.coming.customer.ui.payment.pojo.CreatePaymentToken
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {

    @Inject
    lateinit var session: Session

    val loginLiveData = APILiveData<User>()
    val getProfileLiveData = APILiveData<User>()
    val editProfileLiveData = APILiveData<User>()
    val logoutLiveData = APILiveData<Any>()
    val skipStepLiveData = APILiveData<User>()
    val verifyOTPLiveData = APILiveData<User>()
    val resendOTPLiveData = APILiveData<Any>()
    val getOrderDetailsLiveData = APILiveData<GetOrderDetails>()
    val getAllLocationLiveData = APILiveData<LiveTrackingData>()
    val getCategoryLiveData = APILiveData<ArrayList<CategoryList>>()
    val getMerchantListLiveData = APILiveData<ArrayList<Store>>()
    val getMerchantDetailLiveData = APILiveData<MerchantDetailsAndCategory>()
    val getMyOrderLiveData = APILiveData<ArrayList<OrderList>>()
    val getInvoiceListLiveData = APILiveData<ArrayList<InvoiceList>>()
    val changeNotificationStatusLiveData = APILiveData<User>()
    val placeOrderLiveData = APILiveData<PlaceOrder>()
    val addSpecialOrderLiveData = APILiveData<Any>()
    val getOfferListLiveData = APILiveData<ArrayList<GetOfferList>>()
    val saveCardLiveData = APILiveData<Any>()
    val getCardsLiveData = APILiveData<ArrayList<GetCardList>>()
    val deleteCardLiveData = APILiveData<Any>()
    val getWalletTransactionLiveData = APILiveData<TransactionHistory>()
    val setRateReviewLiveData = APILiveData<Any>()
    val getTopSliderLiveData = APILiveData<ArrayList<HomeMerchantSlider>>()
    val getMerchantCategoryListLiveData = APILiveData<ArrayList<MerchantCategory>>()
    val updatePaymentTypeLiveData = APILiveData<Any>()
//    val customerServiceDetailsLiveData = APILiveData<CustomerServiceDetails>()

    //payment getaway
    val createPaymentTokenLiveData = APILiveData<CreatePaymentToken>()
    val checkoutStatusLiveData = APILiveData<CheckPaymentStatus>()

    val getCleanCartLiveData = APILiveData<CartDetails>()
    //val getUpdatePaidAmountLiveData = APILiveData<Any>()
    // val deductFromWalletLiveData = APILiveData<Any>()

    val getVersionCodeLiveData = APILiveData<GetVersionCode>()

    fun login(param: ApiRequestParams) {
        userRepository.login(param).subscribe(withLiveData(loginLiveData))
    }

    fun getProfile(param: ApiRequestParams) {
        userRepository.getProfile(param).subscribe(withLiveData(getProfileLiveData))
    }

    fun editProfile(param: ApiRequestParams) {
        userRepository.editProfile(param).subscribe(withLiveData(editProfileLiveData))
    }

    fun logout(param: ApiRequestParams) {
        userRepository.logout(param).subscribe(withLiveData(logoutLiveData))
    }

    fun skipStep(param: ApiRequestParams) {
        userRepository.skipStep(param).subscribe(withLiveData(skipStepLiveData))
    }

    fun verifyOTP(param: ApiRequestParams) {
        userRepository.verifyOTP(param).subscribe(withLiveData(verifyOTPLiveData))
    }

    fun resendOTP(param: ApiRequestParams) {
        userRepository.resendOTP(param).subscribe(withLiveData(resendOTPLiveData))
    }

    fun getOrderDetails(param: ApiRequestParams) {
        userRepository.getOrderDetails(param).subscribe(withLiveData(getOrderDetailsLiveData))
    }

    fun getAllLocation(param: ApiRequestParams) {
        userRepository.getAllLocation(param).subscribe(withLiveData(getAllLocationLiveData))
    }

    fun getCategory(param: ApiRequestParams) {
        userRepository.getCategory(param).subscribe(withLiveData(getCategoryLiveData))
    }

    fun getMerchantList(param: ApiRequestParams) {
        userRepository.getMerchantList(param).subscribe(withLiveData(getMerchantListLiveData))
    }

    fun getMerchantDetail(param: ApiRequestParams) {
        userRepository.getMerchantDetail(param).subscribe(withLiveData(getMerchantDetailLiveData))
    }

    fun getMyOrder(param: ApiRequestParams) {
        userRepository.getMyOrder(param).subscribe(withLiveData(getMyOrderLiveData))
    }

    fun getInvoiceList(param: ApiRequestParams) {
        userRepository.getInvoiceList(param).subscribe(withLiveData(getInvoiceListLiveData))
    }

    fun changeNotificationStatus(param: ApiRequestParams) {
        userRepository.changeNotificationStatus(param).subscribe(withLiveData(changeNotificationStatusLiveData))
    }

    fun placeOrder(param: ApiRequestParams) {
        userRepository.placeOrder(param).subscribe(withLiveData(placeOrderLiveData))
    }

    fun addSpecialOrder(param: ApiRequestParams) {
        userRepository.addSpecialOrder(param).subscribe(withLiveData(addSpecialOrderLiveData))
    }

    fun getOfferList(param: ApiRequestParams) {
        userRepository.getOfferList(param).subscribe(withLiveData(getOfferListLiveData))
    }

    fun saveCard(param: ApiRequestParams) {
        userRepository.saveCard(param).subscribe(withLiveData(saveCardLiveData))
    }

    fun getCards(param: ApiRequestParams) {
        userRepository.getCards(param).subscribe(withLiveData(getCardsLiveData))
    }

    fun deleteCard(param: ApiRequestParams) {
        userRepository.deleteCard(param).subscribe(withLiveData(deleteCardLiveData))
    }

    fun getWalletTransaction(param: ApiRequestParams) {
        userRepository.getWalletTransaction(param).subscribe(withLiveData(getWalletTransactionLiveData))
    }

    fun setRateReview(param: ApiRequestParams) {
        userRepository.setRateReview(param).subscribe(withLiveData(setRateReviewLiveData))
    }

    fun getTopSlider() {
        userRepository.getTopSlider().subscribe(withLiveData(getTopSliderLiveData))
    }

    fun getMerchantCategoryList(param: ApiRequestParams) {
        userRepository.getMerchantCategoryList(param).subscribe(withLiveData(getMerchantCategoryListLiveData))
    }

    fun updatePaymentType(param: ApiRequestParams) {
        userRepository.updatePaymentType(param).subscribe(withLiveData(updatePaymentTypeLiveData))
    }
//    fun updatePaidAmount(param: ApiRequestParams) {
//        userRepository.updatePaidAmount(param).subscribe(withLiveData(getUpdatePaidAmountLiveData))
//    }
//    fun deductFromWallet(param: ApiRequestParams) {
//        userRepository.deductFromWallet(param).subscribe(withLiveData(deductFromWalletLiveData))
//    }
//    fun getCustomerServiceDetails() {
//        userRepository.getCustomerServiceDetails().subscribe(withLiveData(customerServiceDetailsLiveData))
//    }

    //payment getaway
    fun createCheckoutToken(amount: String, cardtype: String, useWallet: Boolean, orderId: String) {
        val apiRequest = ApiRequestParams()
        apiRequest.amount = amount
        apiRequest.user_id = session.userId
        apiRequest.registrations = ""
        apiRequest.cardtype = cardtype
        apiRequest.isUseWallet = useWallet
        apiRequest.order_id = orderId

        userRepository.createPaymentToken(apiRequest).subscribe(withLiveData(createPaymentTokenLiveData))
    }

    fun checkPaymentStatus(id: String, cardtype: String, orderId: String) {
        val apiRequest = ApiRequestParams()
        apiRequest.id = id
        apiRequest.cardtype = cardtype
        apiRequest.order_id = orderId
        userRepository.checkPaymentStatus(apiRequest).subscribe(withLiveData(checkoutStatusLiveData))
    }

    fun cleanCart() {
        userRepository.cleanCart().subscribe(withLiveData(getCleanCartLiveData))
    }

    fun getVersionCode() {
        userRepository.getVersionCode().subscribe(withLiveData(getVersionCodeLiveData))
    }

}