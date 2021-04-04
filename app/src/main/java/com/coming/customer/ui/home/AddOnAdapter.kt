package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.AdditionDetailItem
import kotlinx.android.synthetic.main.row_addition.view.*

class AddOnAdapter(var dataSet: ArrayList<AdditionDetailItem>, val onItemClick: OnItemClick) : RecyclerView.Adapter<AddOnAdapter.DishSizeViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishSizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_addition, parent, false)
        return DishSizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishSizeViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class DishSizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(additionDetailItem: AdditionDetailItem) = with(itemView) {
            additionTittle.text = additionDetailItem.categoryName
            if (additionDetailItem.isOptional?.toInt() == 1) {

                textViewLabelUpto.text = additionDetailItem.maxCount.let { resources.getString(R.string.text_up_to_optional) + " " + it }

            } else if (additionDetailItem.isOptional?.toInt() == 0) {
                textViewLabelUpto.text = additionDetailItem.maxCount.let { resources.getString(R.string.text_up_to_mandatory) + " " + it }
                var count = 0
                for (i in 0 until (additionDetailItem.items?.size!!)) {
                    //     if(count < additionDetailItem.maxCount!!.toInt()) {
                    if (count < 1) {
                        additionDetailItem.items[i].isSelected = true
                        count++
                    }
                }
            }
            val adapter = additionDetailItem.items?.let { additionDetailItem.maxCount?.let { it1 -> AddOnItemsAdapter(it, onItemClick, it1.toInt()) } }
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerViewAdditionItems.adapter = adapter
            recyclerViewAdditionItems.layoutManager = layoutManager
        }
    }

    interface OnItemClick {
        fun onExtraItemClicked()
    }
}