package com.coming.customer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.coming.customer.R

class EmptyCartDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_cart_empty, container, false)
    }

    override fun onResume() {
        super.onResume()

        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 80 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight

        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.img_bg_cart)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }
}