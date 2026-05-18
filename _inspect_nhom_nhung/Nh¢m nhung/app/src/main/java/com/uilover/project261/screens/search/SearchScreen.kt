package com.uilover.project261.screens.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel
import com.uilover.project261.screens.dashboard.ProductItemCardGrid
import com.uilover.project261.viewModel.MainViewModel

private const val TAG = "SearchScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load all products once
    val allProducts by viewModel.loadAllProducts().observeAsState(initial = emptyList())

    // Simple logging
    LaunchedEffect(allProducts) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "PRODUCTS LOADED: ${allProducts.size}")
        allProducts.forEachIndexed { index, product ->
            Log.d(TAG, "Product $index: ${product.title}")
            Log.d(TAG, "  - Keywords: ${product.keywords}")
            Log.d(TAG, "  - Type: ${product.productType}")
        }
        Log.d(TAG, "========================================")
    }

    // Direct filtering - NO remember, NO derivedStateOf
    val searchResults = if (searchQuery.isEmpty()) {
        emptyList()
    } else {
        val query = searchQuery.lowercase().trim()
        Log.d(TAG, "FILTERING for: '$query'")

        allProducts.filter { product ->
            val titleMatch = product.title.lowercase().contains(query)
            val categoryMatch = product.categoryTitle.lowercase().contains(query)
            val keywordMatch = product.keywords.any { it.lowercase().contains(query) }
            val typeMatch = product.productType.lowercase().contains(query)

            val isMatch = titleMatch || categoryMatch || keywordMatch || typeMatch

            if (isMatch) {
                Log.d(TAG, "  ✓ MATCH: ${product.title}")
            }

            isMatch
        }.also { results ->
            Log.d(TAG, "TOTAL MATCHES: ${results.size}")
        }
    }

    // Log whenever searchQuery changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            Log.d(TAG, "Search query: '$searchQuery' → Found ${searchResults.size} products")
        }
    }

    // Full screen container with solid background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Solid white background to cover everything
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_light))
                .statusBarsPadding() // Add padding for status bar
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { newQuery ->
                    Log.d(TAG, "User typed: '$newQuery'")
                    searchQuery = newQuery
                },
                onBackClick = onBackClick,
                onClearClick = {
                    searchQuery = ""
                },
                onSearch = {
                    keyboardController?.hide()
                }
            )

        // Debug info
        if (allProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(colorResource(R.color.warning).copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "⚠️ Đang tải sản phẩm từ Firebase...\nNếu thấy thông báo này lâu, kiểm tra kết nối Internet.",
                    color = colorResource(R.color.warning)
                )
            }
        }

        // Search Results or Suggestions
        if (searchQuery.isEmpty()) {
            SearchSuggestions(
                onSuggestionClick = { suggestion ->
                    searchQuery = suggestion
                }
            )
        } else {
            if (searchResults.isEmpty()) {
                EmptySearchResults(query = searchQuery)
            } else {
                SearchResults(
                    products = searchResults,
                    onProductClick = onProductClick
                )
            }
        }
        } // End Column
    } // End Box
}

/**
 * Client-side search function
 * Chạy mỗi khi user gõ vào ô search
 */
fun searchProduct(keyword: String, allProducts: List<ProductModel>): List<ProductModel> {
    // Nếu keyword rỗng, trả về list rỗng (hiển thị suggestions)
    if (keyword.isEmpty()) return emptyList()

    // Chuẩn hóa từ khóa tìm kiếm về chữ thường
    val query = keyword.lowercase().trim()

    Log.d(TAG, "Searching for: '$query' in ${allProducts.size} products")

    // Lọc sản phẩm dựa trên keyword
    val results = allProducts.filter { product ->
        // 1. Chuẩn hóa tên sản phẩm về chữ thường
        val title = product.title.lowercase()

        // 2. Chuẩn hóa category
        val categoryTitle = product.categoryTitle.lowercase()

        // 3. Chuẩn hóa categoryId
        val categoryId = product.categoryId.lowercase()

        // 4. Tìm trong keywords
        val matchesKeywords = product.keywords.any { it.lowercase().contains(query) }

        // 5. Tìm trong productType (son, sua_rua_mat, kem_chong_nang)
        val productType = product.productType.lowercase()

        // Logic tìm kiếm: Tìm trong Tên HOẶC Category HOẶC Keywords HOẶC Type
        val matches = title.contains(query) ||
                     categoryTitle.contains(query) ||
                     categoryId.contains(query) ||
                     matchesKeywords ||
                     productType.contains(query)

        if (matches) {
            Log.d(TAG, "✓ Match: ${product.title}")
        }

        matches
    }

    Log.d(TAG, "→ Found ${results.size} products for '$query'")
    return results
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp), // Reduced horizontal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button - Yellow circular background like homepage
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onBackClick() },
                shape = CircleShape,
                color = colorResource(R.color.accent_gold) // Yellow/golden background
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Quay lại",
                        tint = Color.Black, // Black arrow icon
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search input field
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp), // Fixed height
                placeholder = {
                    Text(
                        "Tìm kiếm sản phẩm...",
                        color = colorResource(R.color.text_hint),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.light_gray),
                    unfocusedContainerColor = colorResource(R.color.light_gray),
                    disabledContainerColor = colorResource(R.color.light_gray),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = colorResource(R.color.text_primary),
                    unfocusedTextColor = colorResource(R.color.text_primary)
                ),
                shape = RoundedCornerShape(24.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Text(
                                "✕",
                                fontSize = 18.sp,
                                color = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                    }
                )
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun SearchSuggestions(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tìm kiếm phổ biến",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val suggestions = listOf(
            "son",
            "Son Dior",
            "Chanel",
            "MAC",
            "Rare Beauty",
            "Kem chống nắng",
            "Sữa rửa mặt",
            "Son lì",
            "Má hồng"
        )

        suggestions.forEach { suggestion ->
            SuggestionItem(
                text = suggestion,
                onClick = { onSuggestionClick(suggestion) }
            )
        }
    }
}

@Composable
fun SuggestionItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔍",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary)
        )
    }
}

@Composable
fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Không tìm thấy kết quả",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Không có sản phẩm nào phù hợp với \"$query\"",
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Thử tìm kiếm với từ khóa khác",
            fontSize = 14.sp,
            color = colorResource(R.color.text_hint)
        )
    }
}

@Composable
fun SearchResults(
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Results count
        Text(
            text = "Tìm thấy ${products.size} sản phẩm",
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary),
            modifier = Modifier.padding(16.dp)
        )

        // Products grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductItemCardGrid(
                    item = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

