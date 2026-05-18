package com.uilover.project261.screens.orders

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.R
import com.uilover.project261.domain.OrderModel
import com.uilover.project261.viewModel.AuthViewModel
import com.uilover.project261.viewModel.OrderViewModel
import com.uilover.project261.viewModel.OrderState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onOrderClick: (OrderModel) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    val orders by orderViewModel.orders.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Đang xử lý", "Đang giao", "Hoàn thành", "Đã hủy")

    // Load orders when screen opens
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            orderViewModel.loadUserOrders(currentUser!!.uid)
        }
    }

    // Handle order state
    LaunchedEffect(orderState) {
        when (val state = orderState) {
            is OrderState.Error -> {
                val errorMessage = state.message
                Log.e("OrderHistoryScreen", "Error loading orders: $errorMessage")
                Toast.makeText(
                    context,
                    "Lỗi tải đơn hàng: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    // Check if user is logged in
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Vui lòng đăng nhập để xem đơn hàng",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.primary_pink)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Đăng nhập ngay")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Back to homepage button
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(R.color.primary_pink)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colorResource(R.color.primary_pink)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quay về trang chủ")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đơn hàng của tôi",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = colorResource(R.color.primary_pink),
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content
            when {
                orderState is OrderState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(R.color.primary_pink)
                        )
                    }
                }
                orders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "📦",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chưa có đơn hàng nào",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Hãy mua sắm ngay!",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    // Filter orders based on selected tab
                    val filteredOrders = when (selectedTab) {
                        0 -> orders // All
                        1 -> orders.filter { it.status == "pending" } // Processing
                        2 -> orders.filter { it.status == "shipping" } // Shipping
                        3 -> orders.filter { it.status == "delivered" } // Completed
                        4 -> orders.filter { it.status == "cancelled" } // Cancelled
                        else -> orders
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không có đơn hàng",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders) { order ->
                                OrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderId,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.primary_pink)
                )

                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // First product preview (show first item)
            if (order.items.isNotEmpty()) {
                val firstItem = order.items.values.first()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Product image
                    Card(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(firstItem.image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = firstItem.title,
                            fontSize = 14.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "x${firstItem.quantity}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        if (order.items.size > 1) {
                            Text(
                                text = "+${order.items.size - 1} sản phẩm khác",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Text(
                        text = CurrencyFormatter.formatVND(firstItem.price),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Total and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tổng cộng:",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = CurrencyFormatter.formatVND(order.totalPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )
                }

                Text(
                    text = formatDate(order.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: String) {
    val (text, backgroundColor, textColor) = when (status) {
        "pending" -> Triple("Đang xử lý", Color(0xFFFFF3E0), Color(0xFFFF6F00))
        "shipping" -> Triple("Đang giao", Color(0xFFE3F2FD), Color(0xFF1976D2))
        "delivered" -> Triple("Hoàn thành", Color(0xFFE8F5E9), Color(0xFF388E3C))
        "cancelled" -> Triple("Đã hủy", Color(0xFFFFEBEE), Color(0xFFD32F2F))
        else -> Triple("Không xác định", Color.LightGray, Color.Black)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

