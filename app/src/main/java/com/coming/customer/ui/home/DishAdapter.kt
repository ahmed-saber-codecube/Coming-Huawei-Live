package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.core.AppCommon
import com.coming.customer.core.AppPreferences
import com.coming.customer.data.pojo.ItemDetailItem
import com.coming.customer.util.loadCenterCrop
import kotlinx.android.synthetic.main.row_dish.view.*

class DishAdapter : ListAdapter<ItemDetailItem, DishAdapter.ItemViewholder>(DishDiffCallback()) {

    lateinit var appPreferences: AppPreferences
    internal var dishSelected: (ItemDetailItem) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        appPreferences = AppPreferences(parent.context)
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_dish, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.itemView.apply {
            getItem(position).isOutStoke?.let {
                if (it == "0") {
                    textViewOutOfStock.visibility = View.INVISIBLE
                } else {
                    when {
                        appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.EN -> {
                            textViewOutOfStock.rotation = -45f
                            textViewOutOfStock.visibility = View.VISIBLE
                        }
                        appPreferences.getString(AppCommon.LANGUAGE) == AppCommon.AR -> {
                            textViewOutOfStock.rotation = 45f
                            textViewOutOfStock.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
        holder.bind(getItem(position), dishSelected)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ItemDetailItem, dishSelected: (ItemDetailItem) -> Unit) = with(itemView) {
            item.itemImage?.let {
                // imageViewDish.loadUrlRoundedCorner(it, 0, 10)
                imageViewDish.loadCenterCrop(it)
            }

            item.itemName?.let {
                textViewDishName.text = it
            }
            item.itemPrice?.let {
                textViewDishPrice.text = it + " " + resources.getString(R.string.label_currency)
            }

            setOnClickListener {
                dishSelected(item)
            }
        }
    }
}

class DishDiffCallback : DiffUtil.ItemCallback<ItemDetailItem>() {
    override fun areItemsTheSame(oldItem: ItemDetailItem, newItem: ItemDetailItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemDetailItem, newItem: ItemDetailItem): Boolean {
        return false
    }
}
