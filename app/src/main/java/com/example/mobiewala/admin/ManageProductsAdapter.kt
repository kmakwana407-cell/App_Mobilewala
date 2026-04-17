package com.example.mobiewala.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.databinding.ItemManageProductBinding

class ManageProductsAdapter(
    private val onUpdateClick: (ProductEntity) -> Unit,
    private val onDeleteClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ManageProductsAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemManageProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.binding.updateButton.setOnClickListener { onUpdateClick(product) }
        holder.binding.deleteButton.setOnClickListener { onDeleteClick(product) }
    }

    inner class ProductViewHolder(val binding: ItemManageProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = "₹${product.price}"
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem == newItem
        }
    }
}