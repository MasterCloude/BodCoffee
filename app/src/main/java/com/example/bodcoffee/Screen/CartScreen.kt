package com.example.bodcoffee.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bodcoffee.Model.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(productViewModel: ProductViewModel) {
    val cartItems = productViewModel.cartItems
    var selectAllChecked by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Pair<Product, Int>>() }
    var totalSelectedPrice by remember { mutableStateOf(0) }

    fun updateTotalPrice() {
        totalSelectedPrice = selectedItems.sumOf { it.first.giaSanPham * it.second }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Giỏ hàng của bạn") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Chọn tất cả")
                        Checkbox(
                            checked = selectAllChecked,
                            onCheckedChange = { isChecked ->
                                selectAllChecked = isChecked
                                if (isChecked) {
                                    selectedItems.clear()
                                    selectedItems.addAll(cartItems)
                                } else {
                                    selectedItems.clear()
                                }
                                updateTotalPrice()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng giá: $totalSelectedPrice VND",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = {
                        // Xử lý logic khi nhấn "Mua ngay"
                        println("Đặt hàng: $selectedItems")
                    },
                    enabled = selectedItems.isNotEmpty()
                ) {
                    Text("Mua ngay")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Giỏ hàng của bạn đang trống.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { cartItem ->
                        var totalPrice by remember { mutableStateOf(cartItem.first.giaSanPham * cartItem.second) }

                        CartItemCard(
                            product = cartItem.first,
                            quantity = cartItem.second,
                            onQuantityChange = { newQuantity ->
                                productViewModel.updateCartQuantity(cartItem.first, newQuantity)
                                totalPrice = cartItem.first.giaSanPham * newQuantity
                                if (selectedItems.contains(cartItem)) {
                                    selectedItems.remove(cartItem)
                                    selectedItems.add(cartItem.first to newQuantity)
                                    updateTotalPrice()
                                }
                            },
                            onRemoveClick = {
                                productViewModel.removeFromCart(cartItem.first)
                                selectedItems.remove(cartItem)
                                updateTotalPrice()
                            },
                            isChecked = selectedItems.contains(cartItem),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedItems.add(cartItem)
                                } else {
                                    selectedItems.remove(cartItem)
                                }
                                updateTotalPrice()
                            },
                            totalPrice = totalPrice
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    totalPrice: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = product.anhSanPham,
                contentDescription = product.tenSanPham,
                modifier = Modifier.size(80.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.tenSanPham,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Giá: ${product.giaSanPham} VND",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Giảm số lượng"
                        )
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(
                        onClick = { onQuantityChange(quantity + 1) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Tăng số lượng"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tổng giá: $totalPrice VND",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Xóa sản phẩm"
                )
            }
        }
    }
}
