package com.ateebahmed.freelance.driver.components.job.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.api.login.response.JobTrip
import com.ateebahmed.freelance.driver.databinding.JobListItemBinding

class JobListAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<JobListAdapter.ViewHolder>() {

    private var trips: List<JobTrip> = emptyList()
    private lateinit var binding: JobListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.job_list_item,
            parent, false) as JobListItemBinding
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trips[position])
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