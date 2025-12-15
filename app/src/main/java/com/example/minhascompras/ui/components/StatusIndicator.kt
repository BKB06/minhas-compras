package com.example.minhascompras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.minhascompras.util.ExpirationStatus
import com.example.minhascompras.util.StockStatus

/**
 * Badge colorido que indica o status de validade de um produto
 */
@Composable
fun ExpirationStatusBadge(status: ExpirationStatus, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        ExpirationStatus.EXPIRED -> Color(0xFFD32F2F) to "VENCIDO"
        ExpirationStatus.CRITICAL -> Color(0xFFD32F2F) to "CRÍTICO"
        ExpirationStatus.WARNING -> Color(0xFFFFA000) to "ATENÇÃO"
        ExpirationStatus.GOOD -> Color(0xFF388E3C) to "OK"
        ExpirationStatus.NO_DATE -> Color(0xFF757575) to "SEM DATA"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Badge colorido que indica o status de estoque de um produto
 */
@Composable
fun StockStatusBadge(status: StockStatus, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StockStatus.CRITICAL -> Color(0xFFD32F2F) to "CRÍTICO"
        StockStatus.LOW -> Color(0xFFFFA000) to "BAIXO"
        StockStatus.OK -> Color(0xFF388E3C) to "OK"
        StockStatus.NO_DATA -> Color(0xFF757575) to "SEM DADOS"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Chip que mostra dias restantes até o vencimento ou de estoque
 */
@Composable
fun DaysRemainingChip(days: Int?, label: String, modifier: Modifier = Modifier) {
    if (days == null) return
    
    val color = when {
        days < 0 -> Color(0xFFD32F2F)
        days <= 3 -> Color(0xFFD32F2F)
        days <= 7 -> Color(0xFFFFA000)
        else -> Color(0xFF388E3C)
    }
    
    val displayText = when {
        days < 0 -> "Vencido há ${-days} ${if (-days == 1) "dia" else "dias"}"
        days == 0 -> "Vence hoje"
        days == 1 -> "Vence amanhã"
        else -> "$days dias"
    }
    
    AssistChip(
        onClick = { },
        label = { Text(displayText) },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
        }
    )
}

/**
 * Indicador circular de status
 */
@Composable
fun StatusIndicatorCircle(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
}

/**
 * Card de resumo para dashboard
 */
@Composable
fun DashboardCard(
    title: String,
    count: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
