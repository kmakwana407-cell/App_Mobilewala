package com.example.mobiewala.database

import androidx.lifecycle.LiveData

class OrderRepository(private val orderDao: OrderDao) {

    fun getOrders(userId: Long): LiveData<List<OrderEntity>> {
        return orderDao.getOrders(userId)
    }

    fun getAllOrders(): LiveData<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }

    suspend fun insert(order: OrderEntity) {
        orderDao.insert(order)
    }

    suspend fun update(order: OrderEntity) {
        orderDao.update(order)
    }
}