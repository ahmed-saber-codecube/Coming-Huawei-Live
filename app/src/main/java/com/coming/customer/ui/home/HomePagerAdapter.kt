package com.coming.customer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.HomeMerchantSlider
import com.coming.customer.util.loadUrlRoundedCorner
import kotlinx.android.synthetic.main.row_view_pager.view.*

class HomePagerAdapter(private var imageList: ArrayList<HomeMerchantSlider>, val onSliderClicked: OnSliderClicked) : RecyclerView.Adapter<HomePagerAdapter.ImageViewHolder>() {

    override fun getItemCount(): Int = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_view_pager, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(homeMerchantSlider: HomeMerchantSlider) = with(itemView) {

            imageViewViewPager.clipToOutline = true

            homeMerchantSlider.backgroundImage?.let {
                imageViewViewPager.loadUrlRoundedCorner(it, 0, 10)
            }

            /* imageViewViewPager.setOnClickListener {
                 onSliderClicked.onMerchantOpen(homeMerchantSlider)
             }*/
        }
    }

    interface OnSliderClicked {
        fun onMerchantOpen(homeMerchantSlider: HomeMerchantSlider)
    }
}