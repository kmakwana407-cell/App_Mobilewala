package com.example.mobiewala.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [UserEntity::class, ProductEntity::class, CartEntity::class, OrderEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mobiewala_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Using INSERT OR IGNORE to prevent issues if email is unique
                        db.execSQL(
                            "INSERT OR IGNORE INTO users (name, email, password, role) " +
                            "VALUES ('Kishan', 'kishan123@gmail.com', 'kishan123', 'ADMIN')"
                        )
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}