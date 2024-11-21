package com.example.bodcoffee

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bodcoffee.Screen.LoginScreen
import com.example.bodcoffee.Screen.RegisterScreen
import com.example.bodcoffee.Screen.HomeScreen
import com.example.bodcoffee.Screen.ResetPasswordScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
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
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}
