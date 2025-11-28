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

    private val _currentPurchase = MutableStateFlow<Purchase?>(null)
    val currentPurchase: StateFlow<Purchase?> = _currentPurchase.asStateFlow()

    private val _currentProducts = MutableStateFlow<List<Product>>(emptyList())
    val currentProducts: StateFlow<List<Product>> = _currentProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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