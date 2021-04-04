package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.SizeDetailItem
import kotlinx.android.synthetic.main.row_dish_size.view.*

class DishSizeAdapter : ListAdapter<SizeDetailItem, DishSizeAdapter.ItemViewHolder>(DiffCallback()) {
    internal var selectAmount: (String) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_dish_size, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.radioButtonSize.isChecked = getItem(position).isSelected
        holder.bind(getItem(position), selectAmount)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: SizeDetailItem, selectAmount: (String) -> Unit) = with(itemView) {
            radioButtonSize.text = item.sizeName
            textViewSizePrice.text = "(${resources.getString(R.string.label_currency)} ${item.sizePrice?.toFloat()})"

            radioButtonSize.setOnClickListener {
                currentList.forEach {
                    it.isSelected = false
                }
                item.isSelected = true
                item.sizePrice?.let { selectAmount(it) }
                notifyDataSetChanged()
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<SizeDetailItem>() {
        override fun areItemsTheSame(oldItem: SizeDetailItem, newItem: SizeDetailItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SizeDetailItem, newItem: SizeDetailItem): Boolean {
            return oldItem == newItem
        }
    }
}