package com.example.minhascompras

import android.app.Application
import com.example.minhascompras.data.database.AppDatabase
import com.example.minhascompras.data.repository.ShoppingRepository

class MinhasComprasApplication : Application() {
    
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    val repository: ShoppingRepository by lazy { 
        ShoppingRepository(database.purchaseDao(), database.productDao()) 
    }
}