package com.example.minhascompras.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Purchase::class,
            parentColumns = ["id"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["purchaseId"])]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val purchaseId: Long,
    val name: String,
    val quantity: Int = 1,
    val unit: String = "un",
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val category: String = "Outros",
    val isInStock: Boolean = true,
    val stockQuantity: Int = 0
)