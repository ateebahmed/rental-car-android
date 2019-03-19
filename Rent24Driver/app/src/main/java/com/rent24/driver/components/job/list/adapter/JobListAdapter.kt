package com.rent24.driver.components.job.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rent24.driver.R
import com.rent24.driver.api.login.response.JobTrip
import com.rent24.driver.databinding.ScheduleJobItemBinding

class JobListAdapter : RecyclerView.Adapter<JobListAdapter.ViewHolder>() {

    private var trips: List<JobTrip>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.schedule_job_item,
            parent, false))
    }

    override fun getItemCount(): Int {
        return trips?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trips?.get(position)!!)
    }

    fun setTrips(trips: List<JobTrip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ScheduleJobItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: JobTrip) {
            binding.model = trip
            binding.executePendingBindings()
        }
    }
}