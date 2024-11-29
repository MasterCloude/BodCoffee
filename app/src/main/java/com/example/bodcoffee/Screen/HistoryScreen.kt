package com.example.bodcoffee.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bodcoffee.Model.ProductViewModel
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    productName: String,
    quantity: Int,
    totalPrice: Int,
    productImage: String
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Chi tiết giao dịch") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = productImage,
                contentDescription = productName,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = productName, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Số lượng: $quantity", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tổng giá: $totalPrice VND",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(productViewModel: ProductViewModel) {
    val historyItems = productViewModel.historyItems

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Lịch sử giao dịch") })
        }
    ) { paddingValues ->
        if (historyItems.isEmpty()) {
            // Hiển thị thông báo nếu không có lịch sử giao dịch
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có lịch sử giao dịch.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Hiển thị danh sách lịch sử giao dịch
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historyItems) { historyItem ->
                    HistoryItemCard(historyItem)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(historyItem: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hiển thị thông tin sản phẩm
            val items = historyItem["items"] as? List<Map<String, Any>>
            val totalPrice = historyItem["totalPrice"] as? Long ?: 0L
            val timestamp = historyItem["timestamp"] as? Long ?: 0L

            Text(
                text = "Tổng giá: $totalPrice VND",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Ngày giao dịch: ${formatTimestamp(timestamp)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            items?.forEach { item ->
                val productName = item["tenSanPham"] as? String ?: "N/A"
                val quantity = (item["quantity"] as? Long)?.toInt() ?: 0

                Text(
                    text = "• $productName (Số lượng: $quantity)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Hàm định dạng timestamp thành ngày giờ dễ đọc
fun formatTimestamp(timestamp: Long): String {
    return java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(timestamp))
}
