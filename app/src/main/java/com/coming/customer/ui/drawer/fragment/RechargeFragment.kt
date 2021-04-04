package com.coming.customer.ui.drawer.fragment

import android.view.View
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.exception.ApplicationException
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.util.Validator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_recharge.*
import javax.inject.Inject

@AndroidEntryPoint
class RechargeFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    companion object {
        var paymentMethodSelected = AppCommon.PaymentMethod.VISA
    }

    private val parent: IsolatedActivity
        get() = requireActivity() as IsolatedActivity

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
        textViewSelectedMethod.text = paymentMethodSelected
        textView50.setOnClickListener(this)
        textView100.setOnClickListener(this)
        textView300.setOnClickListener(this)
        textView500.setOnClickListener(this)
        buttonRecharge.setOnClickListener(this)
        textViewSelectedMethod.setOnClickListener(this)
        textViewLabelCredits.setOnClickListener(this)
    }

    private fun rechargeAccount() {
        try {
            //TODO: Stringify
            if (editTextAmount.text.toString() == "") {
                validator.submit(editTextAmount)
                    .checkEmpty()
                    .errorMessage(resources.getString(R.string.validations_please_enter_recharge_amount))
                    .check()
            } else {
                validator.submit(editTextAmount)
                    .startWithZero()
                    .errorMessage(resources.getString(R.string.validations_please_enter_valid_amount))
                    .check()
            }
//commented
//            if (textViewSelectedMethod.text.isNotEmpty()) {
//                if (editTextAmount.text.toString().isNotEmpty()) {
//                    HomeActivity.homeUpdateCounterInfPass.onPayNow(editTextAmount.text.toString(),"",null,"","",null,"",textViewSelectedMethod.text.toString())
//                } else {
//                    showSnackBar(resources.getString(R.string.label_please_wait_while_we_will_getting_information))
//                }
//            } else {
//                showSnackBar(resources.getString(R.string.label_please_select_payment_method))
//            }

//            navigator.load(SuccessFragment::class.java)
//                .setBundle(Bundle().apply { putString(AppConstants.KEY_SUCCESS_MSG, "${getString(R.string.text_recharge_success)} SAR ${editTextAmount.text.toString()}") })
//                .replace(true)
        } catch (e: ApplicationException) {
            showToastShort(e.message)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonRecharge -> {
                rechargeAccount()
            }

            R.id.textViewSelectedMethod -> {
                navigator.load(PaymentMethodsFragment::class.java)
                    .replace(true)
            }

            R.id.textViewLabelCredits -> {
                navigator.goBack()
            }

            R.id.textView50 -> {
                editTextAmount.apply {
                    text?.clear()
                    setText("50")
                }
            }
            R.id.textView100 -> {
                editTextAmount.apply {
                    text?.clear()
                    setText("100")
                }
            }
            R.id.textView300 -> {
                editTextAmount.apply {
                    text?.clear()
                    setText("300")
                }
            }
            R.id.textView500 -> {
                editTextAmount.apply {
                    text?.clear()
                    setText("500")
                }
            }

        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_recharge

//    private fun showSnackBar(message: String = "") {
//
//        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
//        snackbar.setActionTextColor(this.resources.getColor(R.color.colorTextWhite))
//        val snackView = snackbar.view
//        val textView = snackView.findViewById<TextView>(R.id.snackbar_text)
//        textView.maxLines = 4
//        textView.setTextColor(this.resources.getColor(R.color.colorTextWhite))
//        snackbar.show()
//    }
}