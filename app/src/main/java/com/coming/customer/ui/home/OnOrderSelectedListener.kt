package com.coming.customer.ui.home

import com.coming.customer.data.pojo.OrderList

interface OnOrderSelectedListener {
    fun onOrderSelected(order: OrderList)
    fun onOfferSelected()
    // fun onDeliveredOrderClicked(orderList: OrderList)
}