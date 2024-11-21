package com.example.bodcoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                MainActivityScreen()
            }
        }
    }
}

@Composable
fun MainActivityScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                BottomNavigationBar(
                    navController = navController,
                    items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Cart,
                        BottomNavItem.History,
                        BottomNavItem.Info
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavHost(navController = navController)
        }
    }
}

@Composable
fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    return currentRoute in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Cart.route,
        BottomNavItem.History.route,
        BottomNavItem.Info.route
    )
}
