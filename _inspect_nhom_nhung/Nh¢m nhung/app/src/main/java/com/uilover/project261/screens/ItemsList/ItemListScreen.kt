package com.uilover.project261.screens.ItemsList


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel
import com.uilover.project261.viewModel.MainViewModel

private const val TAG = "ItemListScreen"

@Composable
fun ItemListScreen(
    title: String,
    id: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onOpenDetail: (ProductModel) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Load all products for this brand
    val allBrandProducts by viewModel.loadFiltered(id).observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Price filter state
    var selectedPriceRange by remember { mutableStateOf<PriceRange?>(PriceRange("Tất cả", 0.0, Double.MAX_VALUE)) }

    LaunchedEffect(id) { viewModel.loadFiltered(id) }
    LaunchedEffect(allBrandProducts) {
        isLoading = allBrandProducts.isEmpty()
        Log.d(TAG, "Brand '$title' products loaded: ${allBrandProducts.size}")
    }

    // Filter products based on search query AND price range
    val filteredProducts = allBrandProducts
        .filter { product ->
            // Search filter
            if (searchQuery.isEmpty()) {
                true
            } else {
                val query = searchQuery.lowercase().trim()
                val titleMatch = product.title.lowercase().contains(query)
                val keywordMatch = product.keywords.any { it.lowercase().contains(query) }
                val typeMatch = product.productType.lowercase().contains(query)
                titleMatch || keywordMatch || typeMatch
            }
        }
        .filter { product ->
            // Price filter
            selectedPriceRange?.let { range ->
                product.price >= range.minPrice && product.price < range.maxPrice
            } ?: true
        }
        .also { results ->
            Log.d(TAG, "Found ${results.size} products in '$title' after filters")
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_light))
    ) {
        // Header with back button and title
        ConstraintLayout(
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                .background(Color.White)
        ) {
            val (backBtn, cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt) {
                        centerTo(parent)
                    },
                textAlign = TextAlign.Center,
                text = title,
                fontSize = 20.sp,
                color = colorResource(R.color.text_primary),
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

        // Search bar for this brand's products
        BrandSearchBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onClearClick = { searchQuery = "" },
            brandName = title
        )

        // Price filter (Shopee/Lazada style)
        PriceFilterBar(
            selectedRange = selectedPriceRange,
            onRangeSelected = { range ->
                selectedPriceRange = range
                Log.d(TAG, "Price filter changed: ${range?.label}")
            }
        )

        // Product list or loading indicator
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = colorResource(R.color.primary_pink)
                )
            }
        } else {
            if (filteredProducts.isEmpty() && (searchQuery.isNotEmpty() || selectedPriceRange?.label != "Tất cả")) {
                // No results for search or filter
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "😔",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không tìm thấy sản phẩm",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.text_primary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildString {
                                append("Không có sản phẩm $title phù hợp")
                                if (searchQuery.isNotEmpty()) {
                                    append(" với \"$searchQuery\"")
                                }
                                if (selectedPriceRange?.label != "Tất cả") {
                                    append(" trong khoảng giá ${selectedPriceRange?.label}")
                                }
                            },
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_secondary),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Show filtered products
                ItemsList(
                    items = filteredProducts,
                    onItemClick = { product -> onOpenDetail(product) },
                    onAddToCart = { product ->
                        val managmentCart = com.uilover.project261.Helper.ManagmentCart(context)
                        managmentCart.insertItem(product)
                    }
                )
            }
        }
    }
}

@Composable
fun BrandSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
    brandName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    text = "Tìm sản phẩm $brandName...",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = colorResource(R.color.text_hint)
                )
            },
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            color = colorResource(R.color.text_secondary),
                            modifier = Modifier
                                .clickable { onClearClick() }
                                .padding(horizontal = 8.dp)
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                }
            },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = colorResource(R.color.primary_pink),
                unfocusedBorderColor = colorResource(R.color.border_color),
                textColor = colorResource(R.color.text_primary),
                cursorColor = colorResource(R.color.primary_pink)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            singleLine = true
        )
    }
}
