package com.coming.customer.ui.home.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.ui.base.BaseFragment
import com.coming.customer.ui.home.OfferAdapter2
import com.coming.customer.ui.isolated.IsolatedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_offers.*

@AndroidEntryPoint
class AllOffersFragment : BaseFragment(), View.OnClickListener {

    private val parent: IsolatedActivity
        get() = requireActivity() as IsolatedActivity

    override fun bindData() {
        setupListeners()
        setupRecyclerViewOffers()
    }

    override fun onResume() {
        super.onResume()
        parent.apply {
            showToolbar(true)
            setToolbarTitle(R.string.title_all_offers)
        }
    }

    private fun setupListeners() {

    }

    private fun setupRecyclerViewOffers() {
        val adapter = OfferAdapter2()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        recyclerViewOffers.adapter = adapter
        recyclerViewOffers.layoutManager = layoutManager
    }

    override fun onClick(view: View) {
        when (view.id) {

        }
    }


    override fun createLayout(): Int = R.layout.home_fragment_offers_2

}