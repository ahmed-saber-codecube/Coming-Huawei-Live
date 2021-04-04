package com.coming.customer.ui.auth.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppPreferences
import com.coming.customer.core.Session
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.drawer.fragment.AccountFragment
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.Validator
import com.huawei.hmf.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.auth_fragment_verification.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : BaseFragment(), View.OnClickListener {

    var otp = ""

    @Inject
    lateinit var preferences: AppPreferences

    @Inject
    lateinit var validator: Validator

    private var countDownTimer = object : CountDownTimer(60000, 1000) {
        @SuppressLint("DefaultLocale")
        override fun onTick(millisUntilFinished: Long) {
            var timer = String.format(resources.getString(R.string.label_resend_code_in) + " (0:%d)", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))

            when (AppCommon.AR) {
                appPreferences.getString(AppCommon.LANGUAGE) -> {
                    timer = String.format(resources.getString(R.string.label_resend_code_in) + " (٠:%d)", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))

                }
            }

            textViewResend.text = timer
            textViewResend.isClickable = false
        }

        override fun onFinish() {
            textViewResend.isClickable = true
            textViewResend.text = resources.getString(R.string.label_resend_code)
            textViewResend.setOnClickListener {
                startTimer()
                callReSendOTPApi()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
    }

    override fun bindData() {
        setupListeners()
        startTimer()
        setUpOTPTextChangeListener()
    }

    private fun setUpOTPTextChangeListener() {
        editTextOtp1.requestFocus()
        editTextOtp2.isEnabled = false
        editTextOtp3.isEnabled = false
        editTextOtp4.isEnabled = false

        editTextOtp1.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (editTextOtp1.text.toString().isEmpty()) {
                    editTextOtp2.isEnabled = false
                    editTextOtp3.isEnabled = false
                    editTextOtp4.isEnabled = false

                } else {
                    if (editTextOtp1.text.toString().length == 1) {
                        android.os.Handler().postDelayed({
                            editTextOtp2.requestFocus()
                        }, 0)
                        editTextOtp2.isEnabled = true
                        editTextOtp3.isEnabled = false
                        editTextOtp4.isEnabled = false
                    }

                }
            }

        })
        editTextOtp2.addTextChangedListener(object : TextWatcher {
            var i: Int = 0

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                i = 1
                if (editTextOtp2.text.toString().isEmpty()) {
                    editTextOtp3.isEnabled = false
                    editTextOtp4.isEnabled = false
                    if (i == 1) {
                        i = 0
                        editTextOtp1.requestFocus()
                    }

                } else {
                    if (editTextOtp2.text.toString().length == 1) {
                        android.os.Handler().postDelayed({
                            editTextOtp3.requestFocus()
                        }, 0)
                        editTextOtp3.isEnabled = true
                        editTextOtp4.isEnabled = false
                    }

                }

            }

        })
        editTextOtp3.addTextChangedListener(object : TextWatcher {
            var i: Int = 0

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {
                i = 1
                if (editTextOtp3.text.toString().isEmpty()) {
                    editTextOtp4.isEnabled = false
                    if (i == 1) {
                        i = 0
                        editTextOtp2.requestFocus()
                    }
                } else {
                    if (editTextOtp3.text.toString().length == 1) {
                        android.os.Handler().postDelayed({
                            editTextOtp4.requestFocus()
                        }, 0)
                        editTextOtp4.isEnabled = true
                    }
                }

            }

        })
        editTextOtp4.addTextChangedListener(object : TextWatcher {
            var i: Int = 0

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                i = 1

                if (editTextOtp4.text.toString().length == 1) {
                    hideKeyBoard()
                    return
                    //s4 = editTextPinFour.getText().toString().trim();
                }
                if (i == 1) {
                    i = 0
                    editTextOtp3.requestFocus()
                }
            }

        })

    }

    private fun setupListeners() {
        imageViewBack.setOnClickListener(this)
        buttonVerify.setOnClickListener(this)
//        editTextOtp1.addTextChangedListener(this)
//        editTextOtp2.addTextChangedListener(this)
//        editTextOtp3.addTextChangedListener(this)
//        editTextOtp4.addTextChangedListener(this)
    }

    private fun verifyOtp(): Boolean {

        hideKeyBoard()
        return if (editTextOtp1.text.toString().isEmpty() && editTextOtp2.text.toString().isEmpty() && editTextOtp3.text.toString().isEmpty() && editTextOtp4.text.toString().isEmpty()) {
            showToastShort(resources.getString(R.string.label_please_enter_valid_OTP))
            false
        } else if (editTextOtp1.text.toString().isEmpty() || editTextOtp2.text.toString().isEmpty() || editTextOtp3.text.toString().isEmpty() || editTextOtp4.text.toString().isEmpty()) {
            showToastShort(resources.getString(R.string.label_please_enter_valid_OTP))
            false
        } else {
            true
        }

        /*return try {
            otp = editTextOtp1.text.toString()
            otp += editTextOtp2.text.toString()
            otp += editTextOtp3.text.toString()
            otp += editTextOtp4.text.toString()

            //TODO: Stringify
            validator.checkMinDigits(otp, 4, "Please enter valid OTP")


//            countDownTimer.cancel()
//            preferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, true)
//            navigator.loadActivity(HomeActivity::class.java).byFinishingCurrent().start()
            true
        } catch (e: ApplicationException) {
            showToastShort(e.message)
            false
        }*/
    }

    private fun startTimer() {
        textViewResend.setOnClickListener(null)
        countDownTimer.start()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewBack -> {
                countDownTimer.cancel()
                navigator.goBack()
            }

            R.id.buttonVerify -> {
                if (verifyOtp()) {
                    callVerifyOTPApi()
                }
            }
        }
    }

    override fun createLayout(): Int = R.layout.auth_fragment_verification


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    /*override fun afterTextChanged(s: Editable?) {
        if (editTextOtp1.text.toString().length == 1) {
            editTextOtp2.requestFocus()
            if (editTextOtp2.text.toString().length == 1) {
                editTextOtp3.requestFocus()
                if (editTextOtp3.text.toString().length == 1) {
                    editTextOtp4.requestFocus()
                    if (editTextOtp4.text.toString().length == 1) {
                        hideKeyBoard()
                    }
                }
            }
        }
    }*/

    /*override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }*/

    /*override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }*/


    private val authViewModel by viewModels<AuthViewModel>()
    private var smsVerifyCatcher: SmsVerifyCatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smsVerifyCatcher = SmsVerifyCatcher(requireActivity()) { message ->
            val code: String = parseCode(message) //Parse verification code
            editTextOtp1.setText(code[0].toString())
            editTextOtp2.setText(code[1].toString())
            editTextOtp3.setText(code[2].toString())
            editTextOtp4.setText(code[3].toString())
            callVerifyOTPApi()
            //set code in edit text
            //then you can send verification code to server
        }
    }

    private fun parseCode(message: String): String {
        val p: Pattern = Pattern.compile("\\b\\d{4}\\b")
        val m: Matcher = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher?.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher?.onStop()
    }

    /**
     * need for Android 6 real time permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher?.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private fun setLiveData() {
        //verifyOTP live data source
        authViewModel.verifyOTPLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if ((responseBody.responseCode == 1) && responseBody.data != null) {
                    responseBody.data?.let {
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()
                        countDownTimer.cancel()
                        preferences.putBoolean(AppConstants.PREFS_IS_LOGGED_IN, true)
                        if (session.user?.email.isNullOrEmpty() || session.user?.username.isNullOrEmpty())
                            navigator.loadActivity(IsolatedActivity::class.java, AccountFragment::class.java).byFinishingAll().start()
                        else
                            navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
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
                }
                false
            })

        //resendOTP live data source
        authViewModel.resendOTPLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {

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
                }
                false
            })

    }

    //verifyOTP api
    private fun callVerifyOTPApi() {
        /**
         *
         * params : verifyOTP(user_id,otp,time_zone,device_token,device_type)
         *
         * optional :
         *
         * */
        showLoader()
        otp = editTextOtp1.text.toString()
        otp += editTextOtp2.text.toString()
        otp += editTextOtp3.text.toString()
        otp += editTextOtp4.text.toString()

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
                    apiRequestParams.user_id = session.userId
                    apiRequestParams.otp = otp
                    apiRequestParams.time_zone = TimeZone.getDefault().id
                    apiRequestParams.device_token = token
                    apiRequestParams.device_type = Session.DEVICE_TYPE
                    authViewModel.verifyOTP(apiRequestParams)
                } else {
                    showToastLong(resources.getString(R.string.connection_exception))
                }
            })


    }

    //resendOTP api
    private fun callReSendOTPApi() {
        /**
         *
         * params : resendOTP(user_id,phone,country_code)
         *
         * optional :
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        apiRequestParams.phone = session.user?.phone
        apiRequestParams.country_code = session.user?.countryCode
        authViewModel.resendOTP(apiRequestParams)
    }

}