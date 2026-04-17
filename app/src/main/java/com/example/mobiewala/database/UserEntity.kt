package com.example.mobiewala.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    var password: String,
    val role: String = "USER",
    var birthDate: String? = null,
    var mobileNumber: String? = null,
    var imagePath: String? = null
)