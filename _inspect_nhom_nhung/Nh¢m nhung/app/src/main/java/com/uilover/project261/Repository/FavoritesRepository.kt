package com.uilover.project261.Repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.uilover.project261.domain.ProductModel
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/"
    )

    private val favoritesRef = database.reference.child("favorites")

    // Add product to favorites
    suspend fun addToFavorites(userId: String, product: ProductModel): Result<Boolean> {
        return try {
            favoritesRef.child(userId).child(product.id).setValue(product).await()
            Log.d("FavoritesRepository", "Product ${product.id} added to favorites for user $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error adding to favorites", e)
            Result.failure(e)
        }
    }
    // Remove product from favorites
    suspend fun removeFromFavorites(userId: String, productId: String): Result<Boolean> {
        return try {
            favoritesRef.child(userId).child(productId).removeValue().await()
            Log.d("FavoritesRepository", "Product $productId removed from favorites for user $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error removing from favorites", e)
            Result.failure(e)
        }
    }

    // Check if product is in favorites
    suspend fun isFavorite(userId: String, productId: String): Result<Boolean> {
        return try {
            val snapshot = favoritesRef.child(userId).child(productId).get().await()
            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error checking favorite", e)
            Result.failure(e)
        }
    }

    // Get all favorite products for user
    suspend fun getFavorites(userId: String): Result<List<ProductModel>> {
        return try {
            val snapshot = favoritesRef.child(userId).get().await()
            val favorites = mutableListOf<ProductModel>()

            snapshot.children.forEach { child ->
                child.getValue(ProductModel::class.java)?.let { favorites.add(it) }
            }
            Log.d("FavoritesRepository", "Fetched ${favorites.size} favorites for user $userId")
            Result.success(favorites)
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error fetching favorites", e)
            Result.failure(e)
        }
    }
}

