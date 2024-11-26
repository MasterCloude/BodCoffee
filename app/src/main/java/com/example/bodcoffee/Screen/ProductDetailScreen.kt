package com.example.bodcoffee.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết sản phẩm") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Hiển thị hình ảnh sản phẩm
            AsyncImage(
                model = product.anhSanPham,
                contentDescription = product.tenSanPham,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị tên sản phẩm
            Text(
                text = product.tenSanPham,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị loại sản phẩm
            Text(
                text = "Loại: ${product.loaiSanPham}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị giá sản phẩm
            Text(
                text = "Giá: ${product.giaSanPham} VND",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị đánh giá sản phẩm
            Text(
                text = "Đánh giá: ${product.danhGiaSanPham}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị mô tả sản phẩm
            Text(
                text = "Mô tả:",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = product.moTaSanPham,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị nội dung sản phẩm
            Text(
                text = "Nội dung:",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = product.noiDungSanPham,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}
