package com.rent24.driver.components.snaps

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rent24.driver.R
import com.rent24.driver.components.snaps.adapter.SnapsListAdapter
import com.rent24.driver.databinding.SnapLayoutBinding
import com.rent24.driver.databinding.SnapsFragmentBinding

class SnapsFragment : Fragment() {

    private lateinit var viewModel: SnapsViewModel
    private lateinit var binding: SnapsFragmentBinding
    private lateinit var startLayout: SnapLayoutBinding
    private lateinit var endLayout: SnapLayoutBinding
    private lateinit var receiptLayout: SnapLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = inflater.inflate(R.layout.snaps_fragment, container, false)
        binding = SnapsFragmentBinding.bind(layout)
        startLayout = binding.snapStartLayout
        endLayout = binding.snapEndLayout
        receiptLayout = binding.snapReceiptLayout
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)

        startLayout.headerTextview.text = "Start Snaps"
        startLayout.snapRecyclerview.adapter = SnapsListAdapter()
        startLayout.snapRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        endLayout.headerTextview.text = "End Snaps"
        endLayout.snapRecyclerview.adapter = SnapsListAdapter()
        endLayout.snapRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        receiptLayout.headerTextview.text = "Receipt Snaps"
        receiptLayout.snapRecyclerview.adapter = SnapsListAdapter()
        receiptLayout.snapRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
