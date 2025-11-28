package com.example.minhascompras.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date = Date(),
    val totalValue: Double = 0.0,
    val storeName: String = "",
    val notes: String = ""
)