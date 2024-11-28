package com.example.bodcoffee

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bodcoffee.Model.ProductViewModel
import com.example.bodcoffee.Screen.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Cart : BottomNavItem("cart", Icons.Filled.ShoppingCart, "Cart")
    object History : BottomNavItem("history", Icons.Filled.History, "History")
    object Info : BottomNavItem("info", Icons.Filled.Info, "Info")
}

@Composable
fun AppNavHost(navController: NavHostController, productViewModel: ProductViewModel = ProductViewModel()) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToResetPassword = { navController.navigate("reset_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val product = productViewModel.products.find { it.id == productId }
            if (product != null) {
                ProductDetailScreen(
                    navController = navController,
                    product = product,
                    onBuyNow = { product, quantity ->
                        productViewModel.addToCart(product, quantity)
                        navController.navigate(BottomNavItem.Cart.route)
                    },
                    productViewModel = productViewModel
                )
            } else {
                CenteredText(text = "Không tìm thấy sản phẩm.")
            }
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(BottomNavItem.Home.route) { HomeScreen(navController, productViewModel) }
        composable(BottomNavItem.Cart.route) { CartScreen(productViewModel) }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Info.route) { InfoScreen() }
    }
}
