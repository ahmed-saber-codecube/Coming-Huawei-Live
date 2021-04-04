package com.coming.customer.ui.drawer.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.core.validation.enableWhen
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.activity.HomeActivity
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.Validator
import com.throdle.exception.AuthenticationException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_account.*
import java.net.ConnectException
import java.net.ProtocolException
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        dataValidation()
        setupListeners()
        updateDetails()
    }

    private fun updateDetails() {
        session.user?.let {
            editTextName.setText(it.username)
            editTextPhoneNumber.setText(it.countryCode + " " + it.phone)
            editTextEmail.setText(it.email)
        }
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.menu_account)
        }
    }

    private fun setupListeners() {
        buttonSave.setOnClickListener(this)
    }

    private fun dataValidation() = buttonSave.enableWhen {
        /**check validation for full name*/
        editTextName.isFilled().onValidationSuccess {
            editTextName.isFullName().onValidationSuccess {

            }.onValidationError {
                if (editTextName.text.isNullOrBlank())
                    editTextName.error = resources.getString(R.string.validations_please_enter_name)
                else
                    editTextName.error = resources.getString(R.string.validation_invalid_full_name)
            }
        }
        /**check validation for email*/
        editTextEmail.isFilled().onValidationSuccess {
            editTextEmail.isEmail().onValidationSuccess {

            }.onValidationError {
                if (editTextEmail.text.isNullOrBlank())
                    editTextEmail.error = resources.getString(R.string.validations_please_enter_email_address)
                else
                    editTextEmail.error = resources.getString(R.string.validations_please_enter_valid_email_address)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonSave -> {
                callEditProfileApi()
            }
        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_account

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {

        //editProfile live data source
        authViewModel.editProfileLiveData.observe(this,
            { responseBody ->
                hideLoader()
                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1 && responseBody.data != null) {
                    responseBody.data?.let {
                        session.user = it
                        session.userSession = it.token.toString()
                        session.userId = it.id.toString()
                        navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                    }
                }
            },
            { throwable ->
                hideLoader()
                if (throwable is ServerException) {
                    /*
                    * as per backend inform
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


    //editProfile api
    private fun callEditProfileApi() {
        /**
         *
         * params : editProfile(user_id,username,email,phone,country_code)
         *
         * optional : profile_image
         *
         * */
        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.userId
        apiRequestParams.username = editTextName.text.toString().trim()
        apiRequestParams.email = editTextEmail.text.toString().trim()
        apiRequestParams.phone = session.user?.phone
        apiRequestParams.country_code = session.user?.countryCode
        authViewModel.editProfile(apiRequestParams)
    }
}