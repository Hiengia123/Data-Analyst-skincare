# Project Architecture - Beauty E-Commerce Application

## 📱 Project Overview

**Project Name:** Project261 (Beauty E-Commerce App)  
**Package:** `com.uilover.project261`  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Architecture Pattern:** MVVM (Model-View-ViewModel)  
**Backend:** Firebase Realtime Database  
**Minimum SDK:** 24 (Android 7.0)  
**Target SDK:** 36  

This is a modern Android e-commerce application specializing in luxury beauty products (cosmetics, skincare) from premium brands like Dior, Chanel, M.A.C, and Rare Beauty.

---

## 🏗️ Project Structure

```
com.uilover.project261/
│
├── MainActivity.kt                 # Main entry point, handles navigation
│
├── domain/                         # Data models (Domain layer)
│   ├── ProductModel.kt            # Product data class with variants
│   ├── CategoryModel.kt           # Brand/Category data class
│   └── BannerModel.kt             # Banner/Slider data class
│
├── Repository/                     # Data access layer
│   └── MainRepository.kt          # Firebase database operations
│
├── viewModel/                      # Presentation logic
│   └── MainViewModel.kt           # Main ViewModel for all screens
│
├── Helper/                         # Utility classes
│   ├── ManagmentCart.kt           # Shopping cart management
│   ├── TinyDB.kt                  # Local storage wrapper (SharedPreferences)
│   ├── CurrencyFormatter.kt       # Vietnamese currency formatting
│   └── ChangeNumberItemsListener.kt # Cart quantity change listener
│
├── screens/                        # UI layer (Composable screens)
│   ├── dashboard/                 # Home screen
│   │   ├── MainScreen.kt          # Main home screen layout
│   │   ├── TopBar.kt              # Search & cart top bar
│   │   ├── CategoryItem.kt        # Brand category cards
│   │   ├── ProductItemCardGrid.kt # Product grid items
│   │   └── MyBottomBar.kt         # Bottom navigation bar
│   │
│   ├── search/                    # Search functionality
│   │   └── SearchScreen.kt        # Client-side product search
│   │
│   ├── ItemsList/                 # Brand/Category product listing
│   │   ├── ItemListScreen.kt      # Filtered product list by brand
│   │   ├── ItemsCard.kt           # Product card component
│   │   └── PriceFilterBar.kt      # Price range filter chips
│   │
│   ├── detailProduct/             # Product detail screen
│   │   ├── DetailScreen.kt        # Main product detail layout
│   │   ├── ImageGallery.kt        # Image slider with pager
│   │   ├── ProductInfoCard.kt     # Price, rating, quantity selector
│   │   ├── ProductOptionsSelector.kt # Variant selection (color, weight, capacity)
│   │   ├── DescriptionSection.kt  # Product description
│   │   ├── RecommendedList.kt     # Related products carousel
│   │   └── FooterSection.kt       # Add to cart button
│   │
│   └── cart/                      # Shopping cart
│       └── CartScreen.kt          # Cart items, quantity management, checkout
│
└── ui/                            # UI resources
    ├── navigation/
    │   └── Screen.kt              # Navigation routes
    └── theme/
        ├── Color.kt               # Color palette
        ├── Type.kt                # Typography
        └── Theme.kt               # Material theme configuration
```

---

## 🔥 Firebase Realtime Database Structure

**Database URL:** `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`

### Database Schema

