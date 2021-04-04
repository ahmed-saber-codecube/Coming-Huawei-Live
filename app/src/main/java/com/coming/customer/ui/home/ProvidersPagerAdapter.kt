package com.coming.customer.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.coming.customer.ui.home.fragment.ProvidersFragment

class ProvidersPagerAdapter(fm: FragmentManager, var providersFragmentList: ArrayList<ProvidersFragment>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    //    override fun getItemCount(): Int = providersFragmentList.size
//    override fun createFragment(position: Int): Fragment = providersFragmentList[position]
    override fun getItem(position: Int): Fragment {
        return providersFragmentList[position]
    }

    override fun getCount(): Int {
        return providersFragmentList.size
    }
}