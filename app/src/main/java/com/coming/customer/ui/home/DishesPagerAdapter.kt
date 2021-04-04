package com.coming.customer.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.coming.customer.ui.home.fragment.DishesFragment

class DishesPagerAdapter(fm: FragmentManager, var pageCount: ArrayList<DishesFragment>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return pageCount[position]
    }

    override fun getCount(): Int {
        return pageCount.size
    }
//    override fun getItemCount(): Int = pageCount
//    override fun createFragment(position: Int): Fragment = DishesFragment(it[i])
}