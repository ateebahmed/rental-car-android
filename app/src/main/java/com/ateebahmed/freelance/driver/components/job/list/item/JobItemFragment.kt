package com.ateebahmed.freelance.driver.components.job.list.item

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.components.home.HomeViewModel
import com.ateebahmed.freelance.driver.databinding.JobItemFragmentBinding

class JobItemFragment : Fragment() {

    private lateinit var viewModel: JobItemViewModel
    private var data = 0
    private lateinit var binding: JobItemFragmentBinding
    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)
            .get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.showLoadingProgressBar(true)
        data = arguments?.getInt("id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.job_item_fragment, container, false)
        binding = JobItemFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(JobItemViewModel::class.java)

        viewModel.updateModel(data)

        viewModel.getModel()
            .observe(this, Observer {
                homeViewModel.showLoadingProgressBar(false)
                binding.model = it
            })
    }

    companion object {
        fun newInstance() = JobItemFragment()
    }
}
