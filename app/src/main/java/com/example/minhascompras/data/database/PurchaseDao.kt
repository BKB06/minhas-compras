package com.example.minhascompras.data.database

import androidx.room.*
import com.example.minhascompras.data.model.Purchase
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY date DESC")
    fun getAllPurchases(): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE id = :id")
    suspend fun getPurchaseById(id: Long): Purchase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long

    @Update
    suspend fun updatePurchase(purchase: Purchase)

    @Delete
    suspend fun deletePurchase(purchase: Purchase)

    @Query("SELECT SUM(totalValue) FROM purchases")
    fun getTotalSpent(): Flow<Double?>

    @Query("SELECT * FROM purchases WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getPurchasesByDateRange(startDate: Long, endDate: Long): Flow<List<Purchase>>
}