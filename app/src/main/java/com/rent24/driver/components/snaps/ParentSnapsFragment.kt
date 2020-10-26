package com.rent24.driver.components.snaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.rent24.driver.R
import com.rent24.driver.databinding.ParentFragmentBinding

class ParentSnapsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)
            .get(ParentSnapsViewModel::class.java)
    }
    private lateinit var binding: ParentFragmentBinding
    private val snapsFragment by lazy { SnapsFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val status = arguments?.getInt("status")
        if (null != status) {
            val bundle = Bundle()
            bundle.putInt("status", status)
            snapsFragment.arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.parent_fragment, container, false)
        binding = ParentFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, snapsFragment)
            .commitNow()
    }

    companion object {

        fun newInstance() = ParentSnapsFragment()
    }
}