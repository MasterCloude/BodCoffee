package com.example.bodcoffee.Screen

import com.example.bodcoffee.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bodcoffee.Model.ProductViewModel
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Dữ liệu sản phẩm
data class Product(
    val id: Int,
    val loaiSanPham: String,
    val tenSanPham: String,
    val moTaSanPham: String,
    val noiDungSanPham: String,
    val giaSanPham: Int,
    val anhSanPham: String,
    val danhGiaSanPham: String
)

// API Interface
interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}

// Retrofit Client
val retrofitClient: ApiService by lazy {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    Retrofit.Builder()
        .baseUrl("https://673cc12196b8dcd5f3fb7626.mockapi.io/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}

// Màn hình HomeScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, productViewModel: ProductViewModel) {
    val products = productViewModel.products
    val isLoading = productViewModel.isLoading
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val categories = listOf("Tất cả", "Cà phê", "Trà", "Nước hoa quả", "Đồ ăn vặt")

    if (isLoading) {
        CenteredText(text = "Đang tải dữ liệu...")
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("Tìm kiếm sản phẩm") }
                            )
                        } else {
                            Text("Sản phẩm")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Filled.Search, contentDescription = "Tìm kiếm")
                        }

                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = "Lọc sản phẩm"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            selectedFilter = category
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Slideshow(
                    imageIds = listOf(
                        R.drawable.img,
                        R.drawable.img_1,
                        R.drawable.img_2,
                        R.drawable.img_3
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )

                val filteredProducts = products.filter {
                    (searchQuery.text.isEmpty() || it.tenSanPham.contains(searchQuery.text, ignoreCase = true)) &&
                            (selectedFilter == "Tất cả" || it.loaiSanPham == selectedFilter)
                }

                ProductList(
                    products = filteredProducts,
                    onBuyClick = { product ->
                        navController.navigate("product_detail/${product.id}")
                    }
                )
            }
        }
    }
}



@Composable
fun Slideshow(imageIds: List<Int>, modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Thời gian chờ 3 giây
            currentIndex = (currentIndex + 1) % imageIds.size
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageIds[currentIndex]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProductList(products: List<Product>, onBuyClick: (Product) -> Unit) {
    if (products.isEmpty()) {
        CenteredText(text = "Không tìm thấy sản phẩm.")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(product, onBuyClick)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onBuyClick: (Product) -> Unit) {
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
            AsyncImage(
                model = product.anhSanPham,
                contentDescription = product.tenSanPham,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
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
                Text(
                    text = "Đánh giá: ${product.danhGiaSanPham}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = { onBuyClick(product) }) {
                Text("Mua")
            }
        }
    }
}


@Composable
fun CenteredText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineLarge)
    }
}