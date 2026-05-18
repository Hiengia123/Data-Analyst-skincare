package com.uilover.project261.domain

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val status: String = "pending", // pending, shipping, delivered, cancelled
    val totalPrice: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val shippingAddress: ShippingAddress = ShippingAddress(),
    val items: Map<String, OrderItem> = emptyMap(),
    val paymentMethod: String = "cod", // cod (cash on delivery), card, momo
    val note: String = "",
    val cancelReason: String = ""
)

data class OrderItem(
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val image: String = "",
    val selectedColor: String? = null,
    val selectedWeight: String? = null,
    val selectedCapacity: String? = null
)

data class ShippingAddress(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val district: String = "",
    val ward: String = ""
)

