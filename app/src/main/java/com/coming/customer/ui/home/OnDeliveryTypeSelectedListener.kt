package com.coming.customer.ui.home

import com.coming.customer.data.pojo.MerchantCategory

interface OnDeliveryTypeSelectedListener {
    fun onDeliverTypeSelected(adapterPosition: Int, merchantCategory: MerchantCategory)
}