package com.rent24.driver.components.snaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.rent24.driver.R
import com.rent24.driver.components.snaps.adapter.SnapsFragmentPagerAdapter
import com.rent24.driver.components.snaps.dialog.CategoryListDialogFragment
import com.rent24.driver.databinding.SnapsFragmentBinding

class SnapsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)
    }
    private lateinit var binding: SnapsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = inflater.inflate(R.layout.snaps_fragment, container, false)
        binding = SnapsFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.snapsViewPager.adapter = SnapsFragmentPagerAdapter(childFragmentManager)
        binding.snapsTabLayout.setupWithViewPager(binding.snapsViewPager)
        binding.snapsAddButton
            .setOnClickListener {
                CategoryListDialogFragment.newInstance()
                    .show(childFragmentManager, "Category")
            }
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
