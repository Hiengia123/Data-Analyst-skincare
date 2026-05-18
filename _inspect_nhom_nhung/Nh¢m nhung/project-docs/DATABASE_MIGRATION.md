# Database Migration Summary - Cosmetic App Refactoring

## Migration Date
December 27, 2025

## Overview
This document outlines the complete refactoring of the cosmetic app to work with the new Firebase Realtime Database structure.

---

## Database Structure Changes

### Old Database Structure
```json
{
  "Category": {
    "1": {
      "Id": 1,
      "Name": "Category Name",
      "ImagePath": "url"
    }
  },
  "Products": {
    "1": {
      "Id": 1,
      "Title": "Product Name",
      "CategoryId": "1",
      "Price": 100,
      "BestProduct": true,
      "Star": 4.5,
      "Volume": "50ml",
      "Description": "...",
      "ImagePath": "url",
      // ... other fields
    }
  }
}
```

### New Database Structure
```json
{
  "banners": {
    "dior": { "url": "..." },
    "chanel": { "url": "..." },
    "mac": { "url": "..." },
    "rare": { "url": "..." }
  },
  "categories": {
    "dior": {
      "title": "Dior",
      "picUrl": "..."
    },
    "chanel": {
      "title": "Chanel",
      "picUrl": "..."
    },
    // ... more categories
  },
  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "price": 1150000,
      "image": "...",
      "product_gallery": {
        "img1": "...",
        "img2": "..."
      },
      "description": "...",
      "categoryId": "dior",
      "categoryTitle": "Dior",
      "productType": "son",
      "weight": "3.5g",
      "showRecommend": true,
      "rated": 4.9,
      "keywords": ["son", "dior", ...]
    },
    // ... more items
  },
  "attributes": {
    "capacity": { "30ml": true, "50ml": true, ... },
    "weight": { "3g": true, "3.5g": true, ... },
    "productType": { "son": true, "sua_rua_mat": true, ... }
  }
}
```

---

## Data Model Changes

### 1. CategoryModel
**File:** `domain/CategoryModel.kt`

#### Old Structure
```kotlin
data class CategoryModel(
    var Id: Int = 0,
    var ImagePath: String = "",
    var Name: String = ""
)
```

#### New Structure
```kotlin
data class CategoryModel(
    var id: String = "",        // Changed: Int → String (uses key as ID)
    var title: String = "",     // Changed: Name → title
    var picUrl: String = ""     // Changed: ImagePath → picUrl
)
```

**Key Changes:**
- `Id` (Int) → `id` (String) - Now uses Firebase key
- `Name` → `title`
- `ImagePath` → `picUrl`

---

### 2. ProductModel
**File:** `domain/ProductModel.kt`

#### Old Structure
```kotlin
data class ProductModel(
    var BestProduct: Boolean = false,
    var CategoryId: String = "",
    var Description: String = "",
    var Id: Int = 0,
    var ImagePath: String = "",
    var LocationId: Int = 0,
    var Price: Double = 0.0,
    var PriceId: Int = 0,
    var Star: Double = 0.0,
    var TimeId: Int = 0,
    var TimeValue: Int = 0,
    var Title: String = "",
    var Volume: String = "",
    var numberInCart: Int = 0
) : Serializable
```

#### New Structure
```kotlin
data class ProductModel(
    var id: String = "",                    // Changed: Id (Int) → id (String)
    var title: String = "",                 // Changed: Title → title
    var price: Double = 0.0,                // Changed: Price → price
    var image: String = "",                 // Changed: ImagePath → image
    var product_gallery: ProductGallery = ProductGallery(),  // NEW
    var description: String = "",           // Changed: Description → description
    var categoryId: String = "",            // Kept same
    var categoryTitle: String = "",         // NEW
    var productType: String = "",           // NEW
    var capacity: String = "",              // NEW (for liquids)
    var weight: String = "",                // NEW (for solids) - replaces Volume
    var showRecommend: Boolean = false,     // Changed: BestProduct → showRecommend
    var rated: Double = 0.0,                // Changed: Star → rated
    var keywords: List<String> = emptyList(), // NEW
    var numberInCart: Int = 0               // Kept same
) : Serializable

data class ProductGallery(
    var img1: String = "",
    var img2: String = ""
) : Serializable
```

**Key Changes:**
- `Id` (Int) → `id` (String)
- `Title` → `title`
- `Price` → `price`
- `ImagePath` → `image`
- `Description` → `description`
- `Star` → `rated`
- `BestProduct` → `showRecommend`
- `Volume` → `capacity` + `weight` (separate fields)
- **Removed:** `LocationId`, `PriceId`, `TimeId`, `TimeValue`
- **Added:** `product_gallery`, `categoryTitle`, `productType`, `keywords`

