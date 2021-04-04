package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.DrawerOption
import kotlinx.android.synthetic.main.row_nav_drawer.view.*

class NavDrawerAdapter(private var dataSet: ArrayList<DrawerOption>, var callback: OnDrawerOptionSelectedListener) : RecyclerView.Adapter<NavDrawerAdapter.OptionViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_nav_drawer, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(option: DrawerOption) = with(itemView) {
            val drawable = ContextCompat.getDrawable(itemView.context, option.icon)
            textViewOptionTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
            textViewOptionTitle.text = option.title

            if (option.newCount > 0) {
                textViewNotificationBadge.visibility = View.VISIBLE
                textViewNotificationBadge.text = option.newCount.toString()
            } else {
                textViewNotificationBadge.visibility = View.GONE
            }

            if (adapterPosition == dataSet.size) {
                separator.visibility = View.GONE
            }

            itemView.setOnClickListener {
                callback.onDrawerOptionSelected(option.type)
            }
        }
    }
}
