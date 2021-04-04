package com.coming.customer.ui.drawer.fragment

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_settings.*
import java.util.*

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    var notifications = ""

    override fun bindData() {
        if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.EN)) {
            radioButtonEnglish.isChecked = true
            radioButtonEnglish.gravity = Gravity.START
        } else if (appPreferences.getString(AppCommon.LANGUAGE).equals(AppCommon.AR)) {
            radioButtonArabic.isChecked = true
            radioButtonEnglish.gravity = Gravity.END
        }
        updateNotificationStatus()
        setupListener()
    }

    private fun setupListener() {

        checkBoxNotifications.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                notifications = "1"
            } else {
                notifications = "0"
            }
            callChangeNotificationStatusApi()
        }

        radioGroupLanguage.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.radioButtonEnglish -> {
                    (activity as BaseActivity).updateLocalization(Locale("en"))
                    appPreferences.putString(AppCommon.LANGUAGE, AppCommon.EN)
                }
                else -> {
                    (activity as BaseActivity).updateLocalization(Locale("ar"))
                    appPreferences.putString(AppCommon.LANGUAGE, AppCommon.AR)
                }
            }
            navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
        }
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.menu_settings)
        }
    }

    private fun updateNotificationStatus() {
        checkBoxNotifications.isChecked = session.user?.notifications?.toInt() == 1
    }

    override fun createLayout(): Int = R.layout.home_fragment_settings

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //changeNotificationStatus live data source
        authViewModel.changeNotificationStatusLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        session.userSession = it.token.toString()
                        session.user = it
                        session.userId = it.id.toString()
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
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
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

    //changeNotificationStatus api
    private fun callChangeNotificationStatusApi() {
        /**
         *
         * params : changeNotificationStatus(merchant_id, notifications (0->off,1->on))
         *
         * optional :
         *
         * */

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.user?.id
        apiRequestParams.notifications = notifications
        apiRequestParams.role = Session.APP_VALUE
        authViewModel.changeNotificationStatus(apiRequestParams)
    }
}