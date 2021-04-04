package com.coming.customer.ui.auth.activity

import android.os.Bundle
import android.util.Log
import com.coming.customer.R
import com.coming.customer.core.LocationManager
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.ui.auth.fragment.LoginFragment
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.drawer.fragment.AccountFragment
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.huawei.hms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragment()
    }
    @Inject
    lateinit var session: Session
    private fun setupFragment() {

        if (appPreferences.getBoolean(AppConstants.PREFS_IS_LOGGED_IN)) {
            locationManager.triggerLocation(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    if (latLng != null) {
                        Log.e("onLocationAvailable", "onLocationAvailable: ")
                        appPreferences.putString("lati", latLng.latitude.toString())
                        appPreferences.putString("longi", latLng.longitude.toString())
                        if (session.user?.email.isNullOrEmpty() || session.user?.username.isNullOrEmpty())
                            loadActivity(IsolatedActivity::class.java, AccountFragment::class.java).byFinishingAll().start()
                        else
                            loadActivity(HomeActivity::class.java).byFinishingCurrent().start()
                    }
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {
                    Log.e("onLocationAvailable", "onFail: ")
                    loadActivity(HomeActivity::class.java).byFinishingCurrent().start()
                }
            })
        } else {
            locationManager.triggerLocation(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    if (latLng != null) {
                        Log.e("onLocationAvailable", "onLocationAvailable: ")
                        appPreferences.putString("lati", latLng.latitude.toString())
                        appPreferences.putString("longi", latLng.longitude.toString())
                        load(LoginFragment::class.java).replace(false)

                    }
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {
                    Log.e("onLocationAvailable", "onFail: ")
                }
            })
        }
    }


    override fun findFragmentPlaceHolder(): Int = R.id.placeHolder

    override fun findContentView(): Int = R.layout.auth_activity

}