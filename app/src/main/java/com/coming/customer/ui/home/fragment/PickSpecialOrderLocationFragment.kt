package com.coming.customer.ui.home.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppPreferences
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_special_order_location.*
import javax.inject.Inject

@AndroidEntryPoint
class PickSpecialOrderLocationFragment : BaseFragment(), View.OnClickListener, OnMapReadyCallback, HuaweiMap.OnMapClickListener, LocationListener, HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {


    companion object {
        private const val RC_LOCATION_PERMISSION = 1234
    }

    lateinit var mGoogleApiClient: HuaweiApiClient
    private lateinit var locationRequest: LocationRequest
    var REQUEST_CHECK_SETTINGS = 100

    var latitude = 0.0
    var longitude = 0.0
    var viewedAddress = ""

    lateinit var mGoogleMap: HuaweiMap

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity
//    @Inject
//    lateinit var locationManager: com.coming.customer.core.LocationManager

    private var placesClient: PlacesClient? = null
    private var placeAutoComplete: AutocompleteSupportFragment? = null

    @Inject
    lateinit var preferences: AppPreferences


    override fun bindData() {
        initMaps()
        setupListeners()
        doAutoSearchPlaces()
    }

    override fun onResume() {
        super.onResume()
        parent.apply { showToolbar(false) }
    }

    override fun createLayout(): Int = R.layout.home_fragment_special_order_location


    private fun setupListeners() {
        buttonPickLocation.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
        buttonCurrentLocation_special.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonPickLocation -> {
                val intent = Intent()
                Log.e("1-->", "latitude : " + latitude.toString())
                Log.e("1-->", "longitude : " + longitude.toString())
                intent.putExtra(AppCommon.Location.LATITUDE, latitude.toString())
                intent.putExtra(AppCommon.Location.LONGITUDE, longitude.toString())
                intent.putExtra(AppCommon.Location.ADDRESS, viewedAddress.trim())
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }

            R.id.imageViewBack -> {
                navigator.goBack()
            }
            R.id.buttonCurrentLocation_special -> {
                getCurrentLocation()
            }

        }
    }


    private fun initMaps() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
//                    showToastShort("Cannot provide current location without location permission")
                    showSettingsDialogMap()
                }
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.applicationInfo?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun showSettingsDialogMap() {
        val dialog = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
            .setTitle(resources.getString(R.string.label_need_permissions))
            .setMessage(resources.getString(R.string.label_this_app_needs_permission_to_use_this_feature_you_can_grant_them_in_app_settings))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.label_goto_settings)) { dialog, which ->
                dialog.cancel()
                openSettings()
            }.setNegativeButton(resources.getString(R.string.label_cancel)) { dialog, which -> dialog.cancel() }.create()

        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(resources.getColor(R.color.colorTransferant))
        }
        dialog.show()
    }

    override fun onMapReady(googleMap: HuaweiMap) {
        mGoogleMap = googleMap
        centerCamera(LatLng(preferences.getString("lati").toDouble(), preferences.getString("longi").toDouble()))
        mGoogleMap.setOnMapClickListener(this)
    }


    override fun onMapClick(location: LatLng) {
        centerCamera(location)
    }

    override fun onLocationChanged(location: Location) {
        centerCamera(LatLng(location.latitude, location.longitude))
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onConnected(p0: Bundle?) {
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback(this)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e("--> ", "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("--> ", "onConnectionFailed")
    }

    override fun onResult(@NonNull locationSettingsResult: LocationSettingsResult) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                showToastLong(resources.getString(R.string.label_gps_enabled))
            } else {
                showToastLong(resources.getString(R.string.label_gps_is_not_enabled))
            }
        }
    }

    private fun centerCamera(locationLatLng: LatLng) {
        try {
            if (mGoogleMap != null) {
                mGoogleMap.clear()
            }
            val markerOption = MarkerOptions()
                .position(locationLatLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))

            if (mGoogleMap != null) {
                with(mGoogleMap) {
                    addMarker(markerOption)
                    moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                }
            }
            val geocoder = Geocoder(requireContext())
            val addresses = geocoder.getFromLocation(locationLatLng.latitude, locationLatLng.longitude, 1)
            val address = addresses[0].getAddressLine(0)
            Log.e("1-->", "latitude : " + locationLatLng.latitude)
            Log.e("1-->", "longitude : " + locationLatLng.longitude)
            latitude = locationLatLng.latitude
            longitude = locationLatLng.longitude
            placeAutoComplete?.setText(address)
            viewedAddress = address
            // place_autocomplete.setText(address)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doAutoSearchPlaces() {
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(
                requireActivity(),
                resources.getString(R.string.google_maps_api_key)
            )
        }
        //Search Places auto complete
        placesClient = Places.createClient(requireActivity())
        placeAutoComplete =
            childFragmentManager.findFragmentById(R.id.place_autocomplete) as AutocompleteSupportFragment?
        placeAutoComplete?.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        )
        placeAutoComplete?.view?.setBackgroundColor(Color.WHITE)
        placeAutoComplete?.setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    viewedAddress = place.address.toString()
                    centerCamera(place.latLng!!)
                    // showMessage("" + place.latLng?.latitude + "" + place.latLng?.longitude + "")
                }

                override fun onError(status: Status) {
//                   Log.e("autocomplete",status.statusMessage)
                }
            })
    }

    private fun getCurrentLocation() {
        locationManager.triggerLocation(object : com.coming.customer.core.LocationManager.LocationListener {
            override fun onLocationAvailable(latLng: LatLng) {
                if (latLng != null) {
                    centerCamera(latLng)
                }
            }

            override fun onFail(status: com.coming.customer.core.LocationManager.LocationListener.Status) {
            }
        })
    }
}