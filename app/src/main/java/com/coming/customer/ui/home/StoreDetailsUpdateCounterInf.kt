package com.coming.customer.ui.home

import com.coming.customer.data.pojo.CartDetails
import com.coming.customer.data.pojo.PromoCode
import com.coming.customer.ui.inf.UpdateDetailsAfterPlaceOrder

interface StoreDetailsUpdateCounterInf {
    fun onUpdateCounter(updateCounterValue: String)
    fun onAuthenticated()
    fun onPayNow(amount: String, cartDetails: CartDetails?, deliveryAddress: String, promoCode: PromoCode?, deliveryInstructions: String, orderNote: String, updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder, subTotal: String, cardType: String, useWallet: Boolean)
}

interface HomeUpdateCounterInf {
    fun onUpdateCounter(updateCounterValue: String)
    fun onAuthenticated()
    fun onPayNow(orderId: String, amount: String, deliveryAddress: String, promoCode: PromoCode?, deliveryInstructions: String, orderNote: String, updateDetailsAfterPlaceOrder: UpdateDetailsAfterPlaceOrder?, subTotal: String, cardType: String, useWallet: Boolean)
}
