package com.rent24.driver.components.job.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.rent24.driver.R
import com.rent24.driver.components.job.list.adapter.JobListAdapter
import com.rent24.driver.databinding.JobListTabFragmentBinding

class JobListFragment : Fragment() {

    private lateinit var binding: JobListTabFragmentBinding
    private var tab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tab = arguments?.getInt("tab")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.job_list_tab_fragment, container, false) as ConstraintLayout
        binding = JobListTabFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.model = ViewModelProviders.of(this)
            .get(JobListViewModel::class.java)

        binding.jobList
            .adapter = JobListAdapter()
        binding.jobList
            .layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        performTabOperation(tab)
    }

    private fun performTabOperation(tab: Int) {
        if (tab == 0) {
            binding.model
                ?.updateScheduledTrips()

            binding.model
                ?.getScheduledTrips()!!
                .observe(this, Observer {
                    (binding.jobList
                        .adapter as JobListAdapter)
                        .setTrips(it)
                })
        } else {
            binding.model
                ?.updateCompletedTrips()

            binding.model
                ?.getCompletedTrips()!!
                .observe(this, Observer {
                    (binding.jobList
                        .adapter as JobListAdapter)
                        .setTrips(it)
                })
        }
    }

    companion object {
        fun newInstance() = JobListFragment()
    }
}
