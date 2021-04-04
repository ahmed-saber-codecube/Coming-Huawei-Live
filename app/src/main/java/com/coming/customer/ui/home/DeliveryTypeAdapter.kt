package com.coming.customer.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.MerchantCategory
import com.coming.customer.util.loadUrlWithActualSizeNoCenterCrop
import kotlinx.android.synthetic.main.row_delivery_type.view.*

class DeliveryTypeAdapter(private var dataSet: ArrayList<MerchantCategory>, var callback: OnDeliveryTypeSelectedListener) : RecyclerView.Adapter<DeliveryTypeAdapter.TypeViewHolder>() {

    var currantBGColor = 0

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_delivery_type, parent, false)
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    //TODO: Bg Color
    inner class TypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(merchantCategory: MerchantCategory) = with(itemView) {

//            if (adapterPosition == 0) {
//                imageViewType.visibility = View.VISIBLE
//                imageViewType.setImageResource(R.drawable.ic_bag)
//                imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypePersonalShopper))
//            } else {
//                currantBGColor++
            if (dataSet[adapterPosition].image != null) {
                imageViewType.visibility = View.VISIBLE
                dataSet[adapterPosition].image?.let { imageViewType.loadUrlWithActualSizeNoCenterCrop(it) }
            }
            imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(dataSet[adapterPosition].background_color))
            textViewDeliveryType.setTextColor(ColorStateList.valueOf(Color.parseColor(dataSet[adapterPosition].font_color)))
            merchantCategory.merchantCategoryName?.let {
                textViewDeliveryType.text = it
            }
//            }
//            if (adapterPosition == 0) {
//                imageViewType.setImageResource(R.drawable.ic_bag)
//                imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypePersonalShopper))
//            } else {
//                currantBGColor++
//                if (currantBGColor == 1 ) {
//                    imageViewType.setImageResource(R.drawable.ico_restaurant)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeRestaurant))
//                } else if (currantBGColor == 2) {
//                    imageViewType.setImageResource(R.drawable.ic_food)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeGrocery))
//                } else if (currantBGColor == 3) {
//                    imageViewType.setImageResource(R.drawable.ic_health)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeHealth))
//                } else if (currantBGColor == 4) {
//                    imageViewType.setImageResource(R.drawable.ic_grill)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeGrill))
//                }else if (currantBGColor == 5) {
//                    imageViewType.setImageResource(R.drawable.ic_cafe)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeCofee))
//                }
//                else {
//                    imageViewType.setImageResource(R.drawable.ic_food)
//                    imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorTypeGrocery))
//                    currantBGColor = 0
//                }
//
//            }
////            imageViewTypeBg.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, deliveryType.color))
//
////            textViewDeliveryType.text = deliveryType.title
//
//            merchantCategory.merchantCategoryName?.let {
//                textViewDeliveryType.text = it
//            }

            itemView.setOnClickListener {
                callback.onDeliverTypeSelected(adapterPosition, merchantCategory)
            }
        }
    }
}