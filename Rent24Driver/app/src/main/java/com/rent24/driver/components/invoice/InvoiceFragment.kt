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
    private val viewModel: InvoiceViewModel by lazy {
        ViewModelProviders.of(this)
            .get(InvoiceViewModel::class.java)
    }
    private var jobId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jobId = arguments?.getInt("jobId") ?: jobId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.invoice_fragment, container, false)
        binding = InvoiceFragmentBinding.bind(layout)
        binding.lifecycleOwner = this
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.invoiceRecyclerView
            .adapter = InvoiceAdapter()

        binding.invoiceRecyclerView
            .layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        viewModel.callInvoiceApi(jobId)

        viewModel.getTotalAmount()
            .observe(this, Observer { binding.totalAmount.text = it.toString() })

        viewModel.getEntries()
            .observe(this, Observer {
                (binding.invoiceRecyclerView
                    .adapter as InvoiceAdapter)
                    .setEntries(it)
            })
    }

    companion object {
        fun newInstance() = InvoiceFragment()
    }
}
