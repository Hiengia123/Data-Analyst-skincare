# Complete System Architecture & Implementation Guide

## 📋 Project Overview

**Project Name:** Nhóm Nhung - Beauty E-Commerce App  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Architecture:** MVVM (Model-View-ViewModel)  
**Database:** Firebase Realtime Database  
**Authentication:** Firebase Authentication  
**Last Updated:** December 28, 2025

---

## 🏗️ Current Architecture

### Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture Pattern:** MVVM
- **Dependency Injection:** ViewModelProvider (Manual)
- **Navigation:** Navigation Compose
- **Image Loading:** Coil
- **Local Storage:** TinyDB (SharedPreferences wrapper)
- **Backend:** Firebase Realtime Database
- **Authentication:** Firebase Auth (Email/Password)

### Project Structure

```
com.uilover.project261/
├── domain/                          # Data Models
│   ├── BannerModel.kt              # Banner carousel data
│   ├── CategoryModel.kt            # Brand categories (Dior, Chanel, MAC, Rare)
│   ├── ProductModel.kt             # Product with variants
│   └── UserModel.kt                # User profile data ✅ NEW
│
├── Repository/                      # Data Layer
│   ├── MainRepository.kt           # Product & category data
│   ├── ManagmentCart.kt            # Local cart management
│   └── AuthRepository.kt           # Authentication & user management ✅ NEW
│
├── viewModel/                       # Business Logic
│   ├── MainViewModel.kt            # Home, products, search
│   └── AuthViewModel.kt            # Login, register, auth state ✅ NEW
│
├── screens/                         # UI Screens
│   ├── dashboard/
│   │   ├── MainScreen.kt           # Home with banners, categories, products
│   │   ├── MyBottomBar.kt          # Bottom navigation (5 tabs)
│   │   └── TopBar.kt               # Search & cart buttons
│   │
│   ├── auth/                        # ✅ NEW - Authentication
│   │   ├── LoginScreen.kt          # Login with email/password
│   │   └── RegisterScreen.kt       # User registration
│   │
│   ├── profile/                     # ✅ NEW - User Profile
│   │   └── ProfileScreen.kt        # Profile, orders, favorites, logout
│   │
│   ├── search/
│   │   └── SearchScreen.kt         # Real-time product search
│   │
│   ├── cart/
│   │   └── CartScreen.kt           # Shopping cart with variants
│   │
│   ├── ItemsList/
│   │   └── ItemListScreen.kt       # Products by brand
│   │
│   └── detailProduct/
│       ├── DetailScreen.kt         # Product details
│       ├── ImageGallery.kt         # Image carousel
│       └── ProductOptionsSelector.kt # Variant selector
│
├── Helper/
│   ├── TinyDB.java                 # Local storage helper
│   └── CurrencyFormatter.kt        # VND currency formatting
│
└── ui/
    ├── navigation/
    │   └── Screen.kt               # Navigation routes
    └── theme/
        └── Theme.kt                # App theme & colors
```

---

## 🔥 Firebase Database Structure

### Complete Realtime Database Schema

