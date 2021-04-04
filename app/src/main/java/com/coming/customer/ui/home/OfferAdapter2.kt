package com.coming.customer.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import kotlinx.android.synthetic.main.row_offer.view.*


//TODO: Implement actual binding
class OfferAdapter2 : RecyclerView.Adapter<OfferAdapter2.OfferViewHolder>() {

    override fun getItemCount(): Int = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_offer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {

    }

    inner class OfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() = with(itemView) {
            textViewPriceStriked.paintFlags = textViewPriceStriked.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}