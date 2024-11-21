package com.example.bodcoffee.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bodcoffee.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

// Data class to represent a product
data class Product(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val price: Int = 0
)

@Composable
fun HomeScreen() {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch products from Firebase
    LaunchedEffect(Unit) {
        try {
            val fetchedProducts = fetchProductsFromFirebase()
            products = fetchedProducts
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        CenteredText(text = "Loading...")
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Slideshow at the top
            Slideshow(
                images = listOf(
                    R.drawable.img,
                    R.drawable.img_1,
                    R.drawable.img_2,
                    R.drawable.img_3,
                    R.drawable.img_4,
                    R.drawable.img_5
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Product List
            ProductList(products)
        }
    }
}

@Composable
fun Slideshow(images: List<Int>, modifier: Modifier = Modifier) {
    var currentImageIndex by remember { mutableStateOf(0) }

    // Automatically switch images
    LaunchedEffect(key1 = currentImageIndex) {
        while (true) {
            delay(3000) // Wait for 3 seconds before changing the image
            currentImageIndex = (currentImageIndex + 1) % images.size
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = images[currentImageIndex]),
            contentDescription = "Slideshow Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProductList(products: List<Product>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
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
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Price: ${product.price} $",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(onClick = { /* Handle buy action */ }) {
                Text("Buy")
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

// Function to fetch products from Firebase Firestore
suspend fun fetchProductsFromFirebase(): List<Product> {
    val db = FirebaseFirestore.getInstance()
    val snapshot = db.collection("products").get().await()
    return snapshot.documents.mapNotNull { doc ->
        doc.toObject(Product::class.java)?.copy(id = doc.id)
    }
}
