package com.example.mobiewala.user

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiewala.database.OrderEntity
import com.example.mobiewala.databinding.ItemOrderBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(private val onCancelClick: (OrderEntity) -> Unit) :
    ListAdapter<OrderEntity, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(position + 1, order)
        
        if (order.status == "Pending") {
            holder.binding.cancelOrderButton.visibility = View.VISIBLE
            holder.binding.cancelOrderButton.setOnClickListener { onCancelClick(order) }
        } else {
            holder.binding.cancelOrderButton.visibility = View.GONE
        }
    }

    inner class OrderViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(index: Int, order: OrderEntity) {
            binding.itemNumber.text = index.toString()
            
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            binding.orderDateTextView.text = sdf.format(Date(order.orderDate))

            val backgroundColor = when (order.status) {
                "Pending" -> "#FFF3E0"
                "Completed" -> "#E8F5E9"
                "Canceled" -> "#FFEBEE"
                else -> "#F5F5F5"
            }
            binding.orderItemContainer.setBackgroundColor(Color.parseColor(backgroundColor))

            try {
                val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                val products: List<Map<String, Any>> = Gson().fromJson(order.productsJson, itemType)
                val firstProduct = products.firstOrNull()
                
                if (firstProduct != null) {
                    binding.productNameTextView.text = firstProduct["name"].toString()
                    val qty = (firstProduct["quantity"] as Double).toInt()
                    val color = firstProduct["color"] ?: "Default"
                    binding.productDetailsTextView.text = "Color: $color / Qty: $qty"
                    binding.productPriceTextView.text = "MRP ₹${String.format("%.2f", order.totalAmount)}"

                    // Load Image from the saved path
                    val imagePath = firstProduct["image"]?.toString()
                    if (!imagePath.isNullOrBlank()) {
                        Glide.with(binding.root.context)
                            .load(File(imagePath))
                            .into(binding.productImageView)
                    }
                }
            } catch (e: Exception) {
                binding.productNameTextView.text = "Order #${order.id}"
            }
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<OrderEntity>() {
    override fun areItemsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderEntity, newItem: OrderEntity): Boolean {
        return oldItem == newItem
    }
}