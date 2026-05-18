# Cosmetic App - Project Structure Documentation

## Project Overview
**Project Name:** project261  
**Package Name:** com.uilover.project261  
**Type:** Android Cosmetic E-commerce Application  
**Framework:** Jetpack Compose with Kotlin  
**Backend:** Firebase Realtime Database  
**Architecture:** MVVM (Model-View-ViewModel)

## Table of Contents
1. [Project Configuration](#project-configuration)
2. [Architecture Overview](#architecture-overview)
3. [Directory Structure](#directory-structure)
4. [Core Components](#core-components)
5. [Data Layer](#data-layer)
6. [UI Layer](#ui-layer)
7. [Navigation](#navigation)
8. [Dependencies](#dependencies)
9. [Firebase Integration](#firebase-integration)

---

## Project Configuration

### Build Configuration
- **Gradle Version:** Kotlin DSL
- **Android SDK:**
  - Minimum SDK: 24 (Android 7.0)
  - Target SDK: 36
  - Compile SDK: 36
- **Java Version:** 11
- **Version Code:** 1
- **Version Name:** 1.0

### Key Gradle Files
```
├── build.gradle.kts (Root)
├── settings.gradle.kts
├── app/build.gradle.kts
└── gradle/libs.versions.toml
```

---

## Architecture Overview

### MVVM Pattern
The app follows the **Model-View-ViewModel (MVVM)** architecture pattern:

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    View     │────────>│  ViewModel   │────────>│ Repository  │
│  (Compose)  │<────────│              │<────────│             │
└─────────────┘         └──────────────┘         └─────────────┘
                                                         │
                                                         ▼
                                                  ┌─────────────┐
                                                  │   Firebase  │
                                                  │   Database  │
                                                  └─────────────┘
```

**Benefits:**
- Separation of concerns
- Testability
- Reactive data flow with LiveData
- State management with StateFlow

---

## Directory Structure

### Complete Project Structure
```
project261/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/uilover/project261/
│   │   │   │   ├── domain/              # Data Models
│   │   │   │   │   ├── CategoryModel.kt
│   │   │   │   │   └── ProductModel.kt
│   │   │   │   │
│   │   │   │   ├── Repository/          # Data Layer
│   │   │   │   │   └── MainRepository.kt
│   │   │   │   │
│   │   │   │   ├── viewModel/           # Business Logic
│   │   │   │   │   └── MainViewModel.kt
│   │   │   │   │
│   │   │   │   ├── Helper/              # Utility Classes
│   │   │   │   │   ├── ChangeNumberItemsListener.kt
│   │   │   │   │   ├── ManagmentCart.kt
│   │   │   │   │   └── TinyDB.java
│   │   │   │   │
│   │   │   │   ├── screens/             # UI Screens
│   │   │   │   │   ├── dashboard/       # Home Screen
│   │   │   │   │   │   ├── MainScreen.kt
│   │   │   │   │   │   ├── CategoryItem.kt
│   │   │   │   │   │   ├── ProductItemCardGrid.kt
│   │   │   │   │   │   ├── TopBar.kt
│   │   │   │   │   │   └── MyBottomBar.kt
│   │   │   │   │   │
│   │   │   │   │   ├── detailProduct/   # Product Detail Screen
│   │   │   │   │   │   ├── DetailScreen.kt
│   │   │   │   │   │   ├── HeaderSection.kt
│   │   │   │   │   │   ├── DescriptionSection.kt
│   │   │   │   │   │   ├── FooterSection.kt
│   │   │   │   │   │   ├── RecommendedList.kt
│   │   │   │   │   │   ├── RowDetail.kt
│   │   │   │   │   │   └── TitleNumberRow.kt
│   │   │   │   │   │
│   │   │   │   │   └── ItemsList/       # Category Items Screen
│   │   │   │   │       ├── ItemListScreen.kt
│   │   │   │   │       └── ItemsCard.kt
│   │   │   │   │
│   │   │   │   ├── ui/                  # UI Components
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── Screen.kt
│   │   │   │   │   └── theme/
│   │   │   │   │
│   │   │   │   └── MainActivity.kt      # Entry Point
│   │   │   │
│   │   │   ├── res/                     # Resources
│   │   │   │   ├── drawable/
│   │   │   │   ├── mipmap-*/
│   │   │   │   ├── values/
│   │   │   │   └── xml/
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── androidTest/                 # Android Tests
│   │   └── test/                        # Unit Tests
│   │
│   ├── build.gradle.kts
│   ├── google-services.json             # Firebase Config
│   └── proguard-rules.pro
│
├── gradle/
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── project-docs/                        # Documentation
    └── PROJECT_STRUCTURE.md
```

---

## Core Components

### 1. MainActivity
**Location:** `MainActivity.kt`  
**Purpose:** Application entry point and navigation host

**Responsibilities:**
- Initialize app with `enableEdgeToEdge()`
- Set up Jetpack Compose UI
- Host navigation graph
- Initialize ViewModel

**Key Features:**
```kotlin
- NavHost setup
- ViewModel instantiation
- Screen navigation routing
```

### 2. Navigation
**Location:** `ui/navigation/Screen.kt`

**Screen Routes:**
```kotlin
sealed class Screen(val route: String) {
    - Home: "home"
    - Items: "itemsList/{id}/{title}"
    - Detail: "detail"
}
```

**Navigation Flow:**
```
Home Screen → Category Selection → Items List → Product Detail
     ↓
  Best Products → Product Detail
```

---

## Data Layer

### Domain Models

#### 1. CategoryModel
**Location:** `domain/CategoryModel.kt`

```kotlin
data class CategoryModel(
    var Id: Int = 0,
    var ImagePath: String = "",
    var Name: String = ""
)
```

**Fields:**
- `Id`: Unique category identifier
- `ImagePath`: URL to category image
- `Name`: Category display name

**Usage:** Represents cosmetic categories (e.g., Skincare, Makeup, Fragrance)

---

#### 2. ProductModel
**Location:** `domain/ProductModel.kt`

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

**Fields:**
- `BestProduct`: Flag for featured products
- `CategoryId`: Foreign key to category
- `Description`: Product details
- `ImagePath`: Product image URL
- `Price`: Product price
- `Star`: Rating (0.0 - 5.0)
- `Volume`: Product volume (e.g., "50ml", "100ml")
- `numberInCart`: Quantity in shopping cart

---

### Repository Layer

#### MainRepository
**Location:** `Repository/MainRepository.kt`

**Purpose:** Manages data operations with Firebase Realtime Database

**Key Methods:**

1. **loadCategory()**
   - Returns: `LiveData<MutableList<CategoryModel>>`
   - Fetches all cosmetic categories from Firebase
   - Real-time updates with ValueEventListener

2. **loadBestProducts()**
   - Returns: `LiveData<MutableList<ProductModel>>`
   - Queries products with `BestProduct = true`
   - Used for featured products section

3. **loadFiltered(id: String)**
   - Returns: `LiveData<MutableList<ProductModel>>`
   - Filters products by CategoryId
   - Used in category-specific product listing

**Firebase Structure:**
```
Firebase Realtime Database
├── Category/
│   ├── {categoryId}/
│   │   ├── Id: Int
│   │   ├── Name: String
│   │   └── ImagePath: String
│
└── Products/
    ├── {productId}/
    │   ├── Id: Int
    │   ├── Title: String
    │   ├── CategoryId: String
    │   ├── Price: Double
    │   ├── BestProduct: Boolean
    │   ├── Star: Double
    │   ├── Volume: String
    │   └── ImagePath: String
```

---

### ViewModel Layer

#### MainViewModel
**Location:** `viewModel/MainViewModel.kt`

**Purpose:** Manages UI state and business logic

**State Management:**
```kotlin
- LiveData for category and product lists
- StateFlow for selected product
```

**Key Methods:**
1. `loadCategory()` - Fetch categories
2. `loadBestProducts()` - Fetch featured products
3. `loadFiltered(id)` - Fetch products by category
4. `selectedProduct(product)` - Update selected product state

---

## Helper Classes

### 1. ManagmentCart
**Location:** `Helper/ManagmentCart.kt`

**Purpose:** Shopping cart management

**Key Features:**
- Add/remove products
- Update quantities
- Persist cart with TinyDB
- Calculate totals
- Toast notifications

**Methods:**
```kotlin
- insertItem(item: ProductModel)
- getListCart(): ArrayList<ProductModel>
- minusItem(...)
- plusItem(...)
```

### 2. TinyDB
**Location:** `Helper/TinyDB.java`

**Purpose:** Local data persistence using SharedPreferences

**Features:**
- Store objects as JSON
- Retrieve cart data
- Lightweight storage solution

### 3. ChangeNumberItemsListener
**Location:** `Helper/ChangeNumberItemsListener.kt`

**Purpose:** Interface for cart quantity changes

---

## UI Layer

### Screen Components

#### 1. Dashboard (Home Screen)
**Location:** `screens/dashboard/`

**Components:**
- **MainScreen.kt** - Main container
- **TopBar.kt** - App header
- **CategoryItem.kt** - Category cards
- **ProductItemCardGrid.kt** - Best products grid
- **MyBottomBar.kt** - Navigation bar

**Features:**
- Category horizontal scroll
- Best products grid layout
- Loading states
- Navigation to category items
- Navigation to product details

**UI Layout:**
```
┌─────────────────────────────┐
│        Top Bar              │
├─────────────────────────────┤
│  [Category] [Category]      │ ← Horizontal Scroll
├─────────────────────────────┤
│  Best Products Section      │
│  ┌──────┐  ┌──────┐        │
│  │ Prod │  │ Prod │        │ ← Grid Layout
│  └──────┘  └──────┘        │
│  ┌──────┐  ┌──────┐        │
│  │ Prod │  │ Prod │        │
│  └──────┘  └──────┘        │
├─────────────────────────────┤
│       Bottom Bar            │
└─────────────────────────────┘
```

---

#### 2. Product Detail Screen
**Location:** `screens/detailProduct/`

**Components:**
- **DetailScreen.kt** - Main container
- **HeaderSection.kt** - Product image & basic info
- **DescriptionSection.kt** - Product details
- **RowDetail.kt** - Specification rows
- **TitleNumberRow.kt** - Quantity selector
- **FooterSection.kt** - Add to cart button
- **RecommendedList.kt** - Related products

**Features:**
- Product image display
- Price and rating
- Volume/size information
- Quantity adjustment
- Add to cart functionality
- Recommended products
- Back navigation

**UI Flow:**
```
Product Image
    ↓
Title & Rating
    ↓
Price & Volume
    ↓
Description
    ↓
Quantity Selector
    ↓
Add to Cart Button
    ↓
Recommended Products
```

---

#### 3. Items List Screen
**Location:** `screens/ItemsList/`

**Components:**
- **ItemListScreen.kt** - Category products list
- **ItemsCard.kt** - Product card component

**Features:**
- Display products by category
- Grid or list view
- Filter by category ID
- Navigate to product details

---

## Navigation

### Navigation Graph
**Implementation:** Jetpack Compose Navigation

**Routes:**

1. **Home Route** (`"home"`)
   - Screen: MainScreen
   - Start destination
   - Shows categories and best products

2. **Items Route** (`"itemsList/{id}/{title}"`)
   - Screen: ItemListScreen
   - Parameters: categoryId, categoryTitle
   - Shows products in selected category

3. **Detail Route** (`"detail"`)
   - Screen: DetailScreen
   - Uses StateFlow for product data
   - Shows detailed product information

**Navigation Actions:**
```kotlin
Home → Items: navController.navigate(Screen.Items.path(id, title))
Home → Detail: viewModel.selectedProduct(product) + navigate
Items → Detail: viewModel.selectedProduct(product) + navigate
Detail → Back: navController.navigateUp()
```

---

## Dependencies

### Core Dependencies
```kotlin
// Jetpack Compose
- androidx.compose.bom:2025.10.01
- androidx.compose.material3
- androidx.compose.ui
- androidx.compose.foundation:1.9.4

// Navigation
- androidx.navigation:navigation-compose:2.9.5

// Lifecycle & ViewModel
- androidx.lifecycle:lifecycle-runtime-ktx:2.9.4
- androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4
- androidx.compose.runtime:runtime-livedata:1.7.6

// Image Loading
- io.coil-kt:coil-compose:2.7.0

// Firebase
- com.google.firebase:firebase-database

// Local Storage
- com.google.code.gson:gson:2.13.2

// Layout
- androidx.constraintlayout:constraintlayout-compose:1.1.1

// Activity
- androidx.activity:activity-compose:1.11.0
```

### Testing Dependencies
```kotlin
- junit
- androidx.test.junit
- androidx.test.espresso.core
- androidx.compose.ui.test.junit4
```

---

## Firebase Integration

### Setup
**Configuration File:** `app/google-services.json`

### Database Structure
```json
{
  "Category": {
    "1": {
      "Id": 1,
      "Name": "Skincare",
      "ImagePath": "url_to_image"
    }
  },
  "Products": {
    "1": {
      "Id": 1,
      "Title": "Moisturizing Cream",
      "CategoryId": "1",
      "Price": 29.99,
      "BestProduct": true,
      "Star": 4.5,
      "Volume": "50ml",
      "Description": "Hydrating face cream",
      "ImagePath": "url_to_image"
    }
  }
}
```

### Data Operations
- **Read:** Real-time listeners with `ValueEventListener`
- **Queries:** `orderByChild()`, `equalTo()`
- **Updates:** LiveData emission on data changes

---

## Key Features

### 1. Shopping Cart
- Local persistence with TinyDB
- Add/remove products
- Quantity management
- Cart total calculation

### 2. Product Browsing
- Category-based navigation
- Best/featured products
- Product filtering
- Search by category

### 3. Product Details
- Full product information
- Image gallery
- Rating and reviews
- Recommended products
- Volume/size options

### 4. UI/UX
- Material Design 3
- Jetpack Compose
- Smooth animations
- Loading states
- Error handling
- Bottom navigation
- Edge-to-edge display

---

## Build & Run

### Requirements
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK 24+
- Firebase account

### Build Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Installation
1. Clone the repository
2. Add `google-services.json` to `app/` directory
3. Sync Gradle dependencies
4. Build and run on device/emulator

---

## Future Enhancements

### Suggested Features
1. **User Authentication** - Firebase Auth integration
2. **Order Management** - Checkout and order tracking
3. **Payment Integration** - Payment gateway
4. **User Reviews** - Product reviews and ratings
5. **Wishlist** - Save favorite products
6. **Search** - Global product search
7. **Filters** - Price, brand, rating filters
8. **Notifications** - Order updates and promotions
9. **Profile** - User profile management
10. **Dark Mode** - Theme switching

---

## Project Metadata

**Created:** 2025  
**Last Updated:** December 27, 2025  
**Technology Stack:**
- Language: Kotlin
- Framework: Jetpack Compose
- Backend: Firebase Realtime Database
- Architecture: MVVM
- Build System: Gradle (Kotlin DSL)

**Team:** Nhóm nhung  
**Application Domain:** Cosmetic E-commerce

---

## Contact & Support

For questions or contributions, please refer to the project repository or contact the development team.

---

**End of Documentation**

