package com.coming.customer.ui.home.fragment

import android.animation.Animator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.LocationManager
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.MerchantDetailsCategoryAdapter
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.DishesPagerAdapter
import com.coming.customer.ui.home.OnDishSelectedListener
import com.coming.customer.ui.isolated.IsolatedPaymentGetawayActivity
import com.coming.customer.ui.payment.BasePaymentActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.loadUrl
import com.coming.customer.util.loadUrlRoundedCorner
import com.coming.customer.util.twoDecimal
import com.coming.merchant.util.extentions.getEnglishNumberToArabicNumber
import com.huawei.hms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_restaunrant_details.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StoreDetailFragment : BaseFragment(), View.OnClickListener, OnDishSelectedListener, MerchantDetailsCategoryAdapter.OnItemClick {

    //payment getaway
    //do not translate this string
    private var cardtype: String = "visa"
    private var totalAmount: String = "0.0"

    private var checkoutId = ""

    private val paymentViewModel by viewModels<AuthViewModel>()
    //end payment getaway

    var categoryAdapter: MerchantDetailsCategoryAdapter? = null

    var store = Store()
    var homeMerchantSlider: HomeMerchantSlider? = null
    var storeId = ""

    var categoryData = ArrayList<CategoryDataItem>()

    val fragmentList = arrayListOf<DishesFragment>()

    var itemDetail = ArrayList<ItemDetailItem>()

    var merchantDetail = MerchantDetail()

    private val parent: IsolatedPaymentGetawayActivity get() = requireActivity() as IsolatedPaymentGetawayActivity

    private var chipsMap: HashMap<Int, Chip> = HashMap()

    override fun bindData() {
        setupListeners()
//        prepareChipsMap()
//        setupChipGroupCategory()
//        setupViewPagerDishes()

        setupRecyclearview()

        if (arguments?.containsKey(Session.DATA) == true) {
            store = arguments?.getSerializable(Session.DATA) as Store
        }
//        else if (arguments?.containsKey(Session.MERCHANT_ID) == true) {
//            homeMerchantSlider = arguments?.getSerializable(Session.MERCHANT_ID) as HomeMerchantSlider
//            storeId = homeMerchantSlider?.id.toString()
//        }
        else {
            Log.e("Else", "Else not getting build")
        }

        callGetMerchantDetailApi()

        setupSearchEditText()
    }

    private fun setupRecyclearview() {
//        private val confirmationCartAdapter by lazy { BookingRequiestDetailsAdapter(myBookingArrayList, this,session) }
        categoryAdapter = MerchantDetailsCategoryAdapter(categoryData, this)
        recyclerViewCategory.adapter = categoryAdapter
        recyclerViewCategory.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun onItemClicked(position: Int) {
        viewPagerDishes.currentItem = position
    }

//    private val storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf = object : StoreDetailsUpdateCounterInf {
//        override fun onUpdateCounter(updateCounterValue: String) {
//            updateCartCounter(updateCounterValue)
//        }
//
//        override fun onAuthenticated() {
//            showSettingsDialog()
//        }
//
//        override fun onPayNow(amount: String, cartDetails: CartDetails?, deliveryAddress: String, promoCode: PromoCode?, deliveryInstructions: String, orderNote: String, updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder, subTotal: String, cardType: String, useWallet: Boolean) {
//            totalAmount = amount
//            payNow()
//        }
//    }

    /*payment getaway*/
    fun payNow() {
        // openAlertDialog()
    }

    fun openAlertDialog() {

        val builder = context?.let { AlertDialog.Builder(it) }
//       var optionDialog = builder.create()
        val inflater = layoutInflater

        val dialogLayout: View = inflater.inflate(R.layout.dialog_select_cardtype, null)
        val textViewPayusingMada = dialogLayout.findViewById<TextView>(R.id.textViewPayusingMada)
        val textViewPayusingVisa = dialogLayout.findViewById<TextView>(R.id.textViewPayusingVisa)
        val textViewPayusingMaster = dialogLayout.findViewById<TextView>(R.id.textViewPayusingMaster)
        val textViewPayusingSTC = dialogLayout.findViewById<TextView>(R.id.textViewPayusingSTC)
        builder?.setView(dialogLayout)
//        builder.show()
        val dialog = builder?.show() // this line used to be "dialog.show();"

        textViewPayusingVisa.setOnClickListener {
            //do not translate this string
            cardtype = "visa"
            builder?.let { it1 -> dialog?.let { it2 -> callPayment(it2, it1) } }
        }
        textViewPayusingMada.setOnClickListener {
            //do not translate this string
            cardtype = "mada"
            builder?.let { it1 -> dialog?.let { it2 -> callPayment(it2, it1) } }
        }
        textViewPayusingMaster.setOnClickListener {
            //do not translate this string
            cardtype = "master"
            builder?.let { it1 -> dialog?.let { it2 -> callPayment(it2, it1) } }
        }

        textViewPayusingSTC.setOnClickListener {
            //do not translate this string
            cardtype = "stcpay"
            builder?.let { it1 -> dialog?.let { it2 -> callPayment(it2, it1) } }
        }


    }

    fun callPayment(
        optionDialog: AlertDialog,
        builder: AlertDialog.Builder
    ) {
        builder.setOnDismissListener {
            it.dismiss()
        }
        optionDialog.dismiss()
        optionDialog.cancel()
        optionDialog.hide()
//        callBookedServiceApi()
        callCreatePaymentTokenWs()
    }

    private fun callCreatePaymentTokenWs() {
        val total = totalAmount/*.formatCurrency()*/
        Log.e("Tag", "totalAmount" + total)

        showLoader()
        paymentViewModel.createCheckoutToken(total, cardtype, false, "")
//        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme),total, session.userId, cardtype)
    }


    private fun setLiveDataForPaymentGetaway() {
        //createPaymentToken live data source
        authViewModel.createPaymentTokenLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.id?.let {
                        checkoutId = it
                        appPreferences.putString(AppCommon.CHECKOUT_ID, checkoutId)

                        val frag: BasePaymentActivity? = this.parentFragment as BasePaymentActivity?
//                        frag?.let { it.requestCheckoutId(getString(R.string.checkout_ui_callback_scheme), totalAmount, session.userId, cardtype) }
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
                }
                false
            })

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

    private fun updateCartCounter(cartCount: String = "0") {
        if (cartCount.equals("0")) {
            textViewCartCounter.visibility = View.INVISIBLE
        } else {
            textViewCartCounter.visibility = View.VISIBLE
            textViewCartCounter.text = cartCount
        }

    }

    private fun setupListeners() {
        imageViewSearch.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
        imageViewCart.setOnClickListener(this)
    }

    private fun setupViewPagerDishes() {
        val pagerAdapter = DishesPagerAdapter(childFragmentManager, fragmentList)
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

    private fun setupChipGroupCategory() {
        chipGroupCategory.removeView(chipBurger)
        for ((key, chip) in chipsMap) {
            chipGroupCategory.addView(chip)
        }
        if (chipsMap.size > 0) {
            chipGroupCategory.check(chipsMap[viewPagerDishes.currentItem + 1]?.id!!)
        }


        chipGroupCategory.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i ->
            /*try {
                val chip: Chip = chipGroup.findViewById(i)
                if (chip != null) {
                    for (i in 0 until categoryData.size) {
                        if (categoryData[i].categoryName?.equals(chip.chipText)!!) {
                            viewPagerDishes.currentItem = i
                        }
                    }
                }
            } catch (e: Exception) {
            }*/
        })
    }

    private fun prepareChipsMap() {
        chipGroupCategory.removeView(chipBurger)
        chipsMap.apply {
            put(1, chipBurger)
            put(2, chipBuilder("Coke"))
            put(3, chipBuilder("Cheese"))
            put(4, chipBuilder("Hash"))
            put(5, chipBuilder("French Fries"))
        }
    }

    private fun chipBuilder(chipTitle: String): Chip {
        val chip = Chip(requireContext())
        chip.setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.BaseChip_NoIcon))
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.color_chip_text))
        chip.text = chipTitle
        return chip
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

    private fun updateDetails() {
        var itemDetail = ArrayList<ItemDetailItem>()
        merchantDetail?.let {
            it.backgroundImage?.let { it1 -> thumbnail.loadUrl(it1, 0) }
            it.profileImage?.let { it1 -> imageViewProviderLogo.loadUrlRoundedCorner(it1, 0, 10) }
            it.username?.let { it1 -> textViewProviderName.text = it1.trim() }
            it.distance?.let { it1 ->
                textViewDistance.text = it1.toString().twoDecimal() + " " + resources.getString(R.string.label_km)
            }

            // it.status?.let { it1 -> textViewStatus.text = it1 }
            when (it.status) {
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

            it.rating?.let { it1 -> textViewRating.text = it1 }
            it.rating?.let { it1 -> ratingBarRestaurant.rating = it1.toFloat() }
            it.categoryName?.let { it1 -> textViewProviderVariety.text = it1 }


            it.rating?.let { it1 -> textViewRating.text = getEnglishNumberToArabicNumber(it1, false) }
            it.rating?.let { it1 -> ratingBarRestaurant.rating = it1.toFloat() }
            it.categoryName?.let { it1 -> textViewProviderVariety.text = it1 }

        }


        categoryData?.let {
            chipGroupCategory.removeView(chipBurger)
            for (i in 0 until it.size) {
                // fragmentList.add(DishesFragment(it[i], storeId, storeDetailsUpdateCounterInf))
                chipsMap.put(i + 1, chipBuilder(it[i].categoryName.toString()))
            }

            setupViewPagerDishes()
            setupChipGroupCategory()
        }

    }

    private fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    override fun onDishSelected(itemDetailItem: ItemDetailItem) {
        //val addDishDialogFragment = AddDishDialogFragment().newInstance(storeDetailsUpdateCounterInf, locationManager)
        //addDishDialogFragment.show(childFragmentManager, AddDishDialogFragment::class.simpleName)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewCart -> {
                if (session.userId != null && session.userId.length != 0) {
                    if (textViewCartCounter.text.toString() != "0") {
//                        val cartDialog = CartDialogFragment(storeDetailsUpdateCounterInf, locationManager)
//                        cartDialog.show(childFragmentManager, CartDialogFragment::class.simpleName)
//                        cartDialog.repository = userRepository
//                        cartDialog.userId = session.userId
//                        cartDialog.cartDialog = cartDialog
                    } else {
                        //showToastLong(resources.getString(R.string.label_cart_is_empty))
                    }

                } else {
//                    showToastLong(resources.getString(R.string.authentication_required_please_login))
                    showSettingsDialog()
                }
            }

            R.id.imageViewBack -> {
                navigator.goBack()
            }

            R.id.imageViewSearch -> {
                showOrHideSearch()
            }
        }
    }

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

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (fragmentList.size != 0) {
                    fragmentList[viewPagerDishes.currentItem].onSearch(editTextSearch.text.toString().trim())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }
        })

        layout_search.setEndIconOnClickListener {
            editTextSearch.setText("")
            if (editTextSearch.text.toString().trim().length == 0) {
                if (fragmentList.size != 0) {
                    fragmentList[viewPagerDishes.currentItem].onSearch("")
                }
            }
        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_restaunrant_details


    @Inject
    lateinit var userRepository: UserRepository

//    @Inject
//    lateinit var locationManager: LocationManager

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        setLiveDataForPaymentGetaway()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //getMerchantDetail live data source
        authViewModel.getMerchantDetailLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.merchantDetail?.let {
                        merchantDetail = it
                    }
                    responseBody.data?.categoryData?.let {
                        categoryData.addAll(it)
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
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })


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
                            updateCartCounter(it)
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
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })
    }

    //getProfile api
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

    //getMerchantDetail api
    private fun callGetMerchantDetailApi() {
        /**
         *
         * params : getMerchantDetail(merchant_id ,lat,lng)
         *
         * optional : category_id,user_id,page
         *
         * */

        locationManager.triggerLocation(object : LocationManager.LocationListener {
            override fun onLocationAvailable(latLng: LatLng) {
                if (latLng != null) {
                    locationManager.stop()
                    showLoader()
                    val apiRequestParams = ApiRequestParams()
                    apiRequestParams.lng = latLng.longitude.toString()
                    apiRequestParams.lat = latLng.latitude.toString()
                    apiRequestParams.merchant_id = storeId
                    authViewModel.getMerchantDetail(apiRequestParams)
                }
            }

            override fun onFail(status: LocationManager.LocationListener.Status) {
                showAlertDialog(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store), object : BaseActivity.DialogOkListener {
                    override fun onClick() {
                        callGetMerchantDetailApi()
                    }
                }, getString(R.string.label_ok))
            }
        })
    }
}