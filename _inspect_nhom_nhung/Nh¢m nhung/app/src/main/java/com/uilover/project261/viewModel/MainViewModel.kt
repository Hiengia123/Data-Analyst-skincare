package com.uilover.project261.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.uilover.project261.Repository.MainRepository
import com.uilover.project261.domain.BannerModel
import com.uilover.project261.domain.CategoryModel
import com.uilover.project261.domain.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadBanners(): LiveData<MutableList<BannerModel>> {
        return repository.loadBanners()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadRecommendedProducts(): LiveData<MutableList<ProductModel>> {
        return repository.loadRecommendedProducts()
    }

    fun loadFiltered(categoryId: String): LiveData<MutableList<ProductModel>> {
        return repository.loadFiltered(categoryId)
    }

    fun loadAllProducts(): LiveData<MutableList<ProductModel>> {
        return repository.loadAllProducts()
    }

    fun searchProducts(query: String): LiveData<MutableList<ProductModel>> {
        return repository.searchProducts(query)
    }

    private val _selectedProduct = MutableStateFlow<ProductModel?>(null)
    val selectedProduct: StateFlow<ProductModel?> = _selectedProduct

    fun selectedProduct(productModel: ProductModel?) {
        _selectedProduct.value = productModel
    }
}