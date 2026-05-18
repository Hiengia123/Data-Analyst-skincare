# 🔥 Firebase Setup Guide - Cosmetics E-Commerce App

## 📋 Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Step-by-Step Setup](#step-by-step-setup)
4. [Firebase Console Configuration](#firebase-console-configuration)
5. [Android Project Configuration](#android-project-configuration)
6. [Database Structure Setup](#database-structure-setup)
7. [Security Rules Configuration](#security-rules-configuration)
8. [Testing Firebase Connection](#testing-firebase-connection)
9. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This project uses **Firebase** as the backend infrastructure with the following services:

- **Firebase Authentication**: Email/Password authentication for user login/registration
- **Firebase Realtime Database**: NoSQL database for storing products, users, carts, orders, and favorites
- **Firebase Storage**: Cloud storage for product images and banners

**Firebase Project ID**: `nhung-group`  
**Database Region**: `asia-southeast1` (Singapore)  
**Database URL**: `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`

---

## ✅ Prerequisites

Before starting, ensure you have:

- [ ] Google account (Gmail)
- [ ] Android Studio Hedgehog or later installed
- [ ] JDK 17+ installed
- [ ] Active internet connection
- [ ] Basic understanding of Firebase Console

---

## 🚀 Step-by-Step Setup

### Step 1: Create Firebase Project

1. **Go to Firebase Console**
   - Open [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Sign in with your Google account

2. **Create New Project**
   - Click **"Add project"** or **"Create a project"**
   - Enter Project Name: `Nhung Group` (or your preferred name)
   - Click **"Continue"**

3. **Configure Google Analytics** (Optional)
   - Toggle **"Enable Google Analytics for this project"** (recommended: OFF for development)
   - Click **"Continue"**

4. **Wait for Project Creation**
   - Firebase will create your project (takes ~30 seconds)
   - Click **"Continue"** when ready

---

### Step 2: Add Android App to Firebase

1. **Register Android App**
   - In Firebase Console, click the **Android icon** (🤖)
   - Fill in the form:
     - **Android package name**: `com.uilover.project261`
     - **App nickname**: `Nhung Group App` (optional)
     - **Debug signing certificate SHA-1**: Leave blank for now (optional)
   - Click **"Register app"**

2. **Download Configuration File**
   - Download `google-services.json` file
   - **IMPORTANT**: Save this file for Step 3

3. **Add Firebase SDK**
   - Firebase Console will show SDK instructions
   - **SKIP this step** - we'll configure manually in the next section
   - Click **"Next"** → **"Continue to console"**

---

### Step 3: Add `google-services.json` to Project

1. **Locate Downloaded File**
   - Find the `google-services.json` file you downloaded in Step 2

2. **Copy to Project**
   - Open your project folder: `E:\Nhóm nhung\`
   - Navigate to `app\` folder
   - Paste `google-services.json` here

   **Final Path:**
   ```
   E:\Nhóm nhung\app\google-services.json
   ```

3. **Verify File Location**
   - The file MUST be in the `app\` folder (same level as `build.gradle.kts`)
   - NOT in `app\src\` or any subdirectory

   **Correct Structure:**
   ```
   Nhóm nhung/
   ├── app/
   │   ├── build.gradle.kts
   │   ├── google-services.json  ✅ HERE
   │   ├── proguard-rules.pro
   │   └── src/
   ├── build.gradle.kts
   └── settings.gradle.kts
   ```

---

## ⚙️ Firebase Console Configuration

### Step 4: Enable Authentication

1. **Go to Authentication Section**
   - In Firebase Console, click **"Authentication"** in left sidebar
   - Click **"Get Started"**

2. **Enable Email/Password Provider**
   - Go to **"Sign-in method"** tab
   - Find **"Email/Password"** in the list
   - Click on it
   - Toggle **"Enable"** switch to ON
   - **Email link (passwordless sign-in)**: Leave OFF
   - Click **"Save"**

3. **Verify Authentication Setup**
   - You should see "Email/Password" status as **Enabled**

---

### Step 5: Setup Realtime Database

1. **Create Realtime Database**
   - In Firebase Console, click **"Realtime Database"** in left sidebar
   - Click **"Create Database"**

2. **Choose Database Location**
   - Select **"asia-southeast1 (Singapore)"**
   - Click **"Next"**

3. **Set Security Rules (Temporary)**
   - Choose **"Start in test mode"** (we'll configure rules later)
   - Click **"Enable"**

4. **Note Your Database URL**
   - After creation, you'll see your database URL:
     ```
     https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/
     ```
   - **IMPORTANT**: This URL will be used in the app code

---

### Step 6: Import Database Structure

1. **Prepare Database JSON**
   - Create a file named `database_import.json` with the following content:

```json
{
  "banners": {
    "dior": {
      "url": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/banner_dior.png?alt=media&token=498989bd-3ce9-4e63-95dd-c41e5aaefbcf"
    },
    "chanel": {
      "url": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/banner_chanel.jpg?alt=media&token=d4b9d9c4-3b78-4bf8-b624-7550ce331c16"
    },
    "mac": {
      "url": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/banner_mac.jpg?alt=media&token=fd41cb0d-9cb2-4603-9646-8a31f2a4b3d2"
    },
    "rare": {
      "url": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/banner_rare.jpg?alt=media&token=330d853d-a207-4a64-b2b0-b41f324088b4"
    }
  },
  "categories": {
    "dior": {
      "title": "Dior",
      "picUrl": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/logo_dior.png?alt=media&token=abaf82ab-9d5d-4be7-999a-fbdf60aa3936"
    },
    "chanel": {
      "title": "Chanel",
      "picUrl": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/logo_chanel.png?alt=media&token=ba843904-6337-4b21-9f2e-d77ca8b0def0"
    },
    "mac": {
      "title": "M.A.C",
      "picUrl": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/logo_mac.png?alt=media&token=b2a9c082-9a3a-422e-ad30-9f7fabb0b817"
    },
    "rare": {
      "title": "Rare Beauty",
      "picUrl": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/logo_rare.png?alt=media&token=039a1155-2d3c-43af-93ef-84873d937f7c"
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
  },
  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "price": 1150000,
      "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/son_dior_999.webp?alt=media&token=5046340c-382f-43fd-a498-eab7d72e3138",
      "product_gallery": {
        "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/son_dior_999_1.webp?alt=media&token=72b505c4-00b7-4799-a6e1-1512fc2ff861",
        "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/son_dior_999_2.webp?alt=media&token=5af36014-c0b4-477f-be5b-29ec441dc581"
      },
      "description": "Màu đỏ huyền thoại mang tính biểu tượng của Dior. Chất son Velvet mịn lì như nhung, giữ màu lâu trôi nhưng vẫn mềm môi nhờ chiết xuất hoa mẫu đơn.",
      "categoryId": "dior",
      "categoryTitle": "Dior",
      "productType": "son",
      "weight": "3.5g",
      "availableWeights": ["3g", "3.5g", "7g"],
      "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
      "showRecommend": true,
      "rated": 4.9,
      "keywords": ["son", "dior", "rouge", "999", "do", "lipstick", "velvet", "li", "makeup"]
    }
  }
}
```

   > **Note**: This is a sample structure. Use the full database JSON provided by the user for complete product data.

2. **Import to Firebase**
   - In Firebase Console → Realtime Database
   - Click **⋮** (three dots menu) in top-right
   - Select **"Import JSON"**
   - Choose your `database_import.json` file
   - Click **"Import"**

3. **Verify Import**
   - You should see data nodes: `banners`, `categories`, `attributes`, `items`

---

## 🔒 Security Rules Configuration

### Step 7: Update Database Rules

1. **Go to Rules Tab**
   - In Firebase Console → Realtime Database
   - Click **"Rules"** tab

2. **Replace with Production Rules**
   - Delete existing rules
   - Paste the following:

```json
{
  "rules": {
    ".read": true,
    
    "banners": {
      ".write": false
    },
    "categories": {
      ".write": false
    },
    "attributes": {
      ".write": false
    },
    "items": {
      ".write": false
    },
    
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

3. **Publish Rules**
   - Click **"Publish"** button
   - Confirm the changes

**Security Rules Explanation:**

| Node          | Read Access | Write Access | Purpose |
|---------------|-------------|--------------|---------|
| `banners`     | Everyone    | No one       | Public banners, read-only |
| `categories`  | Everyone    | No one       | Public categories, read-only |
| `attributes`  | Everyone    | No one       | Public product attributes, read-only |
| `items`       | Everyone    | No one       | Public products, read-only |
| `users/{uid}` | Owner only  | Owner only   | User can only access their own data |
| `carts/{uid}` | Owner only  | Owner only   | User can only access their own cart |
| `orders/*`    | Owner only  | Owner only   | User can only create/read their own orders |
| `favorites/{uid}` | Owner only | Owner only | User can only access their own favorites |

---

## 📱 Android Project Configuration

### Step 8: Verify Gradle Configuration

**This project is already configured with Firebase!** Verify the following files:

#### 1. `build.gradle.kts` (Project-level)

```kotlin
// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false // ✅ Firebase plugin
}
```

#### 2. `app/build.gradle.kts` (App-level)

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services) // ✅ Firebase plugin
}

dependencies {
    // 🔥 Firebase Dependencies
    implementation(platform(libs.firebase.bom))      // Firebase BOM (Bill of Materials)
    implementation(libs.firebase.auth)               // Firebase Authentication
    implementation(libs.firebase.database)           // Firebase Realtime Database
    
    // ... other dependencies
}
```

#### 3. `gradle/libs.versions.toml`

```toml
[versions]
googleGmsGoogleServices = "4.4.4"
firebase-bom = "33.7.0"
firebaseDatabase = "22.0.1"

[libraries]
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebase-bom" }
firebase-database = { group = "com.google.firebase", name = "firebase-database" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth-ktx" }

[plugins]
google-gms-google-services = { id = "com.google.gms.google-services", version.ref = "googleGmsGoogleServices" }
```

**✅ No changes needed - already configured!**

---

### Step 9: Sync Gradle

1. **Open Project in Android Studio**
   ```
   File → Open → Select "Nhóm nhung" folder
   ```

2. **Sync Gradle**
   - Click **"Sync Now"** banner (if appears)
   - Or: `File → Sync Project with Gradle Files`
   - Wait for sync to complete

3. **Verify Firebase SDK**
   - Check build output: Should see "BUILD SUCCESSFUL"
   - No errors about `google-services.json` or Firebase

---

## 🧪 Testing Firebase Connection

### Step 10: Test Authentication

1. **Run the App**
   ```
   Run → Run 'app'
   ```

2. **Test Registration**
   - Open app → Click Profile icon
   - Click "Đăng ký ngay"
   - Enter:
     - Email: `test@example.com`
     - Name: `Test User`
     - Password: `123456`
   - Click "Đăng ký"

3. **Verify in Firebase Console**
   - Go to Firebase Console → Authentication → Users tab
   - You should see the new user: `test@example.com`

4. **Verify in Realtime Database**
   - Go to Firebase Console → Realtime Database → Data tab
   - Navigate to `users/{uid}`
   - You should see user data:
     ```json
     {
       "uid": "...",
       "email": "test@example.com",
       "name": "Test User",
       "provider": "email",
       "createdAt": 1234567890
     }
     ```

---

### Step 11: Test Database Read

1. **Browse Products**
   - In app, you should see:
     - Banner carousel
     - Categories (Dior, Chanel, MAC, Rare Beauty)
     - Product grid

2. **Check Logcat**
   - In Android Studio → Logcat
   - Filter: `Firebase`
   - You should see successful database connections:
     ```
     Realtime Database: Connected to Firebase
     ```

---

### Step 12: Test Cart & Orders

1. **Add to Cart (Guest User)**
   - Browse products
   - Add items to cart
   - Verify cart works (local storage)

2. **Login**
   - Click Profile → Login
   - Use test account created earlier

3. **Create Order**
   - Go to Cart → Click "Thanh toán"
   - Fill shipping address
   - Click "Đặt hàng"

4. **Verify in Firebase Database**
   - Go to Firebase Console → Realtime Database
   - Check nodes:
     - `carts/{uid}` - Should contain cart items
     - `orders/{orderId}` - Should contain new order

5. **Check Order History**
   - In app: Bottom Nav → Orders icon
   - You should see your created order

---

## 🔧 Troubleshooting

### Issue 1: `google-services.json` not found

**Error:**
```
File google-services.json is missing.
```

**Solution:**
1. Ensure `google-services.json` is in `app/` folder (NOT in `app/src/`)
2. Sync Gradle again
3. Clean project: `Build → Clean Project`
4. Rebuild: `Build → Rebuild Project`

---

### Issue 2: Firebase Authentication not working

**Error:**
```
FirebaseAuth: Timeout
```

**Solution:**
1. Check internet connection
2. Verify Email/Password is enabled in Firebase Console
3. Ensure `google-services.json` is correct (matches your Firebase project)
4. Check if app package name matches: `com.uilover.project261`

---

### Issue 3: Database permission denied

**Error:**
```
DatabaseError: Permission denied
```

**Solution:**
1. Check Firebase Database Rules
2. For testing, temporarily set:
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
   **⚠️ WARNING: Only use for testing! Reset to production rules after testing.**

---

### Issue 4: Orders not showing up

**Error:**
```
No orders found
```

**Solution:**
1. Add database index for `userId`:
   - Firebase Console → Realtime Database → Rules
   - Add to `orders` node:
     ```json
     "orders": {
       ".indexOn": ["userId"]
     }
     ```
2. Check if user is logged in
3. Verify order was created in Firebase Console → Realtime Database → `orders/`

---

### Issue 5: Images not loading

**Error:**
```
Images showing placeholder/error
```

**Solution:**
1. Check internet connection
2. Verify Firebase Storage URLs are correct
3. Check if Storage CORS is configured:
   - Firebase Console → Storage → Rules
   - Ensure read access:
     ```
     service firebase.storage {
       match /b/{bucket}/o {
         match /{allPaths=**} {
           allow read;
         }
       }
     }
     ```

---

## 📊 Firebase Project Structure

```
Firebase Project: nhung-group
│
├── 🔐 Authentication
│   ├── Email/Password Provider (Enabled)
│   └── Users Collection
│
├── 🗄️ Realtime Database (asia-southeast1)
│   ├── banners/
│   ├── categories/
│   ├── attributes/
│   ├── items/
│   ├── users/
│   ├── carts/
│   ├── orders/
│   └── favorites/
│
└── 📦 Storage (Optional)
    └── Product Images
```

---

## 📝 Database Schema Quick Reference

### Users
```
users/
  {uid}/
    ├── uid: string
    ├── email: string
    ├── name: string
    ├── phone: string
    ├── avatarUrl: string
    ├── provider: "email"
    └── createdAt: timestamp
```

### Carts
```
carts/
  {uid}/
    {productId}/
      ├── id: string
      ├── title: string
      ├── price: number
      ├── quantity: number
      ├── image: string
      ├── selectedColor: string
      └── selectedWeight: string
```

### Orders
```
orders/
  {orderId}/
    ├── orderId: string
    ├── userId: string
    ├── status: "pending" | "shipping" | "delivered" | "cancelled"
    ├── totalPrice: number
    ├── createdAt: timestamp
    ├── paymentMethod: string
    ├── note: string
    ├── shippingAddress/
    │   ├── name: string
    │   ├── phone: string
    │   ├── address: string
    │   ├── city: string
    │   ├── district: string
    │   └── ward: string
    └── items/
        {productId}/
          ├── productId: string
          ├── title: string
          ├── price: number
          ├── quantity: number
          ├── image: string
          ├── selectedColor: string
          └── selectedWeight: string
```

### Favorites
```
favorites/
  {uid}/
    {productId}/
      ├── id: string
      ├── title: string
      ├── price: number
      ├── image: string
      ├── categoryId: string
      └── rated: number
```

---

## 🎓 Best Practices

### 1. **Security**
- ✅ Always use authentication for user-specific data
- ✅ Implement proper database rules
- ✅ Never expose API keys in public repositories
- ❌ Don't use test mode rules in production

### 2. **Database Structure**
- ✅ Use flat data structure (avoid deep nesting)
- ✅ Index frequently queried fields (e.g., `userId`)
- ✅ Use consistent naming conventions
- ✅ Store timestamps as Unix epoch (milliseconds)

### 3. **Performance**
- ✅ Cache frequently accessed data
- ✅ Use `.indexOn` for query optimization
- ✅ Limit data fetching with queries
- ✅ Use pagination for large lists

### 4. **Development Workflow**
- ✅ Use separate Firebase projects for dev/prod
- ✅ Test with emulators when possible
- ✅ Monitor usage in Firebase Console
- ✅ Set up budget alerts

---

## 📚 Additional Resources

### Official Documentation
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication](https://firebase.google.com/docs/auth/android/start)
- [Realtime Database](https://firebase.google.com/docs/database/android/start)
- [Security Rules](https://firebase.google.com/docs/database/security)

### Project Documentation
- `PROJECT_STRUCTURE_APP.md` - Complete project architecture
- `AUTHENTICATION_IMPLEMENTATION_COMPLETE.md` - Auth implementation details
- `FIREBASE_DATABASE_STRUCTURE.md` - Database schema reference
- `CHECKOUT_ORDER_SYSTEM_COMPLETE.md` - Order system guide

---

## ✅ Setup Checklist

Before running the app, ensure:

- [ ] Firebase project created
- [ ] Android app registered in Firebase
- [ ] `google-services.json` downloaded and placed in `app/` folder
- [ ] Email/Password authentication enabled
- [ ] Realtime Database created (asia-southeast1)
- [ ] Database structure imported
- [ ] Security rules configured
- [ ] Gradle synced successfully
- [ ] App builds without errors
- [ ] Test user created and verified in Firebase Console
- [ ] Products loading correctly in app
- [ ] Cart/Orders/Favorites working

---

## 🎉 Congratulations!

Your Firebase setup is complete! You can now:

✅ Register and login users  
✅ Browse products from Realtime Database  
✅ Manage shopping cart  
✅ Create and track orders  
✅ Save favorite products  
✅ Sync data across devices  

**Next Steps:**
1. Test all features thoroughly
2. Add more products to database
3. Configure Firebase Storage for custom uploads
4. Set up Firebase Analytics (optional)
5. Deploy to production

---

**Project**: Nhóm Nhung - Cosmetics E-Commerce  
**Last Updated**: January 6, 2026  
**Firebase Version**: 33.7.0  
**Android SDK**: 24-36  

---

**END OF FIREBASE SETUP GUIDE**

