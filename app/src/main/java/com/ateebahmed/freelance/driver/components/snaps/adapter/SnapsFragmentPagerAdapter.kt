package com.ateebahmed.freelance.driver.components.snaps.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ateebahmed.freelance.driver.components.snaps.list.SnapsTabFragment

class SnapsFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val tabTitles by lazy { arrayOf("Start", "End", "Receipt") }
    private val startTabFragment by lazy { getFragment("pickup") }
    private val endTabFragment by lazy { getFragment("dropoff") }
    private val receiptTabFragment by lazy { getFragment("receipt") }
    private val tabFragments = arrayOf(startTabFragment, endTabFragment, receiptTabFragment)

    override fun getItem(position: Int): Fragment {
        return tabFragments[position]
    }

    override fun getCount() = 3

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]

    fun updateTab(position: Int) {
        (getItem(position) as SnapsTabFragment).updateSnaps()
    }

    private fun getFragment(key: String): Fragment {
        val fragment = SnapsTabFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("key", key)
        fragment.arguments = bundle
        return fragment
    }
}