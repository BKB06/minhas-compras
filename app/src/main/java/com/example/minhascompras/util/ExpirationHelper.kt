package com.example.minhascompras.util

import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Enum que representa o status de validade de um produto
 */
enum class ExpirationStatus {
    EXPIRED,      // Produto vencido
    CRITICAL,     // Vence em até 3 dias
    WARNING,      // Vence em até 7 dias
    GOOD,         // Validade ok (mais de 7 dias)
    NO_DATE       // Sem data de validade definida
}

/**
 * Helper para cálculos relacionados à validade de produtos
 */
object ExpirationHelper {
    
    /**
     * Calcula o número de dias até o vencimento
     * @param expirationDate Data de vencimento
     * @return Número de dias até vencer (negativo se já venceu)
     */
    fun calculateDaysUntilExpiration(expirationDate: Date?): Int? {
        if (expirationDate == null) return null
        
        val currentTime = System.currentTimeMillis()
        val expirationTime = expirationDate.time
        val diffInMillis = expirationTime - currentTime
        
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    }
    
    /**
     * Determina o status de validade baseado na data de vencimento
     * @param expirationDate Data de vencimento
     * @return Status de validade
     */
    fun getExpirationStatus(expirationDate: Date?): ExpirationStatus {
        if (expirationDate == null) {
            return ExpirationStatus.NO_DATE
        }
        
        val daysUntilExpiration = calculateDaysUntilExpiration(expirationDate) ?: return ExpirationStatus.NO_DATE
        
        return when {
            daysUntilExpiration < 0 -> ExpirationStatus.EXPIRED
            daysUntilExpiration <= 3 -> ExpirationStatus.CRITICAL
            daysUntilExpiration <= 7 -> ExpirationStatus.WARNING
            else -> ExpirationStatus.GOOD
        }
    }
    
    /**
     * Verifica se o produto está próximo do vencimento (7 dias ou menos)
     * @param expirationDate Data de vencimento
     * @return true se está próximo do vencimento
     */
    fun isExpiringOrExpired(expirationDate: Date?): Boolean {
        if (expirationDate == null) return false
        val days = calculateDaysUntilExpiration(expirationDate) ?: return false
        return days <= 7
    }
}
