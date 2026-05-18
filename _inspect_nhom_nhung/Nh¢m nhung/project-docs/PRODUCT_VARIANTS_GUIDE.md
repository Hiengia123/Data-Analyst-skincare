# Product Variants/Options System - Complete Guide

## Overview

Added Shopee/Lazada style product options system where users can select:
- **Capacity** (30ml, 50ml, 100ml, 150ml) - for liquids
- **Weight** (3g, 3.5g, 7g) - for solid products  
- **Color** - for makeup products

## How It Works

### User Experience

When viewing a product detail page, users see option selectors (if available):

```
┌─────────────────────────────────────┐
│  [Product Image Gallery]            │
├─────────────────────────────────────┤
│  Son Dior Rouge 999 Velvet          │
│  ⭐ 4.9    Dior           3.5g      │
│                                     │
│  Số lượng    [-]  2  [+]            │
│                                     │
│  Khối lượng                         │  ← NEW!
│  [3g]  [3.5g]  [7g]                │
│         ^^^^^ selected              │
│                                     │
│  Màu sắc                            │  ← NEW!
│  [Đỏ]  [Hồng]  [Cam]               │
│   ^^^^ selected                     │
│                                     │
│  Chi tiết                           │
│  Product description...             │
└─────────────────────────────────────┘
```

### Selection States

- **Default**: First option auto-selected
- **Selected**: Pink border + pink text
- **Unselected**: Gray border + black text
- **Click**: Changes selection instantly

## Database Structure

### Before (Old Structure)
```json
{
  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "weight": "3.5g",  // ← Only ONE value
      "capacity": "",
      // ...
    }
  }
}
```

### After (New Structure with Variants)
```json
{
  "items": {
    "dior_lipstick_999": {
      "title": "Son Dior Rouge 999 Velvet",
      "weight": "3.5g",  // Default/display value
      "availableWeights": ["3g", "3.5g", "7g"],  // ← Options!
      "availableColors": ["Đỏ", "Hồng"],         // ← Options!
      // ...
    },
    "chanel_sunscreen_uv": {
      "title": "Kem Chống Nắng Chanel UV",
      "capacity": "30ml",  // Default value
      "availableCapacities": ["30ml", "50ml", "100ml"],  // ← Options!
      // ...
    }
  }
}
```

## How to Add Product Variants

### Example 1: Lipstick with Weight and Color Options

```json
{
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
      
      "weight": "3.5g",  // Default weight (shown initially)
      "availableWeights": ["3g", "3.5g", "7g"],  // ← ADD THIS
      "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],  // ← ADD THIS
      
      "showRecommend": true,
      "rated": 4.9,
      "keywords": ["son", "dior", "rouge", "999"]
    }
  }
}
```

### Example 2: Sunscreen with Capacity Options

```json
{
  "items": {
    "chanel_sunscreen_uv": {
      "title": "Kem Chống Nắng Chanel UV Essentiel",
      "price": 1650000,
      "image": "https://...",
      "categoryId": "chanel",
      "categoryTitle": "Chanel",
      "productType": "kem_chong_nang",
      
      "capacity": "30ml",  // Default capacity
      "availableCapacities": ["30ml", "50ml", "100ml"],  // ← ADD THIS
      
      "showRecommend": true,
      "rated": 4.7
    }
  }
}
```

### Example 3: Cleanser with Capacity Options

```json
{
  "items": {
    "dior_cleanser_off_on": {
      "title": "Sữa Rửa Mặt Dior La Mousse OFF/ON",
      "price": 1450000,
      "categoryId": "dior",
      "productType": "sua_rua_mat",
      
      "capacity": "150ml",  // Default
      "availableCapacities": ["100ml", "150ml", "200ml"],  // ← ADD THIS
      
      "showRecommend": true,
      "rated": 4.8
    }
  }
}
```

## Complete Updated Database Example

Here's your updated database with product variants:

