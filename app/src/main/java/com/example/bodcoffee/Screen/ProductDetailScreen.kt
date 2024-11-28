package com.example.bodcoffee.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bodcoffee.Model.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    product: Product,
    onBuyNow: (Product, Int) -> Unit,
    productViewModel: ProductViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var isAddingToCart by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isAddingToCart = true
                        productViewModel.addToCart(product, quantity)
                        snackbarMessage = "Đã thêm vào giỏ hàng"
                        isAddingToCart = false
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isAddingToCart
                ) {
                    if (isAddingToCart) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Thêm vào giỏ hàng")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mua ngay")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = product.anhSanPham,
                contentDescription = product.tenSanPham,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product.tenSanPham,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Loại: ${product.loaiSanPham}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Giá: ${product.giaSanPham} VND",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mô tả:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start
            )
            Text(
                text = product.moTaSanPham,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    productViewModel.addToCart(product, quantity)
                    showDialog = false
                    navController.navigate("cart")
                }) {
                    Text("Xác nhận mua")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            },
            title = { Text(text = "Xác nhận mua hàng") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = product.anhSanPham,
                        contentDescription = product.tenSanPham,
                        modifier = Modifier.size(100.dp)
                    )
                    Text(text = "Số lượng: $quantity")
                }
            }
        )
    }

    if (snackbarMessage != null) {
        Snackbar(
            action = {
                Button(onClick = { snackbarMessage = null }) {
                    Text("Đóng")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(snackbarMessage ?: "")
        }
    }
}
