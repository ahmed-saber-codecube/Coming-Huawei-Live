package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.CartDetailItem
import com.coming.customer.util.twoDecimal
import kotlinx.android.synthetic.main.row_cart_item_variable.view.*

class CartAdapter(private var dataSet: ArrayList<CartDetailItem>, var callBack: OnCartChangeListener) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_cart_item_variable, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    fun removeItem(index: Int) {
        dataSet.removeAt(index)
        notifyDataSetChanged()
    }

    fun getCartItemCount(): Int = dataSet.size

    inner class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: CartDetailItem) = with(itemView) {
            textViewDishName.text = item.itemDetail?.itemName

            var additionDetail = ""

            item.sizeDetail?.sizeName?.let {
                additionDetail += it.plus("\n")
            }

            item.additionDetail?.let {
                for (i in 0 until it.size) {
                    additionDetail += it[i].additionName.plus("\n")
                }
            }

            textViewDishCustomization.text = additionDetail
            textViewDishPrice.text = resources.getString(R.string.label_currency) + " " + item?.itemTotal?.twoDecimal()
            textViewDishQuantity.text = item.quantity.toString()
            textViewQuantity.text = "${item.quantity}x"
            buttonPlus.setOnClickListener {
                // preventTwoClick(it)
                item.quantity?.let {
                    var tempQty = it.toInt()
                    tempQty++
                    item.quantity = tempQty.toString()
                }
                notifyDataSetChanged()
                textViewDishQuantity.text = item.quantity.toString()
                textViewQuantity.text = "${item.quantity}x"
                callBack.onPlusUpdate(item)
            }

            buttonMinus.setOnClickListener {
                //preventTwoClick(it)
                if (item.quantity?.toInt()!! > 1) {
                    item.quantity?.let {
                        var tempQty = it.toInt()
                        tempQty--
                        item.quantity = tempQty.toString()
                    }
                    notifyDataSetChanged()
                    textViewDishQuantity.text = item.quantity.toString()
                    textViewQuantity.text = "${item.quantity} x"
                    callBack.onMinusUpdate(item)
                }
            }
            buttonDeleteAll.setOnClickListener {
                notifyDataSetChanged()
                callBack.onRemoveUpdate(item)
            }

            if (adapterPosition == dataSet.size - 1) {
                separator.visibility = View.GONE
            }
        }
    }

    interface OnCartChangeListener {
        fun onPlusUpdate(cartDetailItem: CartDetailItem)
        fun onMinusUpdate(cartDetailItem: CartDetailItem)
        fun onRemoveUpdate(cartDetailItem: CartDetailItem)
    }

    fun preventTwoClick(view: View) {
        view.isEnabled = false
        view.postDelayed({ view.isEnabled = true }, 400)
    }
}