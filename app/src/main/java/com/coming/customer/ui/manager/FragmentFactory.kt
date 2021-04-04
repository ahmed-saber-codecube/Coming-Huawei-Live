package com.coming.customer.ui.manager

import com.coming.customer.ui.base.BaseFragment


object FragmentFactory {

    fun <T : BaseFragment> getFragment(aClass: Class<T>): T? {

        try {
            return aClass.newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }
}
