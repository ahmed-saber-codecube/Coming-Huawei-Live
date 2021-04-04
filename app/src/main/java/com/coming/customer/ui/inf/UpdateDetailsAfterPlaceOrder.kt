package com.coming.customer.ui.inf

import com.coming.customer.data.pojo.PlaceOrder

interface UpdateDetailsAfterPlaceOrder {
    fun onUpdateDetailsAfterPlaceOrder(placeOrder: PlaceOrder)
}