package com.example.mobiewala.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val price: Double,
    val storage: String,
    val ram: String,
    val battery: String,
    val os: String,
    val colors: String,
    val imagePaths: String,
    val otherDetails: String?
)