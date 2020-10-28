package com.ateebahmed.freelance.driver.components.snaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ateebahmed.freelance.driver.R
import com.ateebahmed.freelance.driver.databinding.SnapItemBinding
import kotlinx.android.synthetic.main.snap_item.view.snap_image

class SnapsListAdapter : RecyclerView.Adapter<SnapsListAdapter.ViewHolder>() {

    private lateinit var binding: SnapItemBinding
    private lateinit var context: Context
    private val images by lazy { ArrayList<String>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.snap_item, parent,
            false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(images[position])
            .into(holder.itemView.snap_image)
        holder.bind()
    }

    fun setImages(images: List<String>) {
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: SnapItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.executePendingBindings()
        }
    }
}