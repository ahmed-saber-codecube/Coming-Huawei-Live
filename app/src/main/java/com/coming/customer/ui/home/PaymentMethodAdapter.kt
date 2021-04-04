package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.PaymentMethod
import kotlinx.android.synthetic.main.row_payment_method.view.*

class PaymentMethodAdapter(private var dataSet: ArrayList<PaymentMethod>, var callback: Callback?) : RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_payment_method, parent, false)
        return PaymentMethodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class PaymentMethodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(method: PaymentMethod) = with(itemView) {
            textViewPaymentMethod.text = method.title

            val icon = ContextCompat.getDrawable(itemView.context, method.icon)
            textViewPaymentMethod.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

            textViewPaymentMethod.setOnClickListener {

                callback?.onClick(adapterPosition)
            }

            if (adapterPosition == dataSet.size - 1) {
                separator.visibility = View.GONE
            }
        }
    }

    interface Callback {
        fun onClick(position: Int)
    }

}


