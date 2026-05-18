package com.uilover.project261.screens.orders

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.uilover.project261.Helper.CurrencyFormatter
import com.uilover.project261.R
import com.uilover.project261.domain.OrderModel
import com.uilover.project261.viewModel.OrderState
import com.uilover.project261.viewModel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    order: OrderModel,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onCancelSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val orderState by orderViewModel.orderState.collectAsState()
    val selectedOrder by orderViewModel.selectedOrder.collectAsState()
    val displayOrder = selectedOrder ?: order
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    var cancelReasonError by remember { mutableStateOf(false) }
    LaunchedEffect(orderState) {
        when (orderState) {
            is OrderState.CancelSuccess -> {
                Toast.makeText(context, "Đơn hàng đã được hủy thành công", Toast.LENGTH_SHORT).show()
                orderViewModel.resetCancelState()
                onCancelSuccess()
            }
            is OrderState.Error -> {
                Toast.makeText(context, (orderState as OrderState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }
    if (showCancelDialog) {
        Dialog(onDismissRequest = { showCancelDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Hủy đơn hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    }
                    Text(text = "Bạn có chắc muốn hủy đơn hàng?  ${displayOrder.orderId}?", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                    Text(text = "Lý do hủy đơn *", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it; cancelReasonError = false },
                        placeholder = { Text("Nhập lý do hủy đơn hàng...", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.primary_pink),
                            unfocusedBorderColor = if (cancelReasonError) Color.Red else Color.LightGray
                        ),
                        isError = cancelReasonError,
                        maxLines = 4
                    )
                    if (cancelReasonError) {
                        Text(text = "Vui lòng nhập lý do hủy đơn", fontSize = 12.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { showCancelDialog = false; cancelReason = ""; cancelReasonError = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                        ) {
                            Text("Quay lại", fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {
                                if (cancelReason.isBlank()) {
                                    cancelReasonError = true
                                } else {
                                    showCancelDialog = false
                                    orderViewModel.cancelOrder(
                                        orderId = displayOrder.orderId,
                                        userId = displayOrder.userId,
                                        reason = cancelReason.trim()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            enabled = orderState !is OrderState.Loading
                        ) {
                            if (orderState is OrderState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Xác nhận hủy", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiet don hang", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "Ma don hang", fontSize = 13.sp, color = Color.Gray)
                                Text(text = displayOrder.orderId, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.primary_pink))
                            }
                            OrderStatusChip(status = displayOrder.status)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Ngay dat: ${formatDate(displayOrder.createdAt)}", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            if (displayOrder.status == "cancelled" && displayOrder.cancelReason.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Lý do hủy đơn", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            }
                            Text(text = displayOrder.cancelReason, fontSize = 14.sp, color = Color(0xFFB71C1C))
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = colorResource(R.color.primary_pink), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Dia chi giao hang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Nguoi nhan", fontSize = 12.sp, color = Color.Gray)
                                Text(text = displayOrder.shippingAddress.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "So dien thoai", fontSize = 12.sp, color = Color.Gray)
                                Text(text = displayOrder.shippingAddress.phone, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Dia chi", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = buildString {
                                        append(displayOrder.shippingAddress.address)
                                        if (displayOrder.shippingAddress.ward.isNotBlank()) append(", ${displayOrder.shippingAddress.ward}")
                                        append(", ${displayOrder.shippingAddress.district}")
                                        append(", ${displayOrder.shippingAddress.city}")
                                    },
                                    fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(text = "San pham (${displayOrder.items.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
            }
            items(displayOrder.items.values.toList()) { item ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Card(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(8.dp)) {
                            Image(painter = rememberAsyncImagePainter(item.image), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            val variantText = buildString {
                                item.selectedColor?.takeIf { it.isNotBlank() }?.let { append("Mau: $it  ") }
                                item.selectedWeight?.takeIf { it.isNotBlank() }?.let { append("Size: $it  ") }
                                item.selectedCapacity?.takeIf { it.isNotBlank() }?.let { append("Dung tich: $it") }
                            }
                            if (variantText.isNotBlank()) {
                                Text(text = variantText, fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = CurrencyFormatter.formatVND(item.price), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.primary_pink))
                                Text(text = "x${item.quantity}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Thong tin thanh toan", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Phuong thuc thanh toan", fontSize = 14.sp, color = Color.Gray)
                            Text(text = when (displayOrder.paymentMethod) { "cod" -> "Thanh toan khi nhan hang"; "card" -> "The tin dung"; "momo" -> "Vi MoMo"; else -> "Khac" }, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Tong tien hang", fontSize = 14.sp, color = Color.Gray)
                            Text(text = CurrencyFormatter.formatVND(displayOrder.totalPrice), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Phi van chuyen", fontSize = 14.sp, color = Color.Gray)
                            Text(text = "Mien phi", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorResource(R.color.primary_pink))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Tong thanh toan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = CurrencyFormatter.formatVND(displayOrder.totalPrice), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.primary_pink))
                        }
                    }
                }
            }
            if (displayOrder.note.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = "Ghi chu don hang", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                            Text(text = displayOrder.note, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
            if (displayOrder.status == "pending") {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Hủy đơn hàng", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
