package com.coming.customer.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.LocationManager
import com.coming.customer.core.Session
import com.coming.customer.data.URLFactory
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ApplicationException
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.Passable
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.Validator
import com.huawei.hms.maps.model.LatLng
import com.huawei.hmf.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.auth_fragment_login.*
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment(), View.OnClickListener {


    @Inject
    lateinit var validator: Validator

    override fun bindData() {
        setupListeners()
    }

    private fun isValid(): Boolean {
        return try {
            if (editTextMobile.text?.trim()?.startsWith("0")!!) {
                showToastLong(getString(R.string.invalid_number))
                false
            } else if (!confirm_privacy_checkbox.isChecked) {
                showToastLong(getString(R.string.Please_agree_on_privacy_terms))
                false
            } else {
                validator.submit(editTextMobile)
                    .checkEmpty()
                    .errorMessage(resources.getString(R.string.validations_please_enter_mobile_number))
                    .checkMinDigits(9)
                    .errorMessage(resources.getString(R.string.validations_please_enter_valid_mobile_number))
                    .check()
//            navigator.load(OtpFragment::class.java).replace(true)
                true
            }
        } catch (e: ApplicationException) {
            showToastShort(e.message)
            //super.onError(e)
            false
        }
    }

    private fun setupListeners() {
        buttonLogin.setOnClickListener(this)
        textViewSkip.setOnClickListener(this)

        editTextCountryCode.setOnClickListener {
            navigator.load(CountryCodePickerFragment::class.java).hasData(object :
                Passable<CountryCodePickerFragment> {
                override fun passData(t: CountryCodePickerFragment) {
                    t.setOnCountrySelectListener(object : CountryCodePickerFragment.OnCountrySelectListener {
                        override fun onCountrySelect(countryCode: String?) {
                            if (countryCode.isNullOrEmpty()) {
                                editTextCountryCode.setText("")
                            } else {
                                editTextCountryCode.setText(countryCode)
                            }
                            t.navigator.goBack()
                        }

                    })
                }
            }).add(true)
        }
        confirm_privacy_checkbox.setOnClickListener(this)
        confirm_privacy_tv.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonLogin -> {
                if (isValid()) {
                    callSignUpSignInApi()
                }
            }
            R.id.textViewSkip -> {
                callSkipStepApi()
            }
            R.id.confirm_privacy_tv -> {
                val data = Bundle()
                data.putString(Session.WEB_URL_KEY, Session.WEB_URL_KEY)
                data.putString(Session.TOOL_BAR_TITLE, getString(R.string.menu_privacy_policy))
                data.putString(Session.WEB_URL, URLFactory.privacyEPolicy())
                navigator.loadActivity(IsolatedActivity::class.java, WebViewFragment::class.java).addBundle(data).start()
            }
        }
    }


    override fun createLayout(): Int = R.layout.auth_fragment_login


    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //SignUp-SignIn live data source
        /*authViewModel.loginLiveData.observe(this,
            onChange = { responseBody ->
                hideLoader()
                if ((responseBody.responseCode == 1 || responseBody.responseCode == 4) && responseBody.data != null) {
                    responseBody.data?.let {
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()

                        */
        /**   Right now we have not set SignUp/SignIn step because mobile number always verify password options are not in there so
         *     always verify number
         *
         **//*
                        if (it.signupStep.equals("1")) {
                        }

                        navigator.load(OtpFragment::class.java).replace(true)

                    }
                } else {
                    hideLoader()
                    showToastLong(responseBody.message)
                }
            },
            onError = { throwable, responseBody ->
                hideLoader()
                if (responseBody?.responseCode == 0 || responseBody?.responseCode == 2) {
                    throwable.message?.let { showToastLong(it) }
                    false
                } else if (responseBody?.responseCode == 3 || responseBody?.responseCode == 11) {
                    appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                    navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    false
                } else {
                    true
                }
            })*/
        //SignUp-SignIn live data source
        authViewModel.loginLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1 || responseBody.responseCode == 4) && responseBody.data != null) {
                    responseBody.data?.let {
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

                        /**   Right now we have not set SignUp / SignIn step because mobile number always verify password options are not in there so
                         *   always verify number
                         *
                         **/

                        /*if (it.signupStep.equals("1")) {
                        }
                        navigator.load(OtpFragment::class.java).replace(true)*/


                        BaseActivity.auth?.signInWithEmailAndPassword("u_".plus(session.user?.phone!!).plus("@gmail.com"), "123456")
                            ?.addOnCompleteListener(requireActivity()) { task ->
                                hideLoader()
                                if (task.isSuccessful) {
                                    setDataToFirebase()

                                    /**   Right now we have not set SignUp / SignIn step because mobile number always verify password options are not in there so
                                     *   always verify number
                                     *
                                     **/
                                    if (it.signupStep.equals("1")) {
                                    }
                                    navigator.load(OtpFragment::class.java).replace(true)

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.e("fail", "createUserWithEmail:failure", task.exception)
                                    if (task.exception is FirebaseAuthInvalidUserException)
                                        if ((task.exception as FirebaseAuthInvalidUserException).errorCode == "ERROR_USER_NOT_FOUND") {
                                            BaseActivity.auth?.createUserWithEmailAndPassword("u_".plus(session.user?.phone!!).plus("@gmail.com"), "123456")
                                                ?.addOnCompleteListener(requireActivity()) { task ->
                                                    if (task.isSuccessful) {
                                                        setDataToFirebase()

                                                        /**   Right now we have not set SignUp / SignIn step because mobile number always verify password options are not in there so
                                                         *   always verify number
                                                         *
                                                         **/
                                                        if (it.signupStep.equals("1")) {
                                                        }
                                                        navigator.load(OtpFragment::class.java).replace(true)

                                                    } else {
                                                        Log.e("fail", "createUserWithEmail:failure", task.exception)

                                                    }
                                                }
                                        }
                                }
                            }

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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    } else {

                    }
                }
                false
            })

        //skipStep live data source
        authViewModel.skipStepLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1 || responseBody.responseCode == 4) && responseBody.data != null) {
                    responseBody.data.let {
                        session.userSession = it.token.toString()

                        navigator.loadActivity(HomeActivity::class.java).byFinishingCurrent().start()

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
                    if (throwable.code == 0 || throwable.code == 2) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 3 || throwable.code == 11) {
                        Toast.makeText(requireActivity(), resources.getString(R.string.inactive_account), Toast.LENGTH_SHORT).show()
                        appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                    }
                }
                false
            })

        //skipStep live data source
        /*  authViewModel.skipStepLiveData.observe(this,
              onChange = { responseBody ->
                  hideLoader()
                  if ((responseBody.responseCode == 1 || responseBody.responseCode == 4) && responseBody.data != null) {
                      responseBody.data?.let {
                          session.userSession = it.token.toString()

                          navigator.loadActivity(HomeActivity::class.java).byFinishingCurrent().start()

                      }
                  }
              },
              onError = { throwable, responseBody ->
                  hideLoader()
                  if (responseBody?.responseCode == 0 || responseBody?.responseCode == 2) {
                      throwable.message?.let { showToastLong(it) }
                      false
                  } else if (responseBody?.responseCode == 3 || responseBody?.responseCode == 11) {
                      appPreferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, false)
                      navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
                      false
                  } else {
                      true
                  }
              })*/
    }


    //SignUp-SignIn api
    private fun callSignUpSignInApi() {
        /**
         *
         * params : signup(phone,country_code,login_type, time_zone,device_type,device_token)
         *
         * optional : lat ,lng
         *
         * */
        showLoader()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("Firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                Log.e("token : ", "--> " + token)
                if (token != null) {
                    val apiRequestParams = ApiRequestParams()
                    apiRequestParams.phone = editTextMobile.text?.trim().toString()
                    apiRequestParams.country_code = editTextCountryCode.text?.trim().toString()
                    apiRequestParams.login_type = Session.SING_UP_TYPE_S
                    apiRequestParams.time_zone = TimeZone.getDefault().id
                    apiRequestParams.device_type = Session.DEVICE_TYPE
                    apiRequestParams.device_token = token
                    authViewModel.login(apiRequestParams)
                } else {
                    showToastLong(resources.getString(R.string.connection_exception))
                }
            })


    }

    //skipStep api
    private fun callSkipStepApi() {
        /**
         *
         * params : skipStep()
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        authViewModel.skipStep(apiRequestParams)
    }
}