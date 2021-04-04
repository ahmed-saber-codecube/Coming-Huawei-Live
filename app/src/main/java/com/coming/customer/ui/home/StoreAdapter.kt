package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.Store
import com.coming.customer.data.pojo.StoreStatus
import com.coming.customer.util.loadUrlRoundedCorner
import com.coming.customer.util.twoDecimal
import kotlinx.android.synthetic.main.row_provider.view.*

class StoreAdapter : ListAdapter<Store, StoreAdapter.ItemViewholder>(DiffCallback()) {
    internal var storeSelected: (Store) -> Unit = { _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_provider, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.itemView.apply {
            when (getItem(position).status) {
                StoreStatus.Open.toString() -> {
                    holder.itemView.textViewStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.colorStatusOpen)
                    )
                    textViewStatus.text = resources.getString(R.string.status_open)
                }
                StoreStatus.Busy.toString() -> {
                    textViewStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.colorStatusBusy)
                    )
                    textViewStatus.text = resources.getString(R.string.status_busy)
                }
                StoreStatus.Close.toString() -> {
                    textViewStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.colorStatusClosed)
                    )
                    textViewStatus.text = resources.getString(R.string.status_close)
                }
            }
        }
        holder.bind(getItem(position), storeSelected)
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Store, storeSelected: (Store) -> Unit) = with(itemView) {
            textViewProviderName.text = item.username?.trim()
            rating_bar_merchant.rating = item.rating?.toFloat()!!
            textViewProviderVariety.text = item.categoryName
            textViewDistance.text = item.distance?.toDouble()?.let { it.toString().twoDecimal() } + " " + textViewDistance.context.resources.getString(R.string.label_km)
            imageViewProviderLogo.loadUrlRoundedCorner(item.profileImage.toString(), 0, 10)
            textViewStatus.text = item.status.toString()

            setOnClickListener {
                storeSelected(item)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Store>() {
    override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
        return false
    }
}