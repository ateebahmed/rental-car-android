package com.rent24.driver.components.snaps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rent24.driver.R
import com.rent24.driver.databinding.SnapItemBinding
import kotlinx.android.synthetic.main.snap_item.view.snap_image

class SnapsListAdapter : RecyclerView.Adapter<SnapsListAdapter.ViewHolder>() {

    private lateinit var binding: SnapItemBinding
    private val imageCount = 10
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.snap_item, parent,
            false)
        context = parent.context
        binding.root.layoutParams = RecyclerView.LayoutParams(200, 300)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load("https://picsum.photos/200/300/?random")
            .fitCenter()
            .into(holder.itemView.snap_image)
        holder.bind()
    }

    class ViewHolder(private val binding: SnapItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.snapImage.setOnClickListener {
                binding.snapImage.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                binding.snapImage.scaleType = ImageView.ScaleType.FIT_XY
            }
            binding.executePendingBindings()
        }
    }
}