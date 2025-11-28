package com.example.minhascompras.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.minhascompras.ui.screens.HomeScreen
import com.example.minhascompras.ui.screens.PurchaseDetailScreen
import com.example.minhascompras.ui.screens.AddProductScreen
import com.example.minhascompras.ui.viewmodel.ShoppingViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PurchaseDetail : Screen("purchase/{purchaseId}") {
        fun createRoute(purchaseId: Long) = "purchase/
$purchaseId"
    }
    object AddProduct : Screen("add_product/{purchaseId}") {
        fun createRoute(purchaseId: Long) = "add_product/
$purchaseId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onPurchaseClick = { purchaseId ->
                    navController.navigate(Screen.PurchaseDetail.createRoute(purchaseId))
                },
                onNewPurchase = { purchaseId ->
                    navController.navigate(Screen.PurchaseDetail.createRoute(purchaseId))
                }
            )
        }

        composable(
            route = Screen.PurchaseDetail.route,
            arguments = listOf(
                navArgument("purchaseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val purchaseId = backStackEntry.arguments?.getLong("purchaseId") ?: 0L
            PurchaseDetailScreen(
                purchaseId = purchaseId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddProduct = {
                    navController.navigate(Screen.AddProduct.createRoute(purchaseId))
                }
            )
        }

        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(
                navArgument("purchaseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val purchaseId = backStackEntry.arguments?.getLong("purchaseId") ?: 0L
            AddProductScreen(
                purchaseId = purchaseId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}