package com.uilover.project261.domain

import java.io.Serializable


data class ProductModel(
    var id: String = "",  // Product key (e.g., "dior_lipstick_999")
    var title: String = "",
    var price: Double = 0.0,
    var image: String = "",
    var product_gallery: ProductGallery = ProductGallery(),
    var description: String = "",
    var categoryId: String = "",
    var categoryTitle: String = "",
    var productType: String = "",

    // Product variant options
    var capacity: String = "",  // Default/current capacity (for display)
    var weight: String = "",    // Default/current weight (for display)
    var availableCapacities: List<String> = emptyList(),  // Available capacity options
    var availableWeights: List<String> = emptyList(),     // Available weight options
    var availableColors: List<String> = emptyList(),      // Available color options

    var showRecommend: Boolean = false,
    var rated: Double = 0.0,
    var keywords: List<String> = emptyList(),
    var numberInCart: Int = 0,

    // Selected variant (for cart)
    var selectedCapacity: String = "",
    var selectedWeight: String = "",
    var selectedColor: String = ""
) : Serializable

data class ProductGallery(
    var img1: String = "",
    var img2: String = ""
) : Serializable

