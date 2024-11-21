package com.example.bodcoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() // Navigation Controller

            // Material Theme Surface Wrapper
            Surface(color = MaterialTheme.colorScheme.background) {
                AppNavHost(navController = navController)
            }
        }
    }
}
