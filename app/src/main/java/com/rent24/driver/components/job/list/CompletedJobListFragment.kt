package com.rent24.driver.components.job.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rent24.driver.R
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.components.home.HomeViewModel
import com.rent24.driver.components.job.list.adapter.JobListAdapter
import com.rent24.driver.components.job.list.item.JobItemFragment
import com.rent24.driver.databinding.JobListTabFragmentBinding

private const val JOB_DETAIL_FRAGMENT = "detail"

open class CompletedJobListFragment : Fragment() {

    protected lateinit var binding: JobListTabFragmentBinding
    protected val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
    }
    protected val observer: Observer<List<JobTrip>> by lazy {
        Observer<List<JobTrip>> {
            (binding.jobList
                .adapter as JobListAdapter)
                .setTrips(it)
            homeViewModel.showLoadingProgressBar(false)
        }
    }
    protected lateinit var listener: OnClickListener
    protected val onClickListener: JobListAdapter.OnClickListener by lazy {
        object: JobListAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                openDetailFragment(position)
            }
        }
    }
    protected open val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(CompletedJobListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.job_list_tab_fragment, container, false) as ConstraintLayout
        binding = JobListTabFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.showLoadingProgressBar(true)
        binding.jobList
            .adapter = JobListAdapter(onClickListener)
        binding.jobList
            .layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.getTrips()
            .observe(this, observer)
    }

    private fun openDetailFragment(position: Int) {
        val data = viewModel.getTrips()
            .value!![position].id
        listener.showDetailFragment(data ?: 0)
    }

    companion object {
        fun newInstance(listener: OnClickListener): CompletedJobListFragment {
            val f = CompletedJobListFragment()
            f.listener = listener
            return f
        }
    }

    interface OnClickListener {
        fun showDetailFragment(id: Int)
    }
}