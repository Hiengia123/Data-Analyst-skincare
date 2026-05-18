# Cosmetic App - Quick Reference Guide

## Project Quick Facts

- **App Name:** project261
- **Type:** Cosmetic E-commerce App
- **Platform:** Android
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM
- **Database:** Firebase Realtime Database
- **Language:** Kotlin

---

## Key File Locations

### Models
- Categories: `app/src/main/java/com/uilover/project261/domain/CategoryModel.kt`
- Products: `app/src/main/java/com/uilover/project261/domain/ProductModel.kt`

### Screens
- Home: `app/src/main/java/com/uilover/project261/screens/dashboard/MainScreen.kt`
- Product Detail: `app/src/main/java/com/uilover/project261/screens/detailProduct/DetailScreen.kt`
- Items List: `app/src/main/java/com/uilover/project261/screens/ItemsList/ItemListScreen.kt`

### Core Logic
- Repository: `app/src/main/java/com/uilover/project261/Repository/MainRepository.kt`
- ViewModel: `app/src/main/java/com/uilover/project261/viewModel/MainViewModel.kt`
- Cart Manager: `app/src/main/java/com/uilover/project261/Helper/ManagmentCart.kt`

### Navigation
- Routes: `app/src/main/java/com/uilover/project261/ui/navigation/Screen.kt`
- Navigation Host: `app/src/main/java/com/uilover/project261/MainActivity.kt`

---

## App Screens

1. **Home Screen** - Categories + Best Products
2. **Items List Screen** - Products by Category
3. **Product Detail Screen** - Full product info + Add to Cart

---

## Data Models

### CategoryModel
```kotlin
{
    Id: Int
    ImagePath: String
    Name: String
}
```

### ProductModel
```kotlin
{
    Id: Int
    Title: String
    CategoryId: String
    Price: Double
    BestProduct: Boolean
    Star: Double (rating)
    Volume: String (e.g., "50ml")
    Description: String
    ImagePath: String
    numberInCart: Int
}
```

---

## Firebase Database Paths

- **Categories:** `/Category`
- **Products:** `/Products`

### Query Examples
- All categories: `ref.child("Category")`
- Best products: `ref.child("Products").orderByChild("BestProduct").equalTo(true)`
- Products by category: `ref.child("Products").orderByChild("CategoryId").equalTo(categoryId)`

---

## Common Tasks

### Add New Product Field
1. Update `ProductModel.kt`
2. Update Firebase data structure
3. Update UI components that display the field
4. Test data sync

### Add New Screen
1. Create screen file in `screens/` package
2. Add route to `Screen.kt`
3. Add composable to `MainActivity` NavHost
4. Add navigation calls from other screens

### Modify Cart Logic
- File: `Helper/ManagmentCart.kt`
- Storage: TinyDB (SharedPreferences)

### Update UI Theme
- Location: `app/src/main/java/com/uilover/project261/ui/theme/`

---

## Build Commands

```bash
# Clean
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on device
./gradlew installDebug
```

---

## Key Dependencies

- **Jetpack Compose** - UI toolkit
- **Coil** - Image loading
- **Firebase** - Backend database
- **Gson** - JSON serialization
- **Navigation Compose** - Screen navigation
- **ConstraintLayout** - Layout composition

---

## Common Issues & Solutions

### Firebase not connecting
- Check `google-services.json` is in `app/` folder
- Verify Firebase project configuration
- Check internet permissions in AndroidManifest

### Images not loading
- Verify Coil dependency
- Check image URLs are valid
- Ensure internet permission

### Cart not persisting
- Check TinyDB initialization
- Verify context is passed correctly
- Check SharedPreferences permissions

---

## Package Structure

```
com.uilover.project261/
├── domain/           # Data models
├── Repository/       # Data layer
├── viewModel/        # Business logic
├── Helper/           # Utilities
├── screens/          # UI screens
│   ├── dashboard/
│   ├── detailProduct/
│   └── ItemsList/
├── ui/               # UI components
│   ├── navigation/
│   └── theme/
└── MainActivity.kt   # Entry point
```

---

## Testing

- Unit tests: `app/src/test/`
- Android tests: `app/src/androidTest/`

---

## Resources

- Icons/Images: `app/src/main/res/drawable/`
- Strings: `app/src/main/res/values/strings.xml`
- Colors: `app/src/main/res/values/colors.xml`
- Themes: `app/src/main/res/values/themes.xml`

---

**Last Updated:** December 27, 2025

