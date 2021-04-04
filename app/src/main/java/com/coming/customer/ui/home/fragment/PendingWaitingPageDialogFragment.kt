package com.coming.customer.ui.home.fragment

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppPreferences
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.home.HomeUpdateCounterInf
import com.coming.customer.ui.home.PaymentMethodAdapter
import com.coming.customer.ui.inf.UpdateDetailsAfterPlaceOrder
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.coming.customer.util.twoDecimal
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_pending_waiting_page_dialog.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class PendingWaitingPageDialogFragment : DialogFragment(), View.OnClickListener, Runnable {
    var tax: String? = null
    var promocodeAmount: String? = null
    var grandTotal: String? = null
    var deliveryCost: String? = null
    var placeOrder: PlaceOrder? = null

    @Inject
    lateinit var repository: UserRepository

    //payment getaway
    private var cardtype: String = ""
    var orderNote = ""
    var subTotal = ""

    var promoCode: PromoCode? = null

    var userId: String = ""
    var deliveryAddress: String = ""
    var deliveryInstructions: String = ""

    var payableTotalAmount: String = ""
    var paymentMethod: String = ""
    var orderId = ""
    var placeOrderFragment: PlaceOrderDialogFragment? = null
    var cartDialog: CartDialogFragment? = null
    var getOrderDetails: GetOrderDetails? = null

    companion object {
        lateinit var STATIC_ORDER_ID: String
        var homeUpdateCounterInf: HomeUpdateCounterInf? = null
        fun newInstance(UpdateCounterInfHome: HomeUpdateCounterInf) = PendingWaitingPageDialogFragment().apply {
            homeUpdateCounterInf = UpdateCounterInfHome
        }
    }

    var mainHandler: Handler? = null
    var counter = 0
    var ordersFragment: OrdersFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pending_waiting_page_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        callGetOrderDetailsApi()
        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 80 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight

        dialog?.window?.apply {
            attributes = params
            setBackgroundDrawableResource(R.drawable.img_bg_cart)
        }
//        timer.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupRecyclerViewPaymentMethod()
        callGetOrderDetailsApi()
        bindBroadCast()
        updateDetails()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        try {
            mainHandler?.removeCallbacks(this)
            if (cartDialog != null && placeOrderFragment != null) {
                cartDialog?.dismiss()
                placeOrderFragment?.dismiss()
            }
            dismiss()
        } catch (e: Exception) {
        }
    }

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                orderId = intent.getStringExtra("key")
                callGetOrderDetailsApi()
            }
        }
    }


    private fun bindBroadCast() {

        val lbm = activity?.let { LocalBroadcastManager.getInstance(it) }
        lbm?.registerReceiver(receiver, IntentFilter("filter_string"))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun orderAcceptedByMerchant() {
        //this check to handle nullability error in case of killing fragment
        if (checkboxReceived != null && textViewOrderReceived != null) {
            checkboxReceived.isChecked = true
            textViewOrderReceived.isChecked = true
            viewLine1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlue))
        }
    }

    private fun merchantOrderPreparation() {
        //this check to handle nullability error in case of killing fragment
        if (checkboxPreparation != null && textViewOrderPrepared != null) {
            checkboxPreparation.isChecked = true
            textViewOrderPrepared.isChecked = true
            viewLine1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlue))
        }
    }

    private fun orderOnTheWay() {
        //this check to handle nullability error in case of killing fragment
        if (checkboxOnTheWay != null && textViewOrderOnTheWay != null) {
            checkboxOnTheWay.isChecked = true
            textViewOrderOnTheWay.isChecked = true
            viewLine2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBlue))
        }
    }

    private fun setupListeners() {
        // checkboxReceived.isChecked = false
//        if(paymentMethod=="Wallet"){
//            buttonPay.visibility = View.GONE
//            buttonChooseMethod.visibility = View.GONE
//            textViewLabelPaymentMethod.visibility = View.GONE
//        }
        buttonTrackOrder.setOnClickListener(this)
        textViewOrderId.setOnClickListener(this)
        buttonChooseMethod.setOnClickListener(this)
        textViewLabelChooseMethod.setOnClickListener(this)
        // buttonContact.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
        pendingCheckBoxWallet.setOnClickListener(this)
        pendingTextViewHelp.setOnClickListener(this)
    }

    private fun setupRecyclerViewPaymentMethod() {
        val methods = arrayListOf<PaymentMethod>().apply {
            add(PaymentMethod(resources.getString(R.string.text_credit_card), R.drawable.ic_credit_card_blue))
            add(PaymentMethod(resources.getString(R.string.text_mada_card), R.drawable.ic_credit_card_blue))
            add(PaymentMethod(resources.getString(R.string.text_cash), R.drawable.ic_cash))
            // add(PaymentMethod(resources.getString(R.string.text_wallet), R.drawable.wallet))
        }
        cardtype = AppCommon.PaymentMethod.CASH
        homeUpdateCounterInf?.onPayNow(orderId, payableTotalAmount, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, pendingCheckBoxWallet.isChecked)
        buttonPay.visibility = View.GONE
        buttonChooseMethod.text = resources.getString(R.string.text_cash)

        val adapterAddCard = PaymentMethodAdapter(methods, object : PaymentMethodAdapter.Callback {
            override fun onClick(position: Int) {
                Log.e("--> ", "position : $position")
                if (position == 0) {
                    cardtype = AppCommon.PaymentMethod.VISA
                    buttonPay.visibility = View.VISIBLE
                } else if (position == 1) {
                    cardtype = AppCommon.PaymentMethod.MADA
                    buttonPay.visibility = View.VISIBLE
                } else if (position == 2) {
                    cardtype = AppCommon.PaymentMethod.CASH
                    homeUpdateCounterInf?.onPayNow(orderId, payableTotalAmount, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, pendingCheckBoxWallet.isChecked)
                    buttonPay.visibility = View.GONE
                }
//                else if (position == 3) {
//                    cardtype = AppCommon.PaymentMethod.WALLET
//                }
                buttonChooseMethod.text = methods[position].title
                constraintPaymentMethod.visibility = View.GONE
                buttonChooseMethod.visibility = View.VISIBLE

            }
        })

        val layoutManagerAddCard = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)


        recyclerViewAddNewPaymentMethods.adapter = adapterAddCard
        recyclerViewAddNewPaymentMethods.layoutManager = layoutManagerAddCard
    }

    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }


    private val updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder = object : UpdateDetailsAfterPlaceOrder {
        override fun onUpdateDetailsAfterPlaceOrder(placeOrderTemp: PlaceOrder) {
//            placeOrder = placeOrderTemp
//            updateDetails()
        }

    }
    var wallet: String? = null

    override fun onClick(view: View) {
        when (view.id) {
//            R.id.buttonContact -> {
//                if (getOrderDetails != null && getOrderDetails?.driverDetail != null && getOrderDetails?.driverDetail?.email != null) {
//                    val contactBottomSheet = ContactDriverBottomSheetFragment(getOrderDetails)
//                    contactBottomSheet.show(requireActivity().supportFragmentManager, ContactDriverBottomSheetFragment::class.simpleName)
//                } else {
//                    showSnackBar(resources.getString(R.string.label_driver_not_assign))
//                }
//            }

            R.id.buttonTrackOrder -> {
                if (getOrderDetails != null && getOrderDetails?.driverDetail != null && getOrderDetails?.driverDetail?.email != null) {

                    val intent = Intent(requireContext(), IsolatedActivity::class.java).apply {
                        putExtra(ActivityStarter.ACTIVITY_FIRST_PAGE, TrackOrderFragment::class.java)
                        putExtra(AppCommon.DATA1, getOrderDetails?.orderDetail?.orderId)
                    }
                    startActivity(intent)
                    //activity?.finish()
                } else {
                    showSnackBar(resources.getString(R.string.label_driver_not_assign))
                }
            }

            R.id.textViewOrderId -> {
                val clipboard: ClipboardManager? = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("referral", textViewOrderId.text)
                clipboard?.setPrimaryClip(clip)
                showSnackBar(resources.getString(R.string.label_order_id_copied))
            }

            R.id.buttonChooseMethod -> {
                constraintPaymentMethod.visibility = View.VISIBLE
                buttonChooseMethod.visibility = View.GONE

                scrollToBottom()
            }

            R.id.textViewLabelChooseMethod -> {
                constraintPaymentMethod.visibility = View.GONE
                buttonChooseMethod.visibility = View.VISIBLE
            }
            R.id.buttonPay -> {
                if (cardtype.isNotEmpty()) {
                    if (payableTotalAmount.isNotEmpty()) {
                        homeUpdateCounterInf?.onPayNow(orderId, payableTotalAmount, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, pendingCheckBoxWallet.isChecked)
                        callGetOrderDetailsApi()
                        pendingCheckBoxWallet.isEnabled = false
                    } else {
                        showSnackBar(resources.getString(R.string.label_please_wait_while_we_will_getting_information))
                    }
                } else {
                    showSnackBar(resources.getString(R.string.label_please_select_payment_method))
                }
                //  callPlaceOrderApi()
            }
            R.id.pendingCheckBoxWallet -> {
                if (pendingCheckBoxWallet.isChecked && (wallet?.toFloat()!! >= payableTotalAmount.toFloat())) {
                    buttonPay.visibility = View.VISIBLE
                    // Log.e("checked", "onClick:checked "+checkBoxWallet.isChecked+" "+ wallet?.toInt()+" "+textViewTotal.text.toString().toInt())
                    cardtype = "wallet"
                    buttonChooseMethod.visibility = View.GONE
                    textViewHint.visibility = View.GONE
                    // textViewLabelPaymentMethod.visibility = View.GONE

                } else if (pendingCheckBoxWallet.isChecked && (wallet?.toFloat()!! <= payableTotalAmount.toFloat())) {
                    //  Log.e("checked", "onClick:unchecked "+checkBoxWallet.isChecked+" "+ wallet?.toInt()+" "+textViewTotal.text.toString().toInt())
                    buttonChooseMethod.visibility = View.VISIBLE
                    textViewHint.visibility = View.VISIBLE
                    textViewHint.text = (payableTotalAmount.toFloat() - wallet?.toFloat()!!).toString() + " " + resources.getString(R.string.amount_not_enough)
                    // textViewLabelPaymentMethod.visibility = View.VISIBLE
                } else {
                    buttonChooseMethod.visibility = View.VISIBLE
                    textViewHint.visibility = View.GONE
                    buttonPay.visibility = View.GONE
                    constraintPaymentMethod.visibility = View.GONE
                }
            }
            R.id.pendingTextViewHelp -> {
                startChatActivity()
            }
//            R.id.buttonPay -> {
//                if (cardtype.isNotEmpty()&&cardtype !=AppCommon.PaymentMethod.WALLET) {
//                    if (payableTotalAmount.isNotEmpty()) {
//                        homeUpdateCounterInf.onPayNow(payableTotalAmount,deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype,false)
//                    } else {
//                        showToast(resources.getString(R.string.label_please_wait_while_we_will_getting_information))
//                    }
//                }else if (cardtype.isNotEmpty()&&cardtype ==AppCommon.PaymentMethod.WALLET) {
//                    if (payableTotalAmount.isNotEmpty()) {
//                        observeDeduct()
//                    }
//                }else{
//                    showToast(resources.getString(R.string.label_please_select_payment_method))
//                }
//            }
        }
    }

    private fun updateDetails() {
        Log.e("cartDetails", "pending")

        textViewOrderId.visibility = View.VISIBLE
        textViewOrderId.text = orderId
        subTotal = subTotal.toString()
        if (subTotal.isNotEmpty())
            pendingtextViewSubtotal.text = resources.getString(R.string.label_currency) + " " + subTotal?.twoDecimal()
        pendingtextViewDelivery.text = resources.getString(R.string.label_currency) + " " + deliveryCost?.twoDecimal()
        pendingtextViewTax.text = resources.getString(R.string.label_currency) + " " + tax?.twoDecimal()
        pendingtextViewTotal.text = resources.getString(R.string.label_currency) + " " + grandTotal?.twoDecimal()

        payableTotalAmount = grandTotal.toString()

        promocodeAmount?.let {
            if (it.toInt() > 0) {
                textViewLabelDiscount.visibility = View.VISIBLE
                textViewDiscount.visibility = View.VISIBLE
                textViewDiscount.text = resources.getString(R.string.label_currency_minus) + " " + it
            }
        } ?: run {
            textViewLabelDiscount.visibility = View.GONE
            textViewDiscount.visibility = View.GONE
        }

    }

    private var disposableGetOrderDetails: Disposable? = null

    private fun callGetOrderDetailsApi() {
        disposableGetOrderDetails?.dispose()

        mainHandler?.removeCallbacks(this)

        //order_id
        val apiRequestParams = ApiRequestParams()

        orderId?.let {
            apiRequestParams.order_id = it
        }
        repository.getOrderDetails(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<GetOrderDetails>> {
                override fun onSuccess(data: DataWrapper<GetOrderDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireActivity()).clearAll()
                                    AppPreferences(requireActivity()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)

                                    /** If not null case */
                                    ordersFragment?.navigator?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                                    cartDialog?.storeDetailActivity?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                                    cartDialog?.homeActivity?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                                    /** If not null case */
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

                                if (it.orderDetail?.paidAmount?.toFloat() == it.orderDetail?.grandTotal?.toFloat()) {
                                    try {
                                        buttonPay.visibility = View.GONE
                                        buttonChooseMethod.visibility = View.GONE
                                        pendingCheckBoxWallet.isEnabled = false
                                        pending_payment_status.setBackgroundResource(R.drawable.green_circle)
                                        pending_payment_status.text = resources.getString(R.string.payed_payment)
                                    } catch (e: Exception) {

                                    }


                                } else {
                                    payableTotalAmount = (it.orderDetail?.grandTotal?.toFloat()!! - it.orderDetail?.paidAmount?.toFloat()!!).toString()
                                }
                                getOrderDetails = it
                                STATIC_ORDER_ID = it.orderDetail?.id.toString()
                                // showSnackBar(it.orderDetail?.id.toString())
                                updateDetailsAfterPlaceOrderAndGetOrderDetails(it)
                            }
                        }
                        else -> {
                            data.responseBody?.message?.let { showSnackBar(it) }
                            data.responseBody?.message?.let { Log.e("--> ", "Error : " + it) }

                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposableGetOrderDetails = d
                }

                override fun onError(e: Throwable) {
                    Log.e("--> ", "Error : " + e.printStackTrace())
                    if (e is ServerException) {
                        Log.e("--> ", "ServerException : " + e)
                        e.message?.let { showSnackBar(it) }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireContext()).clearAll()
                        AppPreferences(requireContext()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)

                        /** If not null case */
                        ordersFragment?.navigator?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                        cartDialog?.storeDetailActivity?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                        cartDialog?.homeActivity?.loadActivity(AuthActivity::class.java)?.byFinishingAll()?.start()

                        /** If not null case */
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    // var seconds = "0"
    private fun updateCounter(seconds: String) {
        seconds?.let {
            // val curranMinuit = it.toDouble().roundToInt() / 60
            Log.e("time", "sec " + it)
            counter = (300000.00 - it.toDouble() * 1000).roundToInt()
            Log.e("time", "counter " + counter)
            mainHandler = Handler(Looper.getMainLooper())
            mainHandler?.post(this)
        } ?: run {
            Log.e("Else", "Run call")
        }
    }

    override fun run() {
        try {
//            pending_payment_status.setBackgroundResource(R.drawable.circle)
//            pending_payment_status.text = resources.getString(R.string.pending_payment)
            textViewWallet.text = wallet
            if (counter > 0) {
                textViewTimer.visibility = View.VISIBLE
                Log.e("time", "run:> " + counter)
                val minutes = (counter / 1000) / 60
                val seconds = (counter / 1000) % 60
                textViewTimer.text = "$minutes:$seconds"
                counter -= 1000
                mainHandler?.postDelayed(this, 1000)
            } else if (counter == 0) {
                Log.e("time", "run:== " + counter)
                val minutes = (counter / 1000) / 60
                val seconds = (counter / 1000) % 60
                textViewTimer.visibility = View.INVISIBLE
                //textViewTimer.text = "$minutes:$seconds"
                mainHandler?.removeCallbacks(this)
                //dialog?.dismiss()
            } else {
                textViewTimer.visibility = View.INVISIBLE
                mainHandler?.removeCallbacks(this)
            }
        } catch (e: Exception) {

        }
    }


    private fun updateDetailsAfterPlaceOrderAndGetOrderDetails(getOrderDetails: GetOrderDetails) {
        getOrderDetails?.let {
            // seconds = it.seconds.toString()
            // Log.e("seconds", "updateDetailsAfterPlaceOrderAndGetOrderDetails: "+it.seconds )
            wallet = it.userDetail?.wallet
            updateCounter(it.seconds!!)
            it.orderDetail?.status?.let {
                when (it) {
                    AppCommon.OrderStatus.PENDING -> {
                        // updateCounter(it@getOrderDetails.seconds!!)
                    }
                    AppCommon.OrderStatus.ACCEPT -> {
                        updateCounter("300")
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()
                    }
                    AppCommon.OrderStatus.REJECT -> {

                    }
                    AppCommon.OrderStatus.CACNEL -> {

                    }
                    AppCommon.OrderStatus.COMPLETE -> {
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()
                        orderOnTheWay()
                    }
                    AppCommon.OrderStatus.ON_THE_WAY -> {
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()
                        orderOnTheWay()
                    }
                    AppCommon.OrderStatus.READY -> {
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()

                    }
                    AppCommon.OrderStatus.DELIVERED -> {
                        if (pendingWaitingFoodAnim != null)
                            pendingWaitingFoodAnim.visibility = View.GONE
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()
                        orderOnTheWay()
                    }
                    AppCommon.OrderStatus.DRIVER_ACCEPT -> {
                        orderAcceptedByMerchant()
                        merchantOrderPreparation()
                        orderOnTheWay()
                    }
                }
            }

        }
    }

    private fun showSnackBar(message: String = "") {

        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        snackBar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
        val snackView = snackBar.view
        val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
        textView.maxLines = 4
        textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
        snackBar.show()
    }

    private fun getChatWindowConfiguration(): ChatWindowConfiguration? {
        val userObj = Gson()
        val json: String = AppPreferences(requireContext()).getString("user")
        val user: User = userObj.fromJson(json, User::class.java)
        val gSonObj = Gson()
        val json2: String = AppPreferences(requireContext()).getString("customerServiceDetails")
        val customerServiceDetails: CustomerServiceDetails = gSonObj.fromJson(json2, CustomerServiceDetails::class.java)
        return if (user?.email.isNullOrEmpty() || user?.username.isNullOrEmpty()) {
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, user?.phone, user?.phone + "@gmail.com", null)
        } else {
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, user?.username, user?.email, null)
        }
    }

    private fun startChatActivity() {
        val intent = Intent(requireContext(), ChatWindowActivity::class.java)
        val config = getChatWindowConfiguration()
        config?.asBundle()?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
//    private fun observeDeduct(){
////        if (disposable != null) {
////            disposable!!.dispose()
////        }
////
////        mainHandler?.removeCallbacks(this)
//        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.amount = payableTotalAmount
//
//        repository.deductFromWallet(apiRequestParams)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : SingleObserver<DataWrapper<Any>> {
//                override fun onSuccess(data: DataWrapper<Any>) {
//                    if (data.responseBody?.responseCode == 1) {
//                        updatePaymentType()
//                        data.responseBody.let {
//                            buttonPay.visibility = View.GONE
//                            buttonChooseMethod.visibility = View.GONE
//                            textViewLabelPaymentMethod.visibility = View.GONE
//                            showToast(it.message)
//                        }
//                    } else {
//                        data.responseBody?.message?.let { showSnackBar(it) }
//                        data.responseBody?.message?.let { Log.e("--> ", "Error : " + it) }
//
//                    }
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                  //  disposable = d
//                }
//
//                override fun onError(e: Throwable) {
//                    e.message?.let { showToast(it) }
//                    Log.e("--> ", "Error : " + e.printStackTrace())
//                    if (e is ServerException) {
//                        Log.e("--> ", "ServerException : " + e)
//                        e.message?.let { showToast(it) }
//                    }
//                    if (e is AuthenticationException) {
//                        val intent = Intent(context, AuthActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            })
//    }

//    private fun updatePaymentType() {
////        if (disposable != null) {
////            disposable!!.dispose()
////        }
////
////        mainHandler?.removeCallbacks(this)
//        val apiRequestParams2 = ApiRequestParams()
//        apiRequestParams2.order_id = orderId
//        apiRequestParams2.payment_method = "Wallet"
//        repository.updatePaymentType(apiRequestParams2)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : SingleObserver<DataWrapper<Any>> {
//                override fun onSuccess(data: DataWrapper<Any>) {
////                    if (data.responseBody?.responseCode == 1) {
////                        data.responseBody.let {
////                            showToast(it.message)
////                        }
////                    } else {
////                        data.responseBody?.message?.let { showSnackBar(it) }
////                        data.responseBody?.message?.let { Log.e("--> ", "Error : " + it) }
////
////                    }
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                 //   disposable = d
//                }
//
//                override fun onError(e: Throwable) {
////                    e.message?.let { showToast(it) }
////                    Log.e("--> ", "Error : " + e.printStackTrace())
////                    if (e is ServerException) {
////                        Log.e("--> ", "ServerException : " + e)
////                        e.message?.let { showToast(it) }
////                    }
////                    if (e is AuthenticationException) {
////                        val intent = Intent(context, AuthActivity::class.java)
////                        startActivity(intent)
////                    }
//                }
//            })
//    }


    override fun onDestroyView() {
        disposableGetOrderDetails?.dispose()
        super.onDestroyView()
    }
}