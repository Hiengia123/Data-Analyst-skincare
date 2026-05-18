package com.uilover.project261.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.R
import com.uilover.project261.domain.OrderItem
import com.uilover.project261.domain.ShippingAddress
import com.uilover.project261.viewModel.AuthViewModel
import com.uilover.project261.viewModel.CheckoutState
import com.uilover.project261.viewModel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    checkoutViewModel: CheckoutViewModel,
    authViewModel: AuthViewModel,
    orderItems: List<OrderItem>,
    onBack: () -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    val checkoutState by checkoutViewModel.checkoutState.collectAsState()
    val totalPrice by checkoutViewModel.totalPrice.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Form state
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("TP. Hồ Chí Minh") }
    var district by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("cod") }

    // Set order items when screen loads
    LaunchedEffect(orderItems) {
        checkoutViewModel.setOrderItems(orderItems)
    }

    // Handle checkout state
    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            is CheckoutState.Success -> {
                val orderId = (checkoutState as CheckoutState.Success).orderId
                Toast.makeText(context, "Đặt hàng thành công! Mã đơn: $orderId", Toast.LENGTH_LONG).show()
                checkoutViewModel.resetState() // Reset state after success
                onOrderSuccess(orderId)
            }
            is CheckoutState.Error -> {
                Toast.makeText(
                    context,
                    (checkoutState as CheckoutState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                checkoutViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thanh toán",
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
        },
        bottomBar = {
            // Total and Place Order Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tổng thanh toán",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = CurrencyFormatter.formatVND(totalPrice),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.primary_pink)
                            )
                        }

                        Button(
                            onClick = {
                                when {
                                    name.isBlank() -> Toast.makeText(context, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
                                    phone.isBlank() -> Toast.makeText(context, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
                                    address.isBlank() -> Toast.makeText(context, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show()
                                    district.isBlank() -> Toast.makeText(context, "Vui lòng nhập quận/huyện", Toast.LENGTH_SHORT).show()
                                    else -> {
                                        val shippingAddress = ShippingAddress(
                                            name = name,
                                            phone = phone,
                                            address = address,
                                            city = city,
                                            district = district,
                                            ward = ward
                                        )
                                        checkoutViewModel.createOrder(
                                            userId = currentUser?.uid ?: "",
                                            shippingAddress = shippingAddress,
                                            paymentMethod = selectedPayment,
                                            note = note
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .width(160.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.primary_pink)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = checkoutState !is CheckoutState.Loading
                        ) {
                            if (checkoutState is CheckoutState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Đặt hàng",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // Shipping Address Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = colorResource(R.color.primary_pink),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Địa chỉ giao hàng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Name field
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Họ và tên") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.primary_pink),
                                focusedLabelColor = colorResource(R.color.primary_pink)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phone field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Số điện thoại") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.primary_pink),
                                focusedLabelColor = colorResource(R.color.primary_pink)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Address field
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Địa chỉ cụ thể (số nhà, tên đường)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.primary_pink),
                                focusedLabelColor = colorResource(R.color.primary_pink)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Ward field
                            OutlinedTextField(
                                value = ward,
                                onValueChange = { ward = it },
                                label = { Text("Phường/Xã") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorResource(R.color.primary_pink),
                                    focusedLabelColor = colorResource(R.color.primary_pink)
                                )
                            )

                            // District field
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("Quận/Huyện") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorResource(R.color.primary_pink),
                                    focusedLabelColor = colorResource(R.color.primary_pink)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // City field (pre-filled)
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Tỉnh/Thành phố") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.primary_pink),
                                focusedLabelColor = colorResource(R.color.primary_pink)
                            )
                        )
                    }
                }
            }

            // Product List Section
            item {
                Text(
                    text = "Sản phẩm đã chọn",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(orderItems) { item ->
                ProductCheckoutItem(item)
            }

            // Payment Method Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Phương thức thanh toán",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // COD option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPayment == "cod",
                                onClick = { selectedPayment = "cod" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.primary_pink)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Thanh toán khi nhận hàng (COD)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Thanh toán bằng tiền mặt khi nhận hàng",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Note Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ghi chú đơn hàng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Ghi chú cho người bán (tùy chọn)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.primary_pink),
                                focusedLabelColor = colorResource(R.color.primary_pink)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCheckoutItem(item: OrderItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Image
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Variants
                if (item.selectedColor != null || item.selectedWeight != null || item.selectedCapacity != null) {
                    Text(
                        text = buildString {
                            item.selectedColor?.let { append("Màu: $it  ") }
                            item.selectedWeight?.let { append("Size: $it  ") }
                            item.selectedCapacity?.let { append("Dung tích: $it") }
                        },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = CurrencyFormatter.formatVND(item.price),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.primary_pink)
                    )

                    Text(
                        text = "x${item.quantity}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

