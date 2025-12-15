package com.example.minhascompras.data.repository

import com.example.minhascompras.data.database.ProductDao
import com.example.minhascompras.data.database.PurchaseDao
import com.example.minhascompras.data.model.Product
import com.example.minhascompras.data.model.Purchase
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(
    private val purchaseDao: PurchaseDao,
    private val productDao: ProductDao
) {
    // Purchase operations
    fun getAllPurchases(): Flow<List<Purchase>> = purchaseDao.getAllPurchases()

    suspend fun getPurchaseById(id: Long): Purchase? = purchaseDao.getPurchaseById(id)

    suspend fun insertPurchase(purchase: Purchase): Long = purchaseDao.insertPurchase(purchase)

    suspend fun updatePurchase(purchase: Purchase) = purchaseDao.updatePurchase(purchase)

    suspend fun deletePurchase(purchase: Purchase) = purchaseDao.deletePurchase(purchase)

    fun getTotalSpent(): Flow<Double?> = purchaseDao.getTotalSpent()

    fun getPurchasesByDateRange(startDate: Long, endDate: Long): Flow<List<Purchase>> =
        purchaseDao.getPurchasesByDateRange(startDate, endDate)

    // Product operations
    fun getProductsByPurchaseId(purchaseId: Long): Flow<List<Product>> =
        productDao.getProductsByPurchaseId(purchaseId)

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun insertProducts(products: List<Product>) = productDao.insertProducts(products)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun deleteProductsByPurchaseId(purchaseId: Long) =
        productDao.deleteProductsByPurchaseId(purchaseId)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    fun getAllProductNames(): Flow<List<String>> = productDao.getAllProductNames()

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category)
    
    // Controle de Validade
    fun getExpiringProducts(dateThreshold: Long): Flow<List<Product>> =
        productDao.getExpiringProducts(dateThreshold)
    
    fun getAllProductsWithExpiration(): Flow<List<Product>> =
        productDao.getAllProductsWithExpiration()
    
    // Controle de Estoque
    fun getProductHistory(productName: String): Flow<List<Product>> =
        productDao.getProductHistory(productName)
    
    fun getProductsWithStock(): Flow<List<Product>> =
        productDao.getProductsWithStock()
    
    suspend fun updateProductStock(productId: Long, newStock: Double, consumptionRate: Double?) {
        val product = productDao.getProductById(productId)
        product?.let {
            val updatedProduct = it.copy(
                currentStock = newStock,
                averageConsumptionPerDay = consumptionRate ?: it.averageConsumptionPerDay,
                lastStockUpdate = java.util.Date()
            )
            productDao.updateProduct(updatedProduct)
        }
    }
    
    suspend fun calculateAverageConsumption(productName: String): Double? {
        // Esta função deve ser chamada de forma síncrona em uma corrotina
        // Retorna null por enquanto, será calculada no ViewModel
        return null
    }
}