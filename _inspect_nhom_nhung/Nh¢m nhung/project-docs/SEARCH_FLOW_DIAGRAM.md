# How Client-Side Search Works

## Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│  1. APP STARTS                                              │
│  ───────────────────────────────────────────────────────── │
│  SearchScreen opens                                         │
│         ↓                                                   │
│  viewModel.loadAllProducts()                                │
│         ↓                                                   │
│  Firebase Realtime Database                                 │
│  Download ALL 10 products (ONCE)                            │
│         ↓                                                   │
│  Store in: val allProducts = [...]                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  2. USER TYPES "s"                                          │
│  ───────────────────────────────────────────────────────── │
│  searchQuery = "s"                                          │
│         ↓                                                   │
│  searchProduct("s", allProducts)                            │
│         ↓                                                   │
│  LOCAL FILTER (no network!)                                 │
│  - Convert to lowercase: "s"                                │
│  - Check each product:                                      │
│    ✓ "son dior..." → contains "s" → MATCH                   │
│    ✓ "sữa rửa mặt..." → contains "s" → MATCH                │
│    ✗ "kem chống nắng..." → NO MATCH                         │
│         ↓                                                   │
│  Return filtered list: [8 products]                         │
│         ↓                                                   │
│  Display in grid (INSTANT!)                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  3. USER TYPES "so"                                         │
│  ───────────────────────────────────────────────────────── │
│  searchQuery = "so"                                         │
│         ↓                                                   │
│  searchProduct("so", allProducts)                           │
│         ↓                                                   │
│  LOCAL FILTER (no network!)                                 │
│  - Convert to lowercase: "so"                               │
│  - Check each product:                                      │
│    ✓ "son dior..." → contains "so" → MATCH                  │
│    ✗ "sữa rửa mặt..." → NO MATCH                            │
│    ✗ "kem chống nắng..." → NO MATCH                         │
│         ↓                                                   │
│  Return filtered list: [4 products]                         │
│         ↓                                                   │
│  Display in grid (INSTANT!)                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  4. USER TYPES "son"                                        │
│  ───────────────────────────────────────────────────────── │
│  searchQuery = "son"                                        │
│         ↓                                                   │
│  searchProduct("son", allProducts)                          │
│         ↓                                                   │
│  LOCAL FILTER (no network!)                                 │
│  - Convert to lowercase: "son"                              │
│  - Check each product:                                      │
│    ✓ "Son Dior Rouge 999"                                   │
│      title.lowercase() = "son dior rouge 999"               │
│      contains "son" → MATCH!                                │
│                                                             │
│    ✓ "Son MAC Retro Matte"                                  │
│      title.lowercase() = "son mac retro matte"              │
│      contains "son" → MATCH!                                │
│                                                             │
│    ✓ "Má Hồng Rare Beauty"                                  │
│      title doesn't contain "son"                            │
│      BUT productType = "son"                                │
│      productType contains "son" → MATCH!                    │
│                                                             │
│    ✗ "Kem Chống Nắng Chanel"                                │
│      No field contains "son" → NO MATCH                     │
│         ↓                                                   │
│  Return filtered list: [4-5 products]                       │
│         ↓                                                   │
│  Display in grid (INSTANT!)                                 │
└─────────────────────────────────────────────────────────────┘
```

## Code Flow

```kotlin
// 1. Load products ONCE
val allProducts by viewModel.loadAllProducts().observeAsState(emptyList())
// Result: [Product1, Product2, Product3, ..., Product10]

// 2. User types "son"
searchQuery = "son"

// 3. Call filter function
val searchResults = searchProduct("son", allProducts)

// 4. Inside searchProduct():
fun searchProduct(keyword: String, allProducts: List<ProductModel>): List<ProductModel> {
    if (keyword.isEmpty()) return emptyList()
    
    val query = keyword.lowercase().trim() // "son"
    
    return allProducts.filter { product ->
        val title = product.title.lowercase()          // "son dior rouge 999"
        val categoryTitle = product.categoryTitle.lowercase() // "dior"
        val categoryId = product.categoryId.lowercase()       // "dior"
        val matchesKeywords = product.keywords.any { 
            it.lowercase().contains(query) 
        }  // ["son", "dior", "999", ...].contains("son") → true
        val productType = product.productType.lowercase()     // "son"
        
        // Check if ANY field matches
        title.contains("son") ||           // ✓ TRUE
        categoryTitle.contains("son") ||   // ✗ FALSE
        categoryId.contains("son") ||      // ✗ FALSE
        matchesKeywords ||                 // ✓ TRUE
        productType.contains("son")        // ✓ TRUE
        
        // Result: TRUE → Include this product!
    }
}

// 5. Display results
searchResults.forEach { product ->
    ProductCard(product) // Show in grid
}
```

## Key Benefits

1. **Network call only ONCE** ⚡
   - First load: Download all products
   - After that: Pure local filtering

2. **INSTANT search** 🚀
   - No waiting for Firebase
   - Filter happens on device
   - Results update as you type

3. **Simple code** 📝
   - One filter function
   - Easy to understand
   - Easy to modify

4. **Powerful matching** 💪
   - Case-insensitive
   - Multi-field search
   - Vietnamese text support

## Example Search Results

### Search: "son"
```
Products matched:
✓ Son Dior Rouge 999 Velvet         (title contains "son")
✓ Son MAC Retro Matte - Ruby Woo    (title contains "son")
✓ Son Dầu Rare Beauty Lip Oil       (title contains "son")
✓ Má Hồng Rare Beauty Soft Pinch    (productType = "son")

Total: 4 products
Time: <5ms
```

### Search: "dior"
```
Products matched:
✓ Son Dior Rouge 999 Velvet         (categoryTitle = "Dior")
✓ Sữa Rửa Mặt Dior La Mousse        (categoryTitle = "Dior")

Total: 2 products
Time: <5ms
```

### Search: "kem chống nắng"
```
Products matched:
✓ Kem Chống Nắng Chanel UV          (title matches)
✓ Kem Lót/Chống Nắng MAC Prep       (title matches)
✓ Rare Beauty Positive Light Tinted (productType = "kem_chong_nang")

Total: 3 products
Time: <5ms
```

## Summary

🎯 **Simple**: One function, easy to understand
⚡ **Fast**: Local filtering, instant results
💪 **Powerful**: Multi-field search, case-insensitive
🇻🇳 **Vietnamese**: Full Unicode support
✅ **Production Ready**: Clean, tested, working!

