package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.data.pojo.OrderList
import com.coming.customer.data.pojo.OrderTime
import com.coming.customer.util.DateTimeUtility
import com.coming.customer.util.loadUrlRoundedCorner
import com.coming.customer.util.twoDecimal
import kotlinx.android.synthetic.main.row_order.view.*
import java.text.SimpleDateFormat

class PastOrdersAdapter(private var dataSet: ArrayList<OrderList>, var callBack: OnOrderSelectedListener) : RecyclerView.Adapter<PastOrdersAdapter.OrderViewHolder>() {

//    private var filteredDataSet: ArrayList<Order> = dataSet.filter {
//        it.time == OrderTime.Current
//    } as ArrayList<Order>


    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    fun filterOrdersBy(time: OrderTime) {
//        filteredDataSet = dataSet.filter {
//            it.time == time
//        } as ArrayList<Order>

        notifyDataSetChanged()
    }

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(orderList: OrderList) = with(itemView) {
            textViewProviderName.text = orderList.merchantName
//            textViewOrderDate.text = orderList.date

            orderList.date?.let {
                try {
                    val date = SimpleDateFormat(DateTimeUtility.YYYYMMDD).parse(it)
                    textViewOrderDate.text = DateTimeUtility.formatDateMonth(date)
                } catch (e: Exception) {

                }
            }


//            textViewOrderPrice.text = resources.getString(R.string.label_currency) + " " + orderList.grandTotal

            orderList.grandTotal?.let {
                textViewOrderPrice.text = it.twoDecimal()
            }


            orderList.profileImage?.let { imageViewOrder.loadUrlRoundedCorner(it, 0, 10) }



            textViewOrderStatus.text = orderList.status

            when (orderList.status) {
//                AppCommon.OrderStatus.ON_THE_WAY -> {
//                    textViewOrderStatus.text = textViewOrderStatus.context.resources.getString(R.string.order_status_on_the_way)
//                    textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorStatusOpen))
//                    textViewOfferBadge.visibility = View.INVISIBLE
//                    itemView.setOnClickListener {
//                        callBack.onOrderSelected(orderList)
//                    }
//                }
//                AppCommon.OrderStatus.READY -> {
//                    textViewOrderStatus.text = textViewOrderStatus.context.resources.getString(R.string.order_status_ready)
//                    textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorStatusOpen))
//                    textViewOfferBadge.visibility = View.INVISIBLE
//                    itemView.setOnClickListener {
//                        callBack.onOrderSelected(orderList)
//                    }
//                }
                /* AppCommon.OrderStatus.READY -> {
                     textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTextBlue))
                     textViewOfferBadge.visibility = View.VISIBLE
                     textViewOrderStatus.setOnClickListener { callBack.onOfferSelected() }
                 }*/
                AppCommon.OrderStatus.DELIVERED -> {
                    textViewOrderStatus.text = textViewOrderStatus.context.resources.getString(R.string.order_status_delivered)
                    textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorStatusOpen))
                    textViewOfferBadge.visibility = View.INVISIBLE

                    orderList.status?.let {
                        //do not translate this string
                        if (it == "Delivered" && orderList.type.equals("Normal")) {
                            materialCardViewRoot.setOnClickListener {
//                                callBack.onDeliveredOrderClicked(orderList)
                                callBack.onOrderSelected(orderList)

                            }
                        }
                    }
                }
                AppCommon.OrderStatus.CACNEL -> {
                    textViewOrderStatus.text = textViewOrderStatus.context.resources.getString(R.string.order_status_cancel)
                    textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.ColorOutOfStock))
                    textViewOfferBadge.visibility = View.INVISIBLE
                    itemView.setOnClickListener {
                        callBack.onOrderSelected(orderList)
                    }
                }
                AppCommon.OrderStatus.REJECT -> {
                    textViewOrderStatus.text = textViewOrderStatus.context.resources.getString(R.string.order_status_reject)
                    textViewOrderStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.ColorOutOfStock))
                    textViewOfferBadge.visibility = View.INVISIBLE
                    itemView.setOnClickListener {
                        callBack.onOrderSelected(orderList)
                    }
                }
                else -> {
//                    textViewOrderStatus.visibility = View.INVISIBLE
                }
            }
        }
    }
}