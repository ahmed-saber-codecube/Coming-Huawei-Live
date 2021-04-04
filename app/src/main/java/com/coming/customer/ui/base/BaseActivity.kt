package com.coming.customer.ui.base

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.coming.customer.R
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.LocationManager
import com.coming.customer.core.localization.LocaleAwareCompatActivity
import com.coming.customer.ui.manager.*
import com.coming.customer.util.ConnectivityReceiver
import com.coming.customer.util.extentions.getColorFromResource
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.huawei.agconnect.auth.AGConnectAuth
import java.util.*
import javax.inject.Inject


abstract class BaseActivity : LocaleAwareCompatActivity(), HasToolbar, Navigator, ConnectivityReceiver.ConnectivityReceiverListener {
    /*Firebase chat*/
    companion object {
        var auth: AGConnectAuth? = null
    }

    @Inject
    lateinit var navigationFactory: FragmentNavigationFactory

    @Inject
    lateinit var activityStarter: ActivityStarter

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var appPreferences: AppPreferences


    internal var progressDialog: AlertDialog? = null

    internal var alertDialog: AlertDialog? = null

    private var snackBar: Snackbar? = null
    lateinit var sbView: View
    var connectivityReceiver: ConnectivityReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MAHER", BaseActivity::class.java.name)
        // Initialize Firebase Auth
        if (auth == null)
            auth = AGConnectAuth.getInstance()

        setContentView(findContentView())
        locationManager.setActivity(this)
        setUpAlertDialog()
        connectivityReceiver = ConnectivityReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    fun updateLocalization(locale: Locale) {
        updateLocale(locale)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(findViewById(android.R.id.content), resources.getString(R.string.connection_exception), Snackbar.LENGTH_LONG) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            sbView = snackBar!!.view
            sbView.setBackgroundResource(R.drawable.rounded_corner)
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    private fun setUpDialog() {

        val dialogBuilder = AlertDialog.Builder(this, R.style.alertDialog)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.progress_bar_layout, null, false)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        progressDialog = dialogBuilder.create()

    }

    override fun showAlertDialog(message: String, neutralText: String, dialogOkListener: DialogOkListener?) {

        val dialog = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(neutralText) { dialog, which ->
                dialogOkListener?.onClick()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(baseContext.getColorFromResource(R.color.colorAccent))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(baseContext.getColorFromResource(R.color.colorAccent))
        }

        dialog.show()

    }

    interface DialogOkListener {
        fun onClick()
    }

    private fun setUpAlertDialog() {
        alertDialog = AlertDialog.Builder(this)
            .setPositiveButton("ok", null)
            .setTitle(R.string.app_name)
            .create()
    }

    fun <F : BaseFragment> getCurrentFragment(): F? {
        return if (findFragmentPlaceHolder() == 0) null else supportFragmentManager.findFragmentById(findFragmentPlaceHolder()) as? F
    }

    abstract fun findFragmentPlaceHolder(): Int

    @LayoutRes
    abstract fun findContentView(): Int


    fun showErrorMessage(message: String?) {
        /*val f = getCurrentFragment<BaseFragment<*, *>>()
        if (f != null)
            Snackbar.make(f.getView()!!, message!!, BaseTransientBottomBar.LENGTH_SHORT).show()*/

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun toggleLoader(show: Boolean) {

        if (show) {
            if (!progressDialog!!.isShowing)
                progressDialog!!.show()
        } else {
            if (progressDialog!!.isShowing)
                progressDialog!!.dismiss()
        }
    }

    fun showLoader() {
        if (progressDialog != null) {
            progressDialog!!.show()
        } else {
            setUpDialog()
            progressDialog!!.show()
        }
    }

    fun hideLoader() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    protected fun shouldGoBack(): Boolean {
        return true
    }

    override fun onBackPressed() {
        hideKeyboard()


        val currentFragment = getCurrentFragment<BaseFragment>()
        if (currentFragment == null)
            super.onBackPressed()
        else if (currentFragment.onBackActionPerform() && shouldGoBack())
            super.onBackPressed()

        // pending animation
        // overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);

    }

    fun hideKeyboard() {
        // Check if no view has focus:

        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun showToolbar(b: Boolean) {
        val supportActionBar = supportActionBar
        if (supportActionBar != null) {

            if (b)
                supportActionBar.show()
            else
                supportActionBar.hide()
        }
    }

    override fun setToolbarTitle(title: CharSequence) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }

    override fun setToolbarTitle(@StringRes title: Int) {

        if (supportActionBar != null) {
            supportActionBar!!.setTitle(title)
            //appToolbarTitle.setText(name);
        }
    }

    override fun showBackButton(b: Boolean) {

        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
    }

    override fun setToolbarColor(@ColorRes color: Int) {

        TODO("Remove Comment")
        /*if (toolbar != null) {
            toolbar.setBackgroundResource(color)
        }*/

    }


    override fun setToolbarElevation(isVisible: Boolean) {

        if (supportActionBar != null) {
            supportActionBar!!.elevation = if (isVisible) 8f else 0f
        }
    }

    fun showKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    override fun <T : BaseFragment> load(tClass: Class<T>): FragmentActionPerformer<T> {
        return navigationFactory.make(tClass)
    }

    override fun loadActivity(aClass: Class<out BaseActivity>): ActivityBuilder {
        return activityStarter.make(aClass)
    }

    override fun <T : BaseFragment> loadActivity(aClass: Class<out BaseActivity>, pageTClass: Class<T>): ActivityBuilder {
        return activityStarter.make(aClass).setPage(pageTClass)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.onRequestPermissionsResult(requestCode, permissions as Array<String>, grantResults)
    }

    override fun goBack() {
        onBackPressed()
    }

//    fun onError(throwable: Throwable) {
////        Log.e(javaClass.simpleName, "Error From Base framework ", throwable)
//
//
//        try {
//            when (throwable) {
//                is ServerException -> {
//                    showAlertDialog(throwable.message.toString())
//                }
//                is ConnectException -> {
//                    Toast.makeText(this, resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
//                    //   showToast(resources.getString(R.string.connection_exception))
//                    // showAlertDialog(resources.getString(R.string.connection_exception))
//                }
//                is AuthenticationException -> {
//                    Toast.makeText(this, resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
//                    appPreferences.clearAll()
//                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
//                    loadActivity(AuthActivity::class.java).byFinishingAll().start()
//                }
//                is ApplicationException -> {
////                    showAlertDialog(throwable.message)
//                }
//                is SocketTimeoutException -> {
//                    showAlertDialog(resources.getString(R.string.socket_time_out_exception))
//                }
//                else -> {
//                    showAlertDialog(throwable.message.toString())
////                    Log.e("error", throwable.message.toString())
////                    showMessage(R.string.other_exception)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

}
