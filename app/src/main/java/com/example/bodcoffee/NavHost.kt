package com.example.bodcoffee

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
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
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        // Login screen
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
        // Register screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        // Reset password screen
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        // Bottom navigation screens
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Cart.route) { CartScreen() }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Info.route) { InfoScreen() }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = navController.currentBackStackEntry?.destination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(BottomNavItem.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