```json
{
  "banners": {
    "dior": { "url": "https://..." },
    "chanel": { "url": "https://..." },
    "mac": { "url": "https://..." },
    "rare": { "url": "https://..." }
  },
  
  "categories": {
    "dior": {
      "title": "Dior",
      "picUrl": "https://..."
    },
    "chanel": {
      "title": "Chanel",
      "picUrl": "https://..."
    },
    "mac": {
      "title": "M.A.C",
      "picUrl": "https://..."
    },
    "rare": {
      "title": "Rare Beauty",
      "picUrl": "https://..."
    }
  },
  
  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "price": 1150000,
      "image": "https://...",
      "product_gallery": {
        "img1": "https://...",
        "img2": "https://..."
      },
      "description": "Màu đỏ huyền thoại...",
      "categoryId": "dior",
      "categoryTitle": "Dior",
      "productType": "son",
      "weight": "3.5g",
      "availableWeights": ["3g", "3.5g", "7g"],
      "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
      "showRecommend": true,
      "rated": 4.9,
      "keywords": ["son", "dior", "rouge", "999", "do", "lipstick"]
    }
  },
  
  "users": {
    "uid123": {
      "uid": "uid123",
      "email": "user@example.com",
      "name": "Nguyễn Văn A",
      "phone": "+84912345678",
      "avatarUrl": "https://...",
      "provider": "email",
      "createdAt": 1703750400000
    }
  },
  
  "carts": {
    "uid123": {
      "dior_lipstick_999": {
        "quantity": 2,
        "price": 1150000,
        "selectedWeight": "3.5g",
        "selectedColor": "Đỏ 999",
        "title": "Son Dior Rouge 999 Velvet",
        "image": "https://...",
        "categoryId": "dior"
      }
    }
  },
  
  "orders": {
    "order_abc123": {
      "orderId": "order_abc123",
      "userId": "uid123",
      "status": "pending",
      "totalPrice": 2950000,
      "createdAt": 1703750400000,
      "shippingAddress": {
        "name": "Nguyễn Văn A",
        "phone": "+84912345678",
        "address": "123 Đường ABC, Quận 1, TP.HCM",
        "city": "TP.HCM",
        "district": "Quận 1"
      },
      "items": {
        "dior_lipstick_999": {
          "title": "Son Dior Rouge 999 Velvet",
          "price": 1150000,
          "quantity": 2,
          "image": "https://...",
          "selectedColor": "Đỏ 999",
          "selectedWeight": "3.5g"
        }
      }
    }
  },
  
  "favorites": {
    "uid123": {
      "dior_lipstick_999": true,
      "chanel_lipstick_velvet": true,
      "rare_blush_joy": true
    }
  }
}
```

---

## ✅ Implemented Features (Phase 1)

### 1. Authentication System ✅

**Screens:**
- **LoginScreen** - Shopee/Lazada inspired UI
  - Email/Password login
  - Password visibility toggle
  - Forgot password link (UI only)
  - Navigate to Register
  - Loading state
  - Error handling

- **RegisterScreen** - Beautiful gradient design
  - Full name, email, password fields
  - Password confirmation
  - Terms & conditions checkbox
  - Email validation
  - Password strength check (min 6 chars)
  - Loading state

- **ProfileScreen**
  - User info display (name, email, avatar)
  - Menu items:
    - 📦 My Orders
    - ❤️ Favorites
    - 📍 Shipping Address
    - 🔔 Notifications
    - ⚙️ Settings
  - Logout button
  - Login prompt when not authenticated

**Backend:**
- AuthRepository - Firebase Auth integration
- AuthViewModel - State management
- UserModel - User data structure
- Auto-redirect to login from Profile tab
- Session persistence

### 2. Product Catalog ✅

**15 Products across 4 brands:**
- **Dior** (5 products): Lipsticks, cleansers, sunscreen
- **Chanel** (5 products): Lipsticks, gel cleanser, CC cream, UV sunscreen
- **MAC** (2 products): Ruby Woo lipstick, Prep+Prime
- **Rare Beauty** (3 products): Soft Pinch blush, tinted moisturizer, lip oil

**Features:**
- Product variants (weight, capacity, color)
- Multi-image gallery
- Ratings & reviews
- Product recommendations
- Brand filtering
- Real-time search

### 3. Shopping Cart ✅

**Current Implementation:**
- Local cart (TinyDB)
- Add/Remove items
- Quantity adjustment
- Variant selection preserved
- Price calculation
- Empty cart state

### 4. Search & Navigation ✅

- Real-time search with keywords
- Brand category filtering
- Product detail navigation
- Bottom navigation (5 tabs)
- Smooth screen transitions

---

## 🚀 Next Phase: Orders & Checkout

### Phase 2A: Checkout System (TO BE IMPLEMENTED)

**CheckoutScreen:**
```kotlin
Features:
- Shipping address form
- Payment method selection
- Order summary
- Total price calculation
- Auth check (redirect to login if needed)
- Create order button
```

**Flow:**
```
Cart → Checkout → 
  If not logged in → Login → Return to Checkout
  If logged in → Enter address → Create order → Success
```

**Files to Create:**
```
screens/checkout/
├── CheckoutScreen.kt
├── AddressForm.kt
└── OrderSummary.kt

domain/
└── OrderModel.kt

Repository/
└── OrderRepository.kt

viewModel/
└── CheckoutViewModel.kt
```

### Phase 2B: Orders System (TO BE IMPLEMENTED)

**Order Management:**
```kotlin
Features:
- Create order from cart
- Save to Firebase orders/{orderId}
- Clear cart after order
- Order status tracking
- Order history view
```

