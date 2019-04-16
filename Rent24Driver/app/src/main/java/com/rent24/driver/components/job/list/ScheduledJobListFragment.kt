package com.rent24.driver.components.job.list

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class ScheduledJobListFragment : CompletedJobListFragment() {

    override val viewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(ScheduledJobListViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getActiveJobId()
            .observe(this, Observer {
                homeViewModel.setActiveJobId(it)
            })
    }

    companion object {
        fun newInstance(listener: OnClickListener): ScheduledJobListFragment {
            val f = ScheduledJobListFragment()
            f.listener = listener
            return f
        }
    }
}
