package com.example.mobiewala.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.CartEntity
import com.example.mobiewala.database.CartRepository
import com.example.mobiewala.database.OrderEntity
import com.example.mobiewala.database.OrderRepository
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.database.ProductRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val productRepository: ProductRepository
    private val cartRepository: CartRepository
    private val orderRepository: OrderRepository

    val allProducts: LiveData<List<ProductEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        productRepository = ProductRepository(database.productDao())
        cartRepository = CartRepository(database.cartDao())
        orderRepository = OrderRepository(database.orderDao())
        allProducts = productRepository.allProducts
    }

    fun getCartItems(userId: Long): LiveData<List<CartEntity>> {
        return cartRepository.getCartItems(userId)
    }

    fun addToCart(userId: Long, productId: Long, quantity: Int) {
        viewModelScope.launch {
            val cartItem = CartEntity(userId = userId, productId = productId, quantity = quantity)
            cartRepository.insert(cartItem)
        }
    }

    fun removeFromCart(cartItem: CartEntity) {
        viewModelScope.launch {
            cartRepository.delete(cartItem)
        }
    }

    fun placeOrder(userId: Long, name: String, mobile: String, address: String, productsJson: String, totalAmount: Double) {
        viewModelScope.launch {
            val order = OrderEntity(
                userId = userId,
                customerName = name,
                customerMobile = mobile,
                deliveryAddress = address,
                productsJson = productsJson,
                totalAmount = totalAmount
            )
            orderRepository.insert(order)
            cartRepository.clearCart(userId)
        }
    }

    fun getOrderHistory(userId: Long): LiveData<List<OrderEntity>> {
        return orderRepository.getOrders(userId)
    }
}