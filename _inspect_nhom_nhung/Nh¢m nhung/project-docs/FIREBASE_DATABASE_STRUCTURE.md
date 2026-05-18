# Firebase Realtime Database - Complete Structure

## 🔥 Database Information

**Database URL:** `https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/`  
**Region:** Asia Southeast 1  
**Database Type:** Firebase Realtime Database  
**Access:** Read/Write (configured in Firebase Console)  

---

## 📋 Complete Database Schema

```json
{
  "banners": {
    "{brand_id}": {
      "url": "string"
    }
  },
  "categories": {
    "{brand_id}": {
      "title": "string",
      "picUrl": "string"
    }
  },
  "attributes": {
    "capacity": {
      "{capacity_value}": true
    },
    "weight": {
      "{weight_value}": true
    },
    "productType": {
      "{type_id}": true
    }
  },
  "items": {
    "{product_id}": {
      "title": "string",
      "price": number,
      "image": "string",
      "product_gallery": {
        "img1": "string",
        "img2": "string"
      },
      "description": "string",
      "categoryId": "string",
      "categoryTitle": "string",
      "productType": "string",
      "capacity": "string",
      "weight": "string",
      "availableCapacities": ["array"],
      "availableWeights": ["array"],
      "availableColors": ["array"],
      "showRecommend": boolean,
      "rated": number,
      "keywords": ["array"]
    }
  }
}
```

---

## 🎨 Banners Collection

