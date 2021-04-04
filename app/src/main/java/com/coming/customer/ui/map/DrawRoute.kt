package com.coming.customer.ui.map


import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.coming.customer.R
import com.coming.customer.core.Common
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.*
import com.huawei.hms.support.api.client.PendingResult
import java.util.*
import kotlin.collections.ArrayList


class DrawRoute(private val googleMap: HuaweiMap?, private val context: Activity) : PathUpdateListener {

    private val key: String = context.resources.getString(R.string.google_maps_api_key)
    internal var imageViewBlue: ImageView? = null
    internal var imageViewOrang: ImageView? = null
    internal var customMarkerView: View? = null
    private var updateTimeAndDistance: UpdateTimeAndDistance? = null


    var currentPath: List<com.huawei.hms.maps.model.LatLng>? = null
        private set
    var startMarker: Marker? = null
        private set
    var storeMarker: Marker? = null
        private set
    var initial: Marker? = null
        private set
    var endMarker: Marker? = null
        private set
    private var points: MutableList<LatLng>? = null
    private val builder = LatLngBounds.Builder()
    private var currentPolyline: Polyline? = null
    lateinit var latLngs: List<LatLng>


    val bearing: Float
        get() {
            if (points != null && points!!.size > 1) {
                val from = points!![0]
                val to = points!![1]
                return SphericalUtil.computeHeading(LatLng(from.lat, from.lng), LatLng(to.lat, to.lng)).toFloat()
            }
            return 0f
        }


    private fun addMarkerStart(location: LatLng?): Marker? {
        if (googleMap != null && location != null) {
            val markerOptions = MarkerOptions()
                .position(location)
                .title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
            return googleMap.addMarker(markerOptions)
        }
        return null
    }

