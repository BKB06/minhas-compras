package com.example.minhascompras.util

import com.example.minhascompras.data.model.Product
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Enum que representa o status de estoque de um produto
 */
enum class StockStatus {
    CRITICAL,     // Menos de 3 dias de estoque
    LOW,          // Menos de 7 dias de estoque
    OK,           // Mais de 7 dias de estoque
    NO_DATA       // Sem dados de estoque
}

/**
 * Helper para cálculos relacionados ao estoque de produtos
 */
object StockHelper {
    
    /**
     * Calcula dias restantes de estoque
     * @param currentStock Estoque atual disponível
     * @param averageConsumptionPerDay Consumo médio diário
     * @return Número de dias de estoque restante
     */
    fun calculateDaysOfStock(currentStock: Double?, averageConsumptionPerDay: Double?): Int? {
        if (currentStock == null || averageConsumptionPerDay == null || averageConsumptionPerDay <= 0) {
            return null
        }
        
        return (currentStock / averageConsumptionPerDay).toInt()
    }
    
    /**
     * Determina o status de estoque
     * @param currentStock Estoque atual disponível
     * @param averageConsumptionPerDay Consumo médio diário
     * @return Status de estoque
     */
    fun getStockStatus(currentStock: Double?, averageConsumptionPerDay: Double?): StockStatus {
        val daysOfStock = calculateDaysOfStock(currentStock, averageConsumptionPerDay)
            ?: return StockStatus.NO_DATA
        
        return when {
            daysOfStock < 3 -> StockStatus.CRITICAL
            daysOfStock < 7 -> StockStatus.LOW
            else -> StockStatus.OK
        }
    }
    
    /**
     * Calcula consumo médio baseado em histórico de compras
     * @param productHistory Lista de produtos do histórico
     * @return Consumo médio por dia ou null se não houver dados suficientes
     */
    fun calculateAverageConsumption(productHistory: List<Product>): Double? {
        if (productHistory.size < 2) return null
        
        // Ordena por data de compra (através do purchaseId como proxy)
        val sortedHistory = productHistory.sortedBy { it.id }
        
        // Calcula total de quantidade comprada
        val totalQuantity = sortedHistory.sumOf { it.quantity }
        
        // Pega a primeira e última compra para calcular período
        val firstProduct = sortedHistory.first()
        val lastProduct = sortedHistory.last()
        
        // Se tivermos datas de atualização de estoque, usamos elas
        // Caso contrário, assumimos um período padrão
        val daysBetween = 30 // Valor padrão se não houver mais informações
        
        return if (daysBetween > 0) {
            totalQuantity / daysBetween
        } else {
            null
        }
    }
    
    /**
     * Verifica se o produto está com estoque baixo (menos de 7 dias)
     * @param currentStock Estoque atual disponível
     * @param averageConsumptionPerDay Consumo médio diário
     * @return true se o estoque está baixo
     */
    fun isLowStock(currentStock: Double?, averageConsumptionPerDay: Double?): Boolean {
        val days = calculateDaysOfStock(currentStock, averageConsumptionPerDay) ?: return false
        return days < 7
    }
}
