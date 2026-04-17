package com.example.mobiewala.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.databinding.ItemProductBinding
import java.io.File

class ProductAdapter(private val onProductClick: (ProductEntity) -> Unit) :
    ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
    ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.itemView.setOnClickListener { onProductClick(product) }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = "₹${String.format("%.2f", product.price)}"
            // Get the first image from the comma-separated list
            val firstImage = product.imagePaths.split(",").filter { it.isNotBlank() }.firstOrNull()
            if (firstImage != null) {
                Glide.with(binding.root.context).load(File(firstImage)).into(binding.productImageView)
            }
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
    override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity):
    Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity):
    Boolean {
        return oldItem == newItem
    }
}