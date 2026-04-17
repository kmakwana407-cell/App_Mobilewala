package com.example.mobiewala.database

import androidx.lifecycle.LiveData

class CartRepository(private val cartDao: CartDao) {

    fun getCartItems(userId: Long): LiveData<List<CartEntity>> {
        return cartDao.getCartItems(userId)
    }

    suspend fun insert(cartItem: CartEntity) {
        cartDao.insert(cartItem)
    }

    suspend fun delete(cartItem: CartEntity) {
        cartDao.delete(cartItem)
    }

    suspend fun clearCart(userId: Long) {
        cartDao.clearCart(userId)
    }
}