    private fun addMarkerStore(location: LatLng?): Marker? {
        if (googleMap != null && location != null) {
            val markerOptions = MarkerOptions()
                .position(location)
                .title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_store))
            return googleMap.addMarker(markerOptions)
        }
        return null
    }

    private fun addMarkerEnd(location: LatLng?): Marker? {
        if (googleMap != null && location != null) {
            val markerOptions = MarkerOptions()
                .position(location)
                .title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
            return googleMap.addMarker(markerOptions)
        }
        return null
    }


    fun drawPath(latLngs: List<com.huawei.hms.maps.model.LatLng>?/*, start: Bitmap, last: Bitmap*/, onDrawComplete: OnDrawComplete) {


        this.currentPath = latLngs

        if (latLngs == null || latLngs.size < 3)
            return

        googleMap!!.clear()

        try {
            //   Picasso.with(context).load(R.drawable.user_profile).fetch();
            val context = GeoApiContext.Builder()
            context.apiKey(key)
            val directionsApiRequest = DirectionsApi.newRequest(context.build())

            val latLng1 = com.google.maps.model.LatLng(latLngs[0].latitude, latLngs[0].longitude)
            directionsApiRequest.origin(latLng1)
            startMarker = addMarkerStart(latLngs[0])
//            val latLng = com.google.maps.model.LatLng(latLngs[latLngs.size - 1].latitude, latLngs[latLngs.size - 1].longitude)
//            directionsApiRequest.origin(latLng)
            storeMarker = addMarkerStore(latLngs[latLngs.size - 1])
            // startMarker = addBitmapMarker(latLngs[0], start)
            // startMarker = addCustomMarker(latLngs.get(0), 1);


            val latLng2 = com.google.maps.model.LatLng(latLngs[1].latitude, latLngs[1].longitude)
            directionsApiRequest.destination(latLng2)
            endMarker = addMarkerEnd(latLngs[1])

            builder.include(latLngs[0]).include(latLngs[1]).include(latLngs[latLngs.size - 1])

            directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
                private lateinit var duration: List<String>
                private lateinit var time: List<String>

                override fun onResult(result: DirectionsResult) {

                    this@DrawRoute.context.runOnUiThread {
                        routeDraw(result)
                        onDrawComplete.onComplete()
                        var totalDuration = 0f
                        var totalDistance = 0f

                        if (updateTimeAndDistance != null && result.routes.isNotEmpty()) {

                            for (i in result.routes[0].legs.indices) {
                                duration = result.routes[0].legs[i].duration.toString().split(" ")
                                totalDuration += duration[0].toFloat()
                                time = result.routes[0].legs[i].distance.toString().split(" ")
                                if (time[1] == Common.km) {
                                    totalDistance += time[0].toFloat()
                                }
                            }

                            updateTimeAndDistance!!.onComplete(
                                totalDuration.toString(),
                                totalDistance.toString()
                            )

                        }
                    }

                }


                override fun onFailure(e: Throwable) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setUpdateTimeAndDistance(updateTimeAndDistance: UpdateTimeAndDistance) {
        this.updateTimeAndDistance = updateTimeAndDistance
    }

    fun getTimeAndDistanceWithoutDrawRoute(location: Location, latLngs: List<LatLng>?) {
        val context = GeoApiContext.Builder()
        context.apiKey(key)

        val directionsApiRequest = DirectionsApi.newRequest(context.build())

        directionsApiRequest.origin(com.google.maps.model.LatLng(location.latitude, location.longitude))
        val latLng2 = com.google.maps.model.LatLng(
            latLngs!![latLngs.size - 1].latitude,
            latLngs[latLngs.size - 1].longitude
        )
        directionsApiRequest.destination(latLng2)

        val waypont = ArrayList<LatLng>()
        if (latLngs.size > 2) {

            for (i in 1..latLngs.size - 2) {
                val latLng = LatLng(latLngs[i].latitude, latLngs[i].longitude)
                waypont.add(latLng)
                builder.include(latLngs[i])
                //addNumberMarker(latLngs[i], i)
            }
        }
    }


    private fun drawNewPath(newStart: LatLng, onDrawComplete: OnDrawComplete?) {

        // must have current path
        if (currentPolyline == null)
            return

        val context = GeoApiContext.Builder()
        context.apiKey(key)
        val directionsApiRequest = Direction.newRequest(context.build())

        val start = com.google.maps.model.LatLng(newStart.latitude, newStart.longitude)
        directionsApiRequest.origin(start)

        val endLng = currentPath!![currentPath!!.size - 1]
        val end = com.google.maps.model.LatLng(endLng.latitude, endLng.longitude)
        directionsApiRequest.destination(end)

        builder.include(newStart).include(endLng)

        val waypont = ArrayList<com.google.maps.model.LatLng>()

        if (!waypont.isEmpty())
            directionsApiRequest.waypoints(*waypont.toTypedArray())
        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {

                this@DrawRoute.context.runOnUiThread {
                    routeDraw(result)
                    onDrawComplete?.onComplete()
                    /* if (updateTimeAndDistance != null) {
                         updateTimeAndDistance!!.onComplete(result.routes[0].legs[0].
                                 duration.toString(),
                                 result.routes[0].legs[0].distance.toString());
                     }*/
                }
            }

            override fun onFailure(e: Throwable) {

            }
        })

    }

    private fun addNumberMarker(location: LatLng?, flagNo: Int) {
        if (googleMap != null && location != null) {
            val markerOptions = MarkerOptions().title(getCityName(location.latitude, location.longitude)).position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver_marker))
            googleMap.addMarker(markerOptions)
        }
    }

    private fun routeDraw(result: DirectionsResult) {

        points = ArrayList()

        if (result.routes != null && result.routes.isNotEmpty()) {

            for (i in result.routes[0].legs.indices) {
                for (j in result.routes[0].legs[i].steps.indices) {
                    points!!.addAll(result.routes[0].legs[i].steps[j].polyline.decodePath())
                }
            }


            if (currentPolyline != null)
                currentPolyline!!.remove()

            val currentPolylineOptions = PolylineOptions()
            // polyline.remove();
            currentPolylineOptions.color(ContextCompat.getColor(context, R.color.colorTextBlue))
            currentPolylineOptions.width(10f)

            currentPolylineOptions.addAll(convert(points!!))
            currentPolyline = googleMap!!.addPolyline(currentPolylineOptions)
            val bounds = builder.build()
            val center = bounds.center
            // bounds.including()
            val padding = 150
            val width = context.resources.getDimensionPixelOffset(R.dimen._300dp)
            val height = context.resources.getDimensionPixelOffset(R.dimen._300dp)
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            googleMap.animateCamera(cu)
        }

    }


    fun convert(latLngs: List<com.google.maps.model.LatLng>): List<LatLng> {
        val list = ArrayList<LatLng>()
        for (latLng in latLngs) {
            list.add(LatLng(latLng.lat, latLng.lng))
        }
        return list
    }


    fun clearCurrentPath() {
        if (currentPolyline != null) {
            currentPolyline?.remove()
            currentPolyline?.points?.clear()
            currentPolyline = null
        }
    }


    override fun updatePath(location: LatLng) {

        if (currentPolyline != null) {
            val points = currentPolyline?.points
            if (points != null && !points.isEmpty()) {
                if (!PolyUtil.isLocationOnPath(location, points, true, 150.0)) {
                    Log.d("Is On Path", " " + false)
                    drawNewPath(location, null)
                } else
                    Log.d("Is On Path", " " + true)
            }
        }

    }


    interface OnDrawComplete {

        fun onComplete()
    }

    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        var addresses: List<Address>
        addresses = geocoder.getFromLocation(latitude, longitude, 1)

        val cityName = addresses[0].getAddressLine(0)
        val stateName = addresses[0].getAddressLine(1)
        val countryName = addresses[0].getAddressLine(2)
        return cityName + "" + stateName + "" + countryName
    }
}
