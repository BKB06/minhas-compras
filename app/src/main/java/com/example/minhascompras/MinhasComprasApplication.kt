package com.example.minhascompras

import android.app.Application
import com.example.minhascompras.data.database.AppDatabase

class MinhasComprasApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}