package com.coming.customer.ui.home.activity

import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.LocationManager
import com.coming.customer.core.Session
import com.coming.customer.data.URLFactory
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.auth.fragment.UpdateFragment
import com.coming.customer.ui.auth.fragment.WebViewFragment
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.fragment.*
import com.coming.customer.ui.home.HomeUpdateCounterInf
import com.coming.customer.ui.home.NavDrawerAdapter
import com.coming.customer.ui.home.OnDrawerOptionSelectedListener
import com.coming.customer.ui.home.fragment.*
import com.coming.customer.ui.inf.UpdateDetailsAfterPlaceOrder
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.coming.customer.ui.notificatioservice.NotificationModel
import com.coming.customer.ui.payment.CheckoutBroadcastReceiver
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.NetworkConnection
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huawei.agconnect.crash.AGConnectCrash
import com.google.gson.Gson
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.home_dialog_cart.*
import kotlinx.android.synthetic.main.store_details_activity.*
import java.net.ConnectException
import java.net.ProtocolException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : HomeBasePaymentActivity(), OnDrawerOptionSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    //payment getaway
    //do not translate this string
    private var cardtype: String = "visa"
    private var totalAmount: String = "111"
    private var checkoutId = ""
    var deliveryAddress: String = ""
    var promoCode: PromoCode? = null
    var deliveryInstructions: String = ""
    var orderNote = ""
    var subTotal = ""
    var updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder? = null
    var placeOrder: PlaceOrder? = null
    var store = Store()
    var itemDetail = java.util.ArrayList<ItemDetailItem>()
    var notificationModel: NotificationModel? = null
    var useWallet: Boolean = false
    var orderId: String? = null
    var backendVersionCode: Int = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val delay = 2000L

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var userRepository: UserRepository

    private val paymentViewModel by viewModels<AuthViewModel>()

    private lateinit var logoutDialog: AlertDialog
    var customerServiceDetails: CustomerServiceDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callGetVersionCode()
        setLiveData()
        getCustomerServiceDetails()
        setupFragment()
        setupNavDrawer()
        setupListeners()
        prepareLogoutDialog()

        notificationModel = intent.let {
            it.getParcelableExtra<NotificationModel>("notification")
        }
        if (notificationModel != null) {
//            if (notificationModel!!.tag == AppCommon.NotificationTag.PLACE_ORDER_QTY_REMINDER) {
//                var bundle: Bundle = Bundle()
//                bundle.putBoolean(getString(R.string.showCurrentOrder), true)
//                loadActivity(MyOrdersActivity::class.java).addBundle(bundle).start()
//
//                val item = OrderRequest("", "", "" + notificationModel!!.order_summary_id, "", "", "", "", "" + notificationModel!!.order_id, null, null, "", "")
//                loadActivity(IsolatedActivity::class.java, OrderRequestDetailFragment::class.java).addBundle(bundleOf(Pair(Session.PRODUCT_DETAILS_FROM_ORDER_REQUIST, item))).start()
//            }

            notificationModel?.tag?.let {
                if (true
//                    it == AppCommon.NotificationTag.PLACE_ORDER_ACCEPTED ||
//                    it == AppCommon.NotificationTag.PLACE_ORDER_PACKED ||
//                    it == AppCommon.NotificationTag.PLACE_ORDER_ON_THE_WAY ||
//                    it == AppCommon.NotificationTag.PLACE_ORDER_CONFIRMED
                ) {
//                    val bundle = Bundle()
//                    bundle.putString(AppCommon.OPEN_FROM_NOTIFICATION, notificationModel!!.order_summary_id)
//                    loadActivity(IsolatedActivity::class.java, CurruntOrderDetailsFragment::class.java).addBundle(bundle).start()
                }
            }
        }
    }

    private fun prepareLogoutDialog() {
        logoutDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.menu_logout))
            .setMessage(getString(R.string.text_info_logout))
            .setPositiveButton(getString(R.string.label_yes)) { _, _ -> logout() }
            .setNegativeButton(getString(R.string.label_no)) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun showLogoutDialog() {
        drawerLayout.closeDrawer(GravityCompat.START)
        logoutDialog.show()
    }

    private fun setupFragment() {
        load(HomeFragment::class.java).replace(false)
    }

    fun updateDetails() {
        if (session.userId != null && session.userId.length != 0) {
            textViewCurrentBal.visibility = View.VISIBLE
//            when  {
//                appPreferences.getString(AppCommon.LANGUAGE)== AppCommon.EN -> {
            textViewCurrentBal.text = resources.getString(R.string.label_current_balance).plus(" " + session.user?.wallet + " ").plus(resources.getString(R.string.label_currency))
//                }
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR-> {
//                    textViewCurrentBal.text = resources.getString(R.string.label_current_balance).plus(" " + getEnglishNumberToArabicNumber(session.user?.wallet.toString()) + " ").plus(resources.getString(R.string.label_currency))
//                }
//            }
            textViewCustomerName.text = resources.getString(R.string.text_greeting).plus(" " + session.user?.username)
        } else {
            textViewCurrentBal.visibility = View.INVISIBLE

        }
    }

    private fun setupNavDrawer() {
        var drawerOptions = ArrayList<DrawerOption>()
        if (session.userId != null && session.userId.length != 0) {
            textViewCurrentBal.visibility = View.VISIBLE

            drawerOptions = ArrayList<DrawerOption>().apply {
                //  add(DrawerOption(resources.getString(R.string.menu_referral_code), DrawerOptionType.ReferralCode, R.drawable.ic_voucher, 0))
                add(DrawerOption(resources.getString(R.string.menu_account), DrawerOptionType.Account, R.drawable.ic_account, 0))
                //remove payment options
                //add(DrawerOption(resources.getString(R.string.menu_payment_options), DrawerOptionType.PaymentOption, R.drawable.ic_payment, 0))
                add(DrawerOption(resources.getString(R.string.menu_invoices), DrawerOptionType.Invoices, R.drawable.ic_invoices, 0))
                add(DrawerOption(resources.getString(R.string.menu_customer_service), DrawerOptionType.CustomerService, R.drawable.ic_support, 0))
                add(DrawerOption(resources.getString(R.string.menu_settings), DrawerOptionType.Settings, R.drawable.ic_settings, 0))
                add(DrawerOption(resources.getString(R.string.menu_privacy_policy), DrawerOptionType.Policy, R.drawable.ic_policy, 0))
                add(DrawerOption(resources.getString(R.string.menu_logout), DrawerOptionType.Logout, R.drawable.ic_logout, 0))
            }
        } else {
            textViewCurrentBal.visibility = View.INVISIBLE
            textViewCustomerName.setText(resources.getString(R.string.text_greeting_guest))
            drawerOptions = ArrayList<DrawerOption>().apply {
                add(DrawerOption(resources.getString(R.string.menu_customer_service), DrawerOptionType.CustomerService, R.drawable.ic_support, 0))
                add(DrawerOption(resources.getString(R.string.menu_settings), DrawerOptionType.Settings, R.drawable.ic_settings, 0))
                add(DrawerOption(resources.getString(R.string.menu_privacy_policy), DrawerOptionType.Policy, R.drawable.ic_policy, 0))
                add(DrawerOption(resources.getString(R.string.menu_login), DrawerOptionType.Login, R.drawable.ic_logout, 0))
            }
        }


        val adapter = NavDrawerAdapter(drawerOptions, this)

        recyclerViewDrawer.adapter = adapter
        recyclerViewDrawer.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun setupListeners() {
        bottomNav.setOnNavigationItemSelectedListener(this)
        bottomNav.menu.getItem(3).isChecked = true
        imageViewLocation.setOnClickListener(this)
        imageViewSearch.setOnClickListener(this)
        homeImageViewCart.setOnClickListener(this)
    }

    private fun logout() {
//        preferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
//        loadActivity(AuthActivity::class.java).byFinishingCurrent().start()

        if (session.userId != null && session.userId.length != 0) {
            callLogoutApi()
        } else {
            session.userId = ""
            session.user = null
            appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
            appPreferences.clearAll()
            loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
        }
    }


    override fun onDrawerOptionSelected(type: DrawerOptionType) {
        drawerLayout.closeDrawer(GravityCompat.START)
        when (type) {
            //commented for current time
//            DrawerOptionType.ReferralCode -> {
//                startIsolatedActivityWith(ReferralCodeFragment::class.java)
//            }
            DrawerOptionType.Account -> {
                startIsolatedActivityWith(AccountFragment::class.java)
            }
            //remove payment options
//           DrawerOptionType.PaymentOption -> {
//                startIsolatedActivityWith(PaymentOptionsFragment::class.java)
//            }
            DrawerOptionType.Invoices -> {
                startIsolatedActivityWith(InvoicesFragment::class.java)
            }
            DrawerOptionType.CustomerService -> {
                if (NetworkConnection.isOnline(this)) {
                    startChatActivity()
                } else {
                    showToast(resources.getString(R.string.check_internet_connection))
                }
                // startIsolatedActivityWith(SupportFragment::class.java)
            }
            DrawerOptionType.Settings -> {
                startIsolatedActivityWith(SettingsFragment::class.java)
            }
            DrawerOptionType.Policy -> {
                if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.EN)) {
                    val data = Bundle()
                    data.putString(Session.WEB_URL_KEY, Session.WEB_URL_KEY)
                    data.putString(Session.TOOL_BAR_TITLE, getString(R.string.menu_privacy_policy))
                    data.putString(Session.WEB_URL, URLFactory.privacyEPolicy())
                    loadActivity(IsolatedActivity::class.java, WebViewFragment::class.java).addBundle(data).start()
                } else if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.AR)) {
                    val data = Bundle()
                    data.putString(Session.WEB_URL_KEY, Session.WEB_URL_KEY)
                    data.putString(Session.TOOL_BAR_TITLE, getString(R.string.menu_privacy_policy))
                    data.putString(Session.WEB_URL, URLFactory.privacyEPolicy())
                    loadActivity(IsolatedActivity::class.java, WebViewFragment::class.java).addBundle(data).start()
                }
            }
            DrawerOptionType.Logout -> {
//                logout()
                showLogoutDialog()
            }
            DrawerOptionType.Login -> {
                session.userId = ""
                session.user = null
                appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                appPreferences.clearAll()
                loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
            }
        }
    }

    private fun <T : BaseFragment> startIsolatedActivityWith(jClass: Class<T>) {
        loadActivity(IsolatedActivity::class.java)
            .addBundle(Bundle().apply { putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, jClass) })
            .start()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showSettingsDialog() {
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

    fun updateLocalization(locale: String) {
        updateLocale(Locale(locale))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionOrders -> {
                if (bottomNav.menu.getItem(0).isChecked) {
                    toolBar.visibility = View.GONE
                } else {
                    toolBar.visibility = View.GONE
                }
                if (session.userId != null && session.userId.length != 0) {
                    load(OrdersFragment::class.java).replace(false)
                    return true
                } else {
//                    showToast(resources.getString(R.string.authentication_required_please_login))
                    showSettingsDialog()
                    return false
                }

            }
            R.id.actionHome -> {
                if (bottomNav.menu.getItem(0).isChecked) {
                    toolBar.visibility = View.VISIBLE
                } else {
                    toolBar.visibility = View.VISIBLE
                }
                load(HomeFragment::class.java).replace(false)
                return true
            }
            R.id.actionOffers -> {
                if (bottomNav.menu.getItem(0).isChecked) {
                    toolBar.visibility = View.GONE
                } else {
                    toolBar.visibility = View.GONE
                }
                load(OffersFragment::class.java).replace(false)
                return true
            }
            R.id.actionMenu -> {
                when {
                    bottomNav.menu.getItem(0).isChecked && !bottomNav.menu.getItem(1).isVisible -> toolBar.visibility = View.GONE
                    bottomNav.menu.getItem(0).isChecked && !bottomNav.menu.getItem(2).isVisible -> toolBar.visibility = View.GONE
                    bottomNav.menu.getItem(0).isChecked && !bottomNav.menu.getItem(3).isVisible -> toolBar.visibility = View.VISIBLE
                }
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }


    override fun findFragmentPlaceHolder(): Int = R.id.placeHolder
    override fun findContentView(): Int = R.layout.home_activity

    private val authViewModel by viewModels<AuthViewModel>()

    private fun setLiveData() {

        authViewModel.getProfileLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        val gson = Gson()
                        val json = gson.toJson(it)
                        appPreferences.putString("user", json)
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()

                        session.user?.isVerified?.let {
                            if (it == "0") {
                                appPreferences.putBoolean(AppConstants.USER_LOGIN_FIRST_TIME, true)
                            } else {
                                appPreferences.putBoolean(AppConstants.USER_LOGIN_FIRST_TIME, false)
                            }
                        }
                        session.user?.cartCount?.let {
                            Log.e("carttext", "setLiveData: " + it)
                            updateCartCounter(it)
                        }

                        updateDetails()

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
                    when (throwable.code) {
                        0, 2 -> {
                            throwable.message?.let { showToast(it) }
                        }
                        3, 11 -> {
                            Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                            appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                            loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
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
        authViewModel.logoutLiveData.observe(this,
            { responseBody ->
                hideLoader()
                //showMessage(responseBody.message)
                if (responseBody.responseCode == 1) {
                    responseBody?.let {

//                        auth?.let {
//                            it.signOut()
//                        }
//                        getCurrentFragment<BaseFragment>()?.setDBUserOffline()


                        session.userId = ""
                        session.user = null
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        appPreferences.clearAll()
                        loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    if (throwable.code == 0) {
                        throwable.message?.let { showToast(it) }
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
            }

        )
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

                        if (this.cardtype == AppCommon.PaymentMethod.CASH) {
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
        paymentViewModel.createPaymentTokenLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 7 && responseBody.data != null) {
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
        //checkoutStatus live data source
        paymentViewModel.checkoutStatusLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {
                    //callPlaceOrderApi()
                    // callUpdatePaidAmount(orderId!!, responseBody.data?.amount!!)
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
        paymentViewModel.updatePaymentTypeLiveData.observe(this,
            { responseBody ->
                // hideLoader()
                if (responseBody.responseCode == 1) {
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

        authViewModel.getVersionCodeLiveData.observe(this,
            { responseBody ->
                hideLoader()
                if (responseBody.responseCode == 1) {
                    responseBody.data?.let {
                        if (it.androidCustomerVersion != null) {
                            backendVersionCode = it.androidCustomerVersion!!.toInt()
                        }
                    }
                }

            },
            { throwable: Throwable ->

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

                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(this, resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
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
                }
                false
            })
    }

    //logout api
    private fun callLogoutApi() {
        /**
         *
         * params : logout(user_id)
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        authViewModel.logout(apiRequestParams)
    }

    override fun onResume() {
        super.onResume()
        runnable = Runnable {
            try {

                val pInfo: PackageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
                val version = pInfo.versionCode
                if (version < backendVersionCode) {


                    loadActivity(EmptyActivity::class.java)
                        .addBundle(Bundle().apply {
                            putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, UpdateFragment::class.java)

                        }).byFinishingAll()
                        .start()

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }



        handler = Handler()
        handler.postDelayed(runnable, delay)

        if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.EN)) {
            nav_view.setBackgroundResource(R.drawable.bg_rounded_rect_right_16dp)
        } else if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.AR)) {
            nav_view.setBackgroundResource(R.drawable.bg_rounded_rect_left_16dp)
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

    val homeUpdateCounterInf: HomeUpdateCounterInf = object : HomeUpdateCounterInf {
        override fun onUpdateCounter(updateCounterValue: String) {
            updateCartCounter(updateCounterValue)
        }

        override fun onAuthenticated() {
            showSettingsDialog()
        }

        override fun onPayNow(orderId: String, amount: String, deliveryAddress: String, promoCode: PromoCode?, deliveryInstructions: String, orderNote: String, updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder?, subTotal: String, cardType: String, useWallet: Boolean) {
            totalAmount = amount
            this@HomeActivity.deliveryAddress = deliveryAddress
            this@HomeActivity.promoCode = promoCode
            this@HomeActivity.deliveryInstructions = deliveryInstructions
            this@HomeActivity.orderNote = orderNote
            this@HomeActivity.subTotal = subTotal
            this@HomeActivity.updateDetailsAfterPlaceOrder = updateDetailsAfterPlaceOrder
            this@HomeActivity.useWallet = useWallet
            // openAlertDialog() //change ui on dialog to call directky
            this@HomeActivity.cardtype = cardType
            this@HomeActivity.orderId = orderId
            //callPlaceOrderApi()
            //callUpdatePaymentTypeWs()
            if (cardType == AppCommon.PaymentMethod.CASH) {
                callUpdatePaymentTypeWs()
            } else {
                // openAlertDialog()
                callCreatePaymentTokenWs()
            }


        }
    }
//    private fun callUpdatePaidAmount(orderId: String, amount: String){
//        val apiRequestParams = ApiRequestParams()
//        apiRequestParams.order_id = orderId
//        apiRequestParams.amount = amount
//        authViewModel.updatePaidAmount(apiRequestParams)
//    }

    //createPaymentToken api call
    private fun callCreatePaymentTokenWs() {
        Log.e("Tag", "totalAmount$totalAmount")
        //  showLoader()
        paymentViewModel.createCheckoutToken(totalAmount, cardtype, useWallet, orderId!!)
//        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme),total, session.userId, cardtype)
    }

    //updatePaymentType api call
    private fun callUpdatePaymentTypeWs() {
//        showLoader()
        var apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = orderId

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

        paymentViewModel.updatePaymentType(apiRequestParams)
//        requestCheckoutId(getString(R.string.checkout_ui_callback_scheme),total, session.userId, cardtype)
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
        val checkoutSettings = createCheckoutSettings(checkoutIdd, getString(R.string.home_checkout_ui_callback_scheme), PAYMENT_BRANDS)

        /* Set componentName if you want to receive callbacks from the checkout */
        val componentName = ComponentName(
            packageName, CheckoutBroadcastReceiver::class.java.name
        )

        /* Set up the Intent and start the checkout activity. */
        val intent = checkoutSettings.createCheckoutActivityIntent(this, componentName)

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT)
    }

    companion object {
        lateinit var homeUpdateCounterInfPass: HomeUpdateCounterInf
    }

    init {
        homeUpdateCounterInfPass = homeUpdateCounterInf
        Log.e("TAG", "storeInit : " + homeUpdateCounterInf)
    }

    /*screen ui*/
    fun checkPaymentStatus() {
        Log.e("checkoutStatusLiveData", "checkoutid:" + checkoutId)
        runOnUiThread {
            showLoader()
            paymentViewModel.checkPaymentStatus(checkoutId, cardtype, orderId!!)
        }
    }

    private fun getChatWindowConfiguration(): ChatWindowConfiguration? {
        AGConnectCrash.getInstance().log("customerServiceLicense ${customerServiceDetails?.customerServiceLicense}")
        AGConnectCrash.getInstance().log("customerGroupID ${customerServiceDetails?.customerGroupID}")
        return if (session.user?.email.isNullOrEmpty() || session.user?.username.isNullOrEmpty()) {
            AGConnectCrash.getInstance().log("phone ${session.user?.phone}")
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, session.user?.phone, session.user?.phone + "@gmail.com", null)
        } else {
            AGConnectCrash.getInstance().log("username ${session.user?.username}")
            AGConnectCrash.getInstance().log("email ${session.user?.email}")
            ChatWindowConfiguration(customerServiceDetails?.customerServiceLicense!!, customerServiceDetails?.customerGroupID, session.user?.username, session.user?.email, null)
        }
    }

    private fun startChatActivity() {
        val intent = Intent(this, ChatWindowActivity::class.java)
        try {
            val config = getChatWindowConfiguration()
            config?.asBundle()?.let { intent.putExtras(it) }
            startActivity(intent)
        } catch (e: Exception) {
            AGConnectCrash.getInstance().recordException(RuntimeException(e))
        }
    }


    private var disposable: Disposable? = null
    private fun getCustomerServiceDetails() {
        userRepository.getCustomerServiceDetails()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<CustomerServiceDetails>> {
                override fun onSuccess(data: DataWrapper<CustomerServiceDetails>) {
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(this@HomeActivity, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    appPreferences.clearAll()
                                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                }
                                "Failed to connect to /${BuildConfig.BASE_URL}:8507" -> {
                                    Toast.makeText(this@HomeActivity, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@HomeActivity, data.throwable?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        1 -> {
                            val gson = Gson()
                            val json = gson.toJson(data.responseBody.data!!)
                            appPreferences.putString("customerServiceDetails", json)
                            customerServiceDetails = data.responseBody.data!!
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        e.message?.let {
                            showToast(it)
                        }
                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(this@HomeActivity, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        appPreferences.clearAll()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(this@HomeActivity, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewLocation -> {
                loadActivity(IsolatedActivity::class.java)
                    .addBundle(Bundle().apply {
                        putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, PickLocationFragment::class.java)
                    }).forResult(AppCommon.RequestCode.ADD_SERVICE)
                    .start()
            }

            R.id.imageViewSearch -> {
                loadActivity(IsolatedActivity::class.java)
                    .addBundle(Bundle().apply {
                        putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, SearchFragment::class.java)
                    })
                    .start()
//                navigator.load(SearchFragment::class.java).replace(true)
            }
            R.id.homeImageViewCart -> {
                if (session.userId.isNotEmpty()) {
                    if (homeTextViewCartCounter.text != "0") {
                        val cartDialog = CartDialogFragment.newInstance(null, homeUpdateCounterInfPass)
                        cartDialog.userId = session.userId
                        cartDialog.cartDialog = cartDialog
                        cartDialog.homeActivity = this
                        cartDialog.storeDetailActivity = null
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
        }
    }

    fun updateCartCounter(cartCount: String) {
        if (cartCount == "0") {
            homeTextViewCartCounter.visibility = View.INVISIBLE
//            when {
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
            homeTextViewCartCounter.text = cartCount
//                }
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                    textViewCartCounter.text = getEnglishNumberToArabicNumber(cartCount)
//                }
            //     }
        } else {
            homeTextViewCartCounter.visibility = View.VISIBLE
//            when {
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
            homeTextViewCartCounter.text = cartCount
//                }
//                appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
//                    textViewCartCounter.text = getEnglishNumberToArabicNumber(cartCount)
//                }
            //   }

        }
    }


    //call getVersionCode api
    private fun callGetVersionCode() {

        //   showLoader()
        authViewModel.getVersionCode()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}