```json
{
  "banners": {
    "{brand_id}": {
      "url": "string (Firebase Storage URL)"
    }
  },
  
  "categories": {
    "{brand_id}": {
      "title": "string (Brand name)",
      "picUrl": "string (Logo URL)"
    }
  },
  
  "items": {
    "{product_id}": {
      "title": "string",
      "price": "number (VND)",
      "image": "string (Main product image URL)",
      "product_gallery": {
        "img1": "string (Gallery image 1)",
        "img2": "string (Gallery image 2)"
      },
      "description": "string (Vietnamese)",
      "categoryId": "string (Brand ID: dior, chanel, mac, rare)",
      "categoryTitle": "string (Brand display name)",
      "productType": "string (son, sua_rua_mat, kem_chong_nang)",
      
      // Product variants
      "capacity": "string (Default capacity, e.g., '30ml')",
      "weight": "string (Default weight, e.g., '3.5g')",
      "availableCapacities": ["string array"],
      "availableWeights": ["string array"],
      "availableColors": ["string array"],
      
      // Display flags
      "showRecommend": "boolean (Show on home screen)",
      "rated": "number (1-5 stars)",
      "keywords": ["array of strings (for search)"]
    }
  },
  
  "attributes": {
    "capacity": {
      "30ml": true,
      "50ml": true,
      "100ml": true,
      "150ml": true
    },
    "weight": {
      "3g": true,
      "3_5g": true,
      "7g": true
    },
    "productType": {
      "son": true,
      "sua_rua_mat": true,
      "kem_chong_nang": true
    }
  }
}
```

### Current Database Content

#### Brands (Categories)
1. **Dior** (`dior`) - French luxury brand
2. **Chanel** (`chanel`) - French luxury brand
3. **M.A.C** (`mac`) - Professional makeup brand
4. **Rare Beauty** (`rare`) - Selena Gomez's brand

#### Product Types
- `son` - Lipstick/Lip products
- `sua_rua_mat` - Facial cleanser
- `kem_chong_nang` - Sunscreen/UV protection

#### Products Overview
- **Total Products:** 15 items
- **Dior:** 5 products (lipsticks, cleansers, sunscreen)
- **Chanel:** 5 products (lipstick, gel cleanser, CC cream, UV protection)
- **M.A.C:** 2 products (Ruby Woo lipstick, Prep+Prime)
- **Rare Beauty:** 3 products (blush, tinted moisturizer, lip oil)

---

## 🎯 Architecture Pattern: MVVM

### Layer Breakdown

#### 1. **Model Layer** (`domain/`)
- **ProductModel**: Core product entity with variants support
  - Base properties: id, title, price, images, description
  - Variant properties: availableCapacities, availableWeights, availableColors
  - Selected variants: selectedCapacity, selectedWeight, selectedColor
  - Cart properties: numberInCart
  
- **CategoryModel**: Brand/category entity
- **BannerModel**: Promotional banner entity

#### 2. **Repository Layer** (`Repository/`)
- **MainRepository**: Single source of truth for Firebase data
  - `loadBanners()` - Fetch all promotional banners
  - `loadCategory()` - Fetch all brand categories
  - `loadRecommendedProducts()` - Fetch products where showRecommend = true
  - `loadFiltered(categoryId)` - Fetch products by brand
  - `loadAllProducts()` - Fetch all products (for search)
  - `searchProducts(query)` - Server-side search (currently unused)

#### 3. **ViewModel Layer** (`viewModel/`)
- **MainViewModel**: Centralized ViewModel for all screens
  - Exposes LiveData from repository
  - Manages selected product state (StateFlow)
  - No business logic - pure data binding

#### 4. **View Layer** (`screens/`)
- Jetpack Compose UI components
- Each screen observes LiveData/StateFlow from ViewModel
- Stateless where possible, state hoisting pattern

---

## 🚀 Navigation Flow

