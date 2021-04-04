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
import com.coming.customer.core.LocationManager
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.home.PaymentMethodAdapter
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
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
import kotlinx.android.synthetic.main.home_dialog_waiting_page.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class WaitingPageDialogFragment : DialogFragment(), View.OnClickListener, Runnable {
    var placeOrder: PlaceOrder? = null
    lateinit var storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var repository: UserRepository

    //FIXME: use liveModel observer
    fun newInstance(counter: StoreDetailsUpdateCounterInf) = WaitingPageDialogFragment().apply {
        storeDetailsUpdateCounterInf = counter
    }

    //payment getaway
    private var cardtype: String = ""
    var orderNote = ""
    var subTotal = ""

    var promoCode: PromoCode? = null

    var cartDetails: CartDetails? = null

    var disposable: Disposable? = null

    var userId: String = ""
    var deliveryAddress: String = ""
    var deliveryInstructions: String = ""

    var payableTotalAmount: String = ""

    var placeOrderFragment: PlaceOrderDialogFragment? = null
    var cartDialog: CartDialogFragment? = null

    var orderId = ""

    var getOrderDetails: GetOrderDetails? = null
    var STATIC_ORDER_ID: String? = null

    var mainHandler: Handler? = null
    var counter = 0
    var wallet: String? = null

    /*private var timer = object : CountDownTimer(12000, 1000) {
        override fun onFinish() {
            orderReceived()
        }
        override fun onTick(millis: Long) {
            val minutes = (millis / 1000) / 60
            val seconds = (millis / 1000) % 60
            textViewTimer.text = "$minutes:$seconds"
        }
    }*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_waiting_page, container, false)
    }

    override fun onResume() {
        super.onResume()
        Log.e("aws", "onResume: here 4")
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

    override fun onPause() {
        super.onPause()
//        timer.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupRecyclerViewPaymentMethod()
        Log.e("aws", "onViewCreated: here 1")
        callGetOrderDetailsApi()
        bindBroadCast()
        updateDetails()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        try {
            //updateCounterInf.onPayNow(payableTotalAmount, cartDetails, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype)
            mainHandler?.removeCallbacks(this)
            cartDialog?.dismiss()
            placeOrderFragment?.dismiss()
            dismiss()
        } catch (e: Exception) {
        }
    }

    val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                orderId = intent.getStringExtra("key")
                Log.e("aws", "onReceive: here 2")
                callGetOrderDetailsApi()
            }
        }
    }

    private fun bindBroadCast() {

        val lbm = activity?.let { LocalBroadcastManager.getInstance(it) }
        lbm?.registerReceiver(receiver, IntentFilter("filter_string"))

        /*locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    if (it.hasExtra(AppCommon.ORDER_ID)) {
                        val orderId = it.extras?.getString(AppCommon.ORDER_ID)
                        Log.e("--> ", "Broad_cast_order_id : $orderId")
                        showSnackBar("Order ID IS : $orderId")
                    }
                }
            }
        }*/
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
        buttonTrackOrder.setOnClickListener(this)
        textViewOrderId.setOnClickListener(this)
        buttonChooseMethod.setOnClickListener(this)
        textViewLabelChooseMethod.setOnClickListener(this)
        // buttonContact.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
        checkBoxWallet.setOnClickListener(this)
        textViewHelp.setOnClickListener(this)
    }

    private fun setupRecyclerViewPaymentMethod() {
        /*add(PaymentMethod("Credit Card", R.drawable.ic_credit_card_blue))
            add(PaymentMethod("Apple Pay", R.drawable.ic_apple_pay))
            add(PaymentMethod("STC Pay", R.drawable.ic_stc_pay_small))
            add(PaymentMethod("Cash", R.drawable.ic_cash))*/
        val methods = arrayListOf<PaymentMethod>().apply {
            add(PaymentMethod(resources.getString(R.string.text_credit_card), R.drawable.ic_credit_card_blue))
            add(PaymentMethod(resources.getString(R.string.text_mada_card), R.drawable.ic_credit_card_blue))
            add(PaymentMethod(resources.getString(R.string.text_cash), R.drawable.ic_cash))
            // add(PaymentMethod(resources.getString(R.string.text_wallet), R.drawable.wallet))
        }
        cardtype = AppCommon.PaymentMethod.CASH
        storeDetailsUpdateCounterInf.onPayNow(payableTotalAmount, cartDetails, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, checkBoxWallet.isChecked)
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
                    storeDetailsUpdateCounterInf.onPayNow(payableTotalAmount, cartDetails, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, checkBoxWallet.isChecked)
                    buttonPay.visibility = View.GONE
                }
