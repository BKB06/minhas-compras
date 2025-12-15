package com.example.minhascompras.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.minhascompras.data.model.Product
import com.example.minhascompras.data.model.Purchase

@Database(
    entities = [Purchase::class, Product::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration de versão 1 para 2: adiciona campos de validade e estoque
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adiciona novos campos à tabela products
                database.execSQL("ALTER TABLE products ADD COLUMN expirationDate INTEGER")
                database.execSQL("ALTER TABLE products ADD COLUMN currentStock REAL")
                database.execSQL("ALTER TABLE products ADD COLUMN averageConsumptionPerDay REAL")
                database.execSQL("ALTER TABLE products ADD COLUMN lastStockUpdate INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minhas_compras_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}