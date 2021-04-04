package com.coming.customer.ui.manager

import android.os.Bundle
import android.util.Pair
import android.view.View
import com.coming.customer.ui.base.BaseFragment
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class FragmentNavigationFactory @Inject constructor(val fragmentHandler: FragmentHandler) {

    private var fragment: BaseFragment? = null
    private var tag: String? = null

    fun <T : BaseFragment> make(aClass: Class<T>): FragmentActionPerformer<T> {
        return make(FragmentFactory.getFragment(aClass))
    }

    fun <T : BaseFragment> make(fragment: T?): FragmentActionPerformer<T> {
        this.fragment = fragment
        this.tag = fragment!!.javaClass.simpleName
        return Provider(fragment, this)
    }

    private inner class Provider<T : BaseFragment>
        (private val fragment: T, private val navigationFactory: FragmentNavigationFactory) : FragmentActionPerformer<T> {
        internal var sharedElements: List<Pair<View, String>>? = null

        override fun add(toBackStack: Boolean) {
            navigationFactory.fragmentHandler.openFragment(
                fragment,
                FragmentHandler.Option.ADD, toBackStack, tag!!, sharedElements
            )
        }

        override fun add(toBackStack: Boolean, tag: String) {
            navigationFactory.fragmentHandler.openFragment(
                fragment,
                FragmentHandler.Option.ADD, toBackStack, tag, sharedElements
            )
        }

        override fun replace(toBackStack: Boolean) {
            navigationFactory.fragmentHandler.openFragment(
                fragment,
                FragmentHandler.Option.REPLACE, toBackStack, tag!!, sharedElements
            )
        }

        override fun replace(toBackStack: Boolean, tag: String) {
            navigationFactory.fragmentHandler.openFragment(
                fragment,
                FragmentHandler.Option.REPLACE, toBackStack, tag, sharedElements
            )
        }


        override fun setBundle(bundle: Bundle): FragmentActionPerformer<*> {
            fragment.arguments = bundle
            return this
        }

        override fun addSharedElements(sharedElements: List<Pair<View, String>>): FragmentActionPerformer<*> {
            this.sharedElements = sharedElements
            return this
        }

        override fun clearHistory(tag: String?): FragmentActionPerformer<*> {
            navigationFactory.fragmentHandler.clearFragmentHistory(tag)
            return this
        }

        override fun hasData(passable: Passable<T>): FragmentActionPerformer<*> {
            passable.passData(fragment)
            return this
        }


    }
}