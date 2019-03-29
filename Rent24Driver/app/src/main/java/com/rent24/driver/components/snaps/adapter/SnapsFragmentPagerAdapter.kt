package com.rent24.driver.components.snaps.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.rent24.driver.components.snaps.list.SnapsTabFragment

class SnapsFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val tabTitles by lazy { arrayOf("Start", "End", "Receipt") }
    private val startTabFragment by lazy { SnapsTabFragment.newInstance() }
    private val endTabFragment by lazy { SnapsTabFragment.newInstance() }
    private val receiptTabFragment by lazy { SnapsTabFragment.newInstance() }
    private val tabFragments = arrayOf(startTabFragment, endTabFragment, receiptTabFragment)

    override fun getItem(position: Int): Fragment {
        return tabFragments[position]
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}