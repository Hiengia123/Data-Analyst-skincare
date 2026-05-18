# 📱 PROJECT STRUCTURE - Cosmetics E-Commerce App

## 📋 Table of Contents
1. [Overview](#overview)
2. [Project Architecture](#project-architecture)
3. [Folder Structure](#folder-structure)
4. [Database Structure](#database-structure)
5. [Features & Flow](#features--flow)
6. [Tech Stack](#tech-stack)
7. [Key Components](#key-components)

---

## 🎯 Overview

**Project Name:** Nhung Group - Cosmetics E-Commerce Application  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Database:** Firebase Realtime Database  
**Authentication:** Firebase Authentication (Email/Password)

This is a full-featured e-commerce mobile application for selling luxury cosmetics (Dior, Chanel, M.A.C, Rare Beauty). The app includes authentication, product browsing, cart management, checkout, order tracking, and favorites functionality.

---

## 🏗️ Project Architecture

The application follows **MVVM (Model-View-ViewModel)** architecture pattern with Repository pattern for data management:

```
┌─────────────┐
│    View     │ ← Jetpack Compose Screens
│  (Screens)  │
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  ViewModel  │ ← Business Logic & State Management
└──────┬──────┘
       │
       ↓
┌─────────────┐
│ Repository  │ ← Data Access Layer
└──────┬──────┘
       │
       ↓
┌─────────────┐
│   Domain    │ ← Data Models
│  (Models)   │
└─────────────┘
       │
       ↓
┌─────────────┐
│  Firebase   │ ← Backend Services
│ (Database)  │
└─────────────┘
```

---

## 📁 Folder Structure

```
app/src/main/java/com/uilover/project261/
│
├── 📂 domain/                      # Data Models
│   ├── BannerModel.kt
│   ├── CategoryModel.kt
│   ├── OrderModel.kt              # Order, OrderItem, ShippingAddress
│   ├── ProductModel.kt            # Product & ProductGallery
│   └── UserModel.kt
│
├── 📂 Repository/                  # Data Access Layer
│   ├── AuthRepository.kt          # Authentication operations
│   ├── FavoritesRepository.kt     # Favorites CRUD
│   ├── MainRepository.kt          # Products, Categories, Banners
│   └── OrderRepository.kt         # Order management
│
├── 📂 viewModel/                   # Business Logic & State
│   ├── AuthViewModel.kt
│   ├── CheckoutViewModel.kt
│   ├── FavoritesViewModel.kt
│   ├── MainViewModel.kt
│   └── OrderViewModel.kt
│
├── 📂 screens/                     # UI Screens (Jetpack Compose)
│   ├── 📂 auth/
│   │   ├── LoginScreen.kt
│   │   └── RegisterScreen.kt
│   │
│   ├── 📂 dashboard/
│   │   ├── MainScreen.kt          # Homepage
│   │   ├── TopBar.kt              # Header with search & cart
│   │   ├── MyBottomBar.kt         # Bottom Navigation
│   │   ├── CategoryItem.kt
│   │   └── ProductItemCardGrid.kt
│   │
│   ├── 📂 search/
│   │   └── SearchScreen.kt        # Product search
│   │
│   ├── 📂 ItemsList/
│   │   └── ItemListScreen.kt      # Category product list
│   │
│   ├── 📂 detailProduct/
│   │   └── DetailScreen.kt        # Product details
│   │
│   ├── 📂 cart/
│   │   └── CartScreen.kt          # Shopping cart
│   │
│   ├── 📂 checkout/
│   │   └── CheckoutScreen.kt      # Order checkout
│   │
│   ├── 📂 orders/
│   │   ├── OrderHistoryScreen.kt  # Order list
│   │   └── OrderDetailScreen.kt   # Order details
│   │
│   ├── 📂 favorites/
│   │   └── FavoritesScreen.kt     # Favorite products
│   │
│   └── 📂 profile/
│       └── ProfileScreen.kt       # User profile
│
├── 📂 Helper/
│   ├── ManagmentCart.kt           # Local cart management
│   └── TinyDB.kt                  # Local storage helper
│
├── 📂 ui/
│   └── 📂 navigation/
│       ├── Screen.kt              # Navigation routes
│       └── CheckoutDataHolder.kt  # Checkout data holder
│
└── MainActivity.kt                 # Main entry point & Navigation
```

---

## 🗄️ Database Structure

### Firebase Realtime Database Schema

```json
{
  "banners": {
    "dior": { "url": "..." },
    "chanel": { "url": "..." },
    "mac": { "url": "..." },
    "rare": { "url": "..." }
  },

  "categories": {
    "dior": { "title": "Dior", "picUrl": "..." },
    "chanel": { "title": "Chanel", "picUrl": "..." },
    "mac": { "title": "M.A.C", "picUrl": "..." },
    "rare": { "title": "Rare Beauty", "picUrl": "..." }
  },

  "attributes": {
    "capacity": { "30ml": true, "50ml": true, ... },
    "weight": { "3g": true, "3.5g": true, ... },
    "productType": { "son": true, "sua_rua_mat": true, ... }
  },

  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "price": 1150000,
      "image": "...",
      "product_gallery": { "img1": "...", "img2": "..." },
      "description": "...",
      "categoryId": "dior",
      "categoryTitle": "Dior",
      "productType": "son",
      "weight": "3.5g",
      "availableWeights": ["3g", "3.5g", "7g"],
      "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
      "availableCapacities": [],
      "showRecommend": true,
      "rated": 4.9,
      "keywords": ["son", "dior", "rouge", "999", ...]
    }
  },

  "users": {
    "uid123": {
      "uid": "uid123",
      "email": "user@example.com",
      "name": "User Name",
      "phone": "",
      "avatarUrl": "",
      "provider": "email",
      "createdAt": 1234567890
    }
  },

  "carts": {
    "uid123": {
      "dior_lipstick_999": {
        "id": "dior_lipstick_999",
        "title": "Son Dior Rouge 999 Velvet",
        "price": 1150000,
        "quantity": 2,
        "image": "...",
        "selectedColor": "Đỏ 999",
        "selectedWeight": "3.5g"
      }
    }
  },

  "orders": {
    "ORDER_ABC123": {
      "orderId": "ORDER_ABC123",
      "userId": "uid123",
      "status": "pending",
      "totalPrice": 2950000,
      "createdAt": 1234567890,
      "paymentMethod": "cod",
      "note": "",
      "shippingAddress": {
        "name": "Nguyen Van A",
        "phone": "0123456789",
        "address": "123 Main St",
        "city": "Ho Chi Minh",
        "district": "District 1",
        "ward": "Ward 1"
      },
      "items": {
        "dior_lipstick_999": {
          "productId": "dior_lipstick_999",
          "title": "Son Dior Rouge 999 Velvet",
          "price": 1150000,
          "quantity": 2,
          "image": "...",
          "selectedColor": "Đỏ 999",
          "selectedWeight": "3.5g"
        }
      }
    }
  },

  "favorites": {
    "uid123": {
      "dior_lipstick_999": {
        "id": "dior_lipstick_999",
        "title": "Son Dior Rouge 999 Velvet",
        "price": 1150000,
        "image": "...",
        "categoryId": "dior",
        "rated": 4.9,
        ...
      }
    }
  }
}
```

### Database Rules (Recommended)

```json
{
  "rules": {
    ".read": true,
    "banners": { ".write": false },
    "categories": { ".write": false },
    "attributes": { ".write": false },
    "items": { ".write": false },
    
    "users": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    },
    
    "carts": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    },
    
    "orders": {
      ".indexOn": ["userId"],
      "$orderId": {
        ".write": "data.child('userId').val() === auth.uid || !data.exists()",
        ".read": "data.child('userId').val() === auth.uid"
      }
    },
    
    "favorites": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    }
  }
}
```

---

## 🔄 Features & Flow

### 1. **Authentication System**

#### Login Flow
```
┌─────────────┐
│   Launch    │
│     App     │
└──────┬──────┘
       │
       ↓
┌─────────────┐
│  Homepage   │
│  (Browse)   │
└──────┬──────┘
       │
       │ Click Profile Icon
       ↓
┌─────────────┐    Not Logged In
│   Check     ├───────────────────┐
│    Auth     │                   │
└──────┬──────┘                   │
       │                          ↓
       │ Logged In         ┌─────────────┐
       │                   │   Login     │
       ↓                   │   Screen    │
┌─────────────┐            └──────┬──────┘
│   Profile   │                   │
│   Screen    │                   │ Enter Credentials
└─────────────┘                   ↓
                           ┌─────────────┐
                           │  Firebase   │
                           │    Auth     │
                           └──────┬──────┘
                                  │
                                  │ Success
                                  ↓
                           ┌─────────────┐
                           │Save to DB   │
                           │users/{uid}  │
                           └──────┬──────┘
                                  │
                                  ↓
                           ┌─────────────┐
                           │   Profile   │
                           │   Screen    │
                           └─────────────┘
```

#### Register Flow
```
Login Screen → Click "Đăng ký ngay" 
→ Register Screen 
→ Enter: Email, Name, Password 
→ Firebase Auth (createUserWithEmailAndPassword) 
→ Update Profile (displayName) 
→ Save to Database (users/{uid}) 
→ Auto Login 
→ Profile Screen
```

**Features:**
- Email/Password authentication
- User profile creation in Realtime Database
- Display name update
- Auto-login after registration
- "Quay về trang chủ" button to return to homepage
- Persistent login state

---

### 2. **Product Browsing**

#### Homepage (MainScreen)
```
┌──────────────────────────────┐
│  TopBar                      │
│  ┌────────────────┐  🛒  👤  │
│  │ 🔍 Search...   │          │
│  └────────────────┘          │
├──────────────────────────────┤
│  Banner Carousel             │
│  [Dior] [Chanel] [MAC] ...   │
├──────────────────────────────┤
│  Categories                  │
│  ○Dior ○Chanel ○MAC ○Rare    │
├──────────────────────────────┤
│  Recommended Products        │
│  ┌────┐ ┌────┐ ┌────┐       │
│  │ 💗 │ │ 💗 │ │ 💗 │       │
│  │Img │ │Img │ │Img │       │
│  │$$$│ │$$$│ │$$$│       │
│  └────┘ └────┘ └────┘       │
├──────────────────────────────┤
│  Bottom Navigation Bar       │
│  🏠  🔍  ❤️  📦  👤          │
└──────────────────────────────┘
```

**Features:**
- Banner carousel (auto-scroll)
- Category filters
- Product grid with favorites
- Search bar
- Cart icon (shows in TopBar after login)
- User avatar icon (shows in TopBar after login)
- Bottom navigation (Home, Search, Favorites, Orders, Profile)

---

### 3. **Search System**

```
Homepage → Click Search Bar 
→ SearchScreen 
→ Type keyword 
→ Real-time search in items (keywords matching) 
→ Display results grid 
→ Click product → ProductDetailScreen
```

**Search Features:**
- Real-time keyword matching
- Search in product title, category, keywords
- Vietnamese keyword support
- Product grid display
- "Quay lại" button to return
- Empty state handling

---

### 4. **Product Details**

```
┌──────────────────────────────┐
│  ← Back          🛒 Cart      │
├──────────────────────────────┤
│  Product Image Gallery       │
│  ○ ● ○                       │
├──────────────────────────────┤
│  Product Title               │
│  ⭐⭐⭐⭐⭐ 4.9              │
│  1,150,000 đ                 │
├──────────────────────────────┤
│  Variants:                   │
│  Colors: [Đỏ] [Hồng] [Cam]  │
│  Weight: [3g] [3.5g] [7g]    │
├──────────────────────────────┤
│  Description                 │
│  ...                         │
├──────────────────────────────┤
│  [Thêm vào giỏ]  [Thanh toán]│
└──────────────────────────────┘
│  💗 Favorite (top-right)     │
```

**Features:**
- Image gallery with swipe
- Favorite toggle (heart icon)
- Variant selection (color, weight, capacity)
- Quantity selector
- Two action buttons:
  - **"Thêm vào giỏ"**: Add to cart
  - **"Thanh toán ngay"**: Direct checkout (single product)

---

### 5. **Shopping Cart**

#### Cart Management Flow
```
Before Login: Local Storage (TinyDB)
├─ Add products to cart
├─ Modify quantities
└─ Stored in device

After Login: Merge to Firebase
├─ Upload local cart to Firebase
├─ Path: carts/{userId}/{productId}
└─ Sync across devices
```

#### Cart Screen
```
┌──────────────────────────────┐
│  Giỏ Hàng (5 items)          │
├──────────────────────────────┤
│  ┌──────────────────────┐    │
│  │ [Img] Product Name   │    │
│  │ Color: Đỏ 999        │    │
│  │ 1,150,000đ           │    │
│  │ [-] 2 [+]     ❌     │    │
│  └──────────────────────┘    │
│  ...                         │
├──────────────────────────────┤
│  Tổng tiền: 2,950,000 đ      │
│  [Thanh toán]                │
└──────────────────────────────┘
```

**Features:**
- Add/remove products
- Quantity adjustment (+/-)
- Variant display
- Total price calculation
- Delete items
- "Thanh toán" button → CheckoutScreen (multi-product)

**Cart Logic (ManagmentCart.kt):**
- Stores cart in TinyDB (local)
- Matches products by title + variants
- Minimum quantity = 1
- Auto-merge duplicate items

---

### 6. **Checkout & Order Creation**

#### Checkout Flow
```
Cart/ProductDetail → Click "Thanh toán" 
→ Check if logged in 
   ├─ Not logged in → Redirect to Login
   └─ Logged in → CheckoutScreen

CheckoutScreen:
├─ Display order items
├─ Show total price
├─ Shipping address form:
│  ├─ Name
│  ├─ Phone
│  ├─ Address
│  ├─ City
│  ├─ District
│  └─ Ward
├─ Payment method (COD default)
├─ Note (optional)
└─ [Đặt hàng] button

Click "Đặt hàng":
├─ Validate form
├─ Create OrderModel
├─ Generate orderId (ORDER_XXXXXXXX)
├─ Save to Firebase orders/{orderId}
├─ Clear cart
└─ Navigate to OrderHistoryScreen
```

**Checkout Features:**
- Supports single product or multiple products
- Shipping address form
- Payment method selection
- Order note
- Total price display
- Form validation
- Success/error handling

---

### 7. **Order Tracking**

#### Order History Flow
```
Profile → "Đơn hàng của bạn"
or
Bottom Nav → Orders Icon
→ OrderHistoryScreen

Display:
├─ Fetch orders from Firebase
│  (WHERE userId == currentUser.uid)
├─ Sort by createdAt (newest first)
└─ Display order cards

Click Order Card:
→ OrderDetailScreen
```

#### Order Status States
- **pending**: Chờ xác nhận (Yellow)
- **shipping**: Đang giao hàng (Blue)
- **delivered**: Đã giao hàng (Green)
- **cancelled**: Đã hủy (Red)

#### OrderHistoryScreen
```
┌──────────────────────────────┐
│  Đơn Hàng Của Bạn            │
├──────────────────────────────┤
│  ┌──────────────────────┐    │
│  │ ORDER_ABC123         │    │
│  │ 🟡 Chờ xác nhận      │    │
│  │ 2,950,000 đ          │    │
│  │ 06/01/2026           │    │
│  └──────────────────────┘    │
│  ...                         │
└──────────────────────────────┘
```

#### OrderDetailScreen
```
┌──────────────────────────────┐
│  ← Chi Tiết Đơn Hàng         │
├──────────────────────────────┤
│  Mã đơn: ORDER_ABC123        │
│  Trạng thái: 🟡 Chờ xác nhận │
│  Ngày đặt: 06/01/2026        │
├──────────────────────────────┤
│  Sản phẩm:                   │
│  ┌────────────────────┐      │
│  │ [Img] Product      │      │
│  │ x2   1,150,000đ    │      │
│  └────────────────────┘      │
├──────────────────────────────┤
│  Địa chỉ giao hàng:          │
│  Nguyen Van A                │
│  0123456789                  │
│  123 Main St, Ward 1...      │
├──────────────────────────────┤
│  Tổng tiền: 2,950,000 đ      │
└──────────────────────────────┘
```

---

### 8. **Favorites System**

#### Favorites Flow
```
ProductDetailScreen → Click ❤️ icon
→ Check if logged in
   ├─ Not logged in → Show toast "Please login"
   └─ Logged in → Toggle favorite

Toggle Favorite:
├─ If not favorite:
│  ├─ Add to Firebase favorites/{userId}/{productId}
│  └─ Change icon to filled heart ❤️
└─ If already favorite:
   ├─ Remove from Firebase
   └─ Change icon to empty heart 🤍

View Favorites:
Profile → "Sản phẩm yêu thích"
or
Bottom Nav → ❤️ Icon
→ FavoritesScreen
```

#### FavoritesScreen
```
┌──────────────────────────────┐
│  Sản Phẩm Yêu Thích          │
├──────────────────────────────┤
│  ┌────┐ ┌────┐ ┌────┐       │
│  │Img │ │Img │ │Img │       │
│  │$$$│ │$$$│ │$$$│       │
│  │ ❌ │ │ ❌ │ │ ❌ │       │
│  └────┘ └────┘ └────┘       │
└──────────────────────────────┘
```

**Features:**
- Add/remove favorites
- Persistent storage in Firebase
- Product grid display
- Delete button per product
- Click product → ProductDetailScreen
- Requires login

---

### 9. **Bottom Navigation**

```
┌─────┬─────┬─────┬─────┬─────┐
│ 🏠  │ 🔍  │ ❤️  │ 📦  │ 👤  │
│Home │Search│Fav │Order│Prof │
└─────┴─────┴─────┴─────┴─────┘
```

**Navigation:**
- **Home (🏠)**: MainScreen (Homepage)
- **Search (🔍)**: SearchScreen
- **Favorites (❤️)**: FavoritesScreen (requires login)
- **Orders (📦)**: OrderHistoryScreen (requires login)
- **Profile (👤)**: ProfileScreen or LoginScreen

**Authentication Handling:**
- Guest users can browse, search, add to cart
- Favorites, Orders, Profile require login
- Clicking protected tabs → redirect to LoginScreen

---

## 🛠️ Tech Stack

### Frontend
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Jetpack Navigation Compose
- **State Management**: ViewModel + StateFlow
- **Image Loading**: Coil

### Backend
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Storage**: Firebase Storage (for images)

### Local Storage
- **Cart Management**: TinyDB (SharedPreferences wrapper)
- **Session**: Firebase Auth persistence

### Architecture Patterns
- **MVVM**: Model-View-ViewModel
- **Repository Pattern**: Data abstraction
- **Single Activity**: Navigation with Compose
- **Dependency Injection**: Manual (ViewModelFactory)

---

## 🧩 Key Components

### 1. **Domain Models**

#### ProductModel
```kotlin
data class ProductModel(
    var id: String,
    var title: String,
    var price: Double,
    var image: String,
    var product_gallery: ProductGallery,
    var description: String,
    var categoryId: String,
    var categoryTitle: String,
    var productType: String,
    var capacity: String,
    var weight: String,
    var availableCapacities: List<String>,
    var availableWeights: List<String>,
    var availableColors: List<String>,
    var showRecommend: Boolean,
    var rated: Double,
    var keywords: List<String>,
    var numberInCart: Int,
    var selectedCapacity: String,
    var selectedWeight: String,
    var selectedColor: String
)
```

#### OrderModel
```kotlin
data class OrderModel(
    val orderId: String,
    val userId: String,
    val status: String, // pending, shipping, delivered, cancelled
    val totalPrice: Double,
    val createdAt: Long,
    val shippingAddress: ShippingAddress,
    val items: Map<String, OrderItem>,
    val paymentMethod: String,
    val note: String
)
```

#### UserModel
```kotlin
data class UserModel(
    val uid: String,
    val email: String,
    val name: String,
    val phone: String,
    val avatarUrl: String,
    val provider: String,
    val createdAt: Long
)
```

---

### 2. **Repositories**

#### AuthRepository
```kotlin
class AuthRepository {
    suspend fun register(email, password, name): Result<UserModel>
    suspend fun login(email, password): Result<UserModel>
    suspend fun getCurrentUserData(): UserModel?
    suspend fun updateUserProfile(uid, updates): Result<Boolean>
    fun logout()
    fun isLoggedIn(): Boolean
}
```

#### MainRepository
```kotlin
class MainRepository {
    suspend fun loadBanners(): Result<List<BannerModel>>
    suspend fun loadCategories(): Result<List<CategoryModel>>
    suspend fun loadProducts(): Result<List<ProductModel>>
    suspend fun loadProductsByCategory(categoryId): Result<List<ProductModel>>
    suspend fun getProductById(id): Result<ProductModel?>
    suspend fun searchProducts(query): Result<List<ProductModel>>
}
```

#### OrderRepository
```kotlin
class OrderRepository {
    suspend fun createOrder(order): Result<String>
    suspend fun getUserOrders(userId): Result<List<OrderModel>>
    suspend fun getOrder(orderId): Result<OrderModel?>
    suspend fun updateOrderStatus(orderId, status): Result<Boolean>
}
```

#### FavoritesRepository
```kotlin
class FavoritesRepository {
    suspend fun addToFavorites(userId, product): Result<Boolean>
    suspend fun removeFromFavorites(userId, productId): Result<Boolean>
    suspend fun isFavorite(userId, productId): Result<Boolean>
    suspend fun getFavorites(userId): Result<List<ProductModel>>
}
```

---

### 3. **ViewModels**

#### AuthViewModel
```kotlin
class AuthViewModel(private val repository: AuthRepository) {
    val currentUser: StateFlow<UserModel?>
    val isLoading: StateFlow<Boolean>
    val errorMessage: StateFlow<String?>
    
    fun login(email, password)
    fun register(email, password, name)
    fun logout()
    fun getCurrentUser()
}
```

#### MainViewModel
```kotlin
class MainViewModel(private val repository: MainRepository) {
    val banners: StateFlow<List<BannerModel>>
    val categories: StateFlow<List<CategoryModel>>
    val products: StateFlow<List<ProductModel>>
    val isLoading: StateFlow<Boolean>
    
    fun loadBanners()
    fun loadCategories()
    fun loadProducts()
    fun searchProducts(query)
}
```

#### OrderViewModel
```kotlin
class OrderViewModel(private val repository: OrderRepository) {
    val orders: StateFlow<List<OrderModel>>
    val isLoading: StateFlow<Boolean>
    
    fun createOrder(order)
    fun getUserOrders(userId)
    fun getOrderById(orderId)
}
```

#### CheckoutViewModel
```kotlin
class CheckoutViewModel(private val orderRepository: OrderRepository) {
    val checkoutItems: StateFlow<List<ProductModel>>
    val totalPrice: StateFlow<Double>
    
    fun setCheckoutItems(items)
    fun createOrder(shippingAddress, paymentMethod, note)
}
```

#### FavoritesViewModel
```kotlin
class FavoritesViewModel(private val repository: FavoritesRepository) {
    val favorites: StateFlow<List<ProductModel>>
    val isLoading: StateFlow<Boolean>
    
    fun loadFavorites(userId)
    fun toggleFavorite(userId, product)
    fun removeFavorite(userId, productId)
    fun isFavorite(userId, productId): Boolean
}
```

---

### 4. **Navigation Routes**

```kotlin
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object ItemList : Screen("itemList/{categoryId}/{categoryTitle}")
    object Detail : Screen("detail/{productId}")
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderHistory : Screen("orderHistory")
    object OrderDetail : Screen("orderDetail/{orderId}")
    object Favorites : Screen("favorites")
}
```

---

### 5. **Helper Classes**

#### ManagmentCart
```kotlin
class ManagmentCart(context: Context) {
    fun insertItem(item: ProductModel)
    fun getListCart(): ArrayList<ProductModel>
    fun minusItem(list, position, listener)
    fun plusItem(list, position, listener)
    fun getTotalFee(): Double
    fun clearCart()
}
```

**Features:**
- Local cart storage using TinyDB
- Add/remove items
- Quantity management
- Total price calculation
- Variant matching (same product + same variants = same cart item)

---

## 🔐 Authentication Rules

### Guest Users (Not Logged In)
✅ Can browse products  
✅ Can search products  
✅ Can view product details  
✅ Can add to cart (local)  
❌ Cannot checkout  
❌ Cannot view favorites  
❌ Cannot view orders  
❌ Cannot access profile  

### Logged In Users
✅ All guest features  
✅ Can checkout  
✅ Can create orders  
✅ Can view order history  
✅ Can track orders  
✅ Can add/remove favorites  
✅ Can access profile  
✅ Cart synced to Firebase  

---

## 📊 User Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    APP LAUNCH                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
            ┌────────────────┐
            │   Homepage     │
            │   (MainScreen) │
            └────┬───┬───┬───┘
                 │   │   │
      ┌──────────┘   │   └──────────┐
      │              │              │
      ↓              ↓              ↓
┌─────────┐    ┌─────────┐    ┌─────────┐
│ Browse  │    │ Search  │    │  Cart   │
│Products │    │Products │    │ (Local) │
└────┬────┘    └────┬────┘    └────┬────┘
     │              │              │
     └──────┬───────┴──────┬───────┘
            │              │
            ↓              ↓
      ┌──────────┐   ┌──────────┐
      │ Product  │   │ Checkout │
      │ Detail   │   │ (Login?) │
      └────┬─────┘   └────┬─────┘
           │              │
      ┌────┴────┐    ┌────┴────┐
      │         │    │         │
      ↓         ↓    ↓         ↓
┌─────────┐ ┌──────────┐ ┌──────────┐
│Add Cart │ │Favorite ❤│ │  Order   │
└─────────┘ └──────────┘ └────┬─────┘
                              │
                              ↓
                        ┌──────────┐
                        │  Order   │
                        │ History  │
                        └──────────┘
```

---

## 🎨 UI/UX Highlights

### Design Inspirations
- **Shopee**: Product grid, cart UI, checkout flow
- **Lazada**: Order tracking, status colors, bottom navigation

### Color Scheme
- Primary: Purple/Pink (#9C27B0)
- Success: Green (#4CAF50)
- Warning: Orange (#FF9800)
- Error: Red (#F44336)
- Info: Blue (#2196F3)

### Typography
- Vietnamese language support
- Currency format: "1,150,000 đ"
- Date format: "dd/MM/yyyy"

### Icons
- Material Icons
- Custom brand logos (Dior, Chanel, MAC, Rare Beauty)

---

## 🚀 Build & Run

### Prerequisites
```bash
- Android Studio Hedgehog or later
- JDK 17+
- Gradle 8.0+
- Firebase Project configured
```

### Firebase Setup
1. Add `google-services.json` to `app/` folder
2. Enable Email/Password authentication
3. Set Realtime Database URL: `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`
4. Import database structure (JSON provided)
5. Configure database rules (see Database Rules section)

### Run Project
```bash
1. Open project in Android Studio
2. Sync Gradle
3. Run on emulator or device
```

---

## 📝 Notes

### Cart Behavior
- Before login: Stored in TinyDB (local device)
- After login: Merged to Firebase `carts/{userId}`
- Cart persists across app restarts
- Cleared after successful checkout

### Order ID Format
- Format: `ORDER_XXXXXXXX` (8 random uppercase chars)
- Generated using: `UUID.randomUUID().toString().take(8).uppercase()`

### Search Algorithm
- Keywords field in products
- Case-insensitive matching
- Vietnamese keyword support
- Matches: title, category, keywords array

### Image Loading
- Uses Coil library
- Placeholder support
- Error handling
- Lazy loading in grids

---

## 🐛 Known Issues & Solutions

### Issue: Orders not showing
**Solution**: Add database index for `userId`
```json
{
  "orders": {
    ".indexOn": ["userId"]
  }
}
```

### Issue: Cart quantity showing 0
**Solution**: Set default quantity to 1 in `ManagmentCart.insertItem()`

### Issue: Second order fails
**Solution**: Clear CheckoutDataHolder after order creation (already fixed)

---

## 📚 Documentation References

For detailed implementation guides, see:
- `AUTHENTICATION_IMPLEMENTATION_COMPLETE.md`
- `CHECKOUT_ORDER_SYSTEM_COMPLETE.md`
- `FAVORITES_SYSTEM_COMPLETE.md`
- `SHOPPING_CART_COMPLETE.md`
- `SEARCH_IMPLEMENTATION_COMPLETE.md`
- `FIREBASE_DATABASE_STRUCTURE.md`

---

## 👥 Contributors

**Project**: Nhóm Nhung  
**Date**: January 2026  
**Framework**: Jetpack Compose + Firebase

---

## 📄 License

This is a student project for educational purposes.

---

**END OF PROJECT STRUCTURE DOCUMENTATION**

