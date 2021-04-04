package com.coming.customer.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppPreferences
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ApplicationException
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.Validator
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_personal_shopper.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject

@AndroidEntryPoint
class SpecialOrderFragment : BaseFragment(), View.OnClickListener {

    var store_lat = ""
    var store_lng = ""
    var store_address = ""

    @Inject
    lateinit var preferences: AppPreferences

    @Inject
    lateinit var validator: Validator

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        parent.apply { showToolbar(false) }
    }

    private fun setupListeners() {
        textViewPickLocation.setOnClickListener(this)
        buttonClose.setOnClickListener(this)
        buttonOrder.setOnClickListener(this)
    }

    private fun isValid(): Boolean {
        return try {
            //TODO: Stringify
            validator.submit(editTextStoreName)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_store_name))
                .check()
            validator.submit(editTextDeliveryAddress)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_delivery_address))
                .check()

            validator.submit(editTextOrderDetails)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_order_details))
                .check()

            if (store_lat.isNullOrEmpty() && store_lng.isNullOrEmpty()) {
                throw  ApplicationException(resources.getString(R.string.validations_please_select_store_location))
            }

            true
        } catch (e: ApplicationException) {
            showToastShort(e.message)
            false
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonOrder -> {
                if (isValid()) {
                    callAddSpecialOrderApi()
                }
            }
            R.id.textViewPickLocation -> {
//                navigator.load(PickSpecialOrderLocationFragment::class.java).replace(true)

                navigator.loadActivity(IsolatedActivity::class.java)
                    .addBundle(Bundle().apply {
                        putSerializable(ActivityStarter.ACTIVITY_FIRST_PAGE, PickSpecialOrderLocationFragment::class.java)
                    }).forResult(AppCommon.RequestCode.ADD_SERVICE)
                    .start()
            }

            R.id.buttonClose -> {
                navigator.goBack()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppCommon.RequestCode.ADD_SERVICE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(AppCommon.Location.LATITUDE)) {
                store_lat = data.getStringExtra(AppCommon.Location.LATITUDE)
                store_lng = data.getStringExtra(AppCommon.Location.LONGITUDE)
                store_address = data.getStringExtra(AppCommon.Location.ADDRESS)
                preferences.putString("store_lat", store_lat)
                preferences.putString("store_lng", store_lng)
                preferences.putString("store_address", store_address)

                Log.e("3 ---> ", "DATA : $store_lat")
                Log.e("3 ---> ", "DATA : $store_lng")
                Log.e("3 ---> ", "DATA : $store_address")
                textViewPickLocation.text = store_address.trim()
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_personal_shopper


//    @Inject
//    lateinit var locationManager: LocationManager


    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //addSpecialOrder live data source
        authViewModel.addSpecialOrderLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    navigator.goBack()
                    responseBody.data?.let {

                    }
                }

            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {

                    /* * as per backend infom
                     * 0->operation failed, // show message
                     * 2->no data found // show message
                     * 3->inactive account // exit and signin/signup screen
                     * 11->not approve // exit and signin/signup screen
                     *
                     **/
                    if (throwable?.code == 0 || throwable?.code == 2) {
                        throwable?.message?.let { showToastLong(it) }
                    } else if (throwable?.code == 3 || throwable?.code == 11) {
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

    //AddSpecialOrder api
    private fun callAddSpecialOrderApi() {
        /**
         *
         * params : addSpecialOrder(user_id, store_lat, store_lng, address_lat, address_lng, store_name, type ("Special Order"), order_description)
         *
         * optional :
         *
         * */

//        locationManager.triggerLocation(object : LocationManager.LocationListener {
//            override fun onLocationAvailable(latLng: LatLng) {
//                if (latLng != null) {
//                    locationManager.stop()
//
//                    showLoader()
        val apiRequestParams = ApiRequestParams()
        // apiRequestParams.user_id = session.user?.id
        apiRequestParams.store_lat = store_lat
        apiRequestParams.store_lng = store_lng
        apiRequestParams.address_lat = appPreferences.getString("lati")
        apiRequestParams.address_lng = appPreferences.getString("longi")
        apiRequestParams.store_name = editTextStoreName.text.toString().trim()
        apiRequestParams.delivery_address = editTextDeliveryAddress.text.toString().trim()
        //apiRequestParams.delivery_address = appPreferences
        //do not translate this string
        //apiRequestParams.type = "Special Order"
        apiRequestParams.order_description = editTextOrderDetails.text.toString().trim()
        authViewModel.addSpecialOrder(apiRequestParams)

        //               }
//            }
//
//            override fun onFail(status: LocationManager.LocationListener.Status) {
//                showAlertDialog(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store), object : BaseActivity.DialogOkListener {
//                    override fun onClick() {
//
//                    }
//                }, getString(R.string.label_ok))
//            }
//        })
    }


}