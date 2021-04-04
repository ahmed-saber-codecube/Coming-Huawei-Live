package com.coming.customer.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.*
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_fragment_orders.*
import java.net.ConnectException
import java.net.ProtocolException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener, OnOrderSelectedListener {
    //do not translate this string
    var flag = "CurrentOrder"//"CurrentOrder","PastOrder"

    private var adapter: OrdersAdapter? = null
    private var pastOrdersAdapter: PastOrdersAdapter? = null

    var curruntOrderList = ArrayList<OrderList>()
    var pastOrderList = ArrayList<OrderList>()

    @Inject
    lateinit var userRepository: UserRepository
    var disposable: Disposable? = null
//    private lateinit var strText: String
//    lateinit var spannableStringBuilder: SpannableStringBuilder

    override fun bindData() {
        setLiveData()
        setupListeners()
        callGetMyOrderApi()
        swipeToRefreshCurrent()
        swipeToRefreshPast()

        /*strText = "20.00 SR"
        spannableStringBuilder = SpannableStringBuilder(strText)
        val subscriptSpan = SubscriptSpan()
        spannableStringBuilder.setSpan(subscriptSpan, strText.indexOf("SR"), strText.indexOf("SR") + "SR".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        showSmallSizeText("2")
        showSmallSizeText("SR")
        textViewTest.text = spannableStringBuilder*/

    }

    private fun swipeToRefreshCurrent() {
        ordersCurrentSwipeToRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        ordersCurrentSwipeToRefresh.setColorSchemeColors(Color.WHITE)
        ordersCurrentSwipeToRefresh.setOnRefreshListener {
            flag = "CurrentOrder"
            curruntOrderList.clear()
            adapter?.notifyDataSetChanged()
            callGetMyOrderApi()
            ordersCurrentSwipeToRefresh.isRefreshing = false
        }
    }

    private fun swipeToRefreshPast() {
        ordersPastSwipeToRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        ordersPastSwipeToRefresh.setColorSchemeColors(Color.WHITE)

        ordersPastSwipeToRefresh.setOnRefreshListener {
            flag = "PastOrder"
            pastOrderList.clear()
            pastOrdersAdapter?.notifyDataSetChanged()
            callGetMyOrderApi()
            ordersPastSwipeToRefresh.isRefreshing = false

        }

    }


    /*private fun showSmallSizeText(string: String) {
        val relativeSizeSpan = RelativeSizeSpan(.5f)
        spannableStringBuilder.setSpan(relativeSizeSpan, strText.indexOf(string), strText.indexOf(string) + string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }*/

    private fun setupListeners() {
        radioButtonCurrent.setOnCheckedChangeListener(this)
        radioButtonPrevious.setOnCheckedChangeListener(this)
    }

    private fun setupRecyclerViewCurruntOrders() {
        adapter = OrdersAdapter(curruntOrderList, this)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewOrdersPastOrder.visibility = View.GONE
        recyclerViewOrders.visibility = View.VISIBLE

        recyclerViewOrders.adapter = adapter
        recyclerViewOrders.layoutManager = layoutManager
    }

    private fun setupRecyclerViewPastOrders() {
        pastOrdersAdapter = PastOrdersAdapter(pastOrderList, this)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerViewOrdersPastOrder.visibility = View.VISIBLE
        recyclerViewOrders.visibility = View.GONE
        recyclerViewOrdersPastOrder.adapter = pastOrdersAdapter
        recyclerViewOrdersPastOrder.layoutManager = layoutManager
    }

    override fun onOfferSelected() {
        val offers = OffersDialogFragment()
        offers.show(childFragmentManager, OffersDialogFragment::class.simpleName)
    }

    var doneRatingListener: RatingListener? = null
    var orderList: OrderList? = null
    var driverRating = "0"
    var storeRating = "0"

//    var ratingListener = object : RatingListener {
//        override fun onRating(driverRatingTemp: String, storeRatingTemp: String, doneRatingListenerTemp: RatingListener) {
//            doneRatingListener = doneRatingListenerTemp
//            if (storeRatingTemp == "0") {
//                showToastLong(resources.getString(R.string.label_please_select_store_rating))
//            } else if (driverRatingTemp == "0") {
//                showToastLong(resources.getString(R.string.label_please_select_driver_rating))
//            } else {
//                driverRating = driverRatingTemp
//                storeRating = storeRatingTemp
//                callSetRateReviewApi()
//            }
//        }
//
//        override fun onDoneRating() {
//
//        }
//
//    }

//    override fun onDeliveredOrderClicked(orderListTemp: OrderList) {
//        orderList = orderListTemp
//        val reviewSheet = ReviewBottomSheetFragment(ratingListener)
//        reviewSheet.show(childFragmentManager, ReviewBottomSheetFragment::class.simpleName)
//    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            when (buttonView.id) {
                R.id.radioButtonCurrent -> {
                    //do not translate this string
                    flag = "CurrentOrder"
                    recyclerViewOrdersPastOrder.visibility = View.GONE
                    ordersPastSwipeToRefresh.visibility = View.GONE
                    curruntOrderList.clear()
                    adapter?.notifyDataSetChanged()
                    recyclerViewOrders.visibility = View.VISIBLE
                    ordersCurrentSwipeToRefresh.visibility = View.VISIBLE
                    callGetMyOrderApi()
//                    adapter.filterOrdersBy(OrderTime.Current)
                }

                R.id.radioButtonPrevious -> {
                    //do not translate this string
                    flag = "PastOrder"
                    recyclerViewOrders.visibility = View.GONE
                    ordersCurrentSwipeToRefresh.visibility = View.GONE
                    pastOrderList.clear()
                    recyclerViewOrdersPastOrder.visibility = View.VISIBLE
                    ordersPastSwipeToRefresh.visibility = View.VISIBLE
                    pastOrdersAdapter?.notifyDataSetChanged()
                    callGetMyOrderApi()
//                    adapter.filterOrdersBy(OrderTime.Previous)
                }
            }
        }
    }

    override fun onOrderSelected(order: OrderList) {
        if (order.status.equals(AppCommon.OrderStatus.ARRIVED) || order.status.equals(AppCommon.OrderStatus.ON_THE_WAY)
            || order.status.equals(AppCommon.OrderStatus.READY) || order.status.equals(AppCommon.OrderStatus.DRIVER_ACCEPT)
        ) {
            callGetOrderDetailsApi(order.orderId.toString())
//        if (order.driverId!="0") {
//            navigator.loadActivity(IsolatedActivity::class.java)
//                .addBundle(Bundle().apply {
//                    putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, TrackOrderFragment::class.java)
//                    putSerializable(AppCommon.DATA, order)
//                })
//                .start()
//        }else{
//            navigator.loadActivity(IsolatedActivity::class.java)
//            .addBundle(Bundle().apply {
//                putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, OrderDetailsFragment::class.java)
//                putParcelable(AppConstants.KEY_ORDER, order)
//            })
//            .start()
//        }
        } else if (order.status.equals(AppCommon.OrderStatus.PENDING) || order.status.equals(AppCommon.OrderStatus.ACCEPT)) {
            callGetOrderDetailsApi(order.orderId.toString())
        } else {

            navigator.loadActivity(IsolatedActivity::class.java)
                .addBundle(Bundle().apply {
                    putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, OrderDetailsFragment::class.java)
                    putParcelable(AppConstants.KEY_ORDER, order)
                })
                .start()
        }

    }


    override fun createLayout(): Int = R.layout.home_fragment_orders

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun ArrayList<com.coming.customer.data.pojo.OrderList>.mutableCopyOf(): MutableList<com.coming.customer.data.pojo.OrderList> {
        val original = this
        return mutableListOf<com.coming.customer.data.pojo.OrderList>().apply { addAll(original) }
    }

    private fun setLiveData() {

        //getMyOrder live data source
        authViewModel.getMyOrderLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        //do not translate this string
                        if (flag == "CurrentOrder") {
                            curruntOrderList.addAll(it)
                            setupRecyclerViewCurruntOrders()
                            adapter?.notifyDataSetChanged()

                            if (curruntOrderList.size == 0) {
                                recyclerViewOrders.visibility = View.GONE
                                textViewNoDataFound.visibility = View.VISIBLE
                            } else {
                                recyclerViewOrders.visibility = View.VISIBLE
                                textViewNoDataFound.visibility = View.GONE
                            }
                            //do not translate this string
                        } else if (flag == "PastOrder") {
                            pastOrderList.addAll(it)
                            setupRecyclerViewPastOrders()
                            pastOrdersAdapter?.notifyDataSetChanged()

                            if (pastOrderList.size == 0) {
                                recyclerViewOrdersPastOrder.visibility = View.GONE
                                textViewNoDataFound.visibility = View.VISIBLE
                            } else {
                                recyclerViewOrdersPastOrder.visibility = View.VISIBLE
                                textViewNoDataFound.visibility = View.GONE
                            }
                        }
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    /*
                    * as per backend inform
                    * 0->operation failed, // show message
                    * 2->no data found // show message
                    * 3->inactive account // exit and signin/signup screen
                    * 11->not approve // exit and signin/signup screen
                    *
                    * */
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
                        if (curruntOrderList.size == 0 || pastOrderList.size == 0) {
                            recyclerViewOrders.visibility = View.GONE
                            textViewNoDataFound.visibility = View.VISIBLE
                            throwable.message?.let { textViewNoDataFound.text = it }
                        } else {
                            recyclerViewOrders.visibility = View.VISIBLE
                            textViewNoDataFound.visibility = View.GONE
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })

