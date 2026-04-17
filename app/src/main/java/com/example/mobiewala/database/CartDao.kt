package com.example.mobiewala.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    suspend fun insert(cartItem: CartEntity)

    @Delete
    suspend fun delete(cartItem: CartEntity)

    @Query("SELECT * FROM cart WHERE userId = :userId")
    fun getCartItems(userId: Long): LiveData<List<CartEntity>>

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Long)
}