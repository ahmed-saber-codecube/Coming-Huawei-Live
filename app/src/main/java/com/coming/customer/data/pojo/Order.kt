package com.coming.customer.data.pojo

import java.io.Serializable

data class Order(var name: String, var date: String, var amount: String, var status: String, var time: OrderTime, var offerCount: Int = 0) : Serializable