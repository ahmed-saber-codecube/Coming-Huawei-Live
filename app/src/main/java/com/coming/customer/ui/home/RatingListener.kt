package com.coming.customer.ui.home

interface RatingListener {
    fun onRating(driverRating: String, storeRating: String, doneRatingListener: RatingListener)
    fun onDoneRating()

}