package com.rent24.driver.components.job

import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.rent24.driver.R
import com.rent24.driver.components.job.list.JobListFragment
import com.rent24.driver.databinding.JobFragmentBinding

private val TAG = JobFragment::class.java.name

class JobFragment : Fragment() {

    private lateinit var viewModel: JobViewModel
    private lateinit var binding: JobFragmentBinding
    private val onTabSelectedListener by lazy {
        object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {
                Log.d(TAG, "position ${p0?.position}")
                when(p0?.position) {
                    0 -> replaceFragment(0)
                    1 -> replaceFragment(1)
                }
            }

        }
    }
    private val scheduledFragment by lazy { createFragment(0) }
    private val completedFragment by lazy { createFragment(1) }
    private lateinit var onClickListener: JobListFragment.OnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val tabLayout = inflater.inflate(R.layout.job_fragment, container, false) as ConstraintLayout
        binding = JobFragmentBinding.bind(tabLayout)
        return tabLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        replaceFragment(0)
        binding.tabs
            .addOnTabSelectedListener(onTabSelectedListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(JobViewModel::class.java)
    }

    private fun replaceFragment(tab: Int): Boolean {
        val fragment = if (tab == 0) scheduledFragment else completedFragment

        childFragmentManager.beginTransaction()
            .replace(R.id.job_tab_content, fragment)
            .commitNow()
        return true
    }

    private fun createFragment(tab: Int): Fragment {
        val fragment = JobListFragment.newInstance(onClickListener)
        val bundle = Bundle()
        bundle.putInt("tab", tab)
        fragment.arguments = bundle
        return fragment
    }

    companion object {
        fun newInstance(listener: JobListFragment.OnClickListener): JobFragment {
            val fragment = JobFragment()
            fragment.onClickListener = listener
            return fragment
        }
    }
}