---

### 3. BannerModel (NEW)
**File:** `domain/BannerModel.kt`

```kotlin
data class BannerModel(
    var id: String = "",
    var url: String = ""
) : Serializable
```

**Purpose:** Handle promotional banners for brands

---

## Repository Changes

### MainRepository
**File:** `Repository/MainRepository.kt`

#### New Methods Added:
1. **loadBanners()** - Fetch promotional banners
2. **loadRecommendedProducts()** - Replaces `loadBestProducts()`
3. **loadAllProducts()** - Fetch all products
4. **parseProduct()** - Custom parser for complex product structure

#### Method Changes:

| Old Method | New Method | Changes |
|------------|------------|---------|
| `loadCategory()` | `loadCategory()` | Updated reference: `Category` → `categories` |
| `loadBestProducts()` | `loadRecommendedProducts()` | Changed query: `BestProduct` → `showRecommend` |
| `loadFiltered(id)` | `loadFiltered(categoryId)` | Updated reference: `Products` → `items` |
| - | `loadBanners()` | NEW - Fetch banners |
| - | `loadAllProducts()` | NEW - Fetch all items |

#### Custom Parsing:
The new database structure requires custom parsing because:
- Product gallery is a nested object
- Keywords is an array
- Need to handle both `capacity` and `weight` fields

```kotlin
private fun parseProduct(snapshot: DataSnapshot): ProductModel? {
    // Custom parsing logic for complex nested structure
}
```

---

## ViewModel Changes

### MainViewModel
**File:** `viewModel/MainViewModel.kt`

**New Methods:**
```kotlin
fun loadBanners(): LiveData<MutableList<BannerModel>>
fun loadRecommendedProducts(): LiveData<MutableList<ProductModel>>
fun loadAllProducts(): LiveData<MutableList<ProductModel>>
```

**Updated Methods:**
- `loadBestProducts()` → `loadRecommendedProducts()`

---

## UI Component Updates

### 1. MainScreen
**File:** `screens/dashboard/MainScreen.kt`

**Changes:**
- Added banner support
- `loadBestProducts()` → `loadRecommendedProducts()`
- Updated field references: `cat.Id` → `cat.id`, `cat.Name` → `cat.title`

### 2. CategoryItem
**File:** `screens/dashboard/CategoryItem.kt`

**Changes:**
- `category.ImagePath` → `category.picUrl`
- `category.Name` → `category.title`

### 3. ProductItemCardGrid
**File:** `screens/dashboard/ProductItemCardGrid.kt`

**Changes:**
- `item.ImagePath` → `item.image`
- `item.Title` → `item.title`
- `item.Star` → `item.rated`
- `item.Price` → `item.price`
- `item.TimeValue` → removed, replaced with `capacity` or `weight`
- Price format: `$${price}` → `${price.toInt()}đ` (Vietnamese currency)

### 4. ItemsCard
**File:** `screens/ItemsList/ItemsCard.kt`

**Changes:**
- All field name updates (Title → title, etc.)
- `TimingRow()` → `SizeRow()` - Shows capacity/weight instead of time
- Price format updated to Vietnamese đồng

### 5. DetailScreen
**File:** `screens/detailProduct/DetailScreen.kt`

**Changes:**
- `item.Price` → `item.price`
- `item.Description` → `item.description`
- Price format updated

### 6. HeaderSection
**File:** `screens/detailProduct/HeaderSection.kt`

**Changes:**
- `item.ImagePath` → `item.image`

### 7. TitleNumberRow
**File:** `screens/detailProduct/TitleNumberRow.kt`

**Changes:**
- `item.Title` → `item.title`

### 8. RowDetail
**File:** `screens/detailProduct/RowDetail.kt`

**Major Changes:**
- Removed time display
- Now shows: `categoryTitle`, `rated`, and `capacity`/`weight`
- Layout simplified

**Old Display:**
- Time icon + TimeValue
- Star icon + Star rating
- Flame icon + Volume

**New Display:**
- Category name
- Star icon + rated
- Size (capacity or weight)

### 9. FooterSection
**File:** `screens/detailProduct/FooterSection.kt`

**Changes:**
- Price format: `$${price}` → `${price}đ`

### 10. RecommendedList
**File:** `screens/detailProduct/RecommendedList.kt`

**Changes:**
- `loadBestProducts()` → `loadRecommendedProducts()`
- `item.ImagePath` → `item.image`

---

## Helper Class Updates

### ManagmentCart
**File:** `Helper/ManagmentCart.kt`

**Changes:**
- `it.Title` → `it.title`
- `item.Price` → `item.price`

---

## Field Mapping Reference

