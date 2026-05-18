package com.uilover.project261.screens.detailProduct

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.uilover.project261.Helper.ManagmentCart
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel
import com.uilover.project261.viewModel.MainViewModel
import com.uilover.project261.viewModel.RatingViewModel

@Composable
fun DetailScreen(
    item: ProductModel,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onBuyNowClick: () -> Unit = {},
    viewModel: MainViewModel,
    onOpenDetail: (ProductModel) -> Unit,
    favoritesViewModel: com.uilover.project261.viewModel.FavoritesViewModel? = null,
    authViewModel: com.uilover.project261.viewModel.AuthViewModel? = null,
    ratingViewModel: RatingViewModel? = null
) {
    val context = LocalContext.current
    val managmentCart = remember { ManagmentCart(context) }
    var numberInCart by remember { mutableIntStateOf(1) } // Default quantity = 1

    val currentUser = authViewModel?.currentUser?.collectAsState()?.value
    val isFavorite = favoritesViewModel?.isFavorite(item.id) ?: false

    val currentUserId = currentUser?.uid

    // FIXED LaunchedEffect:
    // 1. Key on item.id + currentUserId (String values, not objects)
    // 2. Do NOT call reset()/clearProduct() here — that caused the reload loop.
    //    reset() was wiping currentProductId → guard never skipped → observer
    //    restarted on every recomposition. Now loadRatings() guards by VALUE.
    // 3. clearProduct() is called only in BackHandler (navigate away).
    androidx.compose.runtime.LaunchedEffect(item.id, currentUserId) {
        ratingViewModel?.loadRatings(item.id, currentUserId)
    }

    BackHandler(enabled = true) {
        // Clear rating state when navigating away — not on recomposition
        ratingViewModel?.clearProduct()
        viewModel.selectedProduct(null)
        onBackClick()
    }

    // Product variant selection states
    var selectedCapacity by remember { mutableStateOf(item.selectedCapacity) }
    var selectedWeight by remember { mutableStateOf(item.selectedWeight) }
    var selectedColor by remember { mutableStateOf(item.selectedColor) }


    ConstraintLayout {
        val (footer, column) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_light))
                .verticalScroll(rememberScrollState())
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 120.dp)
        ) {
            // Image Gallery with Pager
            ImageGallerySection(
                item = item,
                onBackClick = {
                    viewModel.selectedProduct(null)
                    onBackClick()
                },
                isFavorite = isFavorite,
                onToggleFavorite = {
                    if (currentUser != null && favoritesViewModel != null) {
                        favoritesViewModel.toggleFavorite(currentUser.uid, item)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Info Card
            ProductInfoCard(
                item = item,
                numberInCart = numberInCart,
                onIncrement = {
                    numberInCart++
                    item.numberInCart = numberInCart
                },
                onDecrement = {
                    if (numberInCart > 1) {
                        numberInCart--
                        item.numberInCart = numberInCart
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Options Selector (Shopee/Lazada style)
            if (item.availableCapacities.isNotEmpty() ||
                item.availableWeights.isNotEmpty() ||
                item.availableColors.isNotEmpty()) {

                ProductOptionsSelector(
                    availableCapacities = item.availableCapacities,
                    availableWeights = item.availableWeights,
                    availableColors = item.availableColors,
                    selectedCapacity = selectedCapacity,
                    selectedWeight = selectedWeight,
                    selectedColor = selectedColor,
                    onCapacitySelected = { selectedCapacity = it },
                    onWeightSelected = { selectedWeight = it },
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Product Details Card
            DescriptionSection(item.description)

            Spacer(modifier = Modifier.height(8.dp))

            // ⭐ Star Rating Section
            if (ratingViewModel != null) {
                RatingSection(
                    ratingViewModel = ratingViewModel,
                    userId = currentUser?.uid
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recommended Products
            RecommendedList(
                viewModel = viewModel,
                onItemClick = onOpenDetail
            )
        }

        FooterSection(
            onAddToCartClick = {
                // Update item with selected options before adding to cart
                item.selectedCapacity = selectedCapacity
                item.selectedWeight = selectedWeight
                item.selectedColor = selectedColor
                managmentCart.insertItem(item)
                onAddToCartClick()
            },
            onBuyNowClick = {
                // Update item with selected options before buying
                item.selectedCapacity = selectedCapacity
                item.selectedWeight = selectedWeight
                item.selectedColor = selectedColor
                onBuyNowClick()
            },
            totalPrice = (item.price * numberInCart),
            modifier = Modifier.constrainAs(footer) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

