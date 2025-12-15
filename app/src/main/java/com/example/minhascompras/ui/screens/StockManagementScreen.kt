package com.example.minhascompras.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.minhascompras.data.model.Product
import com.example.minhascompras.ui.components.DaysRemainingChip
import com.example.minhascompras.ui.components.StockStatusBadge
import com.example.minhascompras.ui.viewmodel.ShoppingViewModel
import com.example.minhascompras.util.StockHelper
import com.example.minhascompras.util.StockStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onProductClick: (Long) -> Unit = {}
) {
    val allProducts by viewModel.getProductsWithStock()
        .collectAsState(initial = emptyList())

    // Agrupa produtos por status
    val criticalProducts = remember(allProducts) {
        allProducts.filter { 
            StockHelper.getStockStatus(it.currentStock, it.averageConsumptionPerDay) == StockStatus.CRITICAL 
        }
    }
    
    val lowProducts = remember(allProducts) {
        allProducts.filter { 
            StockHelper.getStockStatus(it.currentStock, it.averageConsumptionPerDay) == StockStatus.LOW 
        }
    }
    
    val okProducts = remember(allProducts) {
        allProducts.filter { 
            StockHelper.getStockStatus(it.currentStock, it.averageConsumptionPerDay) == StockStatus.OK 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestão de Estoque") },
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
        if (allProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nenhum produto com estoque rastreado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adicione produtos e configure o estoque para começar.",
                        style = MaterialTheme.typography.bodyMedium,
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
                // Produtos críticos (menos de 3 dias)
                if (criticalProducts.isNotEmpty()) {
                    item {
                        Text(
                            text = "🔴 Estoque Crítico (${criticalProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(criticalProducts) { product ->
                        StockProductCard(
                            product = product, 
                            viewModel = viewModel,
                            onClick = { onProductClick(product.purchaseId) }
                        )
                    }
                }
                
                // Produtos com estoque baixo (menos de 7 dias)
                if (lowProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🟡 Estoque Baixo (${lowProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(lowProducts) { product ->
                        StockProductCard(
                            product = product, 
                            viewModel = viewModel,
                            onClick = { onProductClick(product.purchaseId) }
                        )
                    }
                }
                
                // Produtos com estoque ok
                if (okProducts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🟢 Estoque OK (${okProducts.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(okProducts) { product ->
                        StockProductCard(
                            product = product, 
                            viewModel = viewModel,
                            onClick = { onProductClick(product.purchaseId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StockProductCard(
    product: Product,
    viewModel: ShoppingViewModel,
    onClick: () -> Unit
) {
    val stockStatus = StockHelper.getStockStatus(product.currentStock, product.averageConsumptionPerDay)
    val daysOfStock = StockHelper.calculateDaysOfStock(product.currentStock, product.averageConsumptionPerDay)
    var showUpdateDialog by remember { mutableStateOf(false) }

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
                        text = "Estoque: ${product.currentStock ?: 0.0} ${product.unit}",
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StockStatusBadge(status = stockStatus)
                    IconButton(onClick = { showUpdateDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Atualizar Estoque",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consumo: ${product.averageConsumptionPerDay ?: 0.0} ${product.unit}/dia",
                    style = MaterialTheme.typography.bodyMedium
                )
                DaysRemainingChip(days = daysOfStock, label = "Estoque")
            }
        }
    }
    
    // Dialog para atualizar estoque
    if (showUpdateDialog) {
        UpdateStockDialog(
            product = product,
            onDismiss = { showUpdateDialog = false },
            onUpdate = { newStock, consumptionRate ->
                viewModel.updateProductStock(product.id, newStock, consumptionRate)
                showUpdateDialog = false
            }
        )
    }
}

@Composable
fun UpdateStockDialog(
    product: Product,
    onDismiss: () -> Unit,
    onUpdate: (Double, Double?) -> Unit
) {
    var newStock by remember { mutableStateOf(product.currentStock?.toString() ?: "") }
    var consumptionRate by remember { mutableStateOf(product.averageConsumptionPerDay?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atualizar Estoque") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = newStock,
                    onValueChange = { newStock = it },
                    label = { Text("Estoque Atual (${product.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = consumptionRate,
                    onValueChange = { consumptionRate = it },
                    label = { Text("Consumo Diário (${product.unit}/dia)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val stock = newStock.replace(",", ".").toDoubleOrNull()
                    val consumption = consumptionRate.replace(",", ".").toDoubleOrNull()
                    if (stock != null) {
                        onUpdate(stock, consumption)
                    }
                }
            ) {
                Text("Atualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
