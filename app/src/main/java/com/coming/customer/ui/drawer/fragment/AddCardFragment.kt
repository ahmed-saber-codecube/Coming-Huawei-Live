package com.coming.customer.ui.drawer.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coming.customer.R
import com.coming.customer.apiparams.ApiRequestParams
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ApplicationException
import com.coming.customer.exception.ServerException
import com.coming.customer.ui.auth.activity.AuthActivity
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.viewmodel.AuthViewModel
import com.coming.customer.util.Validator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_add_card.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    var cardMonth = ""
    private var cardYear = ""

    @Inject
    lateinit var validator: Validator

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }
    }

    private fun setupListeners() {
        buttonSave.setOnClickListener(this)
        editTextExpiryDate.setOnClickListener(this)
        textViewLabelCards.setOnClickListener(this)
    }

    private fun addNewCard(): Boolean {
        return try {
            //TODO: Stringify
            validator.submit(editTextName)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_card_holder_name))
                .check()

            validator.submit(editTextCardNumber)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_card_number))
                .checkMinDigits(16)
                .errorMessage(resources.getString(R.string.validations_please_enter_valid_card_number))
                .check()

            validator.submit(editTextExpiryDate)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_expiry_date))

            validator.submit(editTextCvv)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_CVV))
                .checkMinDigits(3)
                .errorMessage(resources.getString(R.string.validations_please_enter_valid_CVV))
                .checkMaxDigits(3)
                .errorMessage(resources.getString(R.string.validations_please_enter_valid_CVV))
                .check()

//            navigator.load(SuccessFragment::class.java).setBundle(Bundle().apply { putString(AppConstants.KEY_SUCCESS_MSG, getString(R.string.text_add_card_success)) }).replace(true)

            true
        } catch (e: ApplicationException) {
            showToastShort(e.message)
            false
        }
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                Calendar.DAY_OF_MONTH
            )
        )
            .apply {
                datePicker.minDate = System.currentTimeMillis() - 10000
            }
            .show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance().apply {
            set(year, month, day)
        }

        val formatter = SimpleDateFormat("MM/YY")
        editTextExpiryDate.setText(formatter.format(cal.time))

        val formatterMonth = SimpleDateFormat("MM")
        cardMonth = formatterMonth.format(cal.time)

        val formatterYear = SimpleDateFormat("YY")
        cardYear = formatterYear.format(cal.time)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonSave -> {
                if (addNewCard()) {
                    callSaveCardApi()
                }
            }

            R.id.editTextExpiryDate -> openDatePicker()

            R.id.textViewLabelCards -> navigator.goBack()
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_add_card


    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLiveData()
        super.onCreate(savedInstanceState)
    }

    private fun setLiveData() {
        //saveCard live data source
        authViewModel.saveCardLiveData.observe(this,
            { responseBody ->
                hideLoader()
//                showToastLong(responseBody.message)
                if (responseBody.responseCode == 1) {
                    navigator.load(SuccessFragment::class.java).setBundle(Bundle().apply { putString(AppConstants.KEY_SUCCESS_MSG, getString(R.string.text_add_card_success)) }).replace(false)
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
                    if (throwable.code == 0) {
                        throwable.message?.let { showToastLong(it) }
                    } else if (throwable.code == 2) {
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

    //saveCard api
    private fun callSaveCardApi() {
        /**
         *
         * params : saveCard(user_id ,exp_month(01-12), exp_year(2020-3000), cardno, cardholdername, cvv)
         *
         * optional :
         *
         * */

        showLoader()
        val apiRequestParams = ApiRequestParams()
        apiRequestParams.user_id = session.user?.id
        apiRequestParams.exp_month = cardMonth
        apiRequestParams.exp_year = cardYear
        apiRequestParams.cardno = editTextCardNumber.text.toString().trim()
        apiRequestParams.cardholdername = editTextName.text.toString().trim()
        apiRequestParams.cvv = editTextCvv.text.toString().trim()
        authViewModel.saveCard(apiRequestParams)
    }

}