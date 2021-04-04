package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.GetOrderDetailsItemDetailItem
import kotlinx.android.synthetic.main.row_cart_item_static.view.*

//TODO: Implement binding and actual data set
class OrderDetailAdapter(private var dataSet: ArrayList<GetOrderDetailsItemDetailItem>) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    //TODO: Return dataSet.size
    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_cart_item_static, parent, false)
        return OrderDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class OrderDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(orderDetail: GetOrderDetailsItemDetailItem) = with(itemView) {
            separator.visibility = View.GONE
            textViewDishName.text = orderDetail.itemDetail?.itemName
            textViewQuantity.text = orderDetail.quantity + "x"
            var additionDetail = ""

            orderDetail.sizeDetail?.sizeName?.let {
                additionDetail += it.plus("\n")
            }

            orderDetail.additionDetail.let {
                for (i in 0 until it?.size!!) {
                    additionDetail += it[i].additionName.plus("\n")
                }
            }
            textViewDishCustomization.text = additionDetail

        }


    }
}
