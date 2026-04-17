package com.example.mobiewala.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val customerName: String,
    val customerMobile: String,
    val deliveryAddress: String,
    val productsJson: String,
    val totalAmount: Double,
    val orderDate: Long = System.currentTimeMillis(),
    var status: String = "Pending"
)