package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.GetOfferList
import com.coming.customer.util.loadUrlRoundedCorner
import kotlinx.android.synthetic.main.row_offer_simple.view.*

//TODO: Implement Actual Binding
class OfferAdapter(private val getOfferList: ArrayList<GetOfferList>) : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    override fun getItemCount(): Int = getOfferList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_offer_simple, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(getOfferList[position])

    }

    inner class OfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(getOfferList: GetOfferList) = with(itemView) {
            getOfferList?.image?.let {
                imageViewViewPager.loadUrlRoundedCorner(it, 0, 20)
            }
        }
    }
}