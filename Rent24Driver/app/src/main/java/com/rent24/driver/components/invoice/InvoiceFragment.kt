package com.rent24.driver.components.invoice

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.rent24.driver.R
import com.rent24.driver.components.invoice.adapter.InvoiceAdapter
import com.rent24.driver.databinding.InvoiceFragmentBinding

class InvoiceFragment : Fragment() {

    private lateinit var binding: InvoiceFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.invoice_fragment, container, false)
        binding = InvoiceFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = ViewModelProviders.of(this)
            .get(InvoiceViewModel::class.java)

        binding.model
            ?.getTotalAmount()!!
            .observe(this, Observer {})

        binding.invoiceRecyclerView
            .adapter = InvoiceAdapter()

        binding.invoiceRecyclerView
            .layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    companion object {
        fun newInstance() = InvoiceFragment()
    }
}
