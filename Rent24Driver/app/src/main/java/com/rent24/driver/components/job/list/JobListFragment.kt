package com.rent24.driver.components.job.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.rent24.driver.R
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.components.job.list.adapter.JobListAdapter
import com.rent24.driver.databinding.JobListTabFragmentBinding

class JobListFragment : Fragment() {

    private lateinit var binding: JobListTabFragmentBinding
    private var tab: Int = 0
    private val viewModel: JobListViewModel by lazy {
        ViewModelProviders.of(this)
            .get(JobListViewModel::class.java)
    }
    private val observer: Observer<List<JobTrip>> by lazy {
        Observer<List<JobTrip>> {
            (binding.jobList
                .adapter as JobListAdapter)
                .setTrips(it)
        }
    }
    private lateinit var listener: OnClickListener
    private val onClickListener: JobListAdapter.OnClickListener = object: JobListAdapter.OnClickListener {
        override fun onClick(view: View, position: Int) {
            openDetailFragment(position)
        }
    }

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

        binding.jobList
            .adapter = JobListAdapter(onClickListener)
        binding.jobList
            .layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        performTabOperation(tab)
    }

    private fun performTabOperation(tab: Int) {
        val data = getObservableData(tab)
        if (tab == 0) viewModel.updateScheduledTrips() else viewModel.updateCompletedTrips()
        data.observe(this, observer)
    }

    private fun getObservableData(tab: Int): LiveData<List<JobTrip>> {
        return if (tab == 0) viewModel.getScheduledTrips() else viewModel.getCompletedTrips()
    }

    private fun openDetailFragment(position: Int) {
        val data = getObservableData(tab).value!![position].id
        listener.showDetailFragment(data)
    }

    companion object {
        fun newInstance(listener: OnClickListener): JobListFragment {
            val f = JobListFragment()
            f.listener = listener
            return f
        }
    }

    interface OnClickListener {
        fun showDetailFragment(id: Int)
    }
}