//        //setRateReview live data source
//        authViewModel.setRateReviewLiveData.observe(this,
//            { responseBody ->
//
//                if (responseBody.responseCode == 1) {
//                    hideLoader()
//                    Log.e("rate", "setLiveData: " + responseBody)
//                    showToastLong(responseBody.message)
//                    doneRatingListener?.let {
//                        it.onDoneRating()
//                    }
//                }
//            },
//            { throwable ->
//                hideLoader()
//                if (throwable is ServerException) {
//                    /*
//                    * as per backend inform
//                    * 0->operation failed, // show message
//                    * 2->no data found // show message
//                    * 3->inactive account // exit and signin/signup screen
//                    * 11->not approve // exit and signin/signup screen
//                    *
//                    * */
//                    if (throwable.code == 0) {
//                        throwable.message?.let { showToastLong(it) }
//                    } else if (throwable.code == 2) {
//                        if (curruntOrderList.size == 0 || pastOrderList.size == 0) {
//                            recyclerViewOrders.visibility = View.GONE
//                            textViewNoDataFound.visibility = View.VISIBLE
//                            throwable.message?.let { textViewNoDataFound.text = it }
//                        } else {
//                            recyclerViewOrders.visibility = View.VISIBLE
//                            textViewNoDataFound.visibility = View.GONE
//                        }
//                    } else if (throwable.code == 3 || throwable.code == 11) {
//                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
//                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
//                    }
//                }
//                false
//            })
    }

    //getMyOrder api
    private fun callGetMyOrderApi() {
        /**
         *
         * params : getMyOrder(user_id, flag)
         *  flag : "CurrentOrder","PastOrder"
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        apiRequestParams.flag = flag
        authViewModel.getMyOrder(apiRequestParams)
    }

    //setRateReview api
//    private fun callSetRateReviewApi() {
//        /**
//         *
//         * params : setRateReview(user_id ,merchant_id, order_id, driver_id, driver_rating, merchant_rating ,who_given="U")
//         *
//         * optional :
//         *
//         * */
//
//        showLoader()
//        val apiRequestParams = ApiRequestParams()
//
//        apiRequestParams.user_id = session.userId
//        orderList?.let {
//            apiRequestParams.merchant_id = it.merchantId
//        }
//        orderList?.let {
//            apiRequestParams.order_id = it.orderId
//        }
//
//        orderList?.let {
//            apiRequestParams.driver_id = it.driverId
//        }
//        apiRequestParams.branch_id = orderList?.branch_id
//        apiRequestParams.driver_rating = driverRating
//        apiRequestParams.merchant_rating = storeRating
//        apiRequestParams.who_given = "U"
//        apiRequestParams.review = ""
//        Log.e("clicked", "callSetRateReviewApi: ")
//
//
//        authViewModel.setRateReview(apiRequestParams)
//    }
    override fun onResume() {
        super.onResume()
        if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.EN)) {
            radioButtonCurrent.setBackgroundResource(R.drawable.bg_radio_button_orders_current)
            radioButtonPrevious.setBackgroundResource(R.drawable.bg_radio_button_orders_previous)

        } else if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.AR)) {
            radioButtonCurrent.setBackgroundResource(R.drawable.bg_radio_button_orders_previous)
            radioButtonPrevious.setBackgroundResource(R.drawable.bg_radio_button_orders_current)
        }
    }

    private fun callGetOrderDetailsApi(orderid: String) {
        disposable?.dispose()
        //order_id
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = orderid

        userRepository.getOrderDetails(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<GetOrderDetails>> {
                override fun onSuccess(data: DataWrapper<GetOrderDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(requireActivity(), data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                        1 -> {
                            data.responseBody.data?.let {
                                if (HomeActivity.homeUpdateCounterInfPass != null) {
                                    HomeActivity.homeUpdateCounterInfPass.onUpdateCounter("0")
                                    val pendingWaitingPageDialogFragment = PendingWaitingPageDialogFragment.newInstance(HomeActivity.homeUpdateCounterInfPass)
                                    if (!isAdded) return
                                    pendingWaitingPageDialogFragment.show(childFragmentManager, PendingWaitingPageDialogFragment::class.simpleName)
                                    pendingWaitingPageDialogFragment.orderNote = it.orderDetail?.notes.toString()
                                    pendingWaitingPageDialogFragment.promoCode?.promocode = it.orderDetail?.promocodeName
                                    pendingWaitingPageDialogFragment.promoCode?.promocodeType = it.orderDetail?.promocodeType
                                    pendingWaitingPageDialogFragment.promoCode?.promocodeDiscount = it.orderDetail?.promocodeDiscount?.toInt()
                                    pendingWaitingPageDialogFragment.promoCode?.promocodeLimit = it.orderDetail?.promocodeAmount?.toInt()
                                    pendingWaitingPageDialogFragment.userId = it.userDetail?.id.toString()
                                    pendingWaitingPageDialogFragment.orderId = it.orderDetail?.orderId.toString()
                                    pendingWaitingPageDialogFragment.deliveryCost = it.orderDetail?.deliveryCost
                                    pendingWaitingPageDialogFragment.grandTotal = it.grandTotal
                                    pendingWaitingPageDialogFragment.promocodeAmount = it.orderDetail?.promocodeAmount
                                    pendingWaitingPageDialogFragment.subTotal = it.subTotal.toString()
                                    pendingWaitingPageDialogFragment.tax = it.tax
                                    pendingWaitingPageDialogFragment.deliveryAddress = it.orderDetail?.deliveryAddress.toString()
                                    pendingWaitingPageDialogFragment.deliveryInstructions = it.orderDetail?.deliveryInstruction.toString()
                                    pendingWaitingPageDialogFragment.payableTotalAmount = it.grandTotal.toString()
                                    pendingWaitingPageDialogFragment.paymentMethod = it.orderDetail?.paymentMethod.toString()
                                    pendingWaitingPageDialogFragment.ordersFragment = this@OrdersFragment
                                }
                            }
                        }
                        else -> {
                            data.responseBody?.message?.let { showSnackBar(it) }
                            data.responseBody?.message?.let { Log.e("--> ", "Error : " + it) }

                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onError(e: Throwable) {
                    Log.e("--> ", "Error : " + e.printStackTrace())
                    if (e is ServerException) {
                        Log.e("--> ", "ServerException : " + e)
                        e.message?.let { showSnackBar(it) }
                    } else if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun showSnackBar(message: String) {

        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
        //snackbar.setAction("Ok") { snackbar.dismiss() }
        val snackView = snackbar.view
        val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
        textView.maxLines = 4
        textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
        //textView.typeface = ResourcesCompat.getFont(this, R.font.gill_sans_regular)
        //snackView.setBackgroundResource(R.drawable.snackbar_gradirent)
        snackbar.show()
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
}