### CategoryModel Field Mapping
| Old Field | New Field | Type Change |
|-----------|-----------|-------------|
| `Id` | `id` | Int → String |
| `Name` | `title` | - |
| `ImagePath` | `picUrl` | - |

### ProductModel Field Mapping
| Old Field | New Field | Notes |
|-----------|-----------|-------|
| `Id` | `id` | Int → String |
| `Title` | `title` | - |
| `Price` | `price` | - |
| `ImagePath` | `image` | - |
| `Description` | `description` | - |
| `Star` | `rated` | - |
| `BestProduct` | `showRecommend` | - |
| `Volume` | `capacity` + `weight` | Split into two fields |
| `CategoryId` | `categoryId` | Kept same |
| - | `product_gallery` | NEW - Nested object |
| - | `categoryTitle` | NEW |
| - | `productType` | NEW |
| - | `keywords` | NEW - Array |
| `LocationId` | - | REMOVED |
| `PriceId` | - | REMOVED |
| `TimeId` | - | REMOVED |
| `TimeValue` | - | REMOVED |

---

## Database Query Updates

### Old Queries
```kotlin
// Categories
firebaseDatabase.getReference("Category")

// Best Products
ref.orderByChild("BestProduct").equalTo(true)

// Filtered Products
ref.orderByChild("CategoryId").equalTo(id)
```

### New Queries
```kotlin
// Banners
firebaseDatabase.getReference("banners")

// Categories
firebaseDatabase.getReference("categories")

// Recommended Products
ref.orderByChild("showRecommend").equalTo(true)

// Filtered Products
ref.orderByChild("categoryId").equalTo(categoryId)
```

---

## Currency Format Update

**Old Format:** `$${price}` (USD)  
**New Format:** `${price.toInt()}đ` (Vietnamese Đồng)

**Example:**
- Old: `$9.99`
- New: `1150000đ`

---

## Breaking Changes Checklist

✅ All data models updated  
✅ Repository queries updated  
✅ ViewModel methods updated  
✅ All UI components updated  
✅ Helper classes updated  
✅ Preview components updated  
✅ Currency format changed  
✅ New banner model added  
✅ Product gallery support added  
✅ Keywords support added  

---

## Testing Checklist

After deployment, verify:

- [ ] Categories load correctly from Firebase
- [ ] Products display with correct information
- [ ] Product images load properly
- [ ] Product gallery images work
- [ ] Recommended products show correctly
- [ ] Category filtering works
- [ ] Cart functionality works
- [ ] Product details page displays correctly
- [ ] Price displays in correct format (VND)
- [ ] Size/weight displays correctly
- [ ] Ratings display properly
- [ ] Banners load (if implemented in UI)

---

## Migration Steps

1. **Backup old database** (if needed)
2. **Update Firebase database** with new structure
3. **Deploy refactored code**
4. **Test all features**
5. **Monitor for errors**

---

## Notes

- The new database uses **string keys** instead of numeric IDs for better Firebase integration
- **Product gallery** support allows multiple product images
- **Keywords** field enables better search functionality (future feature)
- **Product type** categorization (son, sua_rua_mat, kem_chong_nang) enables better filtering
- **Capacity vs Weight** distinction properly handles different product types

---

## Files Modified

### Domain Layer (3 files)
1. `domain/CategoryModel.kt` - Updated
2. `domain/ProductModel.kt` - Updated + Added ProductGallery
3. `domain/BannerModel.kt` - NEW

### Data Layer (2 files)
1. `Repository/MainRepository.kt` - Major refactor
2. `viewModel/MainViewModel.kt` - Updated

### Helper Layer (1 file)
1. `Helper/ManagmentCart.kt` - Updated field references

### UI Layer - Dashboard (3 files)
1. `screens/dashboard/MainScreen.kt` - Updated
2. `screens/dashboard/CategoryItem.kt` - Updated
3. `screens/dashboard/ProductItemCardGrid.kt` - Updated

### UI Layer - Items List (1 file)
1. `screens/ItemsList/ItemsCard.kt` - Major updates

### UI Layer - Detail (5 files)
1. `screens/detailProduct/DetailScreen.kt` - Updated
2. `screens/detailProduct/HeaderSection.kt` - Updated
3. `screens/detailProduct/TitleNumberRow.kt` - Updated
4. `screens/detailProduct/RowDetail.kt` - Major refactor
5. `screens/detailProduct/FooterSection.kt` - Updated
6. `screens/detailProduct/RecommendedList.kt` - Updated

**Total Files Modified:** 16 files  
**Total Files Created:** 1 file (BannerModel.kt)

---

## End of Migration Summary

Migration completed successfully. All components have been updated to work with the new Firebase database structure.

