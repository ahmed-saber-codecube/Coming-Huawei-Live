package com.coming.customer.ui.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.TransactionItem
import com.coming.customer.data.pojo.TransactionType
import com.coming.customer.util.DateTimeUtility
import com.coming.customer.util.twoDecimal
import kotlinx.android.synthetic.main.row_transaction.view.*
import java.text.SimpleDateFormat

class TransactionAdapter(private var dataSet: ArrayList<TransactionItem>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(transaction: TransactionItem) = with(itemView) {
            textViewTransactionName.text = transaction.tag

            transaction.insertdate?.let {
                try {
                    val date = SimpleDateFormat(DateTimeUtility.UTCFormatNode).parse(it)
                    textViewTransactionDate.text = DateTimeUtility.formatDateTransaction(date)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } ?: run {
                textViewTransactionDate.text = resources.getString(R.string.label_date_not_available)
            }

            transaction.amount?.let {
                textViewTransactionAmount.text = resources.getString(R.string.label_sar) + " " + it.twoDecimal()
            }

            if (transaction.type == TransactionType.C.toString()) {
                imageViewTransactionType.setImageResource(R.drawable.img_credit)
                textViewTransactionAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTransactionCredit))
            } else {
                imageViewTransactionType.setImageResource(R.drawable.img_debit)
                textViewTransactionAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTransactionDebit))
            }
        }
    }
}