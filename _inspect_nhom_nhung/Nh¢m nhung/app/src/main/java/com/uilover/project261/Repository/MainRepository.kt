package com.uilover.project261.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.uilover.project261.domain.BannerModel
import com.uilover.project261.domain.CategoryModel
import com.uilover.project261.domain.ProductModel
import com.uilover.project261.domain.ProductGallery

class MainRepository {
    private val TAG = "MainRepository"
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/")

    fun loadBanners(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabase.getReference("banners")

        Log.d(TAG, "Loading banners from Firebase...")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Banners onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<BannerModel>()
                for (item in snapshot.children) {
                    val url = item.child("url").getValue(String::class.java) ?: ""
                    val banner = BannerModel(
                        id = item.key ?: "",
                        url = url
                    )
                    list.add(banner)
                    Log.d(TAG, "Banner loaded: ${banner.id}")
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading banners: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabase.getReference("categories")

        Log.d(TAG, "Loading categories from Firebase...")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Categories onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<CategoryModel>()
                for (item in snapshot.children) {
                    val category = CategoryModel(
                        id = item.key ?: "",
                        title = item.child("title").getValue(String::class.java) ?: "",
                        picUrl = item.child("picUrl").getValue(String::class.java) ?: ""
                    )
                    list.add(category)
                    Log.d(TAG, "Category loaded: ${category.title}")
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading categories: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadRecommendedProducts(): LiveData<MutableList<ProductModel>> {
        val listData = MutableLiveData<MutableList<ProductModel>>()
        val ref = firebaseDatabase.getReference("items")
        val query: Query = ref.orderByChild("showRecommend").equalTo(true)

        Log.d(TAG, "Loading recommended products from Firebase...")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Recommended products onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<ProductModel>()
                for (item in snapshot.children) {
                    val product = parseProduct(item)
                    if (product != null) {
                        list.add(product)
                        Log.d(TAG, "Product loaded: ${product.title}")
                    }
                }
                Log.d(TAG, "Total recommended products loaded: ${list.size}")
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading recommended products: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadFiltered(categoryId: String): LiveData<MutableList<ProductModel>> {
        val listData = MutableLiveData<MutableList<ProductModel>>()
        val ref = firebaseDatabase.getReference("items")
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)

        Log.d(TAG, "Loading filtered products for category: $categoryId")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Filtered products onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<ProductModel>()
                for (item in snapshot.children) {
                    val product = parseProduct(item)
                    if (product != null) {
                        list.add(product)
                    }
                }
                Log.d(TAG, "Total filtered products loaded: ${list.size}")
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading filtered products: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadAllProducts(): LiveData<MutableList<ProductModel>> {
        val listData = MutableLiveData<MutableList<ProductModel>>()
        val ref = firebaseDatabase.getReference("items")

        Log.d(TAG, "Loading all products from Firebase...")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "All products onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<ProductModel>()
                for (item in snapshot.children) {
                    val product = parseProduct(item)
                    if (product != null) {
                        list.add(product)
                    }
                }
                Log.d(TAG, "Total products loaded: ${list.size}")
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading all products: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    /**
     * Search products by query string
     * Searches in: title, keywords, categoryTitle, description
     * Returns products that match the search query (case-insensitive)
     */
    fun searchProducts(query: String): LiveData<MutableList<ProductModel>> {
        val listData = MutableLiveData<MutableList<ProductModel>>()
        val ref = firebaseDatabase.getReference("items")

        Log.d(TAG, "Searching products with query: $query")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Search onDataChange - exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")
                val list = mutableListOf<ProductModel>()
                val searchQuery = query.lowercase().trim()

                if (searchQuery.isEmpty()) {
                    // If empty query, return all products
                    for (item in snapshot.children) {
                        val product = parseProduct(item)
                        if (product != null) {
                            list.add(product)
                        }
                    }
                } else {
                    // Filter products based on search query
                    for (item in snapshot.children) {
                        val product = parseProduct(item)
                        if (product != null && matchesSearchQuery(product, searchQuery)) {
                            list.add(product)
                        }
                    }
                }

                Log.d(TAG, "Search results: ${list.size} products found for query '$query'")
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error searching products: ${error.message}")
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    /**
     * Check if product matches the search query
     * Searches in: title, keywords, categoryTitle, description, productType
     */
    private fun matchesSearchQuery(product: ProductModel, query: String): Boolean {
        // Search in product title
        if (product.title.lowercase().contains(query)) {
            return true
        }

        // Search in category title (brand name)
        if (product.categoryTitle.lowercase().contains(query)) {
            return true
        }

        // Search in keywords
        for (keyword in product.keywords) {
            if (keyword.lowercase().contains(query)) {
                return true
            }
        }

        // Search in description
        if (product.description.lowercase().contains(query)) {
            return true
        }

        // Search in product type
        if (product.productType.lowercase().contains(query)) {
            return true
        }

        return false
    }

    private fun parseProduct(snapshot: DataSnapshot): ProductModel? {
        return try {
            val gallerySnapshot = snapshot.child("product_gallery")
            val gallery = ProductGallery(
                img1 = gallerySnapshot.child("img1").getValue(String::class.java) ?: "",
                img2 = gallerySnapshot.child("img2").getValue(String::class.java) ?: ""
            )

            val keywordsList = mutableListOf<String>()
            snapshot.child("keywords").children.forEach { keyword ->
                keyword.getValue(String::class.java)?.let { keywordsList.add(it) }
            }

            // Parse variant options
            val availableCapacities = mutableListOf<String>()
            snapshot.child("availableCapacities").children.forEach { cap ->
                cap.getValue(String::class.java)?.let { availableCapacities.add(it) }
            }

            val availableWeights = mutableListOf<String>()
            snapshot.child("availableWeights").children.forEach { weight ->
                weight.getValue(String::class.java)?.let { availableWeights.add(it) }
            }

            val availableColors = mutableListOf<String>()
            snapshot.child("availableColors").children.forEach { color ->
                color.getValue(String::class.java)?.let { availableColors.add(it) }
            }

            val currentCapacity = snapshot.child("capacity").getValue(String::class.java) ?: ""
            val currentWeight = snapshot.child("weight").getValue(String::class.java) ?: ""

            val product = ProductModel(
                id = snapshot.key ?: "",
                title = snapshot.child("title").getValue(String::class.java) ?: "",
                price = snapshot.child("price").getValue(Double::class.java) ?: 0.0,
                image = snapshot.child("image").getValue(String::class.java) ?: "",
                product_gallery = gallery,
                description = snapshot.child("description").getValue(String::class.java) ?: "",
                categoryId = snapshot.child("categoryId").getValue(String::class.java) ?: "",
                categoryTitle = snapshot.child("categoryTitle").getValue(String::class.java) ?: "",
                productType = snapshot.child("productType").getValue(String::class.java) ?: "",
                capacity = currentCapacity,
                weight = currentWeight,
                availableCapacities = availableCapacities,
                availableWeights = availableWeights,
                availableColors = availableColors,
                showRecommend = snapshot.child("showRecommend").getValue(Boolean::class.java) ?: false,
                rated = snapshot.child("rated").getValue(Double::class.java) ?: 0.0,
                keywords = keywordsList,
                numberInCart = 0,
                // Set default selected values
                selectedCapacity = if (availableCapacities.isNotEmpty()) availableCapacities[0] else currentCapacity,
                selectedWeight = if (availableWeights.isNotEmpty()) availableWeights[0] else currentWeight,
                selectedColor = if (availableColors.isNotEmpty()) availableColors[0] else ""
            )

            Log.d(TAG, "Parsed product: ${product.title}, capacities: ${availableCapacities.size}, weights: ${availableWeights.size}, colors: ${availableColors.size}")
            product
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing product: ${e.message}", e)
            null
        }
    }
}

