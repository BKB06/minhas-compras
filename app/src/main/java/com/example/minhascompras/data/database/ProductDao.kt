package com.example.minhascompras.data.database

import androidx.room.*
import com.example.minhascompras.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE purchaseId = :purchaseId")
    fun getProductsByPurchaseId(purchaseId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE purchaseId = :purchaseId")
    suspend fun deleteProductsByPurchaseId(purchaseId: Long)

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT DISTINCT name FROM products ORDER BY name")
    fun getAllProductNames(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<Product>>
    
    // Queries para controle de validade
    @Query("SELECT * FROM products WHERE expirationDate IS NOT NULL AND expirationDate <= :dateThreshold ORDER BY expirationDate ASC")
    fun getExpiringProducts(dateThreshold: Long): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE expirationDate IS NOT NULL ORDER BY expirationDate ASC")
    fun getAllProductsWithExpiration(): Flow<List<Product>>
    
    // Queries para histórico de produtos (para cálculo de consumo médio)
    @Query("SELECT * FROM products WHERE name = :productName ORDER BY id ASC")
    fun getProductHistory(productName: String): Flow<List<Product>>
    
    // Query para produtos com estoque baixo
    @Query("SELECT * FROM products WHERE currentStock IS NOT NULL AND averageConsumptionPerDay IS NOT NULL ORDER BY currentStock ASC")
    fun getProductsWithStock(): Flow<List<Product>>
}