### Screen Routes (`ui/navigation/Screen.kt`)

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Items : Screen("itemsList/{id}/{title}")
    object Detail : Screen("detail")
}
```

### Navigation Graph

```
MainActivity (NavHost)
│
├─ Home Screen (MainScreen)
│   ├─ TopBar
│   │   ├─ Search Icon → Search Screen
│   │   └─ Cart Icon → Cart Screen
│   │
│   ├─ Banner Slider (Dior, Chanel, M.A.C, Rare Beauty)
│   │
│   ├─ Category Grid (4 brands)
│   │   └─ Category Click → Items List Screen
│   │
│   ├─ Recommended Products Grid
│   │   └─ Product Click → Detail Screen
│   │
│   └─ Bottom Navigation Bar
│       └─ Cart Badge → Cart Screen
│
├─ Search Screen
│   ├─ Search Bar (client-side filtering)
│   ├─ Product Grid (filtered results)
│   └─ Product Click → Detail Screen
│
├─ Items List Screen (Brand Products)
│   ├─ Back Button → Home
│   ├─ Search Bar (within brand)
│   ├─ Price Filter Chips (0-500k, 500k-1M, 1M-2M, 2M+)
│   ├─ Product Grid (filtered by brand + search + price)
│   └─ Product Click → Detail Screen
│
├─ Detail Screen
│   ├─ Back Button → Previous Screen
│   ├─ Image Gallery (Pager with indicators)
│   ├─ Product Info (Title, Price, Rating, Quantity)
│   ├─ Variant Selector (Capacity/Weight/Color chips)
│   ├─ Description
│   ├─ Recommended Products Carousel
│   │   └─ Product Click → Detail Screen (self-navigation)
│   └─ Add to Cart Button → Stays on Detail Screen
│
└─ Cart Screen
    ├─ Back Button → Previous Screen
    ├─ Cart Items List
    │   ├─ Product Image + Info
    │   ├─ Variant Display (capacity, weight, color)
    │   ├─ Quantity Controls (+/-)
    │   └─ Product Click → Detail Screen
    │
    └─ Checkout Bottom Bar
        ├─ Total Price (VND format)
        └─ Checkout Button (TODO)
```

---

## 🛒 Shopping Cart Implementation

### Local Storage (TinyDB)
- Uses Android SharedPreferences
- Stores cart items as serialized ArrayList<ProductModel>
- Persists between app sessions

### Cart Management (`Helper/ManagmentCart.kt`)

```kotlin
class ManagmentCart(context: Context) {
    
    // Add item to cart (with variant support)
    fun insertItem(item: ProductModel)
    
    // Get all cart items
    fun getListCart(): ArrayList<ProductModel>
    
    // Decrease quantity (removes if quantity = 1)
    fun minusItem(position: Int, listener: ChangeNumberItemsListener)
    
    // Increase quantity
    fun plusItem(position: Int, listener: ChangeNumberItemsListener)
    
