package com.example.minhascompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.minhascompras.ui.navigation.NavGraph
import com.example.minhascompras.ui.theme.MinhasComprasTheme
import com.example.minhascompras.ui.viewmodel.ShoppingViewModel
import com.example.minhascompras.ui.viewmodel.ShoppingViewModelFactory

class MainActivity : ComponentActivity() {
    
    private val viewModel: ShoppingViewModel by viewModels {
        ShoppingViewModelFactory((application as MinhasComprasApplication).repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinhasComprasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}