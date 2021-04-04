package com.coming.customer.ui.drawer.fragment

import android.view.View
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_payment_methods.*

@AndroidEntryPoint
class PaymentMethodsFragment : BaseFragment(), View.OnClickListener {

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.title_payment_methods)
        }
    }

    private fun setupListeners() {
        if (RechargeFragment.paymentMethodSelected == AppCommon.PaymentMethod.MADA) {
            imageViewSelectedMadaPay.visibility = View.VISIBLE
            imageViewSelectedCreditCard.visibility = View.INVISIBLE
        } else {
            imageViewSelectedMadaPay.visibility = View.INVISIBLE
            imageViewSelectedCreditCard.visibility = View.VISIBLE
        }
//        imageViewBgApplePay.setOnClickListener(this)
//        imageViewApplePay.setOnClickListener(this)
        imageViewBgMadaPay.setOnClickListener(this)
        imageViewMadaPay.setOnClickListener(this)
        imageViewCreditCard.setOnClickListener(this)
        imageViewBgCreditCard.setOnClickListener(this)
        textViewCreditCard.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        when (view.id) {
//            R.id.imageViewBgApplePay, R.id.imageViewApplePay -> {
//                RechargeFragment.paymentMethodSelected = "Apple pay"
//
//                imageViewSelectedApplePay.visibility = View.VISIBLE
//                imageViewSelectedStcPay.visibility = View.INVISIBLE
//                imageViewSelectedCreditCard.visibility = View.INVISIBLE
//            }

            R.id.imageViewBgMadaPay, R.id.imageViewMadaPay -> {
                RechargeFragment.paymentMethodSelected = AppCommon.PaymentMethod.MADA

                //imageViewSelectedApplePay.visibility = View.INVISIBLE
                imageViewSelectedMadaPay.visibility = View.VISIBLE
                imageViewSelectedCreditCard.visibility = View.INVISIBLE
            }

            R.id.imageViewCreditCard, R.id.imageViewBgCreditCard, R.id.textViewCreditCard -> {
                RechargeFragment.paymentMethodSelected = AppCommon.PaymentMethod.VISA

                //imageViewSelectedApplePay.visibility = View.INVISIBLE
                imageViewSelectedMadaPay.visibility = View.INVISIBLE
                imageViewSelectedCreditCard.visibility = View.VISIBLE
            }
        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_payment_methods

}