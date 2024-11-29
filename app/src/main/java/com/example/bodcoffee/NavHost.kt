package com.example.bodcoffee

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bodcoffee.Model.ProductViewModel
import com.example.bodcoffee.Screen.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Trang chủ")
    object Cart : BottomNavItem("cart", Icons.Filled.ShoppingCart, "Giỏ hàng")
    object History : BottomNavItem("history", Icons.Filled.History, "Lịch sử")
    object Info : BottomNavItem("info", Icons.Filled.Info, "Thông tin")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    productViewModel: ProductViewModel = ProductViewModel()
) {
    NavHost(navController = navController, startDestination = "login") {
        // Login Screen
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

        // Register Screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Reset Password Screen
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Home Screen
        composable(BottomNavItem.Home.route) {
            HomeScreen(navController, productViewModel)
        }

        // Cart Screen
        composable(BottomNavItem.Cart.route) {
            CartScreen(productViewModel, navController)
        }

        // History Screen
        composable(BottomNavItem.History.route) {
            HistoryScreen(productViewModel)
        }

        // Product Detail Screen
        composable(
            "product_detail/{productId}",
            arguments = listOf(
                navArgument("productId") { type = androidx.navigation.NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            val product = productViewModel.products.find { it.id == productId }
            if (product != null) {
                ProductDetailScreen(
                    navController = navController,
                    product = product,
                    productViewModel = productViewModel
                )
            } else {
                CenteredText(text = "Không tìm thấy sản phẩm.")
            }
        }

        // Individual Transaction Details
        composable(
            route = "history/{productName}/{quantity}/{totalPrice}/{productImage}",
            arguments = listOf(
                navArgument("productName") { type = androidx.navigation.NavType.StringType },
                navArgument("quantity") { type = androidx.navigation.NavType.IntType },
                navArgument("totalPrice") { type = androidx.navigation.NavType.IntType },
                navArgument("productImage") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 0
            val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0
            val productImage = backStackEntry.arguments?.getString("productImage") ?: ""

            HistoryDetailScreen(
                productName = productName,
                quantity = quantity,
                totalPrice = totalPrice,
                productImage = productImage
            )
        }

        // Info Screen
        composable(BottomNavItem.Info.route) {
            InfoScreen()
        }
    }
}
