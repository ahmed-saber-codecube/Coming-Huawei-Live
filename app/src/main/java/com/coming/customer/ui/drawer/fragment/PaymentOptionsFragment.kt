package com.coming.customer.ui.drawer.fragment

import android.view.View
import com.coming.customer.R
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_payment_options.*

@AndroidEntryPoint
class PaymentOptionsFragment : BaseFragment(), View.OnClickListener {

    private val parent: IsolatedActivity get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.menu_payment_options)
        }
    }

    private fun setupListeners() {
        textViewCreditCards.setOnClickListener(this)
        textViewCredits.setOnClickListener(this)
        textViewRedeem.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.textViewCreditCards -> {
                navigator.load(CreditCardsFragment::class.java).replace(true)
            }

            R.id.textViewCredits -> {
                navigator.load(CreditsFragment::class.java).replace(true)
            }

            R.id.textViewRedeem -> {
                navigator.load(RedeemVoucherFragment::class.java)
                    .replace(true)
            }
        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_payment_options

}