package com.coming.customer.ui.home.fragment

import android.app.Activity
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.coming.customer.BuildConfig
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.LocationManager
import com.coming.customer.data.pojo.*
import com.coming.customer.data.repository.UserRepository
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.home.HomeUpdateCounterInf
import com.coming.customer.ui.home.StoreDetailsUpdateCounterInf
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiClient
import com.huawei.hms.support.api.client.PendingResult
import com.huawei.hms.support.api.client.ResultCallback
import com.huawei.hms.support.api.client.Status
import com.google.android.gms.location.*
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_dialog_checkout.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject

@AndroidEntryPoint
class PlaceOrderDialogFragment : DialogFragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnMapReadyCallback, HuaweiMap.OnMapClickListener, HuaweiApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult>, HuaweiApiClient.OnConnectionFailedListener, HuaweiMap.OnCameraIdleListener {
    lateinit var prefrences: AppPreferences
    var latitude = 0.0
    var longitude = 0.0
    var orderNote = ""
    var deliveryAddress = ""
    var promoCode: PromoCode? = null

    var cartDetails: CartDetails? = null
    var cartDialog: CartDialogFragment? = null

    // dialog
    var placeOrderFragment: PlaceOrderDialogFragment? = null

    var disposable: Disposable? = null

    @Inject
    lateinit var repository: UserRepository
    var userId: String = ""

    companion object {
        var STATIC_ORDER_ID: String? = null
    }

    var storeDetailsUpdateCounterInf: StoreDetailsUpdateCounterInf? = null
    var homeDetailsUpdateCounterInf: HomeUpdateCounterInf? = null

    //FIXME
    @Inject
    lateinit var locationManager: LocationManager
    fun newInstance(updateCounterInfStore: StoreDetailsUpdateCounterInf?, UpdateCounterInfHome: HomeUpdateCounterInf?) = PlaceOrderDialogFragment().apply {
        storeDetailsUpdateCounterInf = updateCounterInfStore
        homeDetailsUpdateCounterInf = UpdateCounterInfHome
    }

    lateinit var mGoogleApiClient: HuaweiApiClient
    private lateinit var locationRequest: LocationRequest
    var REQUEST_CHECK_SETTINGS = 100

