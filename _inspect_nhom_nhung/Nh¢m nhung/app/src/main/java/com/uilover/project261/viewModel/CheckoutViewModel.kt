package com.uilover.project261.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project261.Repository.OrderRepository
import com.uilover.project261.domain.OrderItem
import com.uilover.project261.domain.OrderModel
import com.uilover.project261.domain.ShippingAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    fun setOrderItems(items: List<OrderItem>) {
        _orderItems.value = items
        calculateTotal()
    }

    private fun calculateTotal() {
        val total = _orderItems.value.sumOf { it.price * it.quantity }
        _totalPrice.value = total
    }

    fun createOrder(
        userId: String,
        shippingAddress: ShippingAddress,
        paymentMethod: String = "cod",
        note: String = ""
    ) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading

            // Convert OrderItem list to Map for Firebase
            val itemsMap = _orderItems.value.associateBy {
                it.productId + "_" + System.currentTimeMillis()
            }

            val order = OrderModel(
                userId = userId,
                status = "pending",
                totalPrice = _totalPrice.value,
                shippingAddress = shippingAddress,
                items = itemsMap,
                paymentMethod = paymentMethod,
                note = note,
                createdAt = System.currentTimeMillis()
            )

            val result = repository.createOrder(order)

            result.onSuccess { orderId ->
                _checkoutState.value = CheckoutState.Success(orderId)
            }.onFailure { exception ->
                _checkoutState.value = CheckoutState.Error(
                    exception.message ?: "Đặt hàng thất bại"
                )
            }
        }
    }

    fun resetState() {
        _checkoutState.value = CheckoutState.Idle
        _orderItems.value = emptyList()
        _totalPrice.value = 0.0
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val orderId: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