```json
{
  "banners": {
    // ... (unchanged)
  },
  "categories": {
    // ... (unchanged)
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
    },
    "dior_cleanser_off_on": {
      "title": "Sữa Rửa Mặt Dior La Mousse OFF/ON",
      "price": 1450000,
      "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_dior_on.webp?alt=media&token=9b804cef-f83f-428e-8ebc-8e8414727724",
      "product_gallery": {
        "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_dior_on_1.webp?alt=media&token=6d5af5a3-a7aa-4c9c-bdc8-78884b5c5b9d",
        "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_dior_on_2.webp?alt=media&token=4ae46bff-df4b-49ce-887a-6f236c80d316"
      },
      "description": "Sữa rửa mặt tạo bọt với công nghệ OFF/ON: Loại bỏ bụi bẩn, tạp chất (OFF) và bảo vệ, làm dịu da (ON). Thích hợp cho da nhạy cảm.",
      "categoryId": "dior",
      "categoryTitle": "Dior",
      "productType": "sua_rua_mat",
      "capacity": "150ml",
      "availableCapacities": ["100ml", "150ml", "200ml"],
      "showRecommend": true,
      "rated": 4.8,
      "keywords": ["sua rua mat", "dior", "la mousse", "off on", "rua mat", "cleanser", "skincare"]
    },
    "chanel_sunscreen_uv": {
      "title": "Kem Chống Nắng Chanel UV Essentiel",
      "price": 1650000,
      "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_chanel_UV.webp?alt=media&token=2f8a1bbd-d224-4f17-9921-c3edeff14a2a",
      "product_gallery": {
        "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_chanel_UV_1.webp?alt=media&token=9b8a2dc0-bd66-4e17-88b1-2adfc831de38",
        "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_chanel_UV_2.webp?alt=media&token=d40d008f-9782-439b-8302-7bcc1e266a89"
      },
      "description": "Bảo vệ da toàn diện với SPF 50. Kết cấu dạng gel-cream mỏng nhẹ, không gây nhờn rít, tạo lớp nền hoàn hảo bảo vệ da khỏi tia UV và ô nhiễm.",
      "categoryId": "chanel",
      "categoryTitle": "Chanel",
      "productType": "kem_chong_nang",
      "capacity": "30ml",
      "availableCapacities": ["30ml", "50ml", "100ml"],
      "showRecommend": true,
      "rated": 4.7,
      "keywords": ["kcn", "kem chong nang", "chanel", "uv", "spf 50", "sunscreen", "bao ve da"]
    },
    "mac_lipstick_ruby_woo": {
      "title": "Son MAC Retro Matte - Ruby Woo",
      "price": 650000,
      "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_mac_retroMatte.jpg?alt=media&token=ccd5153f-2e52-47a0-809d-5f5c2da188f1",
      "product_gallery": {
        "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_mac_retroMatte_1.jpg?alt=media&token=18465bcd-595c-4a55-b900-e82bfff1b8dd",
        "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_mac_retroMatte_2.jpg?alt=media&token=4504562b-c9e9-430d-94fb-83b849f09125"
      },
      "description": "Sắc đỏ lạnh (Blue-red) kinh điển phù hợp mọi tông da. Chất son Retro Matte siêu lì, độ bám màu lên đến 8 tiếng, là biểu tượng của MAC.",
      "categoryId": "mac",
      "categoryTitle": "M.A.C",
      "productType": "son",
      "weight": "3g",
      "availableWeights": ["3g", "7g"],
      "availableColors": ["Ruby Woo (Đỏ lạnh)", "Russian Red (Đỏ ấm)", "Chili (Đỏ gạch)"],
      "showRecommend": true,
      "rated": 4.6,
      "keywords": ["son", "mac", "ruby woo", "do lanh", "li", "retro matte", "trang diem"]
    }
  }
}
```

## Implementation Details

### ProductModel Updated

```kotlin
data class ProductModel(
    // ...existing fields...
    
    // Default/display values
    var capacity: String = "",
    var weight: String = "",
    
    // Available options (NEW!)
    var availableCapacities: List<String> = emptyList(),
    var availableWeights: List<String> = emptyList(),
    var availableColors: List<String> = emptyList(),
    
    // Selected options (for cart)
    var selectedCapacity: String = "",
    var selectedWeight: String = "",
    var selectedColor: String = ""
)
```

### Firebase Parsing

The Repository automatically parses:
- `availableCapacities` → List of capacity options
- `availableWeights` → List of weight options  
- `availableColors` → List of color options

### UI Components

1. **ProductOptionsSelector** - Main container
2. **OptionSection** - Section for each option type
3. **OptionChip** - Individual selectable option

## UI Design (Shopee/Lazada Style)

### Option Chip States

**Unselected**:
```
┌─────────┐
│   3g    │  ← Gray border, black text
└─────────┘
```

**Selected**:
```
┌─────────┐
│   3.5g  │  ← Pink border, pink text, light pink background
└─────────┘
```

### Complete Layout

```
Product Detail Screen
─────────────────────────────
[Product Image Gallery]

Son Dior Rouge 999 Velvet
⭐ 4.9    Dior           

Số lượng    [-]  1  [+]

Khối lượng                    ← NEW SECTION
┌─────┐ ┌───────┐ ┌─────┐
│ 3g  │ │ 3.5g  │ │ 7g  │
└─────┘ └───────┘ └─────┘
         ^^^^^^^ selected

Màu sắc                       ← NEW SECTION
┌──────┐ ┌──────┐ ┌──────┐
│ Đỏ   │ │ Hồng │ │ Cam  │
└──────┘ └──────┘ └──────┘
  ^^^^^ selected

Chi tiết
Product description...

[Thêm vào giỏ]               ← Adds with selected options
```

## Features

✅ **Auto-selection** - First option selected by default
✅ **Visual feedback** - Pink highlight for selected option
✅ **Flexible** - Show only relevant options (weight for lipstick, capacity for liquids)
✅ **Cart integration** - Selected options saved when adding to cart
✅ **Vietnamese UI** - "Dung tích", "Khối lượng", "Màu sắc"

## Testing Guide

### Test 1: Lipstick with Multiple Options

1. Go to "Son Dior Rouge 999 Velvet"
2. See "Khối lượng": 3g, 3.5g, 7g
3. See "Màu sắc": Đỏ, Hồng, Cam
4. Click different options → Border changes to pink
5. Add to cart → Selected options saved

### Test 2: Sunscreen with Capacity

1. Go to "Kem Chống Nắng Chanel"
2. See "Dung tích": 30ml, 50ml, 100ml
3. Select "50ml"
4. Add to cart → 50ml option saved

### Test 3: Product Without Options

1. Go to product without `availableX` fields
2. Options section NOT shown
3. Works as before

## Summary

✅ **Product variants added** - Weight, capacity, color options
✅ **Shopee/Lazada style** - Chip selection UI
✅ **Auto-parsing** - Reads from Firebase automatically
✅ **Cart integration** - Selected options saved
✅ **Vietnamese UI** - Full Vietnamese labels
✅ **Backwards compatible** - Works with products without options

**Your app now supports product variants like Shopee and Lazada!** 🎉

