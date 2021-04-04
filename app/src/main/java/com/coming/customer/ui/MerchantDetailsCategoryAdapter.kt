package com.coming.customer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.CategoryDataItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_menu_row.*


class MerchantDetailsCategoryAdapter(val eventList: ArrayList<CategoryDataItem>, val onItemClick: OnItemClick) : RecyclerView.Adapter<MerchantDetailsCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_menu_row, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = eventList[position]

        item?.categoryName?.let {
            holder.textViewCategoryname.text = it
        }

        if (item.isSelectedCategory) {
            holder.linearLayoutRootView.setBackgroundResource(R.drawable.bg_rounded_rect_20dp_selected)
            holder.textViewCategoryname.setTextColor(holder.textViewCategoryname.resources.getColor(R.color.colorWhite))
        } else {
            holder.linearLayoutRootView.setBackgroundResource(R.drawable.bg_rounded_rect_20dp_unselected)
            holder.textViewCategoryname.setTextColor(holder.textViewCategoryname.resources.getColor(R.color.colorChipInactive))
        }

//        item.categoryImage?.let {
//            holder.imageViewCategory.visibility = View.VISIBLE
//            holder.imageViewCategory.loadUrlRoundedCorner(it, 0, 3)
//        } ?: run {
//            holder.imageViewCategory.visibility = View.GONE
//        }


        holder.linearLayoutRootView.setOnClickListener {
            onItemClick.onItemClicked(position)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    }

    interface OnItemClick {
        fun onItemClicked(position: Int)
    }
}