    // Calculate total price
    fun getTotalFee(): Double
}
```

### Variant Handling
Cart distinguishes items by:
1. Product title
2. Selected capacity
3. Selected weight
4. Selected color

Same product with different variants = separate cart items.

---

## 🔍 Search Functionality

### Client-Side Search (`SearchScreen.kt`)
- Loads ALL products once on screen mount
- Real-time filtering on user input
- Search criteria:
  1. Product title (Vietnamese)
  2. Category/brand title
  3. Keywords array (multiple languages + variations)
  4. Product type

### Search Algorithm
```kotlin
val searchResults = allProducts.filter { product ->
    val query = searchQuery.lowercase().trim()
    
    product.title.lowercase().contains(query) ||
    product.categoryTitle.lowercase().contains(query) ||
    product.keywords.any { it.lowercase().contains(query) } ||
    product.productType.lowercase().contains(query)
}
```

### Keywords Examples
- `"son", "dior", "rouge", "999", "do", "lipstick", "velvet", "li", "makeup"`
- `"kcn", "kem chong nang", "spf 50", "bao ve da"`
- `"sua rua mat", "cleanser", "gel", "chong o nhiem"`

---

## 🎨 UI/UX Features

### Design System
- **Color Scheme:**
  - Primary: Purple/Violet (#9775FA)
  - Background: Light grey (#F5F5F5)
  - Card background: White
  - Text: Dark grey/Black
  
- **Typography:**
  - Headings: Bold, 18-24sp
  - Body: Regular, 14-16sp
  - Prices: Bold, 16-18sp

### Key UI Components

#### 1. **Product Cards**
- Image with rounded corners
- Title (max 2 lines)
- Brand badge
- Price (VND format: 1.150.000₫)
- Rating stars

#### 2. **Category Items**
- Circular brand logo
- Brand name below
- Click → Filter products by brand

#### 3. **Banner Slider**
- Auto-scroll carousel
- Brand-specific promotional banners
- Accompanist Pager library

#### 4. **Variant Selector**
- Shopee/Lazada-style chip selection
- Three types: Capacity (ml), Weight (g), Color
- Visual feedback on selection

#### 5. **Cart Item**
- Product image (small)
- Title + variant info
- Quantity stepper (+/-)
- Individual price
- Click → Navigate to product detail

---

## 📦 Key Dependencies

### Core Android
- **Kotlin:** 2.1.0
- **AGP (Android Gradle Plugin):** 8.13.0
- **Compose BOM:** 2024.09.00
- **Material3:** Latest
- **Navigation Compose:** 2.9.5

### Firebase
- **Firebase Realtime Database:** 22.0.1

### Image Loading
- **Coil:** 2.7.0 (Async image loading)

### UI Libraries
- **Accompanist Pager:** 0.32.0 (Image carousels)
- **Accompanist FlowLayout:** 0.32.0 (Chip layouts)
- **ConstraintLayout Compose:** 1.1.1

### Storage
- **Gson:** 2.13.2 (JSON serialization for cart)

---

## 🔧 Build Configuration

### Gradle Setup (`app/build.gradle.kts`)

```kotlin
android {
    namespace = "com.uilover.project261"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.uilover.project261"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
    }
}
```

---

## 📱 Screen Responsibilities

### 1. **MainScreen (Home)**
**Responsibilities:**
- Display promotional banners
- Show 4 brand categories
- Show recommended products (showRecommend = true)
- Provide navigation to search, cart, and brand listings

**Data Loading:**
- `viewModel.loadBanners()` → LiveData<List<BannerModel>>
- `viewModel.loadCategory()` → LiveData<List<CategoryModel>>
- `viewModel.loadRecommendedProducts()` → LiveData<List<ProductModel>>

### 2. **SearchScreen**
**Responsibilities:**
- Provide search input field
- Filter all products in real-time
- Display results in grid
- Navigate to product detail on click

**Data Loading:**
- `viewModel.loadAllProducts()` → LiveData<List<ProductModel>>

### 3. **ItemListScreen (Brand Products)**
**Responsibilities:**
- Display products filtered by brand
- Provide in-brand search
- Price range filtering (4 ranges)
- Show product count

**Data Loading:**
- `viewModel.loadFiltered(categoryId)` → LiveData<List<ProductModel>>

### 4. **DetailScreen**
**Responsibilities:**
- Display product images (gallery)
- Show product details (title, price, rating, description)
- Variant selection (capacity, weight, color)
- Quantity selection (1-99)
- Add to cart functionality
- Show recommended products carousel
- Support self-navigation (clicking recommended products)

**Data Source:**
- `viewModel.selectedProduct` → StateFlow<ProductModel?>

### 5. **CartScreen**
**Responsibilities:**
- Display all cart items
- Show variant info for each item
- Quantity management (+/-)
- Calculate and display total price
- Navigate to product detail on item click
- Checkout button (TODO)

**Data Source:**
- `ManagmentCart.getListCart()` → ArrayList<ProductModel>

---

## 🔄 Data Flow

### Product Selection Flow

```
1. User clicks product on any screen
   ↓
2. Call: viewModel.selectedProduct(product)
   ↓
3. StateFlow updates: _selectedProduct.value = product
   ↓
4. Navigate to: Screen.Detail.route
   ↓
5. DetailScreen observes: selectedProduct.collectAsState()
   ↓
6. Display product details
```

### Add to Cart Flow

```
1. User selects variants on DetailScreen
   ↓
2. User adjusts quantity
   ↓
3. User clicks "Thêm vào giỏ hàng"
   ↓
4. Create ProductModel copy with:
   - selectedCapacity
   - selectedWeight
   - selectedColor
   - numberInCart
   ↓
