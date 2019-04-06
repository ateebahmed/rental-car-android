package com.rent24.driver.components.snaps.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rent24.driver.R
import com.rent24.driver.components.snaps.adapter.SnapsListAdapter
import com.rent24.driver.databinding.SnapLayoutBinding

class SnapsTabFragment : Fragment() {

    private lateinit var binding: SnapLayoutBinding
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        key = arguments?.getString("key", "")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.snap_layout, container, false)
        binding = SnapLayoutBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProviders.of(this)
            .get(SnapsTabViewModel::class.java)
        binding.snapRecyclerview.adapter = SnapsListAdapter()
        viewModel.getSnaps(key)
            .observe(this, Observer {
                (binding.snapRecyclerview.adapter as SnapsListAdapter).setImages(it)
            })
        binding.snapRecyclerview.layoutManager =
            GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
    }

    companion object {

        fun newInstance() = SnapsTabFragment()
    }
}