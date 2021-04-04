package com.coming.customer.util

import com.coming.customer.core.AppCommon
import com.coming.customer.data.pojo.CreditCard
import com.coming.customer.data.pojo.Order
import com.coming.customer.data.pojo.OrderTime

class FakeDataUtil {
    companion object {
        /*fun getFakeStores(): ArrayList<Store> = ArrayList<Store>().apply {
            add(Store("McDonald's", "3.2 km ∙", "3.5", "Fast food, American food", StoreStatus.Open))
            add(Store("Pizza Hut", "4.2 km ∙", "4.5", "Italian food", StoreStatus.Closed))
            add(Store("Starbucks", "2 km ∙", "5", "Coffee", StoreStatus.Busy))
            add(Store("McDonald's", "3.2 km ∙", "3.5", "Fast food, American food", StoreStatus.Open))
            add(Store("Pizza Hut", "4.2 km ∙", "4.5", "Italian food", StoreStatus.Closed))
            add(Store("Starbucks", "2 km ∙", "5", "Coffee", StoreStatus.Busy))
            add(Store("McDonald's", "3.2 km ∙", "3.5", "Fast food, American food", StoreStatus.Open))
        }*/

        fun getFakeCreditCards(): ArrayList<CreditCard> = ArrayList<CreditCard>().apply {
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
            add(CreditCard("John Doe", "09/24", "1234"))
        }

        /*fun getFakeTransactions(): ArrayList<Transaction> = ArrayList<Transaction>().apply {
            add(Transaction("Agent", "19-1-2020", TransactionType.Debit))
            add(Transaction("Referral", "19-1-2020/20-1-2020", TransactionType.Credit))
            add(Transaction("Referral", "19-1-2020", TransactionType.Credit))
            add(Transaction("Agent", "19-1-2020", TransactionType.Debit))
            add(Transaction("Agent", "19-1-2020", TransactionType.Debit))
            add(Transaction("Referral", "19-1-2020", TransactionType.Credit))
        }*/

        /*fun getFakeInvoices(): ArrayList<Invoice> = ArrayList<Invoice>().apply {
            add(Invoice("Order #1A4J3K", "McDonald's", "15 Mar 2020"))
            add(Invoice("Order #1A41ZA", "Starbucks", "16 Mar 2020"))
            add(Invoice("Order #T344J3", "Starbucks", "17 Mar 2020"))
            add(Invoice("Order #N54J3K", "Pizza Hut", "18 Mar 2020"))
            add(Invoice("Order #1A2D3K", "Pizza Hut", "19 Mar 2020"))
            add(Invoice("Order #554D3K", "Burger King", "20 Mar 2020"))
            add(Invoice("Order #12353K", "McDonald's", "21 Mar 2020"))
            add(Invoice("Order #1AXX3K", "McDonald's", "22 Mar 2020"))
        }*/

        /*fun getFakeChat(): ArrayList<TimeStamp> = ArrayList<TimeStamp>().apply {
            add(SentMessage("Hello", "5:27PM"))
            add(ReceivedMessage("Hello, How can I help you?", "5:27PM"))
            add(SentMessage("I have problem with checkout", "5:28PM"))
            add(ReceivedMessage("Okay, Let me check", "5:28PM"))
            add(TimeStamp("5:40PM"))
            add(ReceivedMessage("Try now it should be fixed", "5:40PM"))
            add(SentMessage("Yes it works thanks!", "5:42PM"))
        }*/

        fun getFakeOrders(): ArrayList<Order> = ArrayList<Order>().apply {
            add(Order("KFC Super Bucket", "April 15 2020", "SAR 53", AppCommon.OrderStatus.ON_THE_WAY, OrderTime.Current))
            add(Order("Large Pizza", "April 16 2020", "SAR 10", AppCommon.OrderStatus.ON_THE_WAY, OrderTime.Current, 3))
            add(Order("2 Super Krunchy", "April 17 2020", "SAR 53", AppCommon.OrderStatus.DELIVERED, OrderTime.Previous))
            add(Order("Large Pizza", "April 18 2020", "SAR 53", AppCommon.OrderStatus.CACNEL, OrderTime.Previous))
        }

        /*fun getFakeCartDishes(): ArrayList<CartItem> = ArrayList<CartItem>().apply {
            add(CartItem("Cheese Burger", "SAR 20", "Medium\nTomatoes\nCheese", 1))
            add(CartItem("Pizza", "SAR 20", "Medium\nOnion\nOlive", 1))
            add(CartItem("Sandwich", "SAR 20", "Medium\nOnion\nLettuce", 1))
        }*/
    }
}