5. Call: managmentCart.insertItem(product)
   ↓
6. TinyDB saves to SharedPreferences
   ↓
7. Toast: "Đã thêm vào giỏ hàng"
   ↓
8. User stays on DetailScreen (can continue shopping)
```

### Cart Update Flow

```
1. User opens CartScreen
   ↓
2. Load: cartItems = managmentCart.getListCart()
   ↓
3. User clicks +/- button
   ↓
4. Call: managmentCart.plusItem() or minusItem()
   ↓
5. Trigger: changeListener.onChanged()
   ↓
6. Reload: cartItems = managmentCart.getListCart()
   ↓
7. Recalculate: totalPrice = managmentCart.getTotalFee()
   ↓
8. Force recomposition: refreshKey++
```

---

## 🌐 Localization

### Language: Vietnamese (vi)

**Product Type Translations:**
- `son` → "Son môi" (Lipstick)
- `sua_rua_mat` → "Sữa rửa mặt" (Facial cleanser)
- `kem_chong_nang` → "Kem chống nắng" (Sunscreen)

**UI Translations:**
- "Tất cả" → All
- "Tìm kiếm" → Search
- "Giỏ hàng" → Cart
- "Thêm vào giỏ hàng" → Add to cart
- "Mua ngay" → Buy now
- "Đánh giá" → Rating
- "Mô tả sản phẩm" → Product description
- "Sản phẩm tương tự" → Similar products
- "Đã thêm vào giỏ hàng" → Added to cart
- "Chọn" → Select
- "Dung tích" → Capacity
- "Trọng lượng" → Weight
- "Màu sắc" → Color

**Currency Format:**
- Format: `1.150.000₫` (thousands separator, VND symbol)
- Implementation: `CurrencyFormatter.kt`

---

## 🐛 Known Issues & TODs

### TODOs
1. **Checkout functionality** - Currently placeholder in CartScreen
2. **User authentication** - No login/signup system
3. **Order history** - No order tracking
4. **Payment integration** - No payment gateway
5. **Admin panel** - No backend product management
6. **Push notifications** - No Firebase Cloud Messaging

### Potential Improvements
1. Add product reviews and ratings
2. Implement wishlists/favorites
3. Add product comparison feature
4. Implement promotional codes/discounts
5. Add shipping address management
6. Implement order status tracking
7. Add product stock management
8. Implement real-time inventory updates

---

## 📄 Related Documentation

For detailed implementation guides, refer to:
- `PROJECT_STRUCTURE.md` - Original project structure
- `SEARCH_SYSTEM_OVERVIEW.md` - Search implementation details
- `PRODUCT_VARIANTS_GUIDE.md` - Variant handling
- `CART_ALL_ISSUES_FIXED.md` - Cart implementation
- `UI_TRANSFORMATION_SUMMARY.md` - UI/UX improvements
- `FIREBASE_DIAGNOSTICS.md` - Firebase setup
- `DEPLOYMENT_CHECKLIST.md` - Build and deployment

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- JDK 11+
- Android SDK 24+
- Firebase account

### Setup Steps
1. Clone the repository
2. Open in Android Studio
3. Add `google-services.json` to `app/` directory
4. Sync Gradle
5. Run on emulator or physical device (API 24+)

### Firebase Configuration
- Database URL: `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`
- Region: Asia Southeast 1
- Rules: Read-only access (no authentication required)

---

## 📊 Project Statistics

- **Total Kotlin Files:** 36
- **Lines of Code:** ~5,000+ (estimated)
- **Screens:** 5 main screens + components
- **Total Products:** 15 items
- **Brands:** 4 categories
- **Product Types:** 3 categories

---

## 👥 Development Team

**Project:** Nhóm nhung (Beauty Group)  
**Focus:** Luxury cosmetics and skincare e-commerce  
**Target Market:** Vietnamese beauty enthusiasts  

---

**Last Updated:** December 2024  
**Version:** 1.0  
**Documentation Version:** 1.0.0

