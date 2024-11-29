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
import com.example.bodcoffee.BottomNavItem
import com.example.bodcoffee.Model.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    product: Product,
    productViewModel: ProductViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        productViewModel.addToCart(product, quantity)
                        snackbarMessage = "Đã thêm vào giỏ hàng!"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Thêm vào giỏ hàng")
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
            horizontalAlignment = Alignment.Start
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
            Text(text = "Tên sản phẩm: ${product.tenSanPham}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Loại: ${product.loaiSanPham}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Giá: ${product.giaSanPham} VND",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Mô tả sản phẩm:", style = MaterialTheme.typography.headlineSmall)
            Text(text = product.moTaSanPham, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nội dung sản phẩm:", style = MaterialTheme.typography.headlineSmall)
            Text(text = product.noiDungSanPham, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Đánh giá sản phẩm: ${product.danhGiaSanPham}", style = MaterialTheme.typography.bodyLarge)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Xác nhận mua hàng",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = product.anhSanPham,
                        contentDescription = product.tenSanPham,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.tenSanPham, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
                        Text(text = "$quantity", style = MaterialTheme.typography.headlineMedium)
                        IconButton(onClick = { quantity++ }) { Text("+") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tổng giá: ${quantity * product.giaSanPham} VND",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        productViewModel.addToCart(product, quantity)
                        showDialog = false
                        navController.navigate(BottomNavItem.Cart.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mua", textAlign = TextAlign.Center)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hủy", textAlign = TextAlign.Center)
                }
            }
        )
    }
}
