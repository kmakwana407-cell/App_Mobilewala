package com.example.mobiewala.database

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val allUsers: LiveData<List<UserEntity>> = userDao.getAllUsers()

    suspend fun insert(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun update(user: UserEntity) {
        userDao.update(user)
    }

    suspend fun delete(user: UserEntity) {
        userDao.delete(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}