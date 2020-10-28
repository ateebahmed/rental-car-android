package com.ateebahmed.freelance.driver.components.job

import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.components.job.list.CompletedJobListFragment
import com.ateebahmed.freelance.driver.components.job.list.ScheduledJobListFragment
import com.ateebahmed.freelance.driver.databinding.JobFragmentBinding

private val TAG = JobFragment::class.java.name
private const val SCHEDULED_JOB_FRAGMENT = "scheduled"
private const val COMPLETED_JOB_FRAGMENT = "completed"

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
                    0 -> replaceFragment(SCHEDULED_JOB_FRAGMENT)
                    1 -> replaceFragment(COMPLETED_JOB_FRAGMENT)
                }
            }

        }
    }
    private lateinit var onClickListener: CompletedJobListFragment.OnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val tabLayout = inflater.inflate(R.layout.job_fragment, container, false) as ConstraintLayout
        binding = JobFragmentBinding.bind(tabLayout)
        return tabLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        replaceFragment(SCHEDULED_JOB_FRAGMENT)
        binding.tabs
            .addOnTabSelectedListener(onTabSelectedListener)
    }

    private fun replaceFragment(fragmentTag: String) {
        val fragment = childFragmentManager.findFragmentByTag(fragmentTag) ?: createNewFragmentInstance(fragmentTag)

        childFragmentManager.beginTransaction()
            .replace(R.id.job_tab_content, fragment, fragmentTag)
            .setPrimaryNavigationFragment(fragment)
            .commitNow()
    }

    private fun createNewFragmentInstance(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            SCHEDULED_JOB_FRAGMENT -> ScheduledJobListFragment.newInstance(onClickListener)
            COMPLETED_JOB_FRAGMENT -> CompletedJobListFragment.newInstance(onClickListener)
            else -> ScheduledJobListFragment.newInstance(onClickListener)
        }
    }

    companion object {
        fun newInstance(listener: CompletedJobListFragment.OnClickListener): JobFragment {
            val fragment = JobFragment()
            fragment.onClickListener = listener
            return fragment
        }
    }
}
