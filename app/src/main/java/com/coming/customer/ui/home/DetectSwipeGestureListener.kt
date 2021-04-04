package com.coming.customer.ui.home

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent


open class DetectSwipeGestureListener : SimpleOnGestureListener() {


    /* This method is invoked when a swipe gesture happened. */
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        // Get swipe delta value in x axis.
        val deltaX = e1.x - e2.x

        // Get swipe delta value in y axis.
        val deltaY = e1.y - e2.y

        val deltaXAbs = Math.abs(deltaX)
        val deltaYAbs = Math.abs(deltaY)

        if (deltaXAbs >= MIN_SWIPE_DISTANCE_X && deltaXAbs <= MAX_SWIPE_DISTANCE_X) {
            if (deltaX > 0) {
                onSwipeLeft()
            } else {
                onSwipeRight()
            }
        }
        if (deltaYAbs >= MIN_SWIPE_DISTANCE_Y && deltaYAbs <= MAX_SWIPE_DISTANCE_Y) {
            if (deltaY > 0) {
                onSwipeUp()
            } else {
                onSwipeDown()
            }
        }
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return true
    }

    open fun onSwipeLeft() {

    }

    open fun onSwipeRight() {

    }

    open fun onSwipeUp() {

    }

    open fun onSwipeDown() {

    }


    // Invoked when double tap screen.
    override fun onDoubleTap(e: MotionEvent): Boolean {
        return true
    }

    companion object {
        // Minimal x and y axis swipe distance.
        private const val MIN_SWIPE_DISTANCE_X = 250
        private const val MIN_SWIPE_DISTANCE_Y = 250

        // Maximal x and y axis swipe distance.
        private const val MAX_SWIPE_DISTANCE_X = 1000
        private const val MAX_SWIPE_DISTANCE_Y = 1000
    }
}