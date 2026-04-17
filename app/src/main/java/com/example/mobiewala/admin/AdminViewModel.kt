package com.example.mobiewala.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.OrderEntity
import com.example.mobiewala.database.OrderRepository
import com.example.mobiewala.database.ProductEntity
import com.example.mobiewala.database.ProductRepository
import com.example.mobiewala.database.UserEntity
import com.example.mobiewala.database.UserRepository
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val productRepository: ProductRepository
    private val orderRepository: OrderRepository

    val allUsers: LiveData<List<UserEntity>>
    val allProducts: LiveData<List<ProductEntity>>
    val allOrders: LiveData<List<OrderEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        userRepository = UserRepository(database.userDao())
        productRepository = ProductRepository(database.productDao())
        orderRepository = OrderRepository(database.orderDao())

        allUsers = userRepository.allUsers
        allProducts = productRepository.allProducts
        allOrders = orderRepository.getAllOrders()
    }

    fun addProduct(
        name: String, brand: String, price: Double, storage: String, ram: String,
        battery: String, os: String, colors: String, imagePaths: String, otherDetails: String?
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                name = name, brand = brand, price = price, storage = storage, ram = ram,
                battery = battery, os = os, colors = colors, imagePaths = imagePaths,
                otherDetails = otherDetails
            )
            productRepository.insert(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.update(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            userRepository.delete(user)
        }
    }

    fun updateOrderStatus(order: OrderEntity, status: String) {
        viewModelScope.launch {
            val updatedOrder = order.copy(status = status)
            orderRepository.update(updatedOrder)
        }
    }
}