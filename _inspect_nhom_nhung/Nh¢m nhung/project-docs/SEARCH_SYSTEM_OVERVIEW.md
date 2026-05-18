# Search System - Complete Overview

## Your App Now Has 2 Search Systems! 🔍

### 1️⃣ Global Search (All Products)
**Location**: Dashboard/Home screen

```
┌─────────────────────────────────────┐
│  👤  [Tìm kiếm sản phẩm...]  🔔     │ ← CLICK HERE
├─────────────────────────────────────┤
│  Categories                         │
│  [Dior] [Chanel] [MAC] [Rare]      │
│                                     │
│  Sản phẩm cho bạn                   │
│  [Product 1]  [Product 2]           │
└─────────────────────────────────────┘
```

**What it does**:
- Searches ALL products from ALL brands
- Example: Search "son" → Shows ALL lipsticks (Dior + MAC + Rare Beauty)

---

### 2️⃣ Brand-Specific Search (Each Brand Page)
**Location**: Inside Dior/Chanel/MAC/Rare Beauty pages

```
┌─────────────────────────────────────┐
│  [←]         Dior                   │
├─────────────────────────────────────┤
│  [Tìm sản phẩm Dior...        🔍]  │ ← SEARCH HERE
├─────────────────────────────────────┤
│  Dior Products Only                 │
│  [Son Dior]  [Sữa Rửa Mặt Dior]    │
└─────────────────────────────────────┘
```

**What it does**:
- Searches ONLY that brand's products
- Example: In Dior page, search "son" → Shows ONLY Dior lipsticks

---

## Search Comparison

### Scenario: User wants to find lipstick "son"

#### Option A: Global Search
```
Dashboard
  ↓
Click search bar
  ↓
Type "son"
  ↓
Results:
  ✓ Son Dior Rouge 999 (Dior)
  ✓ Son MAC Ruby Woo (MAC)
  ✓ Son Dầu Rare Beauty (Rare Beauty)
  ✓ Má Hồng Rare Beauty (productType = son)
Total: 4 products from 3 brands
```

#### Option B: Brand Search (Dior)
```
Dashboard
  ↓
Click "Dior" category
  ↓
Opens Dior product list
  ↓
Type "son" in Dior search bar
  ↓
Results:
  ✓ Son Dior Rouge 999 (Dior only)
Total: 1 product from Dior
```

---

## When to Use Each

### Use Global Search When:
- 🔍 You don't know which brand has the product
- 🔍 You want to compare products across brands
- 🔍 You want to see ALL options
- Example: "I need a lipstick, show me everything"

### Use Brand Search When:
- 🎯 You already know you want a specific brand
- 🎯 You're browsing one brand's collection
- 🎯 You want focused results
- Example: "I want a Dior lipstick specifically"

---

## Visual Comparison

### Global Search: "son"
```
Search Results (All Brands)
─────────────────────────────
Tìm thấy 4 sản phẩm

┌──────────┐  ┌──────────┐
│ Son Dior │  │ Son MAC  │
│ [Dior]   │  │ [MAC]    │
└──────────┘  └──────────┘

┌──────────┐  ┌──────────┐
│ Son Rare │  │ Má Hồng  │
│ [Rare]   │  │ [Rare]   │
└──────────┘  └──────────┘
```

### Brand Search: "son" (in Dior page)
```
Dior Products
─────────────────────────────
Tìm sản phẩm Dior...son

┌──────────┐
│ Son Dior │
│ 1.150đ   │
└──────────┘

Only Dior products shown!
```

---

## Complete User Journey

### Journey 1: Using Global Search
```
1. Open app → Dashboard
2. Click search bar at top
3. SearchScreen opens
4. Type "son"
5. See ALL lipsticks (Dior, MAC, Rare Beauty)
6. Click a product → Detail page
7. Back → Search screen
8. Back → Dashboard
```

### Journey 2: Using Brand Search
```
1. Open app → Dashboard
2. Click "Dior" category
3. Dior product list opens
4. See search bar: "Tìm sản phẩm Dior..."
5. Type "son"
6. See ONLY Dior lipsticks
7. Click product → Detail page
8. Back → Dior list
9. Back → Dashboard
```

