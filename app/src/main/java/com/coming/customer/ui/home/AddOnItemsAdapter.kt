package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.Element
import kotlinx.android.synthetic.main.row_dish_addon.view.*

class AddOnItemsAdapter(var dataSet: ArrayList<Element>, val onItemClick: AddOnAdapter.OnItemClick, var maxCount: Int) : RecyclerView.Adapter<AddOnItemsAdapter.DishSizeViewHolder>() {

    override fun getItemCount(): Int = dataSet.size
    private var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishSizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_dish_addon, parent, false)
        return DishSizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishSizeViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class DishSizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(element: Element) = with(itemView) {
            textViewSizePrice.text = "(${resources.getString(R.string.label_currency)} ${element.additionPrice?.toFloat()})"
            if (element.isSelected) {
                count++
                multipleAddition.isChecked = element.isSelected
                onItemClick.onExtraItemClicked()
            }
            if (maxCount >= 1) {
                multipleAddition.text = element.additionName
                multipleAddition.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        count++
                    } else if (!isChecked) {
                        count--
                    }

                    if (count > maxCount) {
                        Toast.makeText(context, resources.getString(R.string.add_up_to) + maxCount, Toast.LENGTH_SHORT).show();
                        multipleAddition.isChecked = false
                        count--
                    } else {
                        element.isSelected = multipleAddition.isChecked
                        onItemClick.onExtraItemClicked()
                    }
                }

            } else if (maxCount == 0) {
                multipleAddition.text = element.additionName

                multipleAddition.setOnCheckedChangeListener { _, isChecked ->
                    element.isSelected = isChecked
                    onItemClick.onExtraItemClicked()
                }
            }
        }
    }

}

