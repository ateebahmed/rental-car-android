package com.rent24.driver.components.snaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rent24.driver.R
import com.rent24.driver.databinding.SnapItemBinding
import kotlinx.android.synthetic.main.snap_item.view.*

class SnapsListAdapter : RecyclerView.Adapter<SnapsListAdapter.ViewHolder>() {

    private lateinit var binding: SnapItemBinding
    private val imageCount = 10
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.snap_item, parent,
            false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = imageCount

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load("https://picsum.photos/1080/?random")
            .into(holder.itemView.snap_image)
        holder.bind()
    }

    class ViewHolder(private val binding: SnapItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.executePendingBindings()
        }
    }
}