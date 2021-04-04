package com.coming.customer.ui.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.InvoiceList
import com.coming.customer.util.DateTimeUtility
import kotlinx.android.synthetic.main.row_invoice.view.*
import java.text.SimpleDateFormat

class InvoiceAdapter(private var dataSet: ArrayList<InvoiceList>) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class InvoiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(invoice: InvoiceList) = with(itemView) {

            textViewInvoiceName.text = invoice.userDetail?.username
            textViewInvoiceId.text = invoice.orderDetail?.orderId

            invoice.orderDetail?.insertdate?.let {
                try {
                    val date = SimpleDateFormat(DateTimeUtility.UTCFormatNode).parse(it)
                    textViewInvoiceDate.text = DateTimeUtility.formatDateInvoice(date)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } ?: run {
                textViewInvoiceDate.text = resources.getString(R.string.label_date_not_available)
            }


        }
    }
}