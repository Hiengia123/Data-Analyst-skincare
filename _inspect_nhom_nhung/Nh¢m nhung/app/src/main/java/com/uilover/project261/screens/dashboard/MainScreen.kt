package com.uilover.project261.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uilover.project261.R
import com.uilover.project261.domain.BannerModel
import com.uilover.project261.domain.CategoryModel
import com.uilover.project261.domain.ProductModel
import com.uilover.project261.viewModel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    authViewModel: com.uilover.project261.viewModel.AuthViewModel,
    onOpenItems: (id: String, title: String) -> Unit,
    onOpenDetail: (ProductModel) -> Unit,
    onOpenSearch: () -> Unit = {},
    onOpenCart: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
    onOpenFavorites: () -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()

    val banners = remember { mutableStateListOf<BannerModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    val recommendedProducts = remember { mutableStateListOf<ProductModel>() }

    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showRecommendedLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadBanners().observeForever {
            banners.clear()
            banners.addAll(it)
            showBannerLoading = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategory().observeForever {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadRecommendedProducts().observeForever {
            recommendedProducts.clear()
            recommendedProducts.addAll(it)
            showRecommendedLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            MyBottomBar(
                onCartClick = onOpenCart,
                onProfileClick = onOpenProfile,
                onOrderClick = onOpenOrders,
                onFavoriteClick = onOpenFavorites,
                currentRoute = "Home"
            )
        },
        scaffoldState = scaffoldState,
        backgroundColor = colorResource(R.color.background_light)
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_light))
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            item(span = { GridItemSpan(2) })
            {
                TopBar(
                    onSearchClick = onOpenSearch,
                    onCartClick = onOpenCart,
                    onProfileClick = onOpenProfile,
                    authViewModel = authViewModel
                )
            }

            item(span = { GridItemSpan(2) }) {
                CategorySection(
                    categories = categories,
                    showCategoryLoading = showCategoryLoading,
                    onCategoryClick = { cat -> onOpenItems(cat.id, cat.title) }
                )
            }

            item(
                span = { GridItemSpan(2) }) {
                Text(
                    text = "Sản phẩm cho bạn",
                    color = colorResource(R.color.text_primary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
                )

            }
            if (showRecommendedLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(R.color.primary_pink))
                    }
                }
            } else {
                items(recommendedProducts.size) { index ->
                    ProductItemCardGrid(
                        item = recommendedProducts[index],
                        onClick = { onOpenDetail(recommendedProducts[index]) }
                    )

                }
            }
        }
    }
}