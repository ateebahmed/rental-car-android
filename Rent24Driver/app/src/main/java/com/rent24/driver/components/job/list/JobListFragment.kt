package com.rent24.driver.components.job.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rent24.driver.R

class JobListFragment : Fragment() {

    companion object {
        fun newInstance() = JobListFragment()
    }

    private lateinit var viewModel: JobListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.job_list_tab_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(JobListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