**OrderHistoryScreen:**
- List all user orders
- Filter by status (pending/shipping/done)
- Click to view details
- Real-time status updates

**OrderDetailScreen:**
- Order ID & date
- Product list with variants
- Shipping address
- Total price
- Status badge
- Track order button

**Files to Create:**
```
screens/orders/
├── OrderHistoryScreen.kt
├── OrderDetailScreen.kt
└── OrderStatusBadge.kt
```

### Phase 2C: Favorites System (TO BE IMPLEMENTED)

**FavoritesScreen:**
- Grid of favorite products
- Add/remove favorites
- Navigate to product detail
- Empty state

**Files to Create:**
```
screens/favorites/
└── FavoritesScreen.kt

Repository/
└── FavoritesRepository.kt

viewModel/
└── FavoritesViewModel.kt
```

---

## 🔄 Cart Sync Strategy

### Current State:
✅ **Local Cart (TinyDB)** - Works for guest users

### Next Implementation:
**Firebase Cart Sync** - For logged-in users

**Logic:**
```kotlin
When user logs in:
1. Read local cart from TinyDB
2. Merge with Firebase cart (carts/{uid})
3. Sync to Firebase
4. Clear local cart

When user adds to cart:
- If logged in → Update Firebase cart
- If not logged in → Update local cart

When user logs out:
- Keep local cart for next login
```

**Code Location:**
- Update `ManagmentCart.kt`
- Add sync methods in `AuthViewModel.kt`

---

## 📱 Bottom Navigation Structure

```
Current (5 tabs):
┌─────────┬─────────┬──────────┬─────────┬─────────┐
│  Home   │  Cart   │ Favorite │  Order  │ Profile │
│   🏠    │   🛒    │    ❤️    │   📦    │   👤    │
└─────────┴─────────┴──────────┴─────────┴─────────┘

Functionality:
- Home: ✅ Browse products, banners, categories
- Cart: ✅ View cart, checkout
- Favorite: ⏳ Wishlist (TO IMPLEMENT)
- Order: ⏳ Order history (TO IMPLEMENT)
- Profile: ✅ Login/Profile/Logout
```

---

## 🎨 UI/UX Design Principles

