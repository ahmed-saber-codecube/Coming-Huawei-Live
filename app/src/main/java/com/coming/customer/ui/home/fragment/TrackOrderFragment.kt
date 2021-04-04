package com.coming.customer.ui.home.fragment

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.LocationManager
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.data.pojo.GetOrderDetails
import com.coming.customer.data.pojo.LiveTrackingData
import com.coming.customer.data.pojo.OrderList
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.RatingListener
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.map.DrawRoute
import com.coming.customer.ui.map.MapNavigator
import com.coming.customer.ui.map.UpdateTimeAndDistance
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.loadUrlRoundedCorner
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_track_order.*
import java.net.ConnectException
import java.net.ProtocolException
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class TrackOrderFragment : BaseFragment(), View.OnClickListener, OnMapReadyCallback, UpdateTimeAndDistance, LocationManager.LocationListener {

    private var mapView: MapView? = null

    /*live tracking */
    private var stylistLat: String = ""
    private var stylistLng: String = ""

    private var currentLat: String = ""
    private var currentLng: String = ""

    private var storeLat: String = ""
    private var storeLng: String = ""

    private val latlngs = ArrayList<LatLng>()
    private var mMap: HuaweiMap? = null

    private var drawRoute: DrawRoute? = null
    private var mapNavigator: MapNavigator? = null

    var isDrawRoute = false

    /*end live tracking */

    var orderList: OrderList? = null
    var orderId = ""

    var getOrderDetails: GetOrderDetails? = null

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun createLayout(): Int = R.layout.home_fragment_track_order


//    @Inject
//    lateinit var locationManager: LocationManager

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    override fun bindData() {
        getIntentData()
        //callGetAllLocationApi()
        setLiveData()
        setupListeners()
    }

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000
    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            callGetAllLocationApi()
        }.also { runnable = it }, delay.toLong())
        super.onResume()
        mapView?.onResume()
        parent.apply {
            showToolbar(false)
        }
        //if(mapView != null){
        //   getStylistLatLng()
        //}

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    private fun getIntentData() {
        if (arguments != null && arguments?.containsKey(AppCommon.DATA)!!) {
            orderList = arguments?.getSerializable(AppCommon.DATA) as OrderList
            orderId = orderList?.orderId.toString()
            callGetOrderDetailsApi()
        } else if (arguments != null && arguments?.containsKey(AppCommon.DATA1)!!) {
            orderId = arguments?.getString(AppCommon.DATA1) as String
            callGetOrderDetailsApi()
        }
    }

    private fun setupListeners() {
        imageViewBack.setOnClickListener(this)
//        imageViewCall.setOnClickListener(this)
//        imageViewMessage.setOnClickListener(this)
    }

    private fun updateDetails() {
        getOrderDetails?.let {
            it.driverDetail?.let {
//                imageViewCall.visibility = View.VISIBLE
//                imageViewMessage.visibility = View.VISIBLE
                it.driverImage?.let { it1 -> imageViewDriver.loadUrlRoundedCorner(it1, 0, 15) }

                it.username?.let { it1 -> textViewDriverName.text = it1 }
                it.avgRate?.let { it1 ->
                    try {
                        textViewRating.text = it1
                        ratingBarRestaurant.rating = it1.toFloat()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } ?: run {
//                imageViewCall.visibility = View.INVISIBLE
//                imageViewMessage.visibility = View.INVISIBLE
            }
        }
    }


    private var ratingListener = object : RatingListener {
        override fun onRating(driverRating: String, storeRating: String, doneRatingListener: RatingListener) {

        }

        override fun onDoneRating() {

        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewBack -> {
                navigator.loadActivity(HomeActivity::class.java).byFinishingCurrent().start()
            }
//            R.id.imageViewCall -> {
//                val dialIntent = Intent(Intent.ACTION_DIAL)
//                getOrderDetails?.let {
//                    dialIntent.data = Uri.parse("tel:" + it.driverDetail?.phone)
//                }
//                startActivity(dialIntent)
//            }

//                getOrderDetails?.driverDetail?.phone
//                val reviewSheet = ReviewBottomSheetFragment(ratingListener)
//                reviewSheet.show(childFragmentManager, ReviewBottomSheetFragment::class.simpleName)
//            R.id.imageViewMessage -> {
////                val reviewSheet = ReviewBottomSheetFragment(ratingListener)
////                reviewSheet.show(childFragmentManager, ReviewBottomSheetFragment::class.simpleName)
//                navigator.loadActivity(IsolatedActivity::class.java, ChatFragment::class.java).addBundle(bundleOf(Pair(AppCommon.ORDER_DETAILS, getOrderDetails))).start()
//            }
        }
    }

    private fun setLiveData() {
        //getOrderDetails live data source
        authViewModel.getOrderDetailsLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1) && responseBody.data != null) {
                    responseBody.data?.let {
                        getOrderDetails = it
                        updateDetails()
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


        //getAllLocation live data source
        authViewModel.getAllLocationLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1) && responseBody.data != null) {
                    responseBody.data?.let {
                        updateLiveTrackingDetails(it)
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

    //getOrderDetails api
    private fun callGetOrderDetailsApi() {
        /**
         *
         * params : getOrderDetails(order_id)
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = orderId
        authViewModel.getOrderDetails(apiRequestParams)
    }

    //getAllLocation api
    private fun callGetAllLocationApi() {
        /**
         *
         * params : getAllLocation(order_id)
         *
         * optional :
         *
         * */
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.order_id = orderId
        authViewModel.getAllLocation(apiRequestParams)
    }

    private var isFirst: Boolean = true

    private fun updateLiveTrackingDetails(trackingData: LiveTrackingData) {
        stylistLat = trackingData.driverLat.toString()
        stylistLng = trackingData.driverLng.toString()

        currentLat = trackingData.userLat.toString()
        currentLng = trackingData.userLng.toString()

        storeLat = trackingData.merchantLat.toString()
        storeLng = trackingData.merchantLng.toString()

        addMarkerStylistLocation(stylistLat, stylistLng)
    }

    private fun setUpMap() {
        locationManager.startLocationUpdates()
        val mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

    }

    private fun addTimeAndDistanceText(time: String, km: String) {
        if (activity != null) {
            val time1 = time.split(" ")

            if (textViewDeliveryTime != null)
                textViewDeliveryTime.text = convertFromMinutesToHr(time1[0]) + " " + km
//            if (textViewMin != null) textViewMin.text = time
//            if (textViewDeliveryTime != null) textViewDeliveryTime.text = km

        }
    }

    /*private fun convertFromMinutesToHr(minutes: String): String {
        val hours = minutes.toFloat() / 60
        val minutesmain = minutes.toFloat() % 60f

        if (hours.toInt() <= 0f) {
            return "0 hr " +
                    DecimalFormat("#").format(minutesmain).toString() + getString(R.string.min)
        } else {
            return DecimalFormat("#").format(hours).toString() + " " + getString(R.string.hr) + " " + DecimalFormat(
                "#"
            ).format(minutesmain).toString() + " " + getString(R.string.min)
        }
    }*/

    private fun convertFromMinutesToHr(minutes: String): String {
        val hours = minutes.toFloat() / 60
        val minutesmain = minutes.toFloat() % 60f

        if (hours.toInt() <= 0f) {
            //return "0 hr " + DecimalFormat("#").format(minutesmain).toString() + getString(R.string.min)
            return DecimalFormat("#").format(minutesmain).toString()
        } else {
            return DecimalFormat("#").format(minutesmain).toString()
            //return DecimalFormat("#").format(hours).toString() + " " + getString(R.string.hr) + " " + DecimalFormat("#").format(minutesmain).toString() + " " + getString(R.string.min)
        }
    }

    private fun addMarkerStylistLocation(stylistLat: String, stylistLng: String) {
        mMap?.clear()
        val stylistLatLng = LatLng(stylistLat.toDouble(), stylistLng.toDouble())
        mMap?.addMarker(
            MarkerOptions()
                .position(stylistLatLng).title(resources.getString(R.string.text_driver)).anchor(0.5f, 0.5f)
                .icon(
                    BitmapDescriptorFactory.fromResource(
                        R.drawable.ic_driver_marker
                    )
                )
        )

        if (isFirst) {
            isFirst = false
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(stylistLat.toDouble(), stylistLng.toDouble()), 16f)
            mMap?.animateCamera(cameraUpdate)
        }
        addRoute()

    }


    private fun getStylistLatLng() {
        callGetAllLocationApi()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun addRoute() {
        if (mMap == null)
            setUpMap()
        if (drawRoute == null) {
            if (mMap != null && activity != null && isAdded) {
                mMap?.clear()
                drawRoute = DrawRoute(mMap, requireActivity())
                mapNavigator = MapNavigator(locationManager, mMap, context)
                mapNavigator?.setPathUpdateListener(drawRoute)
                drawPickUpPath()
            } else {
            }
        } else {
            drawRoute?.let {
                try {
                    if (isDrawRoute) {
                        val location = Location("")
                        location.latitude = stylistLat.toDouble()
                        location.longitude = stylistLng.toDouble()
                        mapNavigator?.accept(location)
                        drawPickUpPath()
                    } else {
                        if (mMap != null && activity != null && isAdded) {
                            mMap?.clear()
                            drawRoute = DrawRoute(mMap, requireActivity())
                            mapNavigator = MapNavigator(locationManager, mMap, context)
                            mapNavigator?.setPathUpdateListener(drawRoute)
                            drawPickUpPath()
                        } else {
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map)
        mapView!!.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        setUpMap()
    }

    private fun drawPickUpPath() {

        try {
            Log.d("drawPickUpPath", "currentLat" + currentLat + "currentLng" + currentLng)
            Log.d("drawPickUpPath", "stylistLat" + stylistLat + "stylistLng" + stylistLng)
            if (stylistLat.isNotEmpty() && stylistLng.isNotEmpty()) {

                drawRoute?.drawPath(
                    Arrays.asList<LatLng>(
                        LatLng(stylistLat.toDouble(), stylistLng.toDouble()),
                        LatLng(currentLat.toDouble(), currentLng.toDouble()),
                        LatLng(storeLat.toDouble(), storeLng.toDouble())
                    ),
                    object : DrawRoute.OnDrawComplete {
                        override fun onComplete() {
                            isDrawRoute = true
//                            val startMarker = drawRoute?.startMarker
//                            startMarker!!.rotation = drawRoute?.bearing!!
//                            mapNavigator?.setCurrentMarker(startMarker)

                            mapNavigator?.startNavigation()

                            drawRoute?.setUpdateTimeAndDistance(object : UpdateTimeAndDistance {
                                override fun onComplete(time: String?, km: String?) {
                                    Log.d("tag", "timekmtime" + time)
                                    Log.d("tag", "timekm" + km)
                                    km?.let { time?.let { it1 -> addTimeAndDistanceText(it1, it) } }
                                }

                            })
                        }
                    })
            }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createMarker(latitude: Double, longitude: Double): Marker {
        return mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
        )
    }

    override fun onMapReady(googleMap: HuaweiMap?) {

        //  Log.d("CALL", "onMapReady")
        mMap = googleMap

//        getStylistLatLng()
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
//            return
//        } else {
//
//            mMap?.isMyLocationEnabled = false
//            mMap?.uiSettings?.isMyLocationButtonEnabled = false
//            mMap?.uiSettings?.isCompassEnabled = false
//            mMap?.uiSettings?.isMapToolbarEnabled = false
//            mMap?.setPadding(0, 0, 0, 15)
//        Log.i("onMapReady", "onMapReady:"+currentLat+"  "+ currentLng)
//            if (currentLat.isNotEmpty() && currentLng.isNotEmpty()) {
//                val currentLatLng = LatLng(currentLat.toDouble(), currentLng.toDouble())
//                val markerOptions = MarkerOptions().position(currentLatLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
//                mMap?.addMarker(markerOptions)
//
//                mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
////                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
////                mMap!!.animateCamera(cameraUpdate)
//            } else {
//                locationManager.triggerLocation(object : LocationManager.LocationListener {
//
//                    override fun onLocationAvailable(latLng: LatLng) {
//                        if(latLng.latitude != 0.0 && latLng.longitude != 0.0) {
//                            locationManager.stop()
//                            val markerOptions = MarkerOptions().position(latLng)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
//                            mMap?.addMarker(markerOptions)
//
//                            mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
////                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
////                            mMap!!.animateCamera(cameraUpdate)
//                       }
//                            // else if (mMap != null && activity != null && isAdded) {
//                                drawRoute = DrawRoute(mMap, activity!!)
//                                mapNavigator = MapNavigator(locationManager, mMap, context)
//                                mapNavigator?.setPathUpdateListener(drawRoute)
//                                drawPickUpPath()
//                           // }
//                    }
//
//                    override fun onFail(status: LocationManager.LocationListener.Status) {
//                        if (status == LocationManager.LocationListener.Status.PERMISSION_DENIED || status == LocationManager.LocationListener.Status.DENIED_LOCATION_SETTING) {
//                            Toast.makeText(activity, "Please Enable the Location service", Toast.LENGTH_SHORT).show()
//
//                        }
//                    }
//                })
//
//            }

        //  if (mMap != null && activity != null && isAdded) {
        drawRoute = this.activity?.let { DrawRoute(mMap, it) }
        mapNavigator = MapNavigator(locationManager, mMap, context)
        mapNavigator?.setPathUpdateListener(drawRoute)
        drawPickUpPath()
        //  }

        /*        locationManager.triggerLocation(object : LocationManager.LocationListener {

                    override fun onLocationAvailable(latLng: LatLng) {
                        locationManager.stop()
                        Log.e("onLocationAvailable: ", "  $latLng")
    //                    lat = latLng.latitude
    //                    lng = latLng.longitude

                        mMap?.clear()
                        if (mMap != null && activity != null && isAdded) {
                            *//*if (bookingStatus == Common.ApiBookingType.START) {
                            if (washerLat != 0.0 && washerLng != 0.0) {
                                mMap?.addMarker(MarkerOptions()
                                    .position(LatLng(washerLat, washerLng))
                                    .anchor(0.5f, 0.5f)
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter)))

                                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(washerLat, washerLng), 16f)
                                mMap?.moveCamera(cameraUpdate)
                            }
                        } else {*//*
                            if (mMap != null && activity != null && isAdded) {
                                drawRoute = DrawRoute(mMap, activity!!)
                                mapNavigator = MapNavigator(locationManager, mMap, context)
                                mapNavigator?.setPathUpdateListener(drawRoute)
                                drawPickUpPath()
                            }
                        *//*}*//*
                    }
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {
                    if (status == LocationManager.LocationListener.Status.PERMISSION_DENIED || status == LocationManager.LocationListener.Status.DENIED_LOCATION_SETTING) {
                        Toast.makeText(activity, "Please Enable the Location service", Toast.LENGTH_SHORT).show()

                    }
                }
            })*/

        // }
    }

    override fun onComplete(time: String?, km: String?) {
        Log.d("tag", "time" + time)
        Log.d("tag", "timekm" + km)
        km?.let { time?.let { it1 -> addTimeAndDistanceText(it1, it) } }
    }

    override fun onLocationAvailable(latLng: LatLng) {
        /*currentLat = latLng.latitude.toString()
           currentLng = latLng.longitude.toString()
           Log.d("tagLatitude", latLng.latitude.toString())
           Log.d("tagLongitude", latLng.longitude.toString())
           session.latitude = currentLat
           session.longitude = currentLng*/
    }

    override fun onFail(status: LocationManager.LocationListener.Status) {
        if (status == LocationManager.LocationListener.Status.PERMISSION_DENIED || status == LocationManager.LocationListener.Status.DENIED_LOCATION_SETTING) {
            Toast.makeText(activity, "Please Enable the Location service", Toast.LENGTH_SHORT).show()
        }
    }
}