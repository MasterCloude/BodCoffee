package com.example.bodcoffee.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodcoffee.Screen.Product
import com.example.bodcoffee.Screen.retrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ProductViewModel : ViewModel() {
    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    var cartItems by mutableStateOf<List<Pair<Product, Int>>>(emptyList())
        private set

    var historyItems by mutableStateOf<List<Product>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadProducts()
        loadCartItems()
        loadHistoryItems()
    }

    fun removeFromCart(product: Product) {
        cartItems = cartItems.filterNot { it.first.id == product.id }
        saveCartToFirestore()
    }

    fun updateCartQuantity(product: Product, quantity: Int) {
        if (quantity > 0) {
            cartItems = cartItems.map {
                if (it.first.id == product.id) it.copy(second = quantity) else it
            }
            saveCartToFirestore()
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        val existingItem = cartItems.find { it.first.id == product.id }
        cartItems = if (existingItem != null) {
            cartItems.map {
                if (it.first.id == product.id) it.copy(second = it.second + quantity) else it
            }
        } else {
            cartItems + (product to quantity)
        }
        saveCartToFirestore()
    }

    private fun saveCartToFirestore() {
        val cartData = cartItems.map {
            hashMapOf(
                "productId" to it.first.id,
                "loaiSanPham" to it.first.loaiSanPham,
                "tenSanPham" to it.first.tenSanPham,
                "moTaSanPham" to it.first.moTaSanPham,
                "noiDungSanPham" to it.first.noiDungSanPham,
                "giaSanPham" to it.first.giaSanPham,
                "anhSanPham" to it.first.anhSanPham,
                "danhGiaSanPham" to it.first.danhGiaSanPham,
                "quantity" to it.second
            )
        }

        firestore.collection("cart").document("user_cart").set(hashMapOf("items" to cartData))
            .addOnSuccessListener {
                println("Cart updated successfully")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                isLoading = true
                products = retrofitClient.getProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadCartItems() {
        firestore.collection("cart").document("user_cart").get()
            .addOnSuccessListener { snapshot ->
                val items = (snapshot["items"] as? List<Map<String, Any>>)?.mapNotNull { item ->
                    val productId = (item["productId"] as? Long)?.toInt() ?: return@mapNotNull null
                    val quantity = (item["quantity"] as? Long)?.toInt() ?: 1
                    val product = Product(
                        id = productId,
                        loaiSanPham = item["loaiSanPham"] as? String ?: "",
                        tenSanPham = item["tenSanPham"] as? String ?: "",
                        moTaSanPham = item["moTaSanPham"] as? String ?: "",
                        noiDungSanPham = item["noiDungSanPham"] as? String ?: "",
                        giaSanPham = (item["giaSanPham"] as? Long)?.toInt() ?: 0,
                        anhSanPham = item["anhSanPham"] as? String ?: "",
                        danhGiaSanPham = item["danhGiaSanPham"] as? String ?: ""
                    )
                    product to quantity
                } ?: emptyList()
                cartItems = items
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun loadHistoryItems() {
        firestore.collection("history").get()
            .addOnSuccessListener { result ->
                val items = result.documents.mapNotNull { document ->
                    val productId = document.getLong("productId")?.toInt() ?: return@mapNotNull null
                    val loaiSanPham = document.getString("loaiSanPham") ?: return@mapNotNull null
                    val tenSanPham = document.getString("tenSanPham") ?: return@mapNotNull null
                    val moTaSanPham = document.getString("moTaSanPham") ?: ""
                    val noiDungSanPham = document.getString("noiDungSanPham") ?: ""
                    val giaSanPham = document.getLong("giaSanPham")?.toInt() ?: 0
                    val anhSanPham = document.getString("anhSanPham") ?: ""
                    val danhGiaSanPham = document.getString("danhGiaSanPham") ?: ""

                    Product(
                        id = productId,
                        loaiSanPham = loaiSanPham,
                        tenSanPham = tenSanPham,
                        moTaSanPham = moTaSanPham,
                        noiDungSanPham = noiDungSanPham,
                        giaSanPham = giaSanPham,
                        anhSanPham = anhSanPham,
                        danhGiaSanPham = danhGiaSanPham
                    )
                }
                historyItems = items
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