Promotional banners for brand-specific campaigns.

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
  }
}
```

### Banner Details

| ID | Brand | Purpose | Image Format |
|---|---|---|---|
| `dior` | Dior | Brand promotion | PNG |
| `chanel` | Chanel | Brand promotion | JPG |
| `mac` | M.A.C | Brand promotion | JPG |
| `rare` | Rare Beauty | Brand promotion | JPG |

---

## 🏷️ Categories Collection

Brand/category information for navigation and filtering.

```json
{
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
  }
}
```

### Category Details

| ID | Title | Description | Logo |
|---|---|---|---|
| `dior` | Dior | French luxury fashion house | PNG logo |
| `chanel` | Chanel | French luxury fashion house | PNG logo |
| `mac` | M.A.C | Professional makeup brand | PNG logo |
| `rare` | Rare Beauty | Selena Gomez's beauty brand | PNG logo |

---

## 🏪 Attributes Collection

Master data for product variant options.

```json
{
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

### Attribute Definitions

#### Capacity (Liquid Products)
- `30ml` - 30 milliliters
- `50ml` - 50 milliliters
- `100ml` - 100 milliliters
- `150ml` - 150 milliliters

#### Weight (Solid Products)
- `3g` - 3 grams
- `3_5g` - 3.5 grams (underscore for decimal)
- `7g` - 7 grams

#### Product Types
- `son` - Lipstick/Lip products (Son môi)
- `sua_rua_mat` - Facial cleanser (Sữa rửa mặt)
- `kem_chong_nang` - Sunscreen (Kem chống nắng)

---

## 🛍️ Items Collection (Products)

### Complete Product List

#### 1. **DIOR Products** (5 items)

##### dior_lipstick_999
```json
{
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
```

##### dior_lip_glow_001
```json
{
  "title": "Son Dưỡng Dior Addict Lip Glow",
  "price": 980000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_dior_addict.jpg?alt=media&token=7c020761-a91f-4865-8847-1228c0d8ab94",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_dior_addict_1.jpg?alt=media&token=6f716afb-7ab5-40de-83b2-055be7384b00"
  },
  "description": "Thỏi son dưỡng mang tính biểu tượng của Dior với công nghệ Color Reviver giúp phản ứng với độ pH của môi để lên màu hồng tự nhiên. Chứa dầu anh đào giúp dưỡng ẩm suốt 24 giờ, mang lại đôi môi căng mọng và mềm mịn.",
  "categoryId": "dior",
  "categoryTitle": "Dior",
  "productType": "son",
  "weight": "3.2g",
  "availableWeights": ["3.2g", "3.5g", "7g"],
  "availableColors": ["001 Pink", "004 Coral", "012 Rosewood"],
  "showRecommend": true,
  "rated": 4.8,
  "keywords": ["son", "duong", "dior", "addict", "lip glow", "hong", "balm", "mem moi"]
}
```

##### dior_prestige_cleanser
```json
{
  "title": "Sữa Rửa Mặt Dior Prestige La Mousse Micellaire",
  "price": 2300000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_dior_LaPrestige.webp?alt=media&token=bdceaaa7-ad68-4c54-bc2b-5f08b684369b",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_dior_LaPrestige_1.webp?alt=media&token=0b6cdbe5-a350-4983-8f7c-a387deb951f3"
  },
  "description": "Sữa rửa mặt tạo bọt cao cấp chiết xuất từ vi chất hoa hồng Granville. Giúp làm sạch sâu, loại bỏ tạp chất và bã nhờn mà không gây khô da. Mang lại làn da tươi mới, rạng rỡ và mềm mại như cánh hoa.",
  "categoryId": "dior",
  "categoryTitle": "Dior",
  "productType": "sua_rua_mat",
  "capacity": "120g",
  "availableCapacities": ["50ml", "100ml", "150ml"],
  "showRecommend": true,
  "rated": 4.9,
  "keywords": ["sua rua mat", "dior", "prestige", "la mousse", "micellaire", "hoa hong", "cao cap", "sach sau"]
}
```

##### dior_solar_sunscreen
```json
{
  "title": "Kem Chống Nắng Dior Solar The Protective Creme SPF 50",
  "price": 1350000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_dior_sola.webp?alt=media&token=16dab7e1-bd26-4bc8-b690-05af3613be05",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_dior_sola.webp?alt=media&token=16dab7e1-bd26-4bc8-b690-05af3613be05"
  },
  "description": "Kem chống nắng bảo vệ da toàn diện trước tia UVA/UVB với chỉ số SPF 50. Kết cấu kem mỏng nhẹ, thấm nhanh, không gây nhờn rít hay vệt trắng. Bổ sung dưỡng chất giúp da ẩm mượt và căng bóng dưới ánh nắng.",
  "categoryId": "dior",
  "categoryTitle": "Dior",
  "productType": "kem_chong_nang",
  "capacity": "50ml",
  "availableCapacities": ["50ml", "100ml", "150ml"],
  "showRecommend": true,
  "rated": 4.7,
  "keywords": ["kcn", "kem chong nang", "dior", "solar", "spf 50", "bao ve da", "chong nang"]
}
```

##### dior_cleanser_off_on
```json
{
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
}
```

---

#### 2. **CHANEL Products** (5 items)

##### chanel_lipstick_velvet
```json
{
  "title": "Son Chanel Rouge Allure Velvet",
  "price": 1230000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_chanel_176.jpg?alt=media&token=63b7f88e-7744-4c94-83b9-e93cf69ab792",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_chanel_176_1.jpg?alt=media&token=c175f9b9-edf6-44ce-ad2d-2843ba2b2665"
  },
  "description": "Thỏi son lì dạng nhung mềm mại, mang lại màu sắc rạng rỡ và sâu sắc. Kết cấu son lướt nhẹ trên môi như làn da thứ hai, chứa dầu Jojoba giúp dưỡng ẩm, không gây khô môi dù lì hoàn hảo.",
  "categoryId": "chanel",
  "categoryTitle": "Chanel",
  "productType": "son",
  "weight": "3.5g",
  "availableWeights": ["3.5g", "3g", "7g"],
  "availableColors": ["58 Rouge Vie", "43 La Favorite", "69 Abstrait"],
  "showRecommend": true,
  "rated": 4.8,
  "keywords": ["son", "chanel", "rouge allure", "velvet", "li", "do", "makeup", "cao cap"]
}
```

##### chanel_cleanser_gel
```json
{
  "title": "Gel Rửa Mặt Chanel Le Gel",
  "price": 1450000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_chanel_LeTonique.webp?alt=media&token=410c226d-2df7-4ac0-afdf-1f6223b7994a",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_chanel_LeTonique_1.webp?alt=media&token=fba2ce92-37f6-4a8e-abf2-5d738471a487"
  },
  "description": "Sữa rửa mặt dạng gel tạo bọt giúp làm sạch sâu và chống ô nhiễm. Khi tiếp xúc với nước, kết cấu gel biến thành bọt mịn màng, loại bỏ bụi bẩn, vi hạt ô nhiễm và bã nhờn, trả lại làn da tươi mát.",
  "categoryId": "chanel",
  "categoryTitle": "Chanel",
  "productType": "sua_rua_mat",
  "capacity": "150ml",
  "availableCapacities": ["150ml", "30ml", "50ml"],
  "showRecommend": true,
  "rated": 4.7,
  "keywords": ["sua rua mat", "chanel", "le gel", "cleanser", "gel", "chong o nhiem", "sach sau"]
}
```

##### chanel_cc_cream
```json
{
  "title": "Kem Nền Chống Nắng Chanel CC Cream SPF 50",
  "price": 1690000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/suncreen_chanel_CC.webp?alt=media&token=363fa35c-fbf7-446d-a14d-cfe9616a68cb",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/suncreen_chanel_CC_1.webp?alt=media&token=3575e067-e36b-46b0-8339-90d80429550d"
  },
  "description": "Siêu phẩm 5 trong 1: Làm đều màu, dưỡng ẩm, sửa chữa khuyết điểm, bảo vệ da với chỉ số SPF 50 và làm sáng da. Kết cấu lỏng nhẹ, thẩm thấu nhanh, mang lại lớp nền tự nhiên như không trang điểm.",
  "categoryId": "chanel",
  "categoryTitle": "Chanel",
  "productType": "kem_chong_nang",
  "capacity": "30ml",
  "availableCapacities": ["30ml", "50ml", "100ml"],
  "showRecommend": true,
  "rated": 4.9,
  "keywords": ["kcn", "kem chong nang", "chanel", "cc cream", "spf 50", "kem nen", "trang diem", "bao ve da"]
}
```

##### chanel_sunscreen_uv
```json
{
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
}
```

##### chanel_cleanser_mousse
```json
{
  "title": "Sữa Rửa Mặt Chanel La Mousse",
  "price": 1350000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/cleanser_chanel_La.webp?alt=media&token=9cf2882b-3d9f-491a-a0c5-348ce20b281c",
  "product_gallery": {},
  "description": "Làm sạch sâu và thanh lọc bề mặt da. Công thức kem đặc chuyển hóa thành bọt mịn màng, mang lại cảm giác thư giãn tuyệt đối.",
  "categoryId": "chanel",
  "categoryTitle": "Chanel",
  "productType": "sua_rua_mat",
  "capacity": "150ml",
  "availableCapacities": ["100ml", "150ml", "200ml"],
  "showRecommend": false,
  "rated": 4.8,
  "keywords": ["sua rua mat", "chanel", "la mousse", "cleanser", "bot", "sach sau"]
}
```

---

#### 3. **M.A.C Products** (2 items)

##### mac_lipstick_ruby_woo
```json
{
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
  "availableWeights": ["3g", "3.5g", "7g"],
  "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
  "showRecommend": true,
  "rated": 4.6,
  "keywords": ["son", "mac", "ruby woo", "do lanh", "li", "retro matte", "trang diem"]
}
```

##### mac_prep_prime
```json
{
  "title": "Kem Lót/Chống Nắng MAC Prep+Prime",
  "price": 1050000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_mac_pepPrime.webp?alt=media&token=c80d546a-758b-4d9c-b74c-316c6af82a24",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_mac_pepPrime_1.webp?alt=media&token=f5bc72f5-83e0-4751-9402-203e4b00cba9",
    "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/sunscreen_mac_pepPrime_2.webp?alt=media&token=97f82cf6-fbe9-491c-8edd-5764a844a550"
  },
  "description": "Sản phẩm đa năng vừa làm kem lót trang điểm, vừa chống nắng bảo vệ da với SPF 50. Giúp lớp nền bền màu và mịn màng hơn.",
  "categoryId": "mac",
  "categoryTitle": "M.A.C",
  "productType": "kem_chong_nang",
  "capacity": "30ml",
  "availableCapacities": ["30ml", "50ml", "100ml"],
  "showRecommend": true,
  "rated": 4.5,
  "keywords": ["kcn", "kem chong nang", "mac", "prep prime", "kem lot", "spf 50"]
}
```

---

#### 4. **Rare Beauty Products** (3 items)

##### rare_blush_joy
```json
{
  "title": "Má Hồng Rare Beauty Soft Pinch - Joy",
  "price": 750000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_softPinch.webp?alt=media&token=c20b41db-fcd6-4944-8891-064de51729d7",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_softPinch_1.webp?alt=media&token=1869ae43-59c6-47d0-9da9-224e9bf801ba",
    "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_softPinch_2.webp?alt=media&token=ddcdae60-5910-401c-8d97-1706191db303"
  },
  "description": "Má hồng dạng lỏng đình đám của Selena Gomez. Màu Joy (Cam đào) rạng rỡ với kết cấu Dewy căng bóng, độ bám màu cực tốt cả ngày dài.",
  "categoryId": "rare",
  "categoryTitle": "Rare Beauty",
  "productType": "son",
  "capacity": "7.5ml",
  "availableWeights": ["3g", "3.5g", "7g"],
  "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
  "showRecommend": true,
  "rated": 5.0,
  "keywords": ["ma hong", "rare beauty", "selena", "soft pinch", "joy", "cam dao", "blush"]
}
```

##### rare_tinted_moisturizer
```json
{
  "title": "Rare Beauty Positive Light Tinted (KCN)",
  "price": 890000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_positive.webp?alt=media&token=b817be40-19be-4bd7-93bc-6630939ff30e",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_positive_1.webp?alt=media&token=938a40e6-f4fc-4594-a24a-b07727aa5f31",
    "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_positive_2.webp?alt=media&token=18c64125-2317-4b55-a502-87ceb40b2cad"
  },
  "description": "Kem nền mỏng nhẹ tích hợp chống nắng SPF 20. Làm đều màu da, che phủ khuyết điểm nhẹ nhàng và cấp ẩm cho da căng mướt.",
  "categoryId": "rare",
  "categoryTitle": "Rare Beauty",
  "productType": "kem_chong_nang",
  "capacity": "30ml",
  "availableCapacities": ["30ml", "50ml", "100ml"],
  "showRecommend": false,
  "rated": 4.5,
  "keywords": ["kcn", "kem chong nang", "rare beauty", "tinted", "nen", "duong am", "spf"]
}
```

##### rare_lip_oil_wonder
```json
{
  "title": "Son Dầu Rare Beauty Lip Oil - Wonder",
  "price": 620000,
  "image": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_wonder.webp?alt=media&token=f90e05db-1040-407e-b210-fe3010f7305a",
  "product_gallery": {
    "img1": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_wonder_1.webp?alt=media&token=cb240750-ce4f-480d-858e-d3e5c4c24fe7",
    "img2": "https://firebasestorage.googleapis.com/v0/b/nhung-group.firebasestorage.app/o/lipstick_rare_wonder_2.webp?alt=media&token=ae9619ad-a7e3-4444-8e64-4e73d1bbf72e"
  },
  "description": "Son tint dạng dầu độc đáo, tạo hiệu ứng môi căng mọng (glossy) ban đầu và để lại lớp tint màu bền bỉ sau khi ăn uống. Màu Wonder: Hồng đất nữ tính.",
  "categoryId": "rare",
  "categoryTitle": "Rare Beauty",
  "productType": "son",
  "capacity": "3ml",
  "availableWeights": ["3g", "3.5g", "7g"],
  "availableColors": ["Đỏ 999", "Hồng 100", "Cam 200"],
  "showRecommend": true,
  "rated": 4.9,
  "keywords": ["son", "rare beauty", "lip oil", "wonder", "hong dat", "tint", "bong"]
}
```

---

## 📊 Database Statistics

### Product Distribution
| Brand | Product Count | Percentage |
|---|---|---|
| Dior | 5 | 33.3% |
| Chanel | 5 | 33.3% |
| M.A.C | 2 | 13.3% |
| Rare Beauty | 3 | 20.0% |
| **TOTAL** | **15** | **100%** |

### Product Type Distribution
| Type | Vietnamese | Count |
|---|---|---|
| `son` | Son môi | 8 products |
| `sua_rua_mat` | Sữa rửa mặt | 4 products |
| `kem_chong_nang` | Kem chống nắng | 6 products |

### Price Range Analysis
| Price Range (VND) | Count | Products |
|---|---|---|
| 500k - 1M | 5 | Ruby Woo, Lip Oil, Blush, Addict Lip Glow, Tinted |
| 1M - 1.5M | 6 | Rouge 999, Dior Solar, MAC Prep, La Mousse, Rouge Allure, Le Gel |
| 1.5M - 2M | 3 | UV Essentiel, CC Cream, OFF/ON |
| 2M+ | 1 | Prestige La Mousse (2.3M) |

**Average Price:** 1,223,333 VND  
**Lowest Price:** 620,000 VND (Lip Oil)  
**Highest Price:** 2,300,000 VND (Prestige Cleanser)

### Recommended Products
**Count:** 13 out of 15 (86.7%)  
**Not Recommended:**
- Chanel La Mousse
- Rare Beauty Tinted Moisturizer

### Rating Statistics
- **5 stars:** 1 product (Rare Beauty Soft Pinch)
- **4.9 stars:** 4 products
- **4.8 stars:** 3 products
- **4.7 stars:** 3 products
- **4.6 stars:** 1 product
- **4.5 stars:** 2 products

**Average Rating:** 4.75 / 5.0

---

## 🔍 Search Keywords Analysis

### Most Common Keywords
1. **son** - 7 occurrences
2. **kem chong nang** / **kcn** - 6 occurrences
3. **dior** - 5 occurrences
4. **chanel** - 5 occurrences
5. **spf 50** - 5 occurrences
6. **sua rua mat** - 4 occurrences

### Keyword Categories

#### Brand Names
- `dior`, `chanel`, `mac`, `rare beauty`, `selena`

#### Product Types (Vietnamese)
- `son`, `son duong`, `ma hong`, `sua rua mat`, `kem chong nang`, `kcn`, `kem nen`, `kem lot`

#### Product Features (Vietnamese)
- `li` (matte), `velvet`, `bong` (glossy), `duong am` (moisturizing)
- `bao ve da` (skin protection), `chong o nhiem` (anti-pollution)
- `sach sau` (deep clean), `cao cap` (premium)

#### Colors (Vietnamese)
- `do` (red), `hong` (pink), `cam` (orange), `do lanh` (cool red)

#### English Terms
- `lipstick`, `makeup`, `cleanser`, `sunscreen`, `spf`, `balm`, `blush`, `tint`

---

## 🖼️ Firebase Storage Structure

All images are stored in Firebase Storage under project: `nhung-group.firebasestorage.app`

### Image Naming Convention

#### Banners
- `banner_{brand}.{ext}` (e.g., `banner_dior.png`)

#### Logos
- `logo_{brand}.png` (e.g., `logo_chanel.png`)

#### Product Images
- Main: `{type}_{brand}_{product}.{ext}` (e.g., `son_dior_999.webp`)
- Gallery: `{type}_{brand}_{product}_{number}.{ext}` (e.g., `son_dior_999_1.webp`)

### Image Formats
- **WebP:** Preferred for product images (better compression)
- **JPG:** Used for some banners
- **PNG:** Used for logos and some banners with transparency

---

## 🔐 Firebase Security Rules

**Recommended Rules for Production:**

```json
{
  "rules": {
    "banners": {
      ".read": true,
      ".write": "auth != null && auth.token.admin == true"
    },
    "categories": {
      ".read": true,
      ".write": "auth != null && auth.token.admin == true"
    },
    "attributes": {
      ".read": true,
      ".write": "auth != null && auth.token.admin == true"
    },
    "items": {
      ".read": true,
      ".write": "auth != null && auth.token.admin == true"
    }
  }
}
```

**Current Rules (Development):**
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

---

## 📱 Integration with Android App

### Repository Queries

#### Load Banners
```kotlin
firebaseDatabase.getReference("banners")
    .addValueEventListener { snapshot ->
        // Parse BannerModel objects
    }
```

#### Load Categories
```kotlin
firebaseDatabase.getReference("categories")
    .addValueEventListener { snapshot ->
        // Parse CategoryModel objects
    }
```

#### Load Recommended Products
```kotlin
firebaseDatabase.getReference("items")
    .orderByChild("showRecommend")
    .equalTo(true)
    .addListenerForSingleValueEvent { snapshot ->
        // Parse ProductModel objects
    }
```

#### Load Products by Brand
```kotlin
firebaseDatabase.getReference("items")
    .orderByChild("categoryId")
    .equalTo("dior")
    .addListenerForSingleValueEvent { snapshot ->
        // Parse ProductModel objects
    }
```

#### Load All Products (for search)
```kotlin
firebaseDatabase.getReference("items")
    .addListenerForSingleValueEvent { snapshot ->
        // Parse ProductModel objects
    }
```

---

## 🚀 Future Database Enhancements

### Suggested Additional Collections

#### 1. **Users Collection**
```json
{
  "users": {
    "{userId}": {
      "name": "string",
      "email": "string",
      "phone": "string",
      "addresses": [...],
      "favorites": [...],
      "createdAt": "timestamp"
    }
  }
}
```

#### 2. **Orders Collection**
```json
{
  "orders": {
    "{orderId}": {
      "userId": "string",
      "items": [...],
      "totalPrice": number,
      "status": "string",
      "shippingAddress": {...},
      "createdAt": "timestamp"
    }
  }
}
```

#### 3. **Reviews Collection**
```json
{
  "reviews": {
    "{productId}": {
      "{reviewId}": {
        "userId": "string",
        "rating": number,
        "comment": "string",
        "images": [...],
        "createdAt": "timestamp"
      }
    }
  }
}
```

#### 4. **Inventory Collection**
```json
{
  "inventory": {
    "{productId}": {
      "stock": number,
      "reserved": number,
      "lastUpdated": "timestamp"
    }
  }
}
```

---

**Database Version:** 1.0  
**Last Updated:** December 2024  
**Total Items:** 15 products, 4 brands, 4 banners

