package com.coming.customer.ui.manager

import android.util.Pair
import android.view.View
import androidx.annotation.UiThread
import com.coming.customer.ui.base.BaseFragment


@UiThread
interface FragmentHandler {

    /**
     * @param baseFragment   Fragment to open
     * @param option
     * @param isToBackStack
     * @param tag
     * @param sharedElements
     */
    fun openFragment(baseFragment: BaseFragment, option: Option, isToBackStack: Boolean, tag: String, sharedElements: List<Pair<View, String>>?)

    /**
     * @param fragmentToShow Fragment to show
     * @param fragmentToHide array of fragments to hide
     */
    fun showFragment(fragmentToShow: BaseFragment, vararg fragmentToHide: BaseFragment)

    fun clearFragmentHistory(tag: String?)


    enum class Option {
        ADD, REPLACE, SHOW, HIDE
    }
}