    private lateinit var mGoogleMap: HuaweiMap
    //private var googleHq = LatLng(23.0755316, 72.5239521)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_checkout, container, false)
    }

    override fun onResume() {
        super.onResume()
        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 80 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight

        dialog?.window?.apply {
            attributes = params
            setBackgroundDrawableResource(R.drawable.bg_rounded_rect_18dp)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefrences = AppPreferences(requireActivity())
        setupListeners()
        initMaps()
    }


    private fun getCurrentLocation() {
        locationManager.triggerLocation(object : LocationManager.LocationListener {
            override fun onLocationAvailable(latLng: LatLng) {
                if (latLng != null) {
                    centerCamera(latLng, 16.0f)
                }
            }

            override fun onFail(status: LocationManager.LocationListener.Status) {
                showSnackBar(resources.getString(R.string.label_we_need_location_permission_for_get_nearest_store))
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener(this)
        buttonCheckout.setOnClickListener(this)
        checkBoxInstruct.setOnCheckedChangeListener(this)
//        editTextAddress.keyListener = null
//        checkBoxInstructions.setOnCheckedChangeListener(this)
        pickCurrentLocation.setOnClickListener(this)
    }


    private fun initMaps() {
        var mapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
            childFragmentManager.beginTransaction().add(R.id.mapContainer, mapFragment, SupportMapFragment::class.simpleName).commit()
            childFragmentManager.executePendingTransactions()
            childFragmentManager.beginTransaction()
        }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: HuaweiMap) {
        mGoogleMap = googleMap
//        if ( (prefrences.getString("LATITUDE")).isNullOrEmpty()|| (prefrences.getString("LONGITUDE")).isNullOrEmpty()) {
//            //default location is Riyadh if no location is selected
//            centerCamera(LatLng(24.7136, 46.6753))
//
//        } else {
        centerCamera(LatLng(prefrences.getString("lati").toDouble(), prefrences.getString("longi").toDouble()), 16.0f)
        //   }
        mGoogleMap.setOnMapClickListener(this)
        mGoogleMap.setOnMapLongClickListener(null)
        mGoogleMap.setOnCameraIdleListener(this)

    }

    override fun onMapClick(location: LatLng) {
        centerCamera(location, mGoogleMap.cameraPosition.zoom)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
//            R.id.checkBoxInstruct -> {
//                if (isChecked) {
//                    editTextAddress.visibility = View.VISIBLE
//                } else {
//                    editTextAddress.visibility = View.GONE
//                }
//            }

            R.id.checkBoxInstruct -> {
                if (isChecked) {
                    editTextInstructions.visibility = View.VISIBLE
                } else {
                    editTextInstructions.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.pickCurrentLocation -> {
                getCurrentLocation()
            }
            R.id.buttonBack -> {
                dismiss()
            }

            R.id.buttonCheckout -> {
//                if (editTextInstructions.text.toString().trim().isNotEmpty()) {

                callPlaceOrderApi()

                /*val waitingPageDialogFragment = WaitingPageDialogFragment(updateCounterInf, locationManager)
                waitingPageDialogFragment.show(childFragmentManager, WaitingPageDialogFragment::class.simpleName)
                waitingPageDialogFragment.orderNote = orderNote
                waitingPageDialogFragment.promoCode = promoCode
                waitingPageDialogFragment.disposable = disposable
                waitingPageDialogFragment.repository = repository
                waitingPageDialogFragment.repository = repository
                waitingPageDialogFragment.userId = userId
                waitingPageDialogFragment.cartDetails = cartDetails
                waitingPageDialogFragment.deliveryAddress = editTextAddress.text.toString().trim()
                waitingPageDialogFragment.deliveryInstructions = editTextInstructions.text.toString().trim()*/
//                } else {
//                    showSnackBar(resources.getString(R.string.validations_please_enter_delivery_instruct))
//                }
            }
        }
    }

    private fun callPlaceOrderApi() {
        disposable?.dispose()
        (activity as BaseActivity).showLoader()


        //user_id,merchant_id,delivery_address,address_lat,address_lng,  optional parameter : delivery_instruction,promocode,notes
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = userId
        cartDetails?.cartDetail?.get(0)?.merchantId?.let {
            apiRequestParams.merchant_id = it
        }
        apiRequestParams.delivery_address = deliveryAddress

        apiRequestParams.address_lat = latitude.toString()
        apiRequestParams.address_lng = longitude.toString()

        if (editTextInstructions.text.toString().trim().isNotEmpty()) {
            apiRequestParams.delivery_instruction = editTextInstructions.text.toString().trim()
        } else {
            apiRequestParams.delivery_instruction = " "

        }

        apiRequestParams.notes = orderNote
        cartDetails?.let {
            apiRequestParams.sub_total = it.subTotal.toString()
        }

        promoCode?.promocode?.let {
            apiRequestParams.promocode = it
        }


        repository.placeOrder(apiRequestParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<DataWrapper<PlaceOrder>> {
                override fun onSuccess(data: DataWrapper<PlaceOrder>) {
                    (activity as BaseActivity).hideLoader()
                    when (data.responseBody?.responseCode) {
                        null -> {
                            when (data.throwable?.message) {
                                "Authentication Exception" -> {
                                    Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                                    AppPreferences(requireContext()).clearAll()
                                    AppPreferences(requireContext()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                                    if (cartDialog?.storeDetailActivity != null) {
                                        cartDialog?.storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                                    } else {
                                        cartDialog?.homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
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
                            data.responseBody.message?.let {
                                showSnackBar(it)
                            }
                            data.responseBody?.data?.let {
                                /*placeOrder = it
                                                        updateDetails()*/
                                if (storeDetailsUpdateCounterInf != null) {
                                    storeDetailsUpdateCounterInf?.onUpdateCounter("0")
                                    val waitingPageDialogFragment = WaitingPageDialogFragment().newInstance(storeDetailsUpdateCounterInf!!)
                                    waitingPageDialogFragment.show(childFragmentManager, WaitingPageDialogFragment::class.simpleName)
                                    waitingPageDialogFragment.orderNote = orderNote
                                    waitingPageDialogFragment.promoCode = promoCode
                                    waitingPageDialogFragment.disposable = disposable
                                    waitingPageDialogFragment.userId = userId
                                    waitingPageDialogFragment.orderId = data.responseBody?.data?.orderId.toString()
                                    Log.i("vvvvvv", data.responseBody?.data?.orderId.toString())
                                    waitingPageDialogFragment.STATIC_ORDER_ID = data.responseBody?.data?.orderId.toString()
                                    STATIC_ORDER_ID = data.responseBody?.data?.orderId.toString()
                                    waitingPageDialogFragment.cartDetails = cartDetails
                                    waitingPageDialogFragment.deliveryAddress = deliveryAddress
                                    waitingPageDialogFragment.deliveryInstructions = editTextInstructions.text.toString().trim()
                                    waitingPageDialogFragment.placeOrderFragment = placeOrderFragment
                                    waitingPageDialogFragment.cartDialog = cartDialog
                                } else {
                                    if (homeDetailsUpdateCounterInf != null) {
                                        homeDetailsUpdateCounterInf?.onUpdateCounter("0")
                                        val pendingPageDialogFragment = homeDetailsUpdateCounterInf?.let { it1 -> PendingWaitingPageDialogFragment.newInstance(it1) }
                                        pendingPageDialogFragment?.orderNote = orderNote
                                        pendingPageDialogFragment?.promoCode = promoCode
                                        pendingPageDialogFragment?.userId = userId
                                        pendingPageDialogFragment?.subTotal = cartDetails?.subTotal.toString()
                                        pendingPageDialogFragment?.orderId = data.responseBody?.data?.orderId.toString()
                                        Log.i("vvvvvv", data.responseBody?.data?.orderId.toString())
                                        STATIC_ORDER_ID = data.responseBody?.data?.orderId.toString()
                                        pendingPageDialogFragment?.deliveryAddress = deliveryAddress
                                        pendingPageDialogFragment?.deliveryInstructions = editTextInstructions.text.toString().trim()
                                        pendingPageDialogFragment?.orderId = data.responseBody?.data?.orderId.toString()
                                        pendingPageDialogFragment?.deliveryCost = cartDetails?.deliveryCost
                                        pendingPageDialogFragment?.grandTotal = cartDetails?.grandTotal
                                        pendingPageDialogFragment?.promocodeAmount = cartDetails?.promocodeAmount
                                        pendingPageDialogFragment?.subTotal = cartDetails?.subTotal.toString()
                                        pendingPageDialogFragment?.tax = cartDetails?.tax
                                        pendingPageDialogFragment?.payableTotalAmount = cartDetails?.grandTotal.toString()
                                        pendingPageDialogFragment?.placeOrderFragment = placeOrderFragment
                                        pendingPageDialogFragment?.cartDialog = cartDialog
                                        pendingPageDialogFragment?.show(childFragmentManager, WaitingPageDialogFragment::class.simpleName)
                                    }


                                    // cartDialogPass = cartDialog!!
                                    // placeOrderFragmentPass = placeOrderFragment!!
                                }
                            }
                        }
                        else -> {
                            data.responseBody?.message?.let { showSnackBar(it) }
                        }
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onError(e: Throwable) {
                    if (e is ServerException) {
                        (activity as BaseActivity).hideLoader()
                        e.message?.let { showSnackBar(it) }

                    }
                    if (e is AuthenticationException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
                        (activity as BaseActivity).hideLoader()
                        AppPreferences(requireContext()).clearAll()
                        AppPreferences(requireContext()).putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        if (cartDialog?.storeDetailActivity != null) {
                            cartDialog?.storeDetailActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        } else {
                            cartDialog?.homeActivity!!.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                        }
                    } else if (e is ProtocolException || e is ConnectException) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        locationManager.stop()
    }


    private fun showSnackBar(message: String) {

        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
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

    override fun onConnected(p0: Bundle?) {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback(this)
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {

        val status: Status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                Log.e("Log", "SUCCESS")
//                showToastLong("SUCCESS")
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                try {
                    Log.e("Log", "RESOLUTION_REQUIRED")
//                    showToastLong("RESOLUTION_REQUIRED")
                    status.startResolutionForResult(context as Activity?, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                    //failed to show
                }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                //showToastLong("SETTINGS_CHANGE_UNAVAILABLE")
                Log.e("Log", "SETTINGS_CHANGE_UNAVAILABLE")
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    lateinit var locationLatLngCompare: LatLng
    private fun centerCamera(locationLatLng: LatLng, zoomRatio: Float) {
        try {
            if (mGoogleMap != null) {
                mGoogleMap.clear()
            }
            locationLatLngCompare = locationLatLng
//            val markerOption = MarkerOptions()
//                .position(locationLatLng)
//                .draggable(true)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))

            if (mGoogleMap != null) {
                with(mGoogleMap) {
                    addMarker(MarkerOptions().position(locationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.point)))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, zoomRatio))
                    val geocoder = Geocoder(requireContext())
                    val addresses = geocoder.getFromLocation(locationLatLng.latitude, locationLatLng.longitude, 1)
                    val address = addresses[0].getAddressLine(0)
                    Log.e("1--> ", "latitude : " + locationLatLng.latitude)
                    Log.e("1--> ", "longitude : " + locationLatLng.longitude)
                    latitude = locationLatLng.latitude
                    longitude = locationLatLng.longitude


                    deliveryAddress = address
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCameraIdle() {
        if (mGoogleMap.cameraPosition.target.latitude != latitude && mGoogleMap.cameraPosition.target.longitude != longitude) {
            centerCamera(mGoogleMap.cameraPosition.target, mGoogleMap.cameraPosition.zoom)
        }
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
}