### Color Scheme
- **Primary:** Pink (#FF6B9D)
- **Background:** Light Gray (#F5F5F5)
- **Text Primary:** Dark Gray
- **Text Secondary:** Gray
- **White:** #FFFFFF

### Design Inspiration
- **Shopee:** Vibrant colors, easy navigation
- **Lazada:** Clean product cards, clear CTAs
- **Modern E-commerce:** Gradient backgrounds, rounded corners

### Key UI Components
1. **Gradient Backgrounds** (Login/Register)
2. **Rounded Cards** (Product, Profile menus)
3. **Bottom Sheet** (Variant selector)
4. **Image Carousels** (Banners, Product gallery)
5. **Loading States** (Shimmer effects)
6. **Empty States** (Cart, Search, Orders)

---

## 🔐 Security & Validation

### Authentication
- ✅ Email validation (Android Patterns)
- ✅ Password min length (6 chars)
- ✅ Password confirmation match
- ✅ Firebase Auth error handling
- ✅ Session persistence

### Data Validation
- ✅ Non-empty fields
- ✅ Price formatting (VND)
- ⏳ Address validation
- ⏳ Phone number validation

---

## 📊 User Flows

### 1. Guest User Flow
```
Open App → Browse Products → Add to Cart (Local) → 
  Checkout → Login Required → Register/Login → 
  Checkout Complete → Order Created
```

### 2. Logged-in User Flow
```
Open App → Browse → Add to Cart (Firebase) → 
  Checkout → Enter Address → Place Order → 
  View in Order History
```

### 3. Profile Access
```
Tap Profile Icon → 
  If not logged in → Login Screen
  If logged in → Profile Screen with menu
```

---

## 🧪 Testing Checklist

### Authentication ✅
- [x] Register with valid email
- [x] Register with invalid email (shows error)
- [x] Register with short password (shows error)
- [x] Register with non-matching passwords (shows error)
- [x] Login with correct credentials
- [x] Login with wrong password (shows error)
- [x] Logout functionality
- [x] Session persistence (app restart)

### Cart & Products ✅
- [x] Add product to cart
- [x] Select product variants
- [x] Update cart quantity
- [x] Remove from cart
- [x] Empty cart state
- [x] Price calculation

### Navigation ✅
- [x] Bottom bar navigation
- [x] Product detail navigation
- [x] Brand filtering
- [x] Search functionality
- [x] Back button behavior

### To Test (Next Phase)
- [ ] Checkout flow
- [ ] Order creation
- [ ] Order history
- [ ] Favorites add/remove
- [ ] Cart sync after login

---

## 🚧 Known Issues & Limitations

### Current Limitations:
1. **No checkout screen** - Can't complete purchases yet
2. **No order management** - Can't view order history
3. **No favorites** - Can't save favorite products
4. **Cart is local only** - No Firebase sync yet
5. **No address book** - Manual entry each time

### Deprecation Warnings (Non-critical):
- Accompanist Pager (will migrate to native Compose)
- Some Material Icons (AutoMirrored versions)
- Locale constructor (modern API available)

---

## 📝 Implementation Priority

### Phase 1: Authentication ✅ COMPLETED
- [x] Login Screen
- [x] Register Screen
- [x] Profile Screen
- [x] Firebase Auth integration
- [x] User data in Firebase DB

### Phase 2: Checkout & Orders ⏳ NEXT
- [ ] Checkout Screen
- [ ] Address Form
- [ ] Order Model
- [ ] Create Order Repository
- [ ] Order Success Screen
- [ ] Cart sync (local → Firebase)

### Phase 3: Order Management ⏳ UPCOMING
- [ ] Order History Screen
- [ ] Order Detail Screen
- [ ] Order Status Tracking
- [ ] Filter orders by status

### Phase 4: Favorites ⏳ UPCOMING
- [ ] Favorites Screen
- [ ] Add/Remove favorites
- [ ] Favorites Repository
- [ ] Sync with Firebase

### Phase 5: Enhancements ⏳ FUTURE
- [ ] Address book management
- [ ] Push notifications
- [ ] Payment integration
- [ ] Product reviews
- [ ] Promo codes
- [ ] Google/Facebook login

---

## 🔧 Development Setup

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11
- Firebase account with Realtime Database enabled
- Firebase Authentication enabled (Email/Password)

### Firebase Configuration
```json
Database URL: 
https://nhung-group-default-rtdb.asia-southeast1.firebasestorage.app/

Authentication Methods:
- Email/Password: ✅ Enabled
- Google: ❌ Not configured
- Facebook: ❌ Not configured
```

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

---

## 📚 Code Examples

### Add Product to Cart
```kotlin
val cart = ManagmentCart(context)
cart.insertItem(
    item = product,
    selectedCapacity = "50ml",
    selectedWeight = null,
    selectedColor = "Đỏ 999"
)
```

### Login User
```kotlin
val authViewModel: AuthViewModel = viewModel()
authViewModel.login(email, password)

// Observe state
val authState by authViewModel.authState.collectAsState()
when (authState) {
    is AuthState.Authenticated -> { /* Navigate to home */ }
    is AuthState.Error -> { /* Show error */ }
    is AuthState.Loading -> { /* Show loading */ }
}
```

### Create Order (TEMPLATE - TO IMPLEMENT)
```kotlin
val order = OrderModel(
    orderId = UUID.randomUUID().toString(),
    userId = currentUser.uid,
    items = cartItems,
    totalPrice = calculateTotal(),
    status = "pending",
    createdAt = System.currentTimeMillis(),
    shippingAddress = addressForm.value
)
orderRepository.createOrder(order)
```

---

## 🎯 Success Metrics

### Current Achievements:
- ✅ 100% build success
- ✅ Firebase Auth integrated
- ✅ 15 products loaded
- ✅ 5-screen navigation
- ✅ Local cart working
- ✅ Search functional
- ✅ Modern UI/UX

### Next Goals:
- ⏳ Complete checkout flow
- ⏳ Order creation & tracking
- ⏳ Cart-to-Firebase sync
- ⏳ Favorites functionality
- ⏳ 100+ products catalog

---

## 📞 Support & Resources

### Documentation Links:
- [Firebase Auth Docs](https://firebase.google.com/docs/auth/android/start)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Material 3](https://m3.material.io/)

### Project Repository:
- Local Path: `E:\Nhóm nhung`
- Package: `com.uilover.project261`

---

**Last Build:** December 28, 2025  
**Status:** ✅ Phase 1 Complete - Ready for Phase 2 (Checkout & Orders)  
**Next Milestone:** Implement checkout flow and order management system

