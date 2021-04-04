package com.coming.customer.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.CategoryList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_menu_row.*


class HomeCategoryAdapter(val eventList: ArrayList<CategoryList>, val onItemClick: OnItemClick) : RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_menu_row, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = eventList.get(position)

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
//            holder.imageViewCategory.loadUrlRoundedCorner(it, 0,1)
//        } ?: run {
//            holder.imageViewCategory.visibility = View.GONE
//        }
//
//        if (position == 0) {
//            holder.imageViewCategory.visibility = View.GONE
//        }


        holder.linearLayoutRootView.setOnClickListener {
            //  if (NetworkConnection.isOnline(context)){
            onItemClick.onItemClicked(position)
            notifyDataSetChanged()
//            }else{
//                showToastShort(context.resources.getString(R.string.connection_exception))
//            }

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

    fun showToastShort(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}