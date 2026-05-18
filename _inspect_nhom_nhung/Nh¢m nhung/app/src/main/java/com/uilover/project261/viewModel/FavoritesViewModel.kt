package com.uilover.project261.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project261.Repository.FavoritesRepository
import com.uilover.project261.domain.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val repository = FavoritesRepository()

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState

    private val _favorites = MutableStateFlow<List<ProductModel>>(emptyList())
    val favorites: StateFlow<List<ProductModel>> = _favorites

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            _favoritesState.value = FavoritesState.Loading

            val result = repository.getFavorites(userId)

            result.onSuccess { favoritesList ->
                _favorites.value = favoritesList
                _favoriteIds.value = favoritesList.map { it.id }.toSet()
                _favoritesState.value = FavoritesState.Success
            }.onFailure { exception ->
                _favoritesState.value = FavoritesState.Error(
                    exception.message ?: "Không thể tải danh sách yêu thích"
                )
            }
        }
    }

    fun addToFavorites(userId: String, product: ProductModel) {
        viewModelScope.launch {
            val result = repository.addToFavorites(userId, product)

            result.onSuccess {
                // Update local state
                _favorites.value = _favorites.value + product
                _favoriteIds.value = _favoriteIds.value + product.id
                _favoritesState.value = FavoritesState.Added(product.title)
            }.onFailure { exception ->
                _favoritesState.value = FavoritesState.Error(
                    exception.message ?: "Không thể thêm vào yêu thích"
                )
            }
        }
    }

    fun removeFromFavorites(userId: String, productId: String) {
        viewModelScope.launch {
            val result = repository.removeFromFavorites(userId, productId)

            result.onSuccess {
                // Update local state
                _favorites.value = _favorites.value.filter { it.id != productId }
                _favoriteIds.value = _favoriteIds.value - productId
                _favoritesState.value = FavoritesState.Removed
            }.onFailure { exception ->
                _favoritesState.value = FavoritesState.Error(
                    exception.message ?: "Không thể xóa khỏi yêu thích"
                )
            }
        }
    }

    fun toggleFavorite(userId: String, product: ProductModel) {
        if (_favoriteIds.value.contains(product.id)) {
            removeFromFavorites(userId, product.id)
        } else {
            addToFavorites(userId, product)
        }
    }

    fun isFavorite(productId: String): Boolean {
        return _favoriteIds.value.contains(productId)
    }

    fun resetState() {
        _favoritesState.value = FavoritesState.Idle
    }
}

sealed class FavoritesState {
    object Idle : FavoritesState()
    object Loading : FavoritesState()
    object Success : FavoritesState()
    data class Added(val productName: String) : FavoritesState()
    object Removed : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}

