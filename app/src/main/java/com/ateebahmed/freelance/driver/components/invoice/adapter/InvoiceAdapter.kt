package com.ateebahmed.freelance.driver.components.invoice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.api.login.response.InvoiceEntry
import com.ateebahmed.freelance.driver.databinding.InvoiceListItemBinding

class InvoiceAdapter : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {

    private var entries: List<InvoiceEntry>? = null
    private lateinit var binding: InvoiceListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.invoice_list_item, parent,
            false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return entries?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries?.get(position)!!)
    }

    fun setEntries(entries: List<InvoiceEntry>) {
        this.entries = entries
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: InvoiceListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: InvoiceEntry) {
            binding.model = entry
            binding.executePendingBindings()
        }
    }
}