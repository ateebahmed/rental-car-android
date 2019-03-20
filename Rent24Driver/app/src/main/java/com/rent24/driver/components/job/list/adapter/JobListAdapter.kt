package com.rent24.driver.components.job.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rent24.driver.R
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.databinding.JobListItemBinding

class JobListAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<JobListAdapter.ViewHolder>() {

    private var trips: List<JobTrip>? = null
    private lateinit var binding: JobListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.job_list_item,
            parent, false) as JobListItemBinding
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trips?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trips?.get(position)!!)
        holder.itemView
            .setOnClickListener {
                listener.onClick(it, position)
            }
    }

    fun setTrips(trips: List<JobTrip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: JobListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: JobTrip) {
            binding.model = trip
            binding.executePendingBindings()
        }
    }

    interface OnClickListener {
        fun onClick(view: View, position: Int)
    }
}