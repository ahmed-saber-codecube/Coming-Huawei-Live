package com.coming.customer.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.coming.customer.R
import com.coming.customer.ui.home.OfferAdapter2
import com.coming.customer.ui.isolated.IsolatedActivity
import com.coming.customer.ui.manager.ActivityStarter
import kotlinx.android.synthetic.main.home_dialog_offers.*

class OffersDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_dialog_offers, container, false)
    }

    override fun onResume() {
        super.onResume()

        val calculatedWidth = resources.displayMetrics.widthPixels * 90 / 100
        val calculatedHeight = resources.displayMetrics.heightPixels * 80 / 100

        val params = dialog?.window?.attributes
        params?.width = calculatedWidth
        params?.height = calculatedHeight

        dialog?.window?.apply {
            attributes = params
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupRecyclerViewOffers()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun setupListeners() {
        buttonClose.setOnClickListener(this)
        constraintAllOffers.setOnClickListener(this)
    }

    private fun setupRecyclerViewOffers() {
        val adapter = OfferAdapter2()
        val layoutManager = CarouselLayoutManager(RecyclerView.VERTICAL)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        layoutManager.maxVisibleItems = 2

        recyclerViewOffers.setHasFixedSize(true)
        recyclerViewOffers.adapter = adapter
        recyclerViewOffers.layoutManager = layoutManager
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.constraintAllOffers -> {
                val intent = Intent(requireContext(), IsolatedActivity::class.java).apply {
                    putExtra(ActivityStarter.ACTIVITY_FIRST_PAGE, AllOffersFragment::class.java)
                }

                startActivity(intent)
            }

            R.id.buttonClose -> {
                dismiss()
            }
        }
    }


}