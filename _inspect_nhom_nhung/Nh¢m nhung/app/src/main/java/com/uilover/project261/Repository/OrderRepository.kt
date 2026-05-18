package com.uilover.project261.Repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.uilover.project261.domain.OrderModel
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OrderRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/"
    )

    private val ordersRef = database.reference.child("orders")

    // Create new order
    suspend fun createOrder(order: OrderModel): Result<String> {
        return try {
            val orderId = order.orderId.ifEmpty {
                "ORDER_${UUID.randomUUID().toString().take(8).uppercase()}"
            }

            val orderWithId = order.copy(orderId = orderId)

            ordersRef.child(orderId).setValue(orderWithId).await()

            Log.d("OrderRepository", "Order created: $orderId")
            Result.success(orderId)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error creating order", e)
            Result.failure(e)
        }
    }

    // Get user orders
    suspend fun getUserOrders(userId: String): Result<List<OrderModel>> {
        return try {
            // Try with indexed query first
            try {
                val snapshot = ordersRef
                    .orderByChild("userId")
                    .equalTo(userId)
                    .get()
                    .await()

                val orders = mutableListOf<OrderModel>()
                snapshot.children.forEach { child ->
                    child.getValue(OrderModel::class.java)?.let { orders.add(it) }
                }

                // Sort by date (newest first)
                orders.sortByDescending { it.createdAt }

                Log.d("OrderRepository", "Fetched ${orders.size} orders for user $userId (indexed query)")
                return Result.success(orders)
            } catch (indexError: Exception) {
                Log.w("OrderRepository", "Indexed query failed, trying fallback method", indexError)

                // Fallback: Get all orders and filter locally
                val allSnapshot = ordersRef.get().await()
                val orders = mutableListOf<OrderModel>()

                allSnapshot.children.forEach { child ->
                    child.getValue(OrderModel::class.java)?.let { order ->
                        if (order.userId == userId) {
                            orders.add(order)
                        }
                    }
                }

                // Sort by date (newest first)
                orders.sortByDescending { it.createdAt }

                Log.d("OrderRepository", "Fetched ${orders.size} orders for user $userId (fallback method)")
                Result.success(orders)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching orders", e)
            Result.failure(e)
        }
    }

    // Get single order
    suspend fun getOrder(orderId: String): Result<OrderModel?> {
        return try {
            val snapshot = ordersRef.child(orderId).get().await()
            val order = snapshot.getValue(OrderModel::class.java)
            Result.success(order)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error fetching order", e)
            Result.failure(e)
        }
    }

    // Update order status
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Boolean> {
        return try {
            ordersRef.child(orderId).child("status").setValue(status).await()
            Log.d("OrderRepository", "Order $orderId status updated to $status")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error updating order status", e)
            Result.failure(e)
        }
    }

    // Cancel order with reason
    suspend fun cancelOrder(orderId: String, reason: String): Result<Boolean> {
        return try {
            val updates = mapOf(
                "status" to "cancelled",
                "cancelReason" to reason
            )
            ordersRef.child(orderId).updateChildren(updates).await()
            Log.d("OrderRepository", "Order $orderId cancelled with reason: $reason")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error cancelling order", e)
            Result.failure(e)
        }
    }
}

