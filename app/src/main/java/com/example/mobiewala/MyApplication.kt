package com.example.mobiewala

import android.app.Application
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        seedDatabase()
    }

    private fun seedDatabase() {
        val database = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = database.userDao()
            val admin = userDao.getUserByEmail("admin@mobiewala.com")
            if (admin == null) {
                userDao.insert(
                    UserEntity(
                        name = "Admin",
                        email = "admin@mobiewala.com",
                        password = "admin123",
                        role = "ADMIN"
                    )
                )
            }
        }
    }
}