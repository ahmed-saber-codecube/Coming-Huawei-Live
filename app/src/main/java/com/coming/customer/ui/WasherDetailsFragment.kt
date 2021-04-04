package com.coming.customer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.core.LocationManager
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.map.DrawRoute
import com.coming.customer.ui.map.MapNavigator
import com.coming.customer.ui.map.UpdateTimeAndDistance
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.washer_details_fragment.*
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class WasherDetailsFragment : BaseFragment(), OnMapReadyCallback, UpdateTimeAndDistance {

    var lat = 0.0
    var lng = 0.0

    var washerLat = 0.0
    var washerLng = 0.0
    var bookingLat = 0.0
    var bookingLng = 0.0
    var orderId = ""

    var isDrawRoute = false

    private var washer_arrived = false

    private var drawRoute: DrawRoute? = null
    private var mapNavigator: MapNavigator? = null

    private var currentLocation: Location? = null

    private var mMap: HuaweiMap? = null
    private var mMarker: Marker? = null

    var runnable: Runnable? = null
    var handler: Handler? = null

    companion object {
        lateinit var markers: ArrayList<Marker>
    }

    override fun createLayout(): Int {
        return R.layout.washer_details_fragment
    }
//
//    @Inject
//    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }


    private val homeViewModel by viewModels<AuthViewModel>()

    override fun onResume() {
        super.onResume()
        if (mMap == null && drawRoute == null)
            setUpMap()
        arguments?.let {
            if (it.containsKey("order_data")) {
                setDetails()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacks { runnable }
    }

    override fun bindData() {
        hideKeyBoard()
        setUpMap()
    }

    override fun onComplete(time: String?, km: String?) {
        if (!washer_arrived) {
            if (time?.isNotEmpty()!! && km?.isNotEmpty()!!) {
                addTimeAndDistanceText(time, km)
            }
        }
    }


    override fun onMapReady(googleMap: HuaweiMap?) {
        mMap = googleMap
        markers = ArrayList()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            mMap?.isMyLocationEnabled = false
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
            mMap?.uiSettings?.isCompassEnabled = false
            mMap?.uiSettings?.isMapToolbarEnabled = false
            mMap?.setPadding(0, 0, 0, 15)

            locationManager.triggerLocation(object : LocationManager.LocationListener {

                override fun onLocationAvailable(latLng: LatLng) {
                    locationManager.stop()
                    Log.e("onLocationAvailable: ", "  $latLng")
                    lat = latLng.latitude
                    lng = latLng.longitude

                    mMap?.clear()
                    if (mMap != null && activity != null && isAdded) {

                        if (washerLat != 0.0 && washerLng != 0.0) {
                            mMap?.addMarker(
                                MarkerOptions()
                                    .position(LatLng(washerLat, washerLng))
                                    .anchor(0.5f, 0.5f)
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
                            )

                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(washerLat, washerLng), 16f)
                            mMap?.moveCamera(cameraUpdate)

                        } else {
                            if (mMap != null && activity != null && isAdded) {
                                drawRoute = DrawRoute(mMap, activity!!)
                                mapNavigator = MapNavigator(locationManager, mMap, context)
                                mapNavigator?.setPathUpdateListener(drawRoute)
                                drawPickUpPath()
                            }
                        }
                    }
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {
                    if (status == LocationManager.LocationListener.Status.PERMISSION_DENIED || status == LocationManager.LocationListener.Status.DENIED_LOCATION_SETTING) {
                        Toast.makeText(activity, "Please Enable the Location service", Toast.LENGTH_SHORT).show()

                    }
                }
            })
            //  mMap?.setOnMapClickListener(this)
        }
    }


    private fun setDetails() {
        /* orderData.let { orderData1 ->
             washerLat = orderData1.washer_data?.lat?.toDouble()!!
             washerLng = orderData1.washer_data?.lng?.toDouble()!!


             bookingLat = orderData1.booking_lat?.toDouble()!!
             bookingLng = orderData1.booking_lng?.toDouble()!!

             currentLocation = Location("")
             currentLocation?.latitude = bookingLat
             currentLocation?.longitude = bookingLng

 //            if (bookingStatus == Common.ApiBookingType.START) {
             if (mMap == null)
                 setUpMap()
             else {
                 mMap?.clear()

                 mMap?.addMarker(
                     MarkerOptions()
                         .position(LatLng(washerLat, washerLng))
                         .anchor(0.5f, 0.5f)
                         .flat(false)
                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
                 )

                 val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(washerLat, washerLng), 16f)
                 mMap?.moveCamera(cameraUpdate)
             }
             *//*} else {
                if (mMap == null)
                    setUpMap()
                else {
                    if (drawRoute == null) {
                        if (mMap != null && activity != null && isAdded) {
                            mMap?.clear()
                            drawRoute = DrawRoute(mMap, activity!!)
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
                                    location.latitude = washerLat
                                    location.longitude = washerLng
                                    mapNavigator?.accept(location)
                                } else {
                                    if (mMap != null && activity != null && isAdded) {
                                        mMap?.clear()
                                        drawRoute = DrawRoute(mMap, activity!!)
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
            }*//*
        }*/
    }

    private fun addTimeAndDistanceText(time: String, km: String) {
        if (activity != null) {
            val time1 = time.split(" ")

            if (textViewArrivalTIme != null) textViewArrivalTIme.text = "${convertFromMinutesToHr(time1[0])}"

        }
    }

    private fun convertFromMinutesToHr(minutes: String): String {
        val hours = minutes.toFloat() / 60
        val minutesmain = minutes.toFloat() % 60f

        if (hours.toInt() <= 0f) {
            return "0 hr " +
                    DecimalFormat("#").format(minutesmain).toString() + "min"
        } else {
            return DecimalFormat("#").format(hours).toString() + " hr " + DecimalFormat("#").format(minutesmain).toString() + " min"
        }
    }

    private fun setUpMap() {
        locationManager.startLocationUpdates()
        val mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.placeHolder, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    private fun setLiveData() {
        /* homeViewModel.orderDetailsLiveData.observe(this,
             onChange = { responseBody ->
                 showLoader(false)
                 if (responseBody.responseCode == 1 && responseBody.data != null) {
                     orderData = responseBody.data
                     setDetails()
                 }
             },
             onError = { throwable ->
                 showLoader(false)
                 false
             })*/
    }


    fun drawPickUpPath() {
        try {
            if (washerLat != 0.0 && washerLng != 0.0) {
                drawRoute?.drawPath(Arrays.asList<LatLng>(LatLng(washerLat, washerLng), LatLng(bookingLat, bookingLng)),
                    object : DrawRoute.OnDrawComplete {
                        override fun onComplete() {
                            isDrawRoute = true
                            val startMarker = drawRoute?.startMarker
                            startMarker!!.rotation = drawRoute?.bearing!!
                            mapNavigator?.setCurrentMarker(startMarker)

                            mapNavigator?.startNavigation()

                            drawRoute?.setUpdateTimeAndDistance(this@WasherDetailsFragment)
                        }
                    })
            }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}