package com.uilover.project261.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project261.Repository.OrderRepository
import com.uilover.project261.domain.OrderModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState

    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders

    private val _selectedOrder = MutableStateFlow<OrderModel?>(null)
    val selectedOrder: StateFlow<OrderModel?> = _selectedOrder

    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = repository.getUserOrders(userId)

            result.onSuccess { orderList ->
                _orders.value = orderList
                _orderState.value = OrderState.Success
            }.onFailure { exception ->
                _orderState.value = OrderState.Error(
                    exception.message ?: "Không thể tải đơn hàng"
                )
            }
        }
    }

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = repository.getOrder(orderId)

            result.onSuccess { order ->
                _selectedOrder.value = order
                _orderState.value = OrderState.Success
            }.onFailure { exception ->
                _orderState.value = OrderState.Error(
                    exception.message ?: "Không thể tải chi tiết đơn hàng"
                )
            }
        }
    }

    fun selectOrder(order: OrderModel) {
        _selectedOrder.value = order
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = repository.updateOrderStatus(orderId, newStatus)

            result.onSuccess {
                // Reload orders to reflect the update
                _selectedOrder.value?.let { order ->
                    loadUserOrders(order.userId)
                }
                _orderState.value = OrderState.Success
            }.onFailure { exception ->
                _orderState.value = OrderState.Error(
                    exception.message ?: "Không thể cập nhật trạng thái"
                )
            }
        }
    }
    fun resetCancelState() {
        _orderState.value = OrderState.Idle
    }

    fun cancelOrder(orderId: String, userId: String, reason: String) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = repository.cancelOrder(orderId, reason)

            result.onSuccess {
                // Update selected order locally so UI reflects immediately
                _selectedOrder.value = _selectedOrder.value?.copy(
                    status = "cancelled",
                    cancelReason = reason
                )
                // Reload orders list
                loadUserOrders(userId)
                _orderState.value = OrderState.CancelSuccess
            }.onFailure { exception ->
                _orderState.value = OrderState.Error(
                    exception.message ?: "Không thể hủy đơn hàng"
                )
            }
        }
    }
}

sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    object Success : OrderState()
    object CancelSuccess : OrderState()
    data class Error(val message: String) : OrderState()
}

