package com.coming.customer.data.datasource

import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.data.service.AuthenticationService
import com.coming.customer.ui.payment.pojo.CheckPaymentStatus
import com.coming.customer.ui.payment.pojo.CreatePaymentToken
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLiveDataSource @Inject constructor(private val authenticationService: AuthenticationService) : BaseDataSource(), UserRepository {

    override fun login(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.login(param)))
    }

    override fun getProfile(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.getProfile(param)))
    }

    override fun editProfile(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.editProfile(param)))
    }

    override fun logout(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.logout(param)))
    }

    override fun skipStep(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.skipStep(param)))
    }

    override fun verifyOTP(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.verifyOTP(param)))
    }

    override fun resendOTP(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.resendOTP(param)))
    }

    override fun getOrderDetails(param: ApiRequestParams): Single<DataWrapper<GetOrderDetails>> {
        return execute((authenticationService.getOrderDetails(param)))
    }

    override fun getAllLocation(param: ApiRequestParams): Single<DataWrapper<LiveTrackingData>> {
        return execute((authenticationService.getAllLocation(param)))
    }

    override fun getCategory(param: ApiRequestParams): Single<DataWrapper<ArrayList<CategoryList>>> {
        return execute((authenticationService.getCategory(param)))
    }

    override fun getMerchantList(param: ApiRequestParams): Single<DataWrapper<ArrayList<Store>>> {
        return execute((authenticationService.getMerchantList(param)))
    }

    override fun getMerchantDetail(param: ApiRequestParams): Single<DataWrapper<MerchantDetailsAndCategory>> {
        return execute((authenticationService.getMerchantDetail(param)))
    }

    override fun itemDetail(param: ApiRequestParams): Single<DataWrapper<ItemDetails>> {
        return execute((authenticationService.itemDetail(param)))
    }

    override fun addToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.addToCart(param)))
    }

    override fun viewCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.viewCart(param)))
    }

    override fun removeItemFromCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.removeItemFromCart(param)))
    }

    override fun checkPromocode(param: ApiRequestParams): Single<DataWrapper<PromoCode>> {
        return execute((authenticationService.checkPromocode(param)))
    }

    override fun placeOrder(param: ApiRequestParams): Single<DataWrapper<PlaceOrder>> {
        return execute((authenticationService.placeOrder(param)))
    }

    override fun addSpecialOrder(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.addSpecialOrder(param)))
    }

    override fun getOfferList(param: ApiRequestParams): Single<DataWrapper<ArrayList<GetOfferList>>> {
        return execute((authenticationService.getOfferList(param)))
    }

    override fun saveCard(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.saveCard(param)))
    }

    override fun getCards(param: ApiRequestParams): Single<DataWrapper<ArrayList<GetCardList>>> {
        return execute((authenticationService.getCards(param)))
    }

    override fun deleteCard(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.deleteCard(param)))
    }

    override fun getWalletTransaction(param: ApiRequestParams): Single<DataWrapper<TransactionHistory>> {
        return execute((authenticationService.getWalletTransaction(param)))
    }

    override fun setRateReview(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.setRateReview(param)))
    }

    override fun getTopSlider(): Single<DataWrapper<ArrayList<HomeMerchantSlider>>> {
        return execute((authenticationService.getTopSlider()))
    }

    override fun getMerchantCategoryList(param: ApiRequestParams): Single<DataWrapper<ArrayList<MerchantCategory>>> {
        return execute((authenticationService.getMerchantCategoryList(param)))
    }

    override fun updatePaymentType(param: ApiRequestParams): Single<DataWrapper<Any>> {
        return execute((authenticationService.updatePaymentType(param)))
    }

//    override fun updatePaidAmount(param: ApiRequestParams): Single<DataWrapper<Any>> {
//        return execute((authenticationService.updatePaidAmount(param)))
//    }

    override fun getMyOrder(param: ApiRequestParams): Single<DataWrapper<ArrayList<OrderList>>> {
        return execute((authenticationService.getMyOrder(param)))
    }

    override fun getInvoiceList(param: ApiRequestParams): Single<DataWrapper<ArrayList<InvoiceList>>> {
        return execute((authenticationService.getInvoiceList(param)))
    }

    override fun changeNotificationStatus(param: ApiRequestParams): Single<DataWrapper<User>> {
        return execute((authenticationService.changeNotificationStatus(param)))
    }

    override fun createPaymentToken(param: ApiRequestParams): Single<DataWrapper<CreatePaymentToken>> {
        return execute((authenticationService.createPaymentToken(param)))
    }

    override fun checkPaymentStatus(param: ApiRequestParams): Single<DataWrapper<CheckPaymentStatus>> {
        return execute((authenticationService.checkPaymentStatus(param)))
    }

    override fun cleanCart(): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.cleanCart()))
    }

    override fun increaseItemToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.increaseItemToCart(param)))
    }

    override fun decreaseItemToCart(param: ApiRequestParams): Single<DataWrapper<CartDetails>> {
        return execute((authenticationService.decreaseItemToCart(param)))
    }

    override fun getCustomerServiceDetails(): Single<DataWrapper<CustomerServiceDetails>> {
        return execute((authenticationService.getCustomerServiceDetails()))
    }

//    override fun deductFromWallet(param: ApiRequestParams): Single<DataWrapper<Any>> {
//        return execute((authenticationService.deductFromWallet(param)))
//    }

    override fun getVersionCode(): Single<DataWrapper<GetVersionCode>> {
        return execute(authenticationService.getVersionCode())
    }
}
