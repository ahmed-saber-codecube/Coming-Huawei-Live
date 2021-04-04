package com.coming.customer.ui.drawer.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.coming.customer.R
import com.coming.customer.data.pojo.AppConstants
import com.coming.customer.exception.ApplicationException
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.util.Validator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_dialog_about_voucher.view.*
import kotlinx.android.synthetic.main.home_fragment_redeem_voucher.*
import javax.inject.Inject

@AndroidEntryPoint
class RedeemVoucherFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    private val parent: IsolatedActivity
        get() = requireActivity() as IsolatedActivity

    lateinit var voucherInfoDialog: AlertDialog

    override fun bindData() {
        setupListeners()
        prepareVoucherInfoDialog()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(false)
        }
    }

    private fun setupListeners() {
        buttonRedeem.setOnClickListener(this)
        imageViewHelp.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
    }

    private fun redeemVoucher() {
        try {
            //TODO: Stringify
            validator.submit(editTextCode)
                .checkEmpty()
                .errorMessage(resources.getString(R.string.validations_please_enter_voucher_code))
                .check()

            editTextCode.text?.clear()

            navigator.load(SuccessFragment::class.java)
                .setBundle(Bundle().apply { putString(AppConstants.KEY_SUCCESS_MSG, getString(R.string.text_voucher_success)) })
                .replace(true)

        } catch (e: ApplicationException) {
            showToastShort(e.message)
        }
    }

    private fun prepareVoucherInfoDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.home_dialog_about_voucher, null, false)
        dialogView.apply {
            buttonGotIt.setOnClickListener { closeVoucherInfoDialog() }
        }

        voucherInfoDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        voucherInfoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showVoucherInfoDialog() {
        voucherInfoDialog.show()
    }

    private fun closeVoucherInfoDialog() {
        voucherInfoDialog.dismiss()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonRedeem -> {
                redeemVoucher()
            }

            R.id.imageViewHelp -> {
                showVoucherInfoDialog()
            }

            R.id.imageViewBack -> {
                navigator.goBack()
            }
        }
    }

    override fun createLayout(): Int = R.layout.home_fragment_redeem_voucher

}