//                else if (position == 3) {
//                    cardtype = AppCommon.PaymentMethod.WALLET
//                }

                buttonChooseMethod.text = methods[position].title
                waitingconstraintPaymentMethod.visibility = View.GONE
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
                    // activity?.finish()
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
                waitingconstraintPaymentMethod.visibility = View.VISIBLE
                buttonChooseMethod.visibility = View.GONE

                scrollToBottom()
            }

            R.id.textViewLabelChooseMethod -> {
                waitingconstraintPaymentMethod.visibility = View.GONE
                buttonChooseMethod.visibility = View.VISIBLE
            }
            R.id.buttonPay -> {
                if (cardtype.isNotEmpty()) {
                    if (payableTotalAmount.isNotEmpty()) {
                        storeDetailsUpdateCounterInf.onPayNow(payableTotalAmount, cartDetails, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype, checkBoxWallet.isChecked)
                        Log.e("aws", "buttonpay: here 3")
                        callGetOrderDetailsApi()
                        checkBoxWallet.isEnabled = false
                    } else {
                        showSnackBar(resources.getString(R.string.label_please_wait_while_we_will_getting_information))
                    }
                } else {
                    showSnackBar(resources.getString(R.string.label_please_select_payment_method))
                }
                //  callPlaceOrderApi()
            }
            R.id.checkBoxWallet -> {
                if (checkBoxWallet.isChecked && (wallet?.toFloat()!! >= payableTotalAmount.toFloat())) {
                    buttonPay.visibility = View.VISIBLE
                    // Log.e("checked", "onClick:checked "+checkBoxWallet.isChecked+" "+ wallet?.toInt()+" "+textViewTotal.text.toString().toInt())
                    cardtype = "wallet"
                    //waitingconstraintPaymentMethod.visibility = View.GONE
                    buttonChooseMethod.visibility = View.GONE
                    textViewHint.visibility = View.GONE
                    // textViewLabelPaymentMethod.visibility = View.GONE

                } else if (checkBoxWallet.isChecked && (wallet?.toFloat()!! <= payableTotalAmount.toFloat())) {
                    //  Log.e("checked", "onClick:unchecked "+checkBoxWallet.isChecked+" "+ wallet?.toInt()+" "+textViewTotal.text.toString().toInt())
                    buttonChooseMethod.visibility = View.VISIBLE
                    textViewHint.visibility = View.VISIBLE
                    textViewHint.text = (Math.round((payableTotalAmount.toFloat() - wallet?.toFloat()!!) * 100.0) / 100.0).toString() + " " + resources.getString(R.string.amount_not_enough)
                    // textViewLabelPaymentMethod.visibility = View.VISIBLE
                } else {
                    textViewHint.visibility = View.GONE
                    buttonChooseMethod.visibility = View.VISIBLE
                    buttonPay.visibility = View.GONE
                    waitingconstraintPaymentMethod.visibility = View.GONE
                }
            }
            R.id.textViewHelp -> {
                startChatActivity()
            }

        }


