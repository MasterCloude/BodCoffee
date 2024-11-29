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

    // Danh sách sản phẩm từ server
    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    // Danh sách sản phẩm trong giỏ hàng
    var cartItems by mutableStateOf<List<Pair<Product, Int>>>(emptyList())
        private set

    // Lịch sử giao dịch
    var historyItems by mutableStateOf<List<Map<String, Any>>>(emptyList())
        private set

    // Trạng thái tải
    var isLoading by mutableStateOf(true)
        private set

    // Thông báo lỗi
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Firebase Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadProducts()
        loadCartItems()
        loadHistoryItems()
    }

    // Thêm sản phẩm vào giỏ hàng
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

    // Xóa sản phẩm khỏi giỏ hàng
    fun removeFromCart(product: Product) {
        cartItems = cartItems.filterNot { it.first.id == product.id }
        saveCartToFirestore()
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    fun updateCartQuantity(product: Product, quantity: Int) {
        if (quantity > 0) {
            cartItems = cartItems.map {
                if (it.first.id == product.id) it.copy(second = quantity) else it
            }
            saveCartToFirestore()
        }
    }

    // Xóa toàn bộ giỏ hàng
    fun clearCart() {
        firestore.collection("cart").document("user_cart").delete()
            .addOnSuccessListener {
                cartItems = emptyList()
                println("Cart cleared successfully")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Lưu giỏ hàng lên Firebase Firestore
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

    // Lưu lịch sử giao dịch
    fun addToHistory(selectedItems: List<Pair<Product, Int>>, totalSelectedPrice: Int) {
        val historyData = selectedItems.map {
            mapOf(
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

        val historyDocument = hashMapOf(
            "items" to historyData,
            "totalPrice" to totalSelectedPrice,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("history").add(historyDocument)
            .addOnSuccessListener {
                loadHistoryItems()
                println("History added successfully")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Tải danh sách sản phẩm
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                isLoading = true
                products = retrofitClient.getProducts()
            } catch (e: Exception) {
                errorMessage = "Không thể tải sản phẩm: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Tải danh sách sản phẩm trong giỏ hàng
    private fun loadCartItems() {
        firestore.collection("cart").document("user_cart")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val items = (it["items"] as? List<Map<String, Any>>)?.mapNotNull { item ->
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
            }
    }

    // Tải lịch sử giao dịch
    private fun loadHistoryItems() {
        firestore.collection("history")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                val items = snapshots?.documents?.mapNotNull { document ->
                    document.data
                } ?: emptyList()
                historyItems = items
            }
    }
}
