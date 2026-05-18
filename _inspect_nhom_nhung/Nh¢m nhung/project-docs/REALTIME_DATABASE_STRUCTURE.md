# 🗄️ Firebase Realtime Database – Full Structure Documentation
> **Project:** Nhóm Nhung Cosmetics App  
> **Database URL:** `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`  
> **Last Updated:** May 2026  
> **Region:** Asia Southeast 1 (Singapore)

---

## 📋 Table of Contents
1. [Top-Level Structure Overview](#1-top-level-structure-overview)
2. [banners](#2-banners)
3. [categories](#3-categories)
4. [attributes](#4-attributes)
5. [items (Products)](#5-items-products)
6. [users](#6-users)
7. [carts](#7-carts)
8. [orders](#8-orders)
9. [favorites](#9-favorites)
10. [Security Rules](#10-security-rules)
11. [Indexes Configured](#11-indexes-configured)
12. [Real Data Summary](#12-real-data-summary)
13. [Relationships Diagram](#13-relationships-diagram)

---

## 1. Top-Level Structure Overview

```
nhung-group-default-rtdb (root)
│
├── banners/               ← Brand promotional banners (public)
├── categories/            ← Product brand categories (public)
├── attributes/            ← Product attribute definitions (public)
├── items/                 ← All product catalog (public)
│
├── users/                 ← User profiles (private – owner only)
├── carts/                 ← Shopping carts per user (private)
├── orders/                ← All orders (auth required)
└── favorites/             ← Wishlist per user (private)
```

---

## 2. `banners`

Promotional brand banner images displayed in the homepage carousel.

### Schema
```json
"banners": {
  "<brandKey>": {
    "url": "string"        // Firebase Storage image URL
  }
}
```

### Current Data
| Key | Brand | URL |
|-----|-------|-----|
| `dior` | Dior | `banner_dior.png` |
| `chanel` | Chanel | `banner_chanel.jpg` |
| `mac` | M.A.C | `banner_mac.jpg` |
| `rare` | Rare Beauty | `banner_rare.jpg` |

### Access Rule
```
"banners": { ".read": true }
```
> Public – no authentication required.

---

## 3. `categories`

Brand category list used to display the horizontal brand selector on the homepage.

### Schema
```json
"categories": {
  "<categoryId>": {
    "title": "string",     // Display name (e.g., "Dior")
    "picUrl": "string"     // Brand logo image URL (Firebase Storage)
  }
}
```

### Current Data
| Key | Title | Logo File |
|-----|-------|-----------|
| `dior` | Dior | `logo_dior.png` |
| `chanel` | Chanel | `logo_chanel.png` |
| `mac` | M.A.C | `logo_mac.png` |
| `rare` | Rare Beauty | `logo_rare.png` |

### Access Rule
```
"categories": { ".read": true }
```
> Public – no authentication required.

---

## 4. `attributes`

Lookup tables for product variant options. Used to define available choices for filtering.

### Schema
```json
"attributes": {
  "capacity": {
    "<value>": true        // e.g., "30ml", "50ml", "100ml", "150ml"
  },
  "weight": {
    "<value>": true        // e.g., "3g", "3_5g", "7g"
  },
  "productType": {
    "<value>": true        // e.g., "son", "sua_rua_mat", "kem_chong_nang"
  }
}
```

### Current Values

**capacity:**
- `30ml`, `50ml`, `100ml`, `150ml`

**weight:**
- `3g`, `3_5g` (3.5g), `7g`

**productType:**
- `son` (Lipstick/Lip products)
- `sua_rua_mat` (Face Wash/Cleanser)
- `kem_chong_nang` (Sunscreen/Sun Protection)

### Access Rule
```
"attributes": { ".read": true }
```
> Public – no authentication required.

---

## 5. `items` (Products)

The main product catalog. All cosmetic products are stored here.

### Schema
```json
"items": {
  "<productId>": {
    "title": "string",                  // Full product name (Vietnamese)
    "price": number,                    // Price in VND (e.g., 1150000)
    "image": "string",                  // Main product image URL
    "product_gallery": {
      "img1": "string",                 // Gallery image 1 URL
      "img2": "string"                  // Gallery image 2 URL (optional)
    },
    "description": "string",           // Product description (Vietnamese)
    "categoryId": "string",            // Brand key (matches categories key)
    "categoryTitle": "string",         // Brand display name (e.g., "Dior")
    "productType": "string",           // Type: son | sua_rua_mat | kem_chong_nang
    "capacity": "string",              // Default capacity (for liquid products, e.g., "30ml")
    "weight": "string",                // Default weight (for solid products, e.g., "3.5g")
    "availableCapacities": ["string"], // Array of selectable capacities
    "availableWeights": ["string"],    // Array of selectable weights
    "availableColors": ["string"],     // Array of selectable colors/shades
    "showRecommend": boolean,          // Show in "Recommended" section on homepage
    "rated": number,                   // Rating score (0.0 – 5.0)
    "keywords": ["string"]             // Search keywords (Vietnamese, no accents)
  }
}
```

### Current Products (16 items)

| Product ID | Title | Brand | Type | Price (VND) | Rating |
|------------|-------|-------|------|-------------|--------|
| `dior_lipstick_999` | Son Dior Rouge 999 Velvet | Dior | son | 1,150,000 | 4.9 |
| `dior_lip_glow_001` | Son Dưỡng Dior Addict Lip Glow | Dior | son | 980,000 | 4.8 |
| `dior_prestige_cleanser` | Sữa Rửa Mặt Dior Prestige La Mousse | Dior | sua_rua_mat | 2,300,000 | 4.9 |
| `dior_solar_sunscreen` | Kem Chống Nắng Dior Solar SPF 50 | Dior | kem_chong_nang | 1,350,000 | 4.7 |
| `dior_cleanser_off_on` | Sữa Rửa Mặt Dior La Mousse OFF/ON | Dior | sua_rua_mat | 1,450,000 | 4.8 |
| `chanel_lipstick_velvet` | Son Chanel Rouge Allure Velvet | Chanel | son | 1,230,000 | 4.8 |
| `chanel_cleanser_gel` | Gel Rửa Mặt Chanel Le Gel | Chanel | sua_rua_mat | 1,450,000 | 4.7 |
| `chanel_cc_cream` | Kem Nền Chống Nắng Chanel CC Cream SPF 50 | Chanel | kem_chong_nang | 1,690,000 | 4.9 |
| `chanel_sunscreen_uv` | Kem Chống Nắng Chanel UV Essentiel | Chanel | kem_chong_nang | 1,650,000 | 4.7 |
| `chanel_cleanser_mousse` | Sữa Rửa Mặt Chanel La Mousse | Chanel | sua_rua_mat | 1,350,000 | 4.8 |
| `mac_lipstick_ruby_woo` | Son MAC Retro Matte - Ruby Woo | M.A.C | son | 650,000 | 4.6 |
| `mac_prep_prime` | Kem Lót/Chống Nắng MAC Prep+Prime | M.A.C | kem_chong_nang | 1,050,000 | 4.5 |
| `rare_blush_joy` | Má Hồng Rare Beauty Soft Pinch - Joy | Rare Beauty | son | 750,000 | 5.0 |
| `rare_tinted_moisturizer` | Rare Beauty Positive Light Tinted (KCN) | Rare Beauty | kem_chong_nang | 890,000 | 4.5 |
| `rare_lip_oil_wonder` | Son Dầu Rare Beauty Lip Oil - Wonder | Rare Beauty | son | 620,000 | 4.9 |

### Product ID Naming Convention
```
<brandKey>_<shortDescription>
e.g.: dior_lipstick_999, chanel_cc_cream, mac_prep_prime
```

### Access Rule
```
"items": {
  ".read": true,
  ".indexOn": ["categoryId", "productType", "price", "rated", "showRecommend", "capacity", "weight"]
}
```
> Public – no authentication required. Multiple indexes for efficient queries.

---

## 6. `users`

User profile data created on first login/registration via Firebase Auth.

### Schema
```json
"users": {
  "<uid>": {                         // Firebase Auth UID (auto-generated)
    "uid": "string",                 // Same as key – for convenience
    "email": "string",               // Email address
    "name": "string",                // Display name (entered during registration)
    "phone": "string",               // Phone number (optional, set in profile)
    "avatarUrl": "string",           // Profile photo URL (currently empty)
    "provider": "string",            // Auth method: "email" | "google" | "facebook"
    "createdAt": number              // Unix timestamp (milliseconds) of registration
  }
}
```

### Current Users (7 accounts)
| UID (short) | Name | Email | Registered |
|-------------|------|-------|------------|
| `Mc0kE4k...` | nhung | nhung@gmail.com | Feb 2026 |
| `OuMSB67...` | Nguyen Duy Hien | hien22cdpkthn@gmail.com | Feb 2026 |
| `8O8i4cW...` | Thanh | Minhthanh@gmail.com | Mar 2026 |
| `Z1SKddE...` | thu | thu@gmail.com | Feb 2026 |
| `P70tpnV...` | test | test@gmail.com | Mar 2026 |
| `wfljSDv...` | abc | abc@gmail.com | Mar 2026 |
| `zhWv9zC...` | thanh | thanh@gmail.com | May 2026 |

### Access Rule
```
"users": {
  "$uid": {
    ".read": "auth != null && auth.uid === $uid",
    ".write": "auth != null && auth.uid === $uid"
  }
}
```
> Private – only the owner can read/write their own profile.

---

## 7. `carts`

Shopping cart stored per user in the database. Synced from local cart (TinyDB) after login.

### Schema
```json
"carts": {
  "<uid>": {
    "<productId>": {
      "productId": "string",          // Product key from items/
      "title": "string",              // Product name
      "price": number,                // Unit price in VND
      "quantity": number,             // Quantity in cart (min: 1)
      "image": "string",              // Product image URL
      "selectedColor": "string",      // Selected color/shade
      "selectedWeight": "string",     // Selected weight variant
      "selectedCapacity": "string"    // Selected capacity variant
    }
  }
}
```

### Notes
- Cart is stored **locally** (TinyDB/SharedPreferences) before login
- After login, local cart is **merged** into Firebase under `carts/{uid}`
- Cart is **cleared** after successful order placement

### Access Rule
```
"carts": {
  "$uid": {
    ".read": "auth != null && auth.uid === $uid",
    ".write": "auth != null && auth.uid === $uid"
  }
}
```
> Private – only the cart owner can read/write.

---

## 8. `orders`

All placed orders. Each order contains full shipping info, item snapshot, and status.

### Schema
```json
"orders": {
  "<orderId>": {
    "orderId": "string",             // e.g., "ORDER_4FA744FE" (ORDER_ + 8 hex chars)
    "userId": "string",              // Firebase Auth UID of buyer
    "status": "string",              // "pending" | "shipping" | "delivered" | "cancelled"
    "totalPrice": number,            // Total order value in VND
    "createdAt": number,             // Unix timestamp (milliseconds)
    "paymentMethod": "string",       // "cod" | "card" | "momo"
    "note": "string",                // Optional buyer note
    "shippingAddress": {
      "name": "string",              // Recipient name
      "phone": "string",             // Recipient phone
      "address": "string",           // Street address
      "city": "string",              // City (e.g., "TP. Hồ Chí Minh")
      "district": "string",          // District
      "ward": "string"               // Ward/Commune
    },
    "items": {
      "<productId>_<timestamp>": {   // Key: productId + "_" + createdAt timestamp
        "productId": "string",       // Original product key from items/
        "title": "string",           // Product name at time of order
        "price": number,             // Unit price at time of order (VND)
        "quantity": number,          // Quantity ordered
        "image": "string",           // Product image URL
        "selectedColor": "string",   // Color/shade chosen
        "selectedWeight": "string",  // Weight variant chosen
        "selectedCapacity": "string" // Capacity variant chosen
      }
    }
  }
}
```

### Order ID Format
```
ORDER_<8-char-uppercase-hex>
e.g.: ORDER_4FA744FE, ORDER_63B7FE90
```

### Order Status Flow
```
pending → shipping → delivered
    └──────────────→ cancelled
```

### Order Item Key Format
```
<productId>_<createdAtTimestamp>
e.g.: chanel_cc_cream_1766888515052
```
> This key format ensures uniqueness even when the same product appears in multiple orders.

### Real Orders Sample (from export)

| Order ID | User | Items | Total (VND) | Status |
|----------|------|-------|-------------|--------|
| ORDER_4FA744FE | nhung | MAC Ruby Woo ×1 | 650,000 | pending |
| ORDER_5BF2A1F2 | nhung | Chanel CC Cream ×1 | 1,690,000 | pending |
| ORDER_63B7FE90 | nhung | CC Cream + Velvet ×1 each | 2,920,000 | pending |
| ORDER_706DE8F8 | nhung | CC Cream + Le Gel ×1 each | 3,140,000 | pending |
| ORDER_7A28D96E | Nguyen Duy Hien | Chanel CC Cream ×1 | 1,690,000 | pending |
| ORDER_98085520 | Nguyen Duy Hien | Le Gel + Velvet ×1 each | 2,680,000 | pending |
| ORDER_98DCA787 | nhung | Le Gel + Velvet ×1 each | 2,680,000 | pending |
| ORDER_EF8E1710 | Nguyen Duy Hien | Chanel CC Cream ×1 | 1,690,000 | pending |
| ORDER_40B477D6 | Thanh | Chanel CC Cream ×2 | 3,380,000 | pending |

### Access Rule
```
"orders": {
  ".read": "auth != null",
  ".write": "auth != null",
  ".indexOn": ["userId", "createdAt"]
}
```
> Requires authentication. Indexed by `userId` and `createdAt` for efficient per-user queries sorted by date.

---

## 9. `favorites`

User's saved/wishlisted products. Full product snapshots are stored (not just IDs).

### Schema
```json
"favorites": {
  "<uid>": {
    "<productId>": {              // Full ProductModel snapshot
      "id": "string",
      "title": "string",
      "price": number,
      "image": "string",
      "product_gallery": { "img1": "string", "img2": "string" },
      "description": "string",
      "categoryId": "string",
      "categoryTitle": "string",
      "productType": "string",
      "capacity": "string",
      "weight": "string",
      "availableCapacities": ["string"],
      "availableWeights": ["string"],
      "availableColors": ["string"],
      "showRecommend": boolean,
      "rated": number,
      "keywords": ["string"],
      "numberInCart": number,
      "selectedCapacity": "string",
      "selectedWeight": "string",
      "selectedColor": "string"
    }
  }
}
```

### Design Note
> Favorites store a **full product snapshot** rather than just the product ID. This means product data is available offline and doesn't require a second lookup to `items/`. Trade-off: data may become stale if product info changes.

### Current Favorites (from export)
- User `Mc0kE4k...` (nhung) has favorited: `chanel_cc_cream`, `chanel_cleanser_gel`

### Access Rule
```
"favorites": {
  "$uid": {
    ".read": "auth != null && auth.uid === $uid",
    ".write": "auth != null && auth.uid === $uid"
  }
}
```
> Private – only the owner can read/write their wishlist.

---

## 10. Security Rules

Full security rules configuration:

```json
{
  "rules": {
    "items": {
      ".read": true,
      ".indexOn": ["categoryId", "productType", "price", "rated", "showRecommend", "capacity", "weight"]
    },
    "banners":    { ".read": true },
    "categories": { ".read": true },
    "attributes": { ".read": true },

    "users": {
      "$uid": {
        ".read":  "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    },

    "carts": {
      "$uid": {
        ".read":  "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    },

    "favorites": {
      "$uid": {
        ".read":  "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    },

    "orders": {
      ".read":    "auth != null",
      ".write":   "auth != null",
      ".indexOn": ["userId", "createdAt"]
    }
  }
}
```

### Access Summary Table

| Node | Public Read | Auth Read | Auth Write | Owner Only |
|------|:-----------:|:---------:|:----------:|:----------:|
| `banners` | ✅ | ✅ | ❌ | ❌ |
| `categories` | ✅ | ✅ | ❌ | ❌ |
| `attributes` | ✅ | ✅ | ❌ | ❌ |
| `items` | ✅ | ✅ | ❌ | ❌ |
| `users/$uid` | ❌ | ✅ | ✅ | ✅ |
| `carts/$uid` | ❌ | ✅ | ✅ | ✅ |
| `favorites/$uid` | ❌ | ✅ | ✅ | ✅ |
| `orders` | ❌ | ✅ | ✅ | ❌ |

---

## 11. Indexes Configured

Indexes are required by Firebase for efficient `orderByChild()` queries.

| Node | Indexed Fields | Used For |
|------|---------------|----------|
| `items` | `categoryId` | Filter products by brand |
| `items` | `productType` | Filter by type (son, kem_chong_nang, etc.) |
| `items` | `price` | Price range filter / sort |
| `items` | `rated` | Sort by rating |
| `items` | `showRecommend` | Homepage recommended section |
| `items` | `capacity` | Filter by size (liquid) |
| `items` | `weight` | Filter by size (solid) |
| `orders` | `userId` | Query orders per user |
| `orders` | `createdAt` | Sort orders by date |

---

## 12. Real Data Summary

### As of May 2026

| Collection | Count |
|-----------|-------|
| Banners | 4 |
| Categories | 4 |
| Products (`items`) | 15 |
| Users | 7 |
| Orders | 9 |
| Favorites entries | 1 user with 2 items |

### Revenue from Orders (All Pending)
| User | Orders | Total Spend (VND) |
|------|--------|-------------------|
| nhung (Mc0kE4k...) | 5 | ~13,070,000 |
| Nguyen Duy Hien (OuMSB67...) | 3 | ~6,060,000 |
| Thanh (8O8i4cW...) | 1 | 3,380,000 |

### Most Ordered Product
`chanel_cc_cream` – Kem Nền Chống Nắng Chanel CC Cream SPF 50 *(appears in 6 out of 9 orders)*

### Brand Distribution (Orders)
| Brand | Order Appearances |
|-------|------------------|
| Chanel | 8 |
| M.A.C | 1 |
| Dior | 0 |
| Rare Beauty | 0 |

---

## 13. Relationships Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    FIREBASE REALTIME DATABASE                │
└─────────────────────────────────────────────────────────────┘

  [PUBLIC CATALOG]                    [USER DATA]
  ─────────────────                   ──────────────────────

  banners/                            users/
    └── {brandKey}                      └── {uid}
          url                                 uid, email, name
                                              phone, avatarUrl
  categories/                               provider, createdAt
    └── {categoryId}                           │
          title, picUrl                        │
                                               │
  attributes/                         carts/  │
    ├── capacity                        └── {uid} ◄─── same uid
    ├── weight                                └── {productId}
    └── productType                                 qty, price, variants
                  │
                  │ (lookup)           favorites/
  items/          │                    └── {uid} ◄─── same uid
    └── {productId} ◄───────────────────   └── {productId}
          title, price, image                     full product snapshot
          categoryId ──► categories
          productType ──► attributes
          keywords, rated, etc.        orders/
                                        └── {orderId}
                                              userId ──► users/{uid}
                                              status, totalPrice
                                              createdAt, paymentMethod
                                              shippingAddress { ... }
                                              items/
                                                └── {productId}_{ts}
                                                      productId ──► items
                                                      title, price (snapshot)
                                                      quantity, variants
```

---

## 📝 Notes & Best Practices

1. **Product data in orders is snapshotted** – prices/names in old orders won't change if products are updated.
2. **Favorites also snapshot** – product data stored in favorites may differ from current `items/` data.
3. **Order item key = `productId_timestamp`** – ensures uniqueness when same product appears in multiple orders of a user.
4. **All prices are in Vietnamese Đồng (VND)** – stored as raw integers (e.g., `1150000`), formatted in UI as `1.150.000đ`.
5. **`carts/` node** is defined in rules but cart logic currently uses local storage (TinyDB); Firebase cart sync is planned/partially implemented.
6. **`orders/` has no owner restriction** – any authenticated user can technically read all orders. For production, restrict `.read` to `"auth != null && (auth.uid === data.child('userId').val())"`.

---

*Documentation auto-generated from live database export and source code analysis.*