---

## Technical Details

### Global Search
- **File**: `screens/search/SearchScreen.kt`
- **Data Source**: `viewModel.loadAllProducts()`
- **Filters**: All products across all brands
- **Navigation**: Dedicated search route

### Brand Search
- **File**: `screens/ItemsList/ItemListScreen.kt`
- **Data Source**: `viewModel.loadFiltered(brandId)`
- **Filters**: Only products from selected brand
- **Navigation**: Part of brand page

---

## Code Comparison

### Global Search Filter
```kotlin
// Searches ALL products
val allProducts by viewModel.loadAllProducts().observeAsState(emptyList())

val searchResults = allProducts.filter { product ->
    product.title.lowercase().contains(query) ||
    product.categoryTitle.lowercase().contains(query) ||
    product.keywords.any { it.lowercase().contains(query) }
}
// Result: Products from any brand
```

### Brand Search Filter
```kotlin
// Searches only ONE brand
val allBrandProducts by viewModel.loadFiltered(brandId).observeAsState(emptyList())

val filteredProducts = allBrandProducts.filter { product ->
    product.title.lowercase().contains(query) ||
    product.keywords.any { it.lowercase().contains(query) }
}
// Result: Products from this brand only
```

---

## Real Examples

### Example 1: Looking for Sunscreen

**Global Search**:
```
Search: "kem chống nắng"
Results:
  ✓ Kem Chống Nắng Chanel UV (Chanel)
  ✓ Kem Lót/Chống Nắng MAC Prep+Prime (MAC)
  ✓ Rare Beauty Positive Light Tinted (Rare Beauty)
Total: 3 products, 3 brands
```

**Brand Search (Chanel Page)**:
```
Search: "kem chống nắng"
Results:
  ✓ Kem Chống Nắng Chanel UV (Chanel only)
Total: 1 product, 1 brand
```

### Example 2: Looking for "999"

**Global Search**:
```
Search: "999"
Results:
  ✓ Son Dior Rouge 999 Velvet
Total: 1 product (found in keywords)
```

**Brand Search (MAC Page)**:
```
Search: "999"
Results:
  (empty)
  😔 Không có sản phẩm MAC nào phù hợp với "999"
```

---

## Summary Table

| Feature | Global Search | Brand Search |
|---------|--------------|--------------|
| **Location** | Dashboard top bar | Brand page search bar |
| **Scope** | All brands | One brand only |
| **Opens** | Full screen | Filters current page |
| **Back Button** | Returns to dashboard | Returns to dashboard |
| **Use Case** | General browsing | Focused shopping |
| **Results** | Mixed brands | Single brand |
| **Example** | "Find any lipstick" | "Find Dior lipstick" |

---

## Best Practices

### For Users
✅ Use **Global Search** when exploring
✅ Use **Brand Search** when you know the brand
✅ Both searches work the same way (type and results appear)

### For Testing
✅ Test both search systems
✅ Verify brand search only shows that brand
✅ Verify global search shows all brands
✅ Check empty states work in both

---

## Complete Search Feature List

### Global Search Features
✅ Search all products
✅ Popular search suggestions
✅ Clickable suggestions
✅ Empty state
✅ Clear button
✅ Navigate to product detail
✅ Full-screen dedicated UI

### Brand Search Features
✅ Search within brand
✅ Inline with product list
✅ Clear button
✅ Empty state with brand name
✅ Navigate to product detail
✅ Integrated in existing page

---

## Your App Is Now Complete! 🎉

✅ **Global Search** - Find anything across all products
✅ **Brand Search** - Find products within a specific brand
✅ **Vietnamese UI** - All text in Vietnamese
✅ **Client-side filtering** - Fast, instant results
✅ **Beautiful UI** - Modern Shopee/Lazada style
✅ **Full navigation** - Seamless user flow

**Two powerful search systems working together!** 🔍🔍

