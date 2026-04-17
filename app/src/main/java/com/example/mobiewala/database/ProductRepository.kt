package com.example.mobiewala.database

import androidx.lifecycle.LiveData

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: LiveData<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun insert(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun update(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun delete(product: ProductEntity) {
        productDao.delete(product)
    }

    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }
}