package com.coming.customer.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.data.pojo.GetOrderDetails
import com.coming.customer.ui.drawer.fragment.ChatFragment
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.home_bottom_sheet_contact.*

class ContactDriverBottomSheetFragment(var getOrderDetails: GetOrderDetails? = null) : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_bottom_sheet_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        setupListeners()
    }

    private fun setupListeners() {
        // textViewCall.setOnClickListener(this)
        textViewMessage.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.textViewCall -> {
//                if (getOrderDetails != null && getOrderDetails?.driverDetail != null && getOrderDetails?.driverDetail?.phone != null) {
//                    val dialIntent = Intent(Intent.ACTION_DIAL)
//                    dialIntent.data = Uri.parse("tel:" + getOrderDetails?.driverDetail?.phone)
//                    startActivity(dialIntent)
//                    dismiss()
//                }
//            }
            R.id.textViewMessage -> {
                val intent = Intent(requireContext(), IsolatedActivity::class.java).apply {
                    putExtra(ActivityStarter.ACTIVITY_FIRST_PAGE, ChatFragment::class.java)
                    putExtra(AppCommon.DRIVER_ID, getOrderDetails?.driverDetail?.id.toString())
                    putExtra(AppCommon.DRIVER_USER_NAME, getOrderDetails?.driverDetail?.username.toString())
                    putExtra(AppCommon.DRIVER_IMAGE, getOrderDetails?.driverDetail?.driverImage.toString())

                }
                startActivity(intent)
                dismiss()
            }
        }
    }
}