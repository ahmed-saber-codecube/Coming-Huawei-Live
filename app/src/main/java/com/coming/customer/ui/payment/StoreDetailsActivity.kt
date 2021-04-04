package com.coming.customer.ui.payment

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.MerchantDetailsCategoryAdapter
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.home.DishesPagerAdapter
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
import com.coming.customer.ui.home.fragment.CartDialogFragment
import com.coming.customer.ui.home.fragment.DishesFragment
import com.coming.customer.ui.home.fragment.EmptyCartDialogFragment
import com.coming.customer.ui.home.fragment.PlaceOrderDialogFragment
import com.coming.customer.ui.inf.UpdateDetailsAfterPlaceOrder
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.loadUrlRoundedCorner
import com.coming.customer.util.twoDecimal
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_search.*
import kotlinx.android.synthetic.main.store_details_activity.*
import kotlinx.android.synthetic.main.store_details_activity.imageViewBack
import java.math.RoundingMode
import java.net.ConnectException
import java.net.ProtocolException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StoreDetailsActivity : BasePaymentActivity(), View.OnClickListener, MerchantDetailsCategoryAdapter.OnItemClick {
    //payment getaway
    //do not translate this string
    private var cardtype: String = "visa"
    private var totalAmount: String = "111"
    private var checkoutId = ""
    var cartDetails: CartDetails? = null
    var deliveryAddress: String = ""
    var promoCode: PromoCode? = null
    var deliveryInstructions: String = ""
    var orderNote = ""
    var subTotal = ""
    var updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder? = null

    var placeOrder: PlaceOrder? = null

    var categoryAdapter: MerchantDetailsCategoryAdapter? = null
    var pagerAdapter: PagerAdapter? = null
    var store = Store()
    //var storeId = ""


    var categoryData = ArrayList<CategoryDataItem>()
    val fragmentList = arrayListOf<DishesFragment>()
    var itemDetail = ArrayList<ItemDetailItem>()
    var merchantDetail = MerchantDetail()
    private var chipsMap: HashMap<Int, Chip> = HashMap()

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var userRepository: UserRepository

    private val authViewModel by viewModels<AuthViewModel>()

    private var homeMerchantSlider: HomeMerchantSlider? = null

    var useWallet: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("cartext", "onCreate: " + textViewCartCounter.text.toString())
        setLiveData()
//        imageViewSearch.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
        imageViewCart.setOnClickListener(this)
        setupRecyclearview()

        val bundle = this.intent.extras
        if (bundle?.containsKey(Session.DATA) == true) {
            store = bundle?.getSerializable(Session.DATA) as Store
            //  storeId = store.id.toString()
            callGetMerchantDetailApi()
        } else if (bundle?.containsKey(Session.MERCHANT_ID) == true) {
            homeMerchantSlider = bundle?.getSerializable(Session.MERCHANT_ID) as HomeMerchantSlider
            //  storeId = homeMerchantSlider?.id.toString()
            // callGetMerchantDetailApi()
        } else {
            Log.e("Else", "Else not getting build")
        }


        setupSearchEditText()

    }


    /*end payment getaway*/
    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)

        }

        if (session.userId != null && session.userId.length != 0) {
            session.user?.cartCount?.let {
                updateCartCounter(it)
            }
            //getProfile api call
            callGetProfileApi()
        } else {

        }
    }


    override fun findFragmentPlaceHolder(): Int = 0

    override fun findContentView(): Int = R.layout.store_details_activity


    /*buttonPayNow -> {
                openAlertDialog()
            }*/


    override fun onClick(v: View?) {
        when (v) {
            imageViewCart -> {
                if (session.userId.isNotEmpty()) {
                    if (textViewCartCounter.text != "0") {
                        val cartDialog = CartDialogFragment.newInstance(storeDetailsUpdateCounterInf, null)
                        cartDialog.repository = userRepository
                        cartDialog.userId = session.userId
                        cartDialog.cartDialog = cartDialog
                        cartDialog.storeDetailActivity = this
                        cartDialog.homeActivity = null
                        cartDialog.show(supportFragmentManager, CartDialogFragment::class.simpleName)
                    } else {
                        val emptyCartDialog = EmptyCartDialogFragment()
                        emptyCartDialog.show(supportFragmentManager, EmptyCartDialogFragment::class.simpleName)
//                        showToast(resources.getString(R.string.label_cart_is_empty))
                    }


                } else {
//                    showToastLong(resources.getString(R.string.authentication_required_please_login))
                    showSettingsDialog()
                }
            }

            imageViewBack -> {
                goBack()
            }

//            imageViewSearch -> {
//                showOrHideSearch()
//            }

        }
    }

    /*payment getaway*/

    fun openAlertDialog() {

        val builder = AlertDialog.Builder(this)
//       var optionDialog = builder.create()
        val inflater = layoutInflater

        val dialogLayout: View = inflater.inflate(R.layout.dialog_select_cardtype, null)
        val textViewPayusingMada = dialogLayout.findViewById<TextView>(R.id.textViewPayusingMada)
        val textViewPayusingVisa = dialogLayout.findViewById<TextView>(R.id.textViewPayusingVisa)
        val textViewPayusingMaster = dialogLayout.findViewById<TextView>(R.id.textViewPayusingMaster)
        // val textViewPayusingSTC = dialogLayout.findViewById<TextView>(R.id.textViewPayusingSTC)
        builder.setView(dialogLayout)
//        builder.show()
        val dialog = builder.show() // this line used to be "dialog.show();"

        textViewPayusingVisa.setOnClickListener {
            //do not translate this string
            cardtype = "visa"
            callPayment(dialog, builder)
        }
        textViewPayusingMada.setOnClickListener {
            //do not translate this string
            cardtype = "mada"

            callPayment(dialog, builder)

        }
        textViewPayusingMaster.setOnClickListener {
            //do not translate this string
            cardtype = "master"
            callPayment(dialog, builder)
        }

//        textViewPayusingSTC.setOnClickListener {
//            //do not translate this string
//            cardtype = "stcpay"
//            callPayment(dialog, builder)
//        }
    }

    fun callPayment(optionDialog: AlertDialog, builder: AlertDialog.Builder) {
        optionDialog.dismiss()
//        callBookedServiceApi()
        callCreatePaymentTokenWs()
    }


    override fun onCheckoutIdReceived(checkoutId: String?) {
        super.onCheckoutIdReceived(checkoutId)
        Log.e("tag", "checkoutId  checkoutId checkoutId   " + checkoutId)

        if (checkoutId != null) {
            this.checkoutId = checkoutId
            openCheckoutUI(checkoutId)
        }
    }

    private fun openCheckoutUI(checkoutIdd: String) {
        Log.e("tag", "checkoutIdd    " + checkoutIdd)
        Log.e("tag", "checkoutId     " + checkoutId)
        //do not translate this string
        var paymentBrand = "VISA"
        val PAYMENT_BRANDS: MutableSet<String>
        PAYMENT_BRANDS = LinkedHashSet()

        when (cardtype) {
            "visa" -> {
                PAYMENT_BRANDS.add("VISA")
            }
            "master" -> {
                PAYMENT_BRANDS.add("MASTER")
            }
            "mada" -> {
                PAYMENT_BRANDS.add("MADA")
            }
//            "stc" -> {
//                PAYMENT_BRANDS.add("STC_PAY")
//            }


        }
        val checkoutSettings = createCheckoutSettings(checkoutIdd, getString(R.string.checkout_ui_callback_scheme), PAYMENT_BRANDS)

        /* Set componentName if you want to receive callbacks from the checkout */
        val componentName = ComponentName(
            packageName, CheckoutBroadcastReceiver::class.java.name
        )

        /* Set up the Intent and start the checkout activity. */
        val intent = checkoutSettings.createCheckoutActivityIntent(this, componentName)

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT)
    }

    /*screen ui*/

    private fun setupRecyclearview() {
//        private val confirmationCartAdapter by lazy { BookingRequiestDetailsAdapter(myBookingArrayList, this,session) }
        categoryAdapter = MerchantDetailsCategoryAdapter(categoryData, this)
        recyclerViewCategory.adapter = categoryAdapter
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    private fun showOrHideSearch() {
        if (layout_search.visibility == View.GONE) {
            layout_search.animate()
                .translationX(0f)
                .setDuration(300)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        layout_search.visibility = View.VISIBLE
                    }

                })
        } else {
            layout_search.animate()
                .translationX(layout_search.width.toFloat())
                .setDuration(300)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        layout_search.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
        }
    }

    fun showSettingsDialog() {
        val dialog = android.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(resources.getString(R.string.label_to_access_this_you_need_to_login))
            .setMessage(resources.getString(R.string.label_do_you_want_to_login_now))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.label_yes)) { dialog, which ->
                appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                appPreferences.clearAll()
                loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
            }.setNegativeButton(resources.getString(R.string.label_not_now)) { dialog, which ->
                dialog.cancel()
            }.create()

        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
        }
        dialog.show()
    }


    val storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf = object : StoreDetailsUpdateCounterInf {
        override fun onUpdateCounter(updateCounterValue: String) {
            updateCartCounter(updateCounterValue)
        }

        override fun onAuthenticated() {
            showSettingsDialog()
        }

        override fun onPayNow(
            payableTotalAmount: String,
            cartDetails: CartDetails?,
            deliveryAddress: String,
            promoCode: PromoCode?,
            deliveryInstructions: String,
            orderNote: String,
            updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder,
            subTotal: String,
            cardType: String,
            useWallet: Boolean
        ) {
            totalAmount = payableTotalAmount
            this@StoreDetailsActivity.cartDetails = cartDetails
            this@StoreDetailsActivity.deliveryAddress = deliveryAddress
            this@StoreDetailsActivity.promoCode = promoCode
            this@StoreDetailsActivity.deliveryInstructions = deliveryInstructions
            this@StoreDetailsActivity.orderNote = orderNote
            this@StoreDetailsActivity.subTotal = subTotal
            this@StoreDetailsActivity.updateDetailsAfterPlaceOrder = updateDetailsAfterPlaceOrder
            // openAlertDialog() //change ui on dialog to call directky
            this@StoreDetailsActivity.cardtype = cardType
            this@StoreDetailsActivity.useWallet = useWallet
            //callPlaceOrderApi()
            //callUpdatePaymentTypeWs()
            if (this@StoreDetailsActivity.cardtype == AppCommon.PaymentMethod.CASH) {
                callUpdatePaymentTypeWs()
            } else {
                // openAlertDialog()
                callCreatePaymentTokenWs()
            }
        }
    }

    private fun updateCartCounter(cartCount: String) {
        if (cartCount == "0") {
            textViewCartCounter.visibility = View.INVISIBLE
//            when {
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
            textViewCartCounter.text = cartCount
//                }
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                    textViewCartCounter.text = getEnglishNumberToArabicNumber(cartCount)
//                }
            //     }
        } else {
            textViewCartCounter.visibility = View.VISIBLE
//            when {
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
            textViewCartCounter.text = cartCount
            //  imageViewCart.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
            //textViewCartCounter.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))

//                }
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                    textViewCartCounter.text = getEnglishNumberToArabicNumber(cartCount)
//                }
            //   }

        }

    }

    private fun updateDetails() {
        var itemDetail = ArrayList<ItemDetailItem>()

        merchantDetail?.let {
            it.backgroundImage?.let { it1 -> thumbnail.loadUrlRoundedCorner(it1, 0, 15) }
            it.profileImage?.let { it1 -> imageViewProviderLogo.loadUrlRoundedCorner(it1, 0, 15) }
            it.username?.let { it1 -> textViewProviderName.text = it1.trim() }
            it.distance?.let { it1 ->
                textViewDistance.text = it1.toString().twoDecimal() + " " + resources.getString(R.string.label_km)
            }
            it.rating?.let { it1 -> textViewRating.text = it1 }
            it.rating?.let { it1 -> ratingBarRestaurant.rating = it1.toFloat() }
            it.categoryName?.let { it1 -> textViewProviderVariety.text = it1 }

        }

        when (merchantDetail.status) {
            StoreStatus.Open.toString() -> {
                textViewStatus.text = resources.getString(R.string.status_open)
                textViewStatus.setTextColor(resources.getColor(R.color.colorStatusOpen))
            }
            StoreStatus.Busy.toString() -> {
                textViewStatus.text = resources.getString(R.string.status_busy)
                textViewStatus.setTextColor(resources.getColor(R.color.colorStatusBusy))
            }
            StoreStatus.Close.toString() -> {
                textViewStatus.text = resources.getString(R.string.status_close)
                textViewStatus.setTextColor(resources.getColor(R.color.colorStatusClosed))
            }
        }
//        categoryData?.let {
//            chipGroupCategory.removeView(chipBurger)
//            for (i in 0 until it.size) {
//                fragmentList.add(DishesFragment(it[i], storeId, updateCounterInf))
//                chipsMap.put(i , chipBuilder(it[i].categoryName.toString()))
//            }
//            setupViewPagerDishes()
//            setupChipGroupCategory()
//        }

    }

    private fun setupViewPagerDishes() {
        pagerAdapter = DishesPagerAdapter(supportFragmentManager, fragmentList)
        viewPagerDishes.adapter = pagerAdapter

        viewPagerDishes?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {


            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until categoryData.size) {
                    categoryData[i].isSelectedCategory = i == position
                }
//                chipGroupCategory.check(chipsMap[position + 1]?.id!!)
                categoryAdapter?.notifyDataSetChanged()
                recyclerViewCategory?.smoothScrollToPosition(position)
            }

        })

        /* viewPagerDishes.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
             override fun onPageSelected(position: Int) {
                 chipGroupCategory.check(chipsMap[position + 1]?.id!!)
             }
         })*/
    }

    private fun chipBuilder(chipTitle: String): Chip {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromAttributes(this, null, 0, R.style.BaseChip_NoIcon))
        chip.setTextColor(ContextCompat.getColorStateList(this, R.color.color_chip_text))
        chip.text = chipTitle
        return chip
    }

    private fun roundOffDecimal(number: Double): Double? {
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val df = nf as DecimalFormat
        df.roundingMode = RoundingMode.FLOOR
        df.applyPattern("#.##")
        return df.format(number).toDouble()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchEditText() {
        /*editTextSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyBoard()
                if (fragmentList.size != 0) {
                    fragmentList[viewPagerDishes.currentItem].onSearch(editTextSearch.text.toString().trim())
                }
                return@OnEditorActionListener true
            }
            false
        })*/
//        editTextSearch.textChanges().debounce(300)
//            .onEach {
//                if (fragmentList.size != 0) {
//                    if (it.isNotEmpty())
//                        fragmentList[ viewPagerDishes.currentItem].onSearch(editTextSearch.text.toString().trim())
//                    else
//                        fragmentList[ viewPagerDishes.currentItem].onSearch("")
//                }
//            }
//            .launchIn(lifecycleScope)
//
//        layout_search.setEndIconOnClickListener {
//            editTextSearch.setText("")
//            if (editTextSearch.text.toString().trim().isEmpty() && fragmentList.size != 0) {
//                fragmentList[0].onSearch("", false)
//            }
//        }
    }

    override fun onItemClicked(position: Int) {
        viewPagerDishes.currentItem = position
    }

    //live data
    private fun setLiveData() {
        //getProfile live data source
        authViewModel.getProfileLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()

                        session.user?.cartCount?.let {
                            Log.e("carttext", "setLiveData: " + it)
                            updateCartCounter(it.toString())
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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let {
                            showToast(it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })


        //getMerchantDetail live data source
        authViewModel.getMerchantDetailLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    fragmentList.clear()
                    categoryData.clear()
                    categoryData.add(CategoryDataItem("", "", resources.getString(R.string.label_all), "", "", "", "", true, ""))
                    fragmentList.add(DishesFragment().newInstance(CategoryDataItem("", "", resources.getString(R.string.label_all), "", "", "", "", true, ""), store, storeDetailsUpdateCounterInf, this))


                    responseBody.data?.merchantDetail?.let {
                        merchantDetail = it
                    }
                    responseBody.data?.categoryData?.let {

                        categoryData.addAll(it)
                        for (i in 0 until it.size) {
                            fragmentList.add(DishesFragment().newInstance(it[i], store, storeDetailsUpdateCounterInf, this))
                            chipsMap.put(i + 2, chipBuilder(it[i].categoryName.toString()))
                        }

                        pagerAdapter?.notifyDataSetChanged()
                        categoryAdapter?.notifyDataSetChanged()

                        setupViewPagerDishes()
//                        setupChipGroupCategory()
                    }
                    if (categoryData.size > 0) {
                        categoryData[0].isSelectedCategory = true
                    }
                    categoryAdapter?.notifyDataSetChanged()

                    responseBody.data?.itemDetail?.let {
                        itemDetail.addAll(it)
                    }
                    updateDetails()
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
                        throwable.message?.let {
                            showToast(it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })

        //placeOrder live data source
        authViewModel.placeOrderLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {

                    if (responseBody?.responseCode == 1) {
                        responseBody.message?.let {
                            showToast(it)
                        }
                        responseBody?.data?.let {
                            placeOrder = it
                            //updateDetails()
                        }

                        if (this@StoreDetailsActivity.cardtype == AppCommon.PaymentMethod.CASH) {
                            callUpdatePaymentTypeWs()
                        } else {
                            callCreatePaymentTokenWs()
                        }

                    } else {
                        responseBody?.message?.let {
                            showToast(it)
                        }
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
                        throwable.message?.let {
                            showToast(it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })

        //createPaymentToken live data source
        authViewModel.createPaymentTokenLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 7) {
                    showToast(responseBody.message)
                } else if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.id?.let {
                        checkoutId = it
                        appPreferences.putString(AppCommon.CHECKOUT_ID, checkoutId)
                        requestCheckoutId(responseBody.data.totalAmount, session.userId, cardtype)
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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToast(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })


        //checkoutStatus live data source
        authViewModel.checkoutStatusLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {
                    //callPlaceOrderApi()
                    //callUpdatePaidAmount(PlaceOrderDialogFragment.STATIC_ORDER_ID!!,responseBody.data?.amount!!)
                    callUpdatePaymentTypeWs()
                    showToast(responseBody.message)
                    Log.e("checkoutStatusLiveData", "setLiveData: 1 " + responseBody.message + " " + responseBody.data?.amount)

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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let {
                            showToast(it)
                            Log.e("checkoutStatusLiveData", "setLiveData: 2  " + it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Log.e("checkoutStatusLiveData", "setLiveData: 3 " + throwable.message)
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })

        //updatePaymentType live data source
        authViewModel.updatePaymentTypeLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {
                    showToast(responseBody.message)
                    placeOrder?.let { updateDetailsAfterPlaceOrder?.onUpdateDetailsAfterPlaceOrder(it) }
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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let {
                            showToast(it)
                        }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                } else if (throwable is AuthenticationException) {
                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                    appPreferences.clearAll()
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                } else if (throwable is ProtocolException || throwable is ConnectException) {
                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                }
                false
            })

    }
//    private fun callUpdatePaidAmount(orderId:String,amount:String){
//        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.order_id = orderId
//        apiRequestParams.amount = amount
//        authViewModel.updatePaidAmount(apiRequestParams)
//    }

    //getProfile api call
    private fun callGetProfileApi() {
        /**
         *
         * params : getProfile(user_id)
         *
         * optional :
         *
         * */
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        authViewModel.getProfile(apiRequestParams)
    }

    //getMerchantDetail api call
    private fun callGetMerchantDetailApi() {
        /**
         *
         * params : getMerchantDetail(merchant_id ,lat,lng)
         *
         * optional : category_id,user_id,page
         *
         * */

//
//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
        // showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.lng = appPreferences.getString("longi")
        apiRequestParams.lat = appPreferences.getString("lati")
        apiRequestParams.merchant_id = store.id
        apiRequestParams.branch_id = store.branchId
        Log.e("Ahmed", "callGetMerchantDetailApi: " + store.id + "  " + store.branchId)

        authViewModel.getMerchantDetail(apiRequestParams)
        //  locationManager.stop()
//                }
//            }

//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showAlertDialog(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store), "", object : DialogOkListener {
//                    override fun onClick() {
//                        callGetMerchantDetailApi()
//                    }
////                    }, getString(R.string.label_ok))
//                })
//            }
//        })


    }

    //placeOrder api call
//    private fun callPlaceOrderApi() {
//        //user_id,merchant_id,delivery_address,address_lat,address_lng,  optional parameter : delivery_instruction,promocode,notes
//        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.user_id = session?.user?.id
//        cartDetails?.cartDetail?.get(0)?.merchantId?.let {
//            apiRequestParams.merchant_id = it
//        }
//        deliveryAddress?.let {
//            apiRequestParams.delivery_address = it
//        }
//        apiRequestParams.delivery_instruction = deliveryInstructions
//        apiRequestParams.notes = orderNote
//        apiRequestParams.sub_total = subTotal
//        promoCode?.promocode?.let {
//            apiRequestParams.promocode = it
//        }
//
//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
//                    showLoader()
//                    apiRequestParams.address_lat = latLng.latitude.toString()
//                    apiRequestParams.address_lng = latLng.longitude.toString()
//                    locationManager.stop()
//                    authViewModel.placeOrder(apiRequestParams)
//                }
//            }
//
//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showToast(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store))
//            }
//        })
//
//    }

    //createPaymentToken api call
    private fun callCreatePaymentTokenWs() {
        Log.e("Tag", "totalAmount" + totalAmount)
        //  showLoader()
        authViewModel.createCheckoutToken(totalAmount, cardtype, useWallet, PlaceOrderDialogFragment.STATIC_ORDER_ID!!)
//        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme),total, session.userId, cardtype)
    }

    //updatePaymentType api call
    private fun callUpdatePaymentTypeWs() {
//        showLoader()
        var apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = PlaceOrderDialogFragment.STATIC_ORDER_ID

        when (cardtype) {
            AppCommon.PaymentMethod.VISA -> {
                //do not translate this string
                apiRequestParams.payment_method = "Card"
            }
            AppCommon.PaymentMethod.MADA -> {
                //do not translate this string
                apiRequestParams.payment_method = "Card"
            }
//            AppCommon.PaymentMethod.STC_PAY -> {
//                //do not translate this string
//                apiRequestParams.payment_method = "STC pay"
//            }
            AppCommon.PaymentMethod.CASH -> {
                //do not translate this string
                apiRequestParams.payment_method = "Cash"
            }
        }

        authViewModel.updatePaymentType(apiRequestParams)
//        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme),total, session.userId, cardtype)
    }

    //checkPaymentStatus api call
    fun checkPaymentStatus() {
        Log.e("checkoutStatusLiveData", "checkoutid:" + checkoutId)
        runOnUiThread {
            showLoader()
            authViewModel.checkPaymentStatus(checkoutId, cardtype, PlaceOrderDialogFragment.STATIC_ORDER_ID!!)
        }
    }

}