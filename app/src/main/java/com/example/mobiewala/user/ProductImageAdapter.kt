package com.example.mobiewala.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiewala.databinding.ItemProductImageBinding
import java.io.File

class ProductImageAdapter(private val imagePaths: List<String>) :
    RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemProductImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val path = imagePaths[position]
        Glide.with(holder.itemView.context).load(File(path)).into(holder.binding.sliderImageView)
    }

    override fun getItemCount(): Int = imagePaths.size

    inner class ImageViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}