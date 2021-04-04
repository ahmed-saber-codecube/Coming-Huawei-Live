package com.coming.customer.ui.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.GetCardList
import com.coming.customer.util.loadUrlWithActualSize
import kotlinx.android.synthetic.main.row_credit_card.view.*

class CreditCardAdapter(private var dataSet: ArrayList<GetCardList>, val onCardActivity: OnCardActivity) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_credit_card, parent, false)
        return CreditCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class CreditCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(getCardList: GetCardList) = with(itemView) {

            getCardList.originalCard?.let {
                if (it.length > 4) {
                    textViewCardNum1.text = it.substring(it.length - 4)
                }
            }

            getCardList.cardholdername?.let {
                textViewHolderName.text = it
            }

            getCardList.brand?.let {
                imageViewCardLogo.loadUrlWithActualSize(it, 0)
            }

            getCardList.expMonth?.let {
                textViewExpiryDate.text = it + "/" + getCardList.expYear
            }

            if (adapterPosition == dataSet.size - 1) {
                textViewAddCard.visibility = View.VISIBLE
            } else {
                textViewAddCard.visibility = View.INVISIBLE
            }
            textViewAddCard.setOnClickListener { onCardActivity.onAddCard() }
            textViewRemove.setOnClickListener { onCardActivity.onRemoveCard(getCardList) }

        }
    }

    interface OnCardActivity {
        fun onRemoveCard(getCardList: GetCardList)
        fun onAddCard()
    }

}