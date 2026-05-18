package com.uilover.project261.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.uilover.project261.Helper.ChangeNumberItemsListener
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.Helper.ManagmentCart
import com.uilover.project261.R
import com.uilover.project261.domain.ProductModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit = {},
    onProductClick: (ProductModel) -> Unit = {}
) {
    val context = LocalContext.current
    val managmentCart = remember { ManagmentCart(context) }

    var cartItems by remember { mutableStateOf(managmentCart.getListCart()) }
    var totalPrice by remember { mutableStateOf(managmentCart.getTotalFee()) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val changeListener = remember {
        object : ChangeNumberItemsListener {
            override fun onChanged() {
                cartItems = ArrayList(managmentCart.getListCart()) // Create new list
                totalPrice = managmentCart.getTotalFee()
                refreshKey++ // Force recomposition
            }
        }
    }

    Scaffold(
        topBar = {
            CartTopBar(
                onBackClick = onBackClick,
                itemCount = cartItems.size
            )
        },
        bottomBar = {
            CartBottomBar(
                totalPrice = totalPrice,
                onCheckoutClick = onCheckoutClick
            )
        },
        containerColor = colorResource(R.color.background_light),
        modifier = Modifier.statusBarsPadding() // Add system bars padding
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCart(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
            ) {
                itemsIndexed(
                    items = cartItems,
                    key = { index, item -> "${item.id}_${item.selectedWeight}_${item.selectedCapacity}_${item.selectedColor}_${index}_$refreshKey" }
                ) { index, item ->
                    CartItemCard(
                        item = item,
                        onPlusClick = {
                            // Get fresh list from storage to avoid index issues
                            val freshList = managmentCart.getListCart()
                            managmentCart.plusItem(freshList, index, changeListener)
                        },
                        onMinusClick = {
                            // Get fresh list from storage to avoid index issues
                            val freshList = managmentCart.getListCart()
                            managmentCart.minusItem(freshList, index, changeListener)
                        },
                        onProductClick = {
                            onProductClick(item)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CartTopBar(
    onBackClick: () -> Unit,
    itemCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Removed extra top padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button - LARGER
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Quay lại",
                modifier = Modifier
                    .size(32.dp)  // Increased from 24dp to 32dp
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Giỏ hàng",
                fontSize = 20.sp,  // Increased from 18sp
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "($itemCount)",
                fontSize = 18.sp,  // Increased from 16sp
                color = colorResource(R.color.text_secondary)
            )
        }
    }
}

@Composable
fun CartItemCard(
    item: ProductModel,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onProductClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onProductClick() }, // Make the whole card clickable
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Product Image
                AsyncImage(
                    model = item.image,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorResource(R.color.light_gray)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Product Details Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    // Product Title
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.text_primary),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price
                    Text(
                        text = CurrencyFormatter.formatVND(item.price),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }
            }

            // Selected Options Row
            if (item.selectedWeight.isNotEmpty() ||
                item.selectedCapacity.isNotEmpty() ||
                item.selectedColor.isNotEmpty()) {

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (item.selectedWeight.isNotEmpty()) {
                        ProductVariantChip(
                            label = "Khối lượng",
                            value = item.selectedWeight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (item.selectedCapacity.isNotEmpty()) {
                        ProductVariantChip(
                            label = "Dung tích",
                            value = item.selectedCapacity
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (item.selectedColor.isNotEmpty()) {
                        ProductVariantChip(
                            label = "Màu",
                            value = item.selectedColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Controls and Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.border_color),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    // Minus Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onMinusClick() }
                            .background(
                                color = colorResource(R.color.light_gray),
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.text_primary)
                        )
                    }

                    // Quantity Display
                    Text(
                        text = item.numberInCart.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.text_primary),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    // Plus Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onPlusClick() }
                            .background(
                                color = colorResource(R.color.primary_pink),
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Total Price for this item
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Tổng",
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_secondary)
                    )
                    Text(
                        text = CurrencyFormatter.formatVND(item.price * item.numberInCart),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductVariantChip(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(R.color.primary_pink).copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.primary_pink).copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            color = colorResource(R.color.text_secondary)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.primary_pink)
        )
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    onCheckoutClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Total Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tổng thanh toán",
                        fontSize = 13.sp,
                        color = colorResource(R.color.text_secondary)
                    )
                    Text(
                        text = CurrencyFormatter.formatVND(totalPrice),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }

                // Checkout Button
                Button(
                    onClick = onCheckoutClick,
                    modifier = Modifier
                        .height(50.dp)
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.primary_pink)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Mua hàng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCart(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🛒",
            fontSize = 80.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Giỏ hàng trống",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hãy thêm sản phẩm vào giỏ hàng nhé!",
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary)
        )
    }
}