//            R.id.buttonPay -> {
//                if (cardtype.isNotEmpty()&&cardtype !=AppCommon.PaymentMethod.WALLET) {
//                    if (payableTotalAmount.isNotEmpty()) {
//                        storeDetailsUpdateCounterInf.onPayNow(payableTotalAmount, cartDetails, deliveryAddress, promoCode, deliveryInstructions, orderNote, updateDetailsAfterPlaceOrder, subTotal, cardtype)
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
//                //  callPlaceOrderApi()
//            }
//        }
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


    private fun updateDetails() {

        cartDetails?.let {
            textViewOrderId.visibility = View.VISIBLE
            textViewOrderId.text = STATIC_ORDER_ID
            subTotal = it.subTotal.toString()
            waitingtextViewSubtotal.text = resources.getString(R.string.label_currency) + " " + it.subTotal?.twoDecimal()
            waitingtextViewDelivery.text = resources.getString(R.string.label_currency) + " " + it.deliveryCost?.twoDecimal()
            waitingtextViewTax.text = resources.getString(R.string.label_currency) + " " + it.tax?.twoDecimal()
            waitingtextViewTotal.text = resources.getString(R.string.label_currency) + " " + it.grandTotal?.twoDecimal()

            payableTotalAmount = it.grandTotal.toString()

            it.promocodeAmount?.let {
                textViewLabelDiscount.visibility = View.VISIBLE
                textViewDiscount.visibility = View.VISIBLE
                textViewDiscount.text = resources.getString(R.string.label_currency_minus) + " " + it
            } ?: run {
                textViewLabelDiscount.visibility = View.GONE
                textViewDiscount.visibility = View.GONE
            }

        }

    }


    private fun callGetOrderDetailsApi() {
        disposable?.dispose()

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
                                    AppPreferences(requireContext()).clearAll()
                                    AppPreferences(requireContext()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    when {
                                        cartDialog?.storeDetailActivity != null -> {
                                            cartDialog?.storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                        }
                                        cartDialog?.homeActivity != null -> {
                                            cartDialog?.homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                        }
                                    }
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
                                Log.i("onSuccess", "onSuccess: " + data.responseBody.data?.orderDetail?.paidAmount?.toFloat() + " " + data.responseBody.data?.orderDetail?.grandTotal?.toFloat())
                                if (it.orderDetail?.paidAmount?.toFloat() == it.orderDetail?.grandTotal?.toFloat()) {
                                    try {
                                        payment_status.setBackgroundResource(R.drawable.green_circle)
                                        payment_status.text = resources.getString(R.string.payed_payment)

                                        buttonPay.visibility = View.GONE
                                    } catch (e: Exception) {

                                    }

                                } else {
                                    payableTotalAmount = (it.orderDetail?.grandTotal?.toFloat()!! - it.orderDetail?.paidAmount?.toFloat()!!).toString()
                                }
                                getOrderDetails = it
                                STATIC_ORDER_ID = it.orderDetail?.id.toString()
                                // showSnackBar(it.orderDetail?.id.toString())
                                updateDetailsAfterPlaceOrderAndGetOrderDetails()
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
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        AppPreferences(requireContext()).clearAll()
                        AppPreferences(requireContext()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        when {
                            cartDialog?.storeDetailActivity != null -> {
                                cartDialog?.storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                            }
                            cartDialog?.homeActivity != null -> {
                                cartDialog?.homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                            }
                        }
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
            waitingTextViewWallet.text = wallet
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

    private fun updateDetailsAfterPlaceOrderAndGetOrderDetails() {
        getOrderDetails?.let {
            // seconds = it.seconds.toString()
            wallet = it.userDetail?.wallet
            updateCounter(it.seconds!!)
//            AppPreferences(requireContext()).putString("wallet",it.userDetail?.wallet)
            //?: AppPreferences(requireContext()).getString("")
            it.orderDetail?.status?.let {
                when (it) {
                    AppCommon.OrderStatus.PENDING -> {
                        //  updateCounter(it@getOrderDetails!!.seconds!!)
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
                        waitingFoodAnim.visibility = View.GONE
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


    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


//    private fun callPlaceOrderApi() {
//        if (disposable != null) {
//            disposable!!.dispose()
//        }
//        //user_id,merchant_id,delivery_address,address_lat,address_lng,  optional parameter : delivery_instruction,promocode,notes
//        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.user_id = userId
//        cartDetails?.cartDetail?.get(0)?.merchantId?.let {
//            apiRequestParams.merchant_id = it
//        }
//        deliveryAddress?.let {
//            apiRequestParams.delivery_address = it
//        }
//
//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
//
//                    apiRequestParams.address_lat = latLng.latitude.toString()
//                    apiRequestParams.address_lng = latLng.longitude.toString()
//
//
//                    apiRequestParams.delivery_instruction = deliveryInstructions
//
//                    apiRequestParams.notes = orderNote
//                    apiRequestParams.sub_total = subTotal
//
//                    promoCode?.promocode?.let {
//                        apiRequestParams.promocode = it
//                    }
//
//                    repository.placeOrder(apiRequestParams)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(object : SingleObserver<DataWrapper<PlaceOrder>> {
//                            override fun onSuccess(data: DataWrapper<PlaceOrder>) {
//                                if (data.responseBody?.responseCode == 1) {
//                                    data.responseBody.message?.let {
//                                        showSnackBar(it)
//                                    }
//                                    data.responseBody?.data?.let {
//                                        placeOrder = it
//                                        updateDetails()
//                                    }
//                                } else {
//                                    data.responseBody?.message?.let { showSnackBar(it) }
//                                }
//                            }
//
//                            override fun onSubscribe(d: Disposable) {
//                                disposable = d
//                            }
//
//                            override fun onError(e: Throwable) {
//                                if (e is ServerException) {
//                                    Log.e("--> ", "ServerException : " + e)
//                                    e.message?.let { showSnackBar(it) }
//                                }
//                                if (e is AuthenticationException) {
//                                    val intent = Intent(context, AuthActivity::class.java)
//                                    startActivity(intent)
//                                }
//                            }
//                        })
//
//
//                    locationManager.stop()
//                }
//            }
//
//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showSnackBar(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store))
//            }
//        })
//    }

    private fun showSnackBar(message: String = "") {

        val snackbar = Snackbar.make(waitingRootView, message, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
        //  snackbar.setAction("Ok") { snackbar.dismiss() }
        val snackView = snackbar.view
        val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
        textView.maxLines = 4
        textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
        //      textView.typeface = ResourcesCompat.getFont(this, R.font.gill_sans_regular)

//        snackView.setBackgroundResource(R.drawable.snackbar_gradirent)
        snackbar.show()
    }


/*    open fun requestCheckoutId(): String? {
        val url: URL
        val urlString: String
        var connection: HttpURLConnection? = null
        var checkoutId: String? = null
        urlString = URLFactory.provideHttpUrl().toString() + "?amount=48.99&currency=EUR&paymentType=DB"
        try {u
            url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            val reader = JsonReader(
                InputStreamReader(connection.getInputStream(), "UTF-8")
            )
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName().equals("checkoutId")) {
                    checkoutId = reader.nextString()
                    break
                }
            }
            reader.endObject()
            reader.close()
        } catch (e: java.lang.Exception) {
            *//* error occurred *//*
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }
        return checkoutId
    }*/


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
//                   // disposable = d
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
//}

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
//                    if (data.responseBody?.responseCode == 1) {
//                        data.responseBody.let {
//                           // showToast(it.message)
//                        }
//                    } else {
//                       // data.responseBody?.message?.let { showSnackBar(it) }
//                      //  data.responseBody?.message?.let { Log.e("--> ", "Error : " + it) }
//
//                    }
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                   // disposable = d
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
        disposable?.dispose()
        super.onDestroyView()
    }
}