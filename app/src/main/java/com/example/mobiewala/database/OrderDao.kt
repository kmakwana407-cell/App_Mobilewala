package com.example.mobiewala.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: OrderEntity)

    @Update
    suspend fun update(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrders(userId: Long): LiveData<List<OrderEntity>>

    @Query("SELECT * FROM orders")
    fun getAllOrders(): LiveData<List<OrderEntity>>
}