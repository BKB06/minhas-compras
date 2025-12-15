package com.example.minhascompras.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.minhascompras.data.model.Product
import com.example.minhascompras.ui.components.DaysRemainingChip
import com.example.minhascompras.ui.components.ExpirationStatusBadge
import com.example.minhascompras.ui.viewmodel.ShoppingViewModel
import com.example.minhascompras.util.ExpirationHelper
import com.example.minhascompras.util.ExpirationStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiringProductsScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onProductClick: (Long) -> Unit = {}
) {
    val allProducts by viewModel.getAllProductsWithExpiration()
        .collectAsState(initial = emptyList())

    // Agrupa produtos por status
    val expiredProducts = remember(allProducts) {
        allProducts.filter { 
            ExpirationHelper.getExpirationStatus(it.expirationDate) == ExpirationStatus.EXPIRED 
        }
    }
    
    val criticalProducts = remember(allProducts) {
        allProducts.filter { 
            ExpirationHelper.getExpirationStatus(it.expirationDate) == ExpirationStatus.CRITICAL 
        }
    }
    
    val warningProducts = remember(allProducts) {
        allProducts.filter { 
            ExpirationHelper.getExpirationStatus(it.expirationDate) == ExpirationStatus.WARNING 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos Vencendo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (expiredProducts.isEmpty() && criticalProducts.isEmpty() && warningProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✅ Nenhum produto próximo do vencimento!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Produtos vencidos
                if (expiredProducts.isNotEmpty()) {
                    item {
                        Text(
                            text = "🔴 Vencidos (${expiredProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(expiredProducts) { product ->
                        ExpiringProductCard(product = product, onClick = { onProductClick(product.purchaseId) })
                    }
                }
                
                // Produtos críticos (vence em até 3 dias)
                if (criticalProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔴 Críticos - Vencem em até 3 dias (${criticalProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(criticalProducts) { product ->
                        ExpiringProductCard(product = product, onClick = { onProductClick(product.purchaseId) })
                    }
                }
                
                // Produtos em atenção (vence em até 7 dias)
                if (warningProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🟡 Atenção - Vencem em até 7 dias (${warningProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(warningProducts) { product ->
                        ExpiringProductCard(product = product, onClick = { onProductClick(product.purchaseId) })
                    }
                }
            }
        }
    }
}

@Composable
fun ExpiringProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val expirationStatus = ExpirationHelper.getExpirationStatus(product.expirationDate)
    val daysUntilExpiration = ExpirationHelper.calculateDaysUntilExpiration(product.expirationDate)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.quantity} ${product.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    product.category?.let { category ->
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                ExpirationStatusBadge(status = expirationStatus)
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Validade: ${product.expirationDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(it) } ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                DaysRemainingChip(days = daysUntilExpiration, label = "Validade")
            }
        }
    }
}
