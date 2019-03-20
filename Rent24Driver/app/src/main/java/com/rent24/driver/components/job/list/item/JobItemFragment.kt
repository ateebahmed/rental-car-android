package com.rent24.driver.components.job.list.item

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.rent24.driver.R
import com.rent24.driver.databinding.JobItemFragmentBinding

class JobItemFragment : Fragment() {

    private lateinit var viewModel: JobItemViewModel
    private var data = 0
    private lateinit var binding: JobItemFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            .observe(this, Observer { binding.model = it })
    }

    companion object {
        fun newInstance() = JobItemFragment()
    }
}
