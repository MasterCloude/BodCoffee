package com.example.bodcoffee.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodcoffee.Screen.Product
import com.example.bodcoffee.Screen.retrofitClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ProductViewModel : ViewModel() {
    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                products = retrofitClient.getProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
