package com.example.mobiewala.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobiewala.database.CartEntity
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.databinding.ItemCartBinding
import java.io.File

class CartAdapter(
    private val onRemoveClick: (CartEntity) -> Unit,
    private val onUpdateQuantity: (CartEntity, Int) -> Unit,
    private val onUpdateColor: (CartEntity, String) -> Unit
) : ListAdapter<Pair<CartEntity, ProductEntity>, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (cartItem, product) = getItem(position)
        holder.bind(position + 1, cartItem, product)
        
        holder.binding.removeButton.setOnClickListener { onRemoveClick(cartItem) }
        holder.binding.plusButton.setOnClickListener { onUpdateQuantity(cartItem, cartItem.quantity + 1) }
        holder.binding.minusButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                onUpdateQuantity(cartItem, cartItem.quantity - 1)
            }
        }
    }

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(index: Int, cartItem: CartEntity, product: ProductEntity) {
            binding.itemNumber.text = index.toString()
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = "MRP ₹${product.price}"
            binding.productRamStorage.text = "${product.ram} / ${product.storage}"
            binding.quantityTextView.text = cartItem.quantity.toString()

            val firstImage = product.imagePaths.split(",").filter { it.isNotBlank() }.firstOrNull()
            if (firstImage != null) {
                Glide.with(binding.root.context).load(File(firstImage)).into(binding.productImageView)
            }

            // Setup Color Spinner
            val colors = product.colors.split(",").map { it.trim() }.filter { it.isNotBlank() }
            if (colors.isNotEmpty()) {
                val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, colors)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.colorSpinner.adapter = adapter

                // Set previously selected color
                val selectedIndex = colors.indexOf(cartItem.selectedColor)
                if (selectedIndex != -1) {
                    binding.colorSpinner.setSelection(selectedIndex)
                }

                binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        val newColor = colors[pos]
                        if (newColor != cartItem.selectedColor) {
                            onUpdateColor(cartItem, newColor)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<Pair<CartEntity, ProductEntity>>() {
    override fun areItemsTheSame(
        oldItem: Pair<CartEntity, ProductEntity>,
        newItem: Pair<CartEntity, ProductEntity>
    ): Boolean {
        return oldItem.first.id == newItem.first.id
    }

    override fun areContentsTheSame(
        oldItem: Pair<CartEntity, ProductEntity>,
        newItem: Pair<CartEntity, ProductEntity>
    ): Boolean {
        return oldItem == newItem
    }
}