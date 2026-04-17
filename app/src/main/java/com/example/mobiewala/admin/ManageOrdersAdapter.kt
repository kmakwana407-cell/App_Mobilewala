package com.example.mobiewala.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiewala.database.OrderEntity
import com.example.mobiewala.databinding.ItemManageOrderBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManageOrdersAdapter(
    private val onUpdateStatusClick: (OrderEntity) -> Unit
) : ListAdapter<OrderEntity, ManageOrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemManageOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
        holder.binding.updateStatusButton.setOnClickListener { onUpdateStatusClick(order) }
    }

    inner class OrderViewHolder(val binding: ItemManageOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderEntity) {
            binding.orderIdTextView.text = "Order #${order.id}"
            binding.customerInfoTextView.text = "Name: ${order.customerName}\nMobile: ${order.customerMobile}"
            binding.addressTextView.text = "Address: ${order.deliveryAddress}"
            binding.orderTotalTextView.text = "Total: ₹${String.format("%.2f", order.totalAmount)}"
            binding.orderStatusTextView.text = order.status

            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            binding.orderDateTextView.text = sdf.format(Date(order.orderDate))

            // Parse and display products list
            try {
                val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                val products: List<Map<String, Any>> = Gson().fromJson(order.productsJson, itemType)
                val productsStringBuilder = StringBuilder()
                products.forEach { product ->
                    val name = product["name"]
                    val qty = (product["quantity"] as Double).toInt()
                    productsStringBuilder.append("• $name (Qty: $qty)\n")
                }
                binding.productsListTextView.text = productsStringBuilder.toString().trim()
            } catch (e: Exception) {
                binding.productsListTextView.text = "Error loading items"
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
}