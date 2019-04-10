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
import com.rent24.driver.components.job.list.CompletedJobListFragment
import com.rent24.driver.components.job.list.ScheduledJobListFragment
import com.rent24.driver.databinding.JobFragmentBinding

private val TAG = JobFragment::class.java.name

class JobFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(JobViewModel::class.java)
    }
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
    private val scheduledFragment by lazy { ScheduledJobListFragment.newInstance(onClickListener) }
    private val completedFragment by lazy { CompletedJobListFragment.newInstance(onClickListener) }
    private lateinit var onClickListener: CompletedJobListFragment.OnClickListener

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

    private fun replaceFragment(tab: Int) {
        val fragment = if (tab == 0) scheduledFragment else completedFragment

        childFragmentManager.beginTransaction()
            .replace(R.id.job_tab_content, fragment)
            .commitNow()
    }

    companion object {
        fun newInstance(listener: CompletedJobListFragment.OnClickListener): JobFragment {
            val fragment = JobFragment()
            fragment.onClickListener = listener
            return fragment
        }
    }
}
