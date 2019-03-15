package com.rent24.driver.components.snaps

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rent24.driver.R

class SnapsFragment : Fragment() {

    private lateinit var viewModel: SnapsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.snaps_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(SnapsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = SnapsFragment()
    }
}
