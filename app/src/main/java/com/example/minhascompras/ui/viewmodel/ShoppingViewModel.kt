package com.example.minhascompras.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.minhascompras.data.model.Product
import com.example.minhascompras.data.model.Purchase
import com.example.minhascompras.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val allPurchases = repository.getAllPurchases()
    val totalSpent = repository.getTotalSpent()
    
    // Controle de Validade
    private val _expiringProducts = MutableStateFlow<List<Product>>(emptyList())
    val expiringProducts: StateFlow<List<Product>> = _expiringProducts.asStateFlow()
    
    private val _expiringCount = MutableStateFlow(0)
    val expiringCount: StateFlow<Int> = _expiringCount.asStateFlow()
    
    // Controle de Estoque
    private val _lowStockProducts = MutableStateFlow<List<Product>>(emptyList())
    val lowStockProducts: StateFlow<List<Product>> = _lowStockProducts.asStateFlow()
    
    private val _lowStockCount = MutableStateFlow(0)
    val lowStockCount: StateFlow<Int> = _lowStockCount.asStateFlow()

    private val _currentPurchase = MutableStateFlow<Purchase?>(null)
    val currentPurchase: StateFlow<Purchase?> = _currentPurchase.asStateFlow()

    private val _currentProducts = MutableStateFlow<List<Product>>(emptyList())
    val currentProducts: StateFlow<List<Product>> = _currentProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        // Carrega produtos próximos do vencimento (7 dias)
        loadExpiringProducts()
        // Carrega produtos com estoque baixo
        loadLowStockProducts()
    }
    
    private fun loadExpiringProducts() {
        viewModelScope.launch {
            val sevenDaysFromNow = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
            repository.getExpiringProducts(sevenDaysFromNow).collect { products ->
                _expiringProducts.value = products
                _expiringCount.value = products.size
            }
        }
    }
    
    private fun loadLowStockProducts() {
        viewModelScope.launch {
            repository.getProductsWithStock().collect { products ->
                val lowStock = products.filter { product ->
                    val stock = product.currentStock ?: 0.0
                    val consumption = product.averageConsumptionPerDay ?: 0.0
                    if (consumption > 0) {
                        (stock / consumption) < 7
                    } else {
                        false
                    }
                }
                _lowStockProducts.value = lowStock
                _lowStockCount.value = lowStock.size
            }
        }
    }
    
    fun refreshDashboardData() {
        loadExpiringProducts()
        loadLowStockProducts()
    }

    fun loadPurchase(purchaseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentPurchase.value = repository.getPurchaseById(purchaseId)
            repository.getProductsByPurchaseId(purchaseId).collect { products ->
                _currentProducts.value = products
            }
            _isLoading.value = false
        }
    }

    fun createPurchase(storeName: String, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val purchase = Purchase(
                storeName = storeName,
                date = Date(),
                totalValue = 0.0
            )
            val id = repository.insertPurchase(purchase)
            onComplete(id)
        }
    }

    fun updatePurchase(purchase: Purchase) {
        viewModelScope.launch {
            repository.updatePurchase(purchase)
        }
    }

    fun deletePurchase(purchase: Purchase) {
        viewModelScope.launch {
            repository.deleteProductsByPurchaseId(purchase.id)
            repository.deletePurchase(purchase)
        }
    }

    fun addProduct(product: Product, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertProduct(product)
            updatePurchaseTotal(product.purchaseId)
            onComplete()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            updatePurchaseTotal(product.purchaseId)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            updatePurchaseTotal(product.purchaseId)
        }
    }

    private suspend fun updatePurchaseTotal(purchaseId: Long) {
        val purchase = repository.getPurchaseById(purchaseId)
        purchase?.let {
            repository.getProductsByPurchaseId(purchaseId).collect { products ->
                val total = products.sumOf { p -> p.price * p.quantity }
                repository.updatePurchase(it.copy(totalValue = total))
            }
        }
    }

    fun searchProducts(query: String) = repository.searchProducts(query)

    fun getAllProductNames() = repository.getAllProductNames()
    
    // Funções para controle de estoque
    fun updateProductStock(productId: Long, newStock: Double, consumptionRate: Double? = null) {
        viewModelScope.launch {
            repository.updateProductStock(productId, newStock, consumptionRate)
            refreshDashboardData()
        }
    }
    
    fun getProductHistory(productName: String) = repository.getProductHistory(productName)
    
    fun getAllProductsWithExpiration() = repository.getAllProductsWithExpiration()
    
    fun getProductsWithStock() = repository.getProductsWithStock()

    class Factory(private val repository: ShoppingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShoppingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ShoppingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}