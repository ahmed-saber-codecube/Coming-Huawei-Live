package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.GetOrderDetailsItemDetailItem
import com.coming.customer.data.pojo.OrderList
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.OrderDetailAdapter
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.loadUrlRoundedCorner
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_dialog_cart.*
import kotlinx.android.synthetic.main.home_fragment_order_details_pending.*
import kotlinx.android.synthetic.main.home_fragment_orders.textViewLabelOrders
import java.net.ConnectException
import java.net.ProtocolException
import kotlinx.android.synthetic.main.home_dialog_cart.textViewDelivery as textViewDelivery1
import kotlinx.android.synthetic.main.home_dialog_cart.textViewDiscount as textViewDiscount1
import kotlinx.android.synthetic.main.home_dialog_cart.textViewTax as textViewTax1
import kotlinx.android.synthetic.main.home_dialog_cart.textViewTotal as textViewTotal1
import kotlinx.android.synthetic.main.home_fragment_order_details_pending.textViewSubtotal as textViewSubtotal1

@AndroidEntryPoint
class OrderDetailsFragment : BaseFragment(), View.OnClickListener {

    private val parent: IsolatedActivity
        get() = requireActivity() as IsolatedActivity
    var order_id = ""

    private val authViewModel by viewModels<AuthViewModel>()
    var orderDetailList = ArrayList<GetOrderDetailsItemDetailItem>()
    private var orderDetailAdapter: OrderDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    override fun bindData() {
        setupListeners()
        setupRecyclerViewOrderDetail()
        //setupRecyclerViewPaymentMethod()
        handleOrderDetails()
        callGetMyOrderApi()


    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }
    }

    private fun setupListeners() {
        textViewLabelOrders.setOnClickListener(this)
        //textViewDriveChat.setOnClickListener(this)
    }

    private fun setupRecyclerViewOrderDetail() {
        orderDetailAdapter = OrderDetailAdapter(orderDetailList)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewOrderDetails.adapter = orderDetailAdapter
        recyclerViewOrderDetails.layoutManager = layoutManager
    }

//    private fun setupRecyclerViewPaymentMethod() {
//        val methods = arrayListOf<PaymentMethod>().apply {
//            add(PaymentMethod("Credit Card", R.drawable.ic_credit_card_blue))
//            add(PaymentMethod("Apple Pay", R.drawable.ic_apple_pay))
//            add(PaymentMethod("STC Pay", R.drawable.ic_stc_pay_small))
//            add(PaymentMethod("Cash", R.drawable.ic_cash))
//        }
//
//        val adapter = PaymentMethodAdapter(methods, object : PaymentMethodAdapter.Callback {
//            override fun onClick(position: Int) {
//
//            }
//
//        })
//
//        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//
//        recyclerViewPaymentMethods.adapter = adapter
//        recyclerViewPaymentMethods.layoutManager = layoutManager
//    }

    private fun handleOrderDetails() {
        val order = arguments?.getSerializable(AppConstants.KEY_ORDER) as OrderList
        textViewOrderId.text = order.orderId.toString()
        order_id = order.orderId.toString()
        when (order.status) {
            AppCommon.OrderStatus.ON_THE_WAY -> {
                textViewOrderStatus.visibility = View.VISIBLE
                textViewOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

//                textViewPaidWith.visibility = View.GONE
//                textViewLabelPaymentMethod.visibility = View.VISIBLE
//                constraintPaymentMethod.visibility = View.VISIBLE

                // textViewDriveChat.setOnClickListener(this)
            }
//            OrderStatus.OffersAvailable -> {
            AppCommon.OrderStatus.READY -> {
                textViewOrderStatus.visibility = View.VISIBLE
                textViewOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

//                textViewPaidWith.visibility = View.GONE
//                textViewLabelPaymentMethod.visibility = View.VISIBLE
//                constraintPaymentMethod.visibility = View.VISIBLE
//                if (order.driverId=="0"){
//                    textViewDriveChat.visibility = View.GONE
//                }else{
//                    textViewDriveChat.setOnClickListener(this)
//                }


            }
            AppCommon.OrderStatus.DELIVERED -> {
                textViewOrderStatus.visibility = View.VISIBLE
                textViewOrderStatus.text = resources.getString(R.string.label_delivered)
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_done)
                textViewOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

//                textViewPaidWith.visibility = View.VISIBLE
//                textViewLabelPaymentMethod.visibility = View.GONE
//                constraintPaymentMethod.visibility = View.GONE
                // textViewDriveChat.visibility = View.GONE
            }
            AppCommon.OrderStatus.REJECT, AppCommon.OrderStatus.CACNEL -> {
                textViewOrderStatus.visibility = View.VISIBLE
                textViewOrderStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorStatusClosed))
                textViewOrderStatus.text = resources.getString(R.string.label_not_delivered)
                textViewOrderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

//                textViewPaidWith.visibility = View.GONE
//                textViewLabelPaymentMethod.visibility = View.GONE
//                constraintPaymentMethod.visibility = View.GONE

                //  textViewDriveChat.visibility = View.GONE

            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewLabelOrders -> {
                navigator.goBack()
            }

//            R.id.textViewDriveChat -> {
//                navigator.load(ChatFragment::class.java).replace(true)
//            }
        }
    }


    private fun setLiveData() {
        //getOrderDetails live data source
        authViewModel.getOrderDetailsLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1) || responseBody.data != null) {
                    responseBody.data?.let {
                        it.orderDetail?.itemDetail?.let { it1 -> orderDetailList.addAll(it1) }
                        textViewTotal.text = resources.getString(R.string.label_sar) + " " + it.orderDetail?.grandTotal
                        textViewSubtotal.text = resources.getString(R.string.label_sar) + " " + it.orderDetail?.subTotal
                        textViewTax.text = resources.getString(R.string.label_sar) + " " + it.orderDetail?.tax
                        textViewDelivery.text = resources.getString(R.string.label_sar) + " " + it.orderDetail?.deliveryCost
                        textViewDiscount.text = resources.getString(R.string.label_sar) + " " + it.orderDetail?.promocodeDiscount
                        textViewProviderName.text = it.merchantDetail?.username
                        textViewOrderStatus.text = it.orderDetail?.status
                        it.merchantDetail?.profileImage?.let { imageViewProviderLogo.loadUrlRoundedCorner(it, 0, 10) }
                        orderDetailAdapter?.notifyDataSetChanged()
                    }
                }

            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    /*
                    * as per backend infom
                    * 0->operation failed, // show message
                    * 2->no data found // show message
                    * 3->inactive account // exit and signin/signup screen
                    * 11->not approve // exit and signin/signup screen
                    *
                    * */
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
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
    }

    //getMyOrder api
    private fun callGetMyOrderApi() {

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = order_id
        authViewModel.getOrderDetails(apiRequestParams)
    }

    //getMyOrder api
    private fun callGetMyItemDetailApi() {

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = order_id

    }

    override fun createLayout(): Int = R.layout.home_fragment_order_details_pending

}