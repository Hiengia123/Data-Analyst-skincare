# 📊 Data Analytics & AI Readiness Audit
## Nhóm Nhung — Cosmetics E-Commerce App

**Audit Date**: May 15, 2026  
**Auditor Role**: Senior Product Analyst · Senior Ecommerce Data Analyst · Senior Product Manager  
**App Type**: Mobile Ecommerce — Cosmetics / Skincare  
**Tech Stack**: Android (Jetpack Compose) · Firebase Realtime Database · Firebase Auth  
**Audit Goal**: Evolve this app into a realistic ecommerce data platform for DA / BI / AI/ML portfolio use

---

## 📋 TABLE OF CONTENTS

1. [Current State Evaluation](#1-current-state-evaluation)
2. [What Is Missing — Feature Gap Analysis](#2-what-is-missing--feature-gap-analysis)
3. [Event Tracking Architecture](#3-event-tracking-architecture)
4. [Database Schema Upgrades](#4-database-schema-upgrades)
5. [User Behaviors to Store & Analyze](#5-user-behaviors-to-store--analyze)
6. [High-Value Data Points Ignored by Student Projects](#6-high-value-data-points-ignored-by-student-projects)
7. [KPIs & Metrics This App Should Calculate](#7-kpis--metrics-this-app-should-calculate)
8. [Recommended Analytics Pipeline](#8-recommended-analytics-pipeline)
9. [AI-Ready Data Structure](#9-ai-ready-data-structure)
10. [Recommended Data Warehouse Star Schema](#10-recommended-data-warehouse-star-schema)
11. [Future ML Models](#11-future-ml-models)
12. [Realistic Cosmetics Ecommerce Scenarios](#12-realistic-cosmetics-ecommerce-scenarios)
13. [Upgrade Roadmap](#13-upgrade-roadmap)
14. [Prioritized Recommendations](#14-prioritized-recommendations)
15. [Final Scorecard](#15-final-scorecard)

---

## 1. CURRENT STATE EVALUATION

### 1.1 What the App Currently Has

| Feature | Status | Analytics Value |
|---|---|---|
| User Registration / Login | ✅ Done | Medium |
| Product Listing by Category | ✅ Done | Low |
| Product Detail Page | ✅ Done | Medium |
| Shopping Cart (local + Firebase) | ✅ Done | High |
| Checkout / Order Placement | ✅ Done | High |
| Order History / Tracking | ✅ Done | High |
| Favorites / Wishlist | ✅ Done | High |
| Search | ✅ Done | Very High |
| Firebase Realtime Database | ✅ Done | Medium |
| Product Variants (color, weight) | ✅ Done | Medium |
| Banner Carousel | ✅ Done | Medium |
| Brand Filtering | ✅ Done | Low |

---

### 1.2 Honest Assessment

#### Business Realism Score: **5.5 / 10**

> The app has the basic shopping flow. However, it lacks the behavior signals, feedback loops, and operational complexity that real ecommerce platforms run on. There is no pricing engine, no promotion system, no inventory awareness, no review system, and no behavioral telemetry.

#### Analytics Maturity Score: **2 / 10**

> Currently the app stores transactions (orders, carts, favorites) but does NOT track any behavioral events. This means you cannot build funnels, cannot measure conversion, cannot calculate time-on-page, cannot detect drop-off points, and cannot feed any AI system. The database is a snapshot store, not an analytics platform.

#### AI Readiness Score: **1.5 / 10**

> There is no event log, no user session data, no implicit feedback (views, dwell time, scrolls), no explicit feedback (ratings, reviews), no A/B test infrastructure, and no feature vectors for any ML model. The data in its current form cannot train a recommendation system, a churn predictor, or a sentiment classifier.

---

## 2. WHAT IS MISSING — FEATURE GAP ANALYSIS

### 2.1 MUST HAVE (Critical for Dataset Value)

#### A. Behavioral Event Tracking System
The single most important thing missing. Every meaningful DA/AI project starts with event logs. Without events, there is no funnel, no cohort, no recommendation system, no churn model.

**Events needed** (see Section 3 for full schema):
- `product_viewed`
- `search_performed`
- `add_to_cart`
- `remove_from_cart`
- `checkout_started`
- `order_placed`
- `page_viewed`
- `session_started` / `session_ended`

#### B. Product Review & Rating System
Reviews are the #1 data source for:
- NLP sentiment analysis
- Product quality signals
- Recommendation system input
- Customer voice / VoC analysis

Currently `rated` is a static hardcoded number in the database. This is fake data. Real ratings come from real user submissions with timestamps, user IDs, and text content.

**Schema needed:**
```
reviews/
  {reviewId}/
    ├── userId
    ├── productId
    ├── rating: 1-5
    ├── title: string
    ├── body: string
    ├── sentiment: null (filled by AI later)
    ├── verifiedPurchase: boolean
    ├── helpfulVotes: number
    ├── createdAt: timestamp
    └── images: []
```

#### C. Session Tracking
Without sessions, you cannot calculate:
- Session duration
- Pages per session
- Bounce rate
- Engagement rate
- Return visit frequency

**Schema needed:**
```
sessions/
  {sessionId}/
    ├── userId (null if guest)
    ├── deviceId
    ├── platform: "android"
    ├── startedAt
    ├── endedAt
    ├── duration_seconds
    ├── pageCount
    ├── source: "direct" | "notification" | "link"
    └── events: [{eventId, timestamp}]
```

#### D. Product View / Dwell Time Log
The gap between "user saw product" and "user bought product" is where recommendation systems live. You need to know:
- Which products each user viewed
- How long they spent on each product page
- How many times they viewed the same product
- Whether they viewed competitors before buying

---

### 2.2 SHOULD HAVE (High Value, Realistic Effort)

#### E. Promotions / Discount System
Real ecommerce runs on promotions. More importantly, promotions create natural experiments for A/B testing and price elasticity analysis.

```
promotions/
  {promoId}/
    ├── code: "SALE20"
    ├── discountType: "percent" | "fixed"
    ├── discountValue: 20
    ├── applicableTo: "all" | [productIds] | [categoryIds]
    ├── minOrderValue: 500000
    ├── usageLimit: 100
    ├── usedCount: 45
    ├── startDate
    └── endDate

orders/
  {orderId}/
    ├── promoCode: "SALE20"
    ├── discountAmount: 230000
    └── ...
```

**Analytics value**: Price sensitivity, promotion ROI, coupon attribution.

#### F. Inventory / Stock System
Stock signals drive urgency behavior ("Only 3 left!") and generate scarcity analytics.

```
inventory/
  {productId}/
    ├── stock: 45
    ├── lowStockThreshold: 10
    ├── reservedCount: 3
    └── lastUpdated
```

**Analytics value**: Demand forecasting, stockout detection, supply chain signals.

#### G. User Profile Enrichment
Current user model has only: email, name, phone, provider, createdAt.

Add:
```
users/
  {uid}/
    ├── ... existing fields
    ├── skinType: "dry" | "oily" | "combination" | "sensitive"
    ├── skinTone: "fair" | "medium" | "tan" | "deep"
    ├── ageRange: "18-24" | "25-34" | "35-44" | "45+"
    ├── concerns: ["acne", "wrinkles", "brightening"]
    ├── preferredBrands: ["dior", "chanel"]
    ├── city: string
    ├── loyaltyPoints: number
    ├── segment: null (calculated by ML later)
    └── lifetimeValue: number (calculated)
```

**Analytics value**: Segmentation, personalization, cohort analysis, targeted recommendations.

#### H. Notification System with Open Tracking
Push notifications + open tracking generates:
- Re-engagement funnel
- Notification effectiveness data
- Churn prevention signals

#### I. A/B Test Infrastructure
Even simple flag-based experiments generate priceless dataset value.

```
experiments/
  {experimentId}/
    ├── name: "homepage_banner_layout"
    ├── variants: ["control", "treatment_a"]
    ├── allocation: {control: 50, treatment_a: 50}
    ├── startDate
    └── endDate

userExperiments/
  {uid}/
    {experimentId}: "control" | "treatment_a"
```

---

### 2.3 NICE TO HAVE (Advanced, High Impact on CV)

#### J. Product Recommendation Engine (Logged)
Store recommendation impressions and clicks:
```
recommendation_logs/
  {logId}/
    ├── userId
    ├── algorithm: "collab_filter" | "content_based" | "trending"
    ├── shownProducts: [productId1, productId2, ...]
    ├── clickedProduct: productId | null
    ├── conversionOccurred: boolean
    └── timestamp
```

#### K. Price History
```
price_history/
  {productId}/
    {timestamp}/
      └── price: number
```
**Analytics value**: Price elasticity modeling, trend detection.

#### L. Cross-sell / Bundle Tracking
Which products are frequently bought together? This requires order item co-occurrence analysis.

#### M. Customer Support / Returns (Simulated)
```
returns/
  {returnId}/
    ├── orderId
    ├── userId
    ├── reason: "wrong_product" | "damaged" | "not_as_described" | "changed_mind"
    ├── resolution: "refund" | "exchange"
    └── createdAt
```

---

## 3. EVENT TRACKING ARCHITECTURE

### 3.1 Master Event Schema

Every event must follow this envelope:

```json
{
  "eventId": "uuid-v4",
  "eventName": "product_viewed",
  "userId": "uid123 or null",
  "sessionId": "session_abc",
  "deviceId": "device_xyz",
  "timestamp": 1716768000000,
  "platform": "android",
  "appVersion": "1.0.0",
  "properties": {
    "...event-specific properties..."
  }
}
```

---

### 3.2 Core Events by Analytics Goal

#### Customer Behavior Analysis

| Event | Properties | Why It Matters |
|---|---|---|
| `app_opened` | source, referrer | DAU/MAU, acquisition |
| `session_started` | sessionId | Session analysis |
| `session_ended` | duration, pageCount | Engagement depth |
| `screen_viewed` | screenName, previousScreen | Navigation patterns |
| `product_viewed` | productId, categoryId, source, durationMs | Product interest signal |
| `product_gallery_swiped` | productId, imageIndex | Visual engagement |
| `product_description_expanded` | productId | Purchase intent signal |
| `search_performed` | query, resultCount, filters | Search demand |
| `search_result_clicked` | query, productId, rank | Search relevance |
| `search_no_result` | query | Catalog gap detection |
| `category_selected` | categoryId | Navigation preference |
| `banner_clicked` | bannerId, position | Banner effectiveness |

#### Funnel Analysis

| Event | Properties | Funnel Stage |
|---|---|---|
| `product_viewed` | productId | Awareness |
| `add_to_cart` | productId, price, quantity, selectedColor | Interest |
| `cart_viewed` | itemCount, totalValue | Intent |
| `checkout_started` | itemCount, totalValue | Intent+ |
| `shipping_address_entered` | city, district | Commitment |
| `payment_method_selected` | method | Near-conversion |
| `order_placed` | orderId, totalValue, itemCount, promoCode | Conversion |
| `order_viewed` | orderId | Post-purchase |
| `checkout_abandoned` | step, itemCount, totalValue | Drop-off |
| `cart_abandoned` | itemCount, totalValue, durationInCart | Drop-off |

#### Recommendation System

| Event | Properties | Signal Type |
|---|---|---|
| `product_viewed` | productId, durationMs | Implicit positive |
| `product_added_to_wishlist` | productId | Strong positive |
| `product_added_to_cart` | productId | Strong positive |
| `order_placed` (item) | productId | Strongest positive |
| `product_removed_from_cart` | productId | Negative |
| `product_removed_from_wishlist` | productId | Negative |
| `search_performed` | query | Interest signal |
| `review_submitted` | productId, rating | Explicit feedback |

#### Retention Analysis

| Event | Why |
|---|---|
| `session_started` | Measures return visits |
| `app_opened` with `userId` | Identifies returning users |
| `notification_received` | Delivery confirmation |
| `notification_opened` | Re-engagement |
| `order_placed` | Repeat purchase detection |
| `review_submitted` | Highly engaged user signal |

---

### 3.3 Firebase Event Store Schema

```
events/
  {eventId}/
    ├── eventName: string
    ├── userId: string | null
    ├── sessionId: string
    ├── deviceId: string
    ├── timestamp: number
    ├── platform: "android"
    ├── appVersion: string
    └── properties: { object }
```

> **Implementation Note**: For high-volume events, batch-write to Firebase every 30 seconds or on session end to reduce write costs and improve performance.

---

## 4. DATABASE SCHEMA UPGRADES

### 4.1 Enhanced Items Schema

Add these fields to every product:

```json
{
  "viewCount": 0,
  "cartAddCount": 0,
  "orderCount": 0,
  "wishlistCount": 0,
  "reviewCount": 0,
  "averageRating": 0.0,
  "conversionRate": 0.0,
  "tags": ["moisturizing", "anti-aging", "spf"],
  "ingredients": ["hyaluronic acid", "vitamin C"],
  "skinTypes": ["dry", "combination"],
  "concerns": ["brightening", "hydration"],
  "ageTarget": ["25-34", "35-44"],
  "stockCount": 100,
  "isOnSale": false,
  "salePrice": null,
  "originalPrice": 1150000,
  "popularity_score": 0.0,
  "trending_score": 0.0
}
```

### 4.2 Full Database Node Map

```
Firebase Realtime Database
│
├── banners/                    ← existing
├── categories/                 ← existing
├── attributes/                 ← existing
├── items/                      ← existing + enhanced
│
├── users/                      ← existing + enriched profile
│   └── {uid}/
│       ├── ... existing
│       ├── skinType
│       ├── skinTone
│       ├── ageRange
│       ├── concerns[]
│       ├── loyaltyPoints
│       └── segment
│
├── carts/                      ← existing
├── orders/                     ← existing + promoCode, discountAmount
├── favorites/                  ← existing
│
├── reviews/                    ← NEW
│   └── {reviewId}/
│       ├── userId
│       ├── productId
│       ├── rating: 1-5
│       ├── title
│       ├── body
│       ├── verifiedPurchase
│       ├── sentiment: null
│       └── createdAt
│
├── sessions/                   ← NEW
│   └── {sessionId}/
│       ├── userId
│       ├── deviceId
│       ├── startedAt
│       ├── endedAt
│       ├── duration_seconds
│       └── pageCount
│
├── events/                     ← NEW (behavioral log)
│   └── {eventId}/
│       ├── eventName
│       ├── userId
│       ├── sessionId
│       ├── timestamp
│       └── properties{}
│
├── promotions/                 ← NEW
│   └── {promoId}/
│       ├── code
│       ├── discountType
│       ├── discountValue
│       ├── usageLimit
│       ├── usedCount
│       ├── startDate
│       └── endDate
│
├── inventory/                  ← NEW
│   └── {productId}/
│       ├── stock
│       ├── lowStockThreshold
│       └── lastUpdated
│
├── product_views/              ← NEW (aggregated)
│   └── {productId}/
│       └── {userId}: {count, lastViewedAt, totalDurationMs}
│
├── recommendation_logs/        ← NEW
│   └── {logId}/
│       ├── userId
│       ├── algorithm
│       ├── shownProducts[]
│       ├── clickedProduct
│       └── timestamp
│
├── price_history/              ← NEW
│   └── {productId}/
│       └── {timestamp}: price
│
└── experiments/                ← NEW
    └── {experimentId}/
        ├── name
        ├── variants[]
        └── userAssignments/{uid}: variant
```

---

## 5. USER BEHAVIORS TO STORE & ANALYZE

### 5.1 Implicit Signals (Behavioral)

| Behavior | What It Tells You |
|---|---|
| Product page dwell time > 30s | High purchase intent |
| Product viewed 3+ times in one session | Strong consideration |
| Product in cart > 24 hours | Price sensitivity or hesitation |
| Search → no click → re-search | Catalog gap or poor search quality |
| Add to cart → remove | Price objection or changed mind |
| Checkout started → abandoned | Friction in checkout flow |
| App opened within 1 hour of notification | High re-engagement responsiveness |
| Same product added to multiple variant carts | Size/shade confusion |

### 5.2 Explicit Signals (User Actions)

| Signal | Source | Value |
|---|---|---|
| Star rating submitted | Review screen | Explicit product satisfaction |
| Review text submitted | Review screen | NLP sentiment source |
| Wishlist add | Heart icon | Purchase intent without commitment |
| Repeat order (same product) | Order history | Brand loyalty signal |
| Profile skin type filled | Onboarding | Personalization seed |
| Promo code applied | Checkout | Price sensitivity |
| Order cancelled | Order tracking | Dissatisfaction / friction |

### 5.3 Temporal Patterns to Analyze

- **Purchase velocity**: How quickly do users move from first view to purchase?
- **Cart-to-order conversion by time of day**: Do users convert more at night?
- **Day-of-week purchase patterns**: Weekend shoppers vs weekday?
- **Session frequency vs. purchase frequency**: Browsers vs buyers
- **Seasonal patterns**: SPF products spike in summer, lip products spike in winter

---

## 6. HIGH-VALUE DATA POINTS IGNORED BY STUDENT PROJECTS

These are the data points that separate a real analytics platform from a CRUD app:

### 6.1 🔑 Time-to-Purchase (TTB — Time to Buy)
Store the timestamp of the first `product_viewed` event and the `order_placed` event for the same product. The delta is your **Time-to-Buy** metric. This is used in:
- Urgency optimization ("Buy before price increases")
- Email timing optimization
- Retargeting window calculation

### 6.2 🔑 Search-to-Purchase Funnel
Track: what users searched → what they clicked → what they bought. This powers:
- Search quality scoring
- Catalog gap analysis
- Keyword-to-revenue attribution
- SEO/ASO strategy

### 6.3 🔑 Cross-Session Product Journey
A user views a lipstick on Monday, adds it to cart on Wednesday, and buys on Friday. This **multi-session journey** is invisible without session + event linking. It powers:
- Cart abandonment email timing
- Retargeting strategy
- Customer decision cycle modeling

### 6.4 🔑 Variant Selection Patterns
Which color / weight / size combinations are most popular? This is critical for:
- Inventory optimization
- Bundle recommendations
- Product variant rationalization (which variants to keep vs. drop)

### 6.5 🔑 "Almost Purchased" Signals
Users who viewed a product 5+ times, added to cart but never purchased are your highest-value non-customers. They are:
- Highly convertible with a small nudge
- Price-sensitive (offer a 5% discount)
- Perfect for retargeting

### 6.6 🔑 Zero-Result Search Queries
Every search that returns 0 results is a product catalog gap. Store these queries with counts. This is used for:
- Product sourcing decisions
- Category expansion strategy
- Demand sensing for new products

### 6.7 🔑 Category Affinity
Does a user always browse Dior? Or do they cross-browse Chanel + MAC? Affinity patterns power:
- Cross-brand recommendations
- Brand loyalty segmentation
- Promotional targeting

### 6.8 🔑 Device & Time Context
- Time of day
- Day of week
- Session source (cold open vs notification)

These seem trivial but power behavioral segmentation and notification timing optimization.

---

## 7. KPIs & METRICS THIS APP SHOULD CALCULATE

### 7.1 Acquisition & Activation

| KPI | Formula |
|---|---|
| New User Registrations | COUNT(users WHERE createdAt >= period) |
| Guest-to-Registered Conversion | Registered users / Total sessions |
| Onboarding Completion Rate | Users who completed profile / Total registered |

### 7.2 Engagement

| KPI | Formula |
|---|---|
| DAU / MAU | Daily Active Users / Monthly Active Users |
| DAU/MAU Ratio (Stickiness) | DAU / MAU — target: >20% |
| Average Session Duration | AVG(session.duration_seconds) |
| Pages Per Session | AVG(session.pageCount) |
| Search Rate | Sessions with search / Total sessions |
| Feature Adoption Rate | Users using X feature / Total users |

### 7.3 Product Analytics

| KPI | Formula |
|---|---|
| Product View Rate | Views / Impressions |
| Product Click-Through Rate | Product page opens / Product impressions |
| Add-to-Cart Rate | Add-to-cart events / Product views |
| Product Conversion Rate | Orders containing product / Product views |
| Product Return Rate | Returns / Orders (by product) |
| Average Review Rating | AVG(review.rating) per product |
| Wishlist-to-Cart Rate | Cart adds from wishlist / Total wishlist items |

### 7.4 Funnel & Conversion

| KPI | Formula |
|---|---|
| Cart Conversion Rate | Orders / Cart sessions |
| Checkout Abandonment Rate | Abandoned checkouts / Checkout starts |
| Cart Abandonment Rate | Sessions with cart but no order / Sessions with cart |
| Overall Purchase Conversion | Orders / Total sessions |
| Funnel Drop-off by Step | Events at step N / Events at step N-1 |

### 7.5 Revenue & Orders

| KPI | Formula |
|---|---|
| Gross Merchandise Value (GMV) | SUM(order.totalPrice) |
| Average Order Value (AOV) | GMV / Total orders |
| Revenue Per User | GMV / Active users |
| Orders Per User | Total orders / Unique ordering users |
| Promo Redemption Rate | Orders with promo / Total orders |
| Discount Rate | Total discount amount / Total GMV |

### 7.6 Retention & Loyalty

| KPI | Formula |
|---|---|
| D1 / D7 / D30 Retention | Users returning on day N / Users acquired on day 0 |
| Repeat Purchase Rate | Users with 2+ orders / Total ordering users |
| Customer Lifetime Value (CLV) | AVG(total spend per user) |
| Churn Rate | Users lost in period / Users at start |
| Net Promoter Score (NPS) | (Promoters - Detractors) / Total respondents |

### 7.7 Search Quality

| KPI | Formula |
|---|---|
| Search Usage Rate | Sessions with search / Total sessions |
| Zero-Result Rate | Searches with 0 results / Total searches |
| Search Click-Through Rate | Searches with a click / Total searches |
| Search-to-Purchase Rate | Searches leading to purchase / Total searches |

---

## 8. RECOMMENDED ANALYTICS PIPELINE

```
┌─────────────────────────────────────────────────────────┐
│                     MOBILE APP                          │
│  User Actions → Event Tracker → Batch Queue             │
└───────────────────────┬─────────────────────────────────┘
                        │ Write (batched every 30s)
                        ▼
┌─────────────────────────────────────────────────────────┐
│              FIREBASE REALTIME DATABASE                  │
│  events/ · sessions/ · orders/ · reviews/ · users/      │
└───────────────────────┬─────────────────────────────────┘
                        │ Export / ETL
                        ▼
┌─────────────────────────────────────────────────────────┐
│                  DATA WAREHOUSE LAYER                    │
│  Google BigQuery / PostgreSQL / Snowflake                │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│  │ fact_events  │ │ fact_orders  │ │ fact_product_view│ │
│  └──────────────┘ └──────────────┘ └──────────────────┘ │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│  │ dim_users    │ │ dim_products │ │ dim_dates        │ │
│  └──────────────┘ └──────────────┘ └──────────────────┘ │
└───────────────────────┬─────────────────────────────────┘
                        │
          ┌─────────────┼──────────────┐
          ▼             ▼              ▼
┌──────────────┐ ┌───────────┐ ┌────────────────┐
│  BI Dashboard│ │ ML Models │ │ External Data  │
│  (Looker/    │ │ (Collab   │ │ (Shopee/Lazada │
│  Power BI /  │ │ Filter,   │ │  scraping,     │
│  Metabase)   │ │ Churn,    │ │  TikTok/Reddit │
│              │ │ NLP)      │ │  sentiment)    │
└──────────────┘ └───────────┘ └────────────────┘
```

### 8.1 ETL Strategy (Firebase → BigQuery)

**Option A (Simple — Student Project)**
- Export Firebase data manually to CSV/JSON
- Load into BigQuery using `bq load` or Python pandas
- Schedule weekly with Cloud Functions

**Option B (Automated — Advanced)**
- Firebase Extension: "Export Collections to BigQuery"
- Real-time streaming to BigQuery
- Trigger: Firebase → Pub/Sub → Dataflow → BigQuery

**Option C (Lightweight — Start Here)**
- Firebase → Python script (firebase-admin SDK)
- Transform in pandas
- Load to local PostgreSQL or BigQuery free tier

---

## 9. AI-READY DATA STRUCTURE

### 9.1 For Collaborative Filtering (Recommendation)

You need a **User-Item Interaction Matrix**:

```
user_item_interactions/
  {userId}/
    {productId}/
      ├── viewCount: 3
      ├── totalDwellMs: 45000
      ├── addedToCart: true
      ├── purchased: true
      ├── favorited: true
      ├── rating: 5
      └── lastInteraction: timestamp

→ Derived score = weighted sum:
  view(0.1) + dwell(0.2) + cart(0.3) + favorite(0.4) + purchase(1.0) + rating_norm(0.5)
```

### 9.2 For Content-Based Filtering

Each product needs a feature vector:

```json
{
  "productId": "dior_lipstick_999",
  "features": {
    "brand": "dior",
    "productType": "son",
    "priceRange": "high",
    "color": "red",
    "finish": "velvet_matte",
    "skinConcern": [],
    "ingredients_embedding": [0.2, 0.5, ...],
    "description_embedding": [0.1, 0.8, ...]
  }
}
```

The `description_embedding` is a vector generated by a sentence transformer (e.g., `paraphrase-multilingual-MiniLM`) applied to the Vietnamese product description. This enables semantic similarity search.

### 9.3 For NLP (Sentiment Analysis)

Reviews need:
```json
{
  "reviewId": "...",
  "text": "Son này màu đẹp lắm, bền màu cả ngày không trôi",
  "language": "vi",
  "sentiment": null,
  "sentiment_score": null,
  "aspect_sentiments": {
    "color": "positive",
    "durability": "positive",
    "texture": null
  }
}
```

Vietnamese NLP models: `underthesea`, `vinai/phobert-base`, `vncorenlp`

### 9.4 For Churn Prediction

Feature set per user (snapshot at time T):
```json
{
  "userId": "uid123",
  "daysSinceLastOrder": 45,
  "daysSinceLastSession": 12,
  "totalOrders": 3,
  "totalSpend": 3450000,
  "avgOrderValue": 1150000,
  "favoriteCount": 7,
  "reviewCount": 2,
  "searchCount_30d": 15,
  "sessionCount_30d": 8,
  "cartAbandonRate": 0.4,
  "label_churned": null
}
```

### 9.5 For Demand Forecasting

Time series per product:
```json
{
  "productId": "dior_lipstick_999",
  "date": "2026-05-01",
  "unitsOrdered": 12,
  "uniqueBuyers": 10,
  "views": 320,
  "cartAdds": 45,
  "revenue": 13800000
}
```

---

## 10. RECOMMENDED DATA WAREHOUSE STAR SCHEMA

```
                    ┌─────────────┐
                    │  dim_dates  │
                    │  date_key   │
                    │  year       │
                    │  month      │
                    │  day        │
                    │  dayofweek  │
                    │  is_weekend │
                    └──────┬──────┘
                           │
┌──────────────┐   ┌───────┴──────────┐   ┌─────────────────┐
│  dim_users   │   │   fact_orders    │   │  dim_products   │
│  user_key    ├───┤  order_key       ├───┤  product_key    │
│  uid         │   │  user_key (FK)   │   │  product_id     │
│  age_range   │   │  product_key (FK)│   │  title          │
│  skin_type   │   │  date_key (FK)   │   │  brand          │
│  city        │   │  quantity        │   │  category       │
│  segment     │   │  unit_price      │   │  product_type   │
│  clv         │   │  total_price     │   │  price_range    │
└──────────────┘   │  promo_code      │   │  rating         │
                   │  discount_amount │   └─────────────────┘
                   │  status          │
                   │  payment_method  │
                   └──────────────────┘

                    ┌─────────────────────┐
                    │  fact_events        │
                    │  event_key          │
                    │  event_name         │
                    │  user_key (FK)      │
                    │  product_key (FK)   │
                    │  date_key (FK)      │
                    │  session_id         │
                    │  properties (JSON)  │
                    └─────────────────────┘

                    ┌─────────────────────┐
                    │  fact_reviews       │
                    │  review_key         │
                    │  user_key (FK)      │
                    │  product_key (FK)   │
                    │  date_key (FK)      │
                    │  rating             │
                    │  review_text        │
                    │  sentiment_score    │
                    │  verified_purchase  │
                    └─────────────────────┘
```

---

## 11. FUTURE ML MODELS

### Priority 1 — High Impact, Achievable

| Model | Input Data | Output | Business Value |
|---|---|---|---|
| **Collaborative Filtering** | User-item interactions | "Users like you also bought..." | Increase AOV |
| **Content-Based Recommender** | Product features + user profile | Personalized feed | Increase engagement |
| **Churn Prediction** | User behavior features | Churn probability 0-1 | Retention campaigns |
| **Search Autocomplete** | Past search queries | Query suggestions | Search UX + conversion |

### Priority 2 — Advanced, High CV Value

| Model | Input Data | Output | Business Value |
|---|---|---|---|
| **Sentiment Analysis (Vietnamese)** | Review text | Positive/Negative/Neutral + aspects | Product quality signal |
| **Demand Forecasting** | Order time series | Next 30-day demand | Inventory optimization |
| **Price Elasticity** | Price history + order volume | Optimal price point | Revenue optimization |
| **Customer Segmentation (K-Means)** | RFM features | Segments: Champions, At-Risk, etc. | Targeted marketing |

### Priority 3 — Ambitious, Research-Grade

| Model | Input Data | Output | Business Value |
|---|---|---|---|
| **Multi-modal Search** | Product images + text | Visual similarity search | Discovery improvement |
| **LTV Prediction** | Early user behavior | Predicted 12-month value | Acquisition bidding |
| **Trend Detection** | Social + order data | Emerging trend alerts | Merchandising |
| **Cross-platform Intelligence** | Shopee/Lazada scrape | Market positioning | Competitive advantage |

---

## 12. REALISTIC COSMETICS ECOMMERCE SCENARIOS

### Scenario 1: The "Shade Finder" Problem
Users can't decide between lipstick shades (Đỏ 999 vs Hồng 100). Track:
- Which shade they viewed longer
- Which shade they added/removed from cart
- Final purchase shade

**ML application**: Shade preference predictor based on skin tone + past purchases

### Scenario 2: The "Skincare Routine" Bundle
A user who buys a Dior cleanser is a high-probability buyer of:
- Dior sunscreen (next purchase)
- Dior toner (future purchase)

**ML application**: Product affinity association rules (Apriori algorithm)

### Scenario 3: The "Post-Payday" Purchase Spike
Analyze order timestamps. Vietnamese payday is typically 1st and 15th of the month. This creates predictable purchase spikes.

**Analytics application**: Promotion timing optimization

### Scenario 4: The "Influencer Spike" Simulation
Simulate a TikTok video going viral about `rare_blush_joy`. Track:
- Search volume spike for "má hồng rare beauty"
- View count spike
- Cart add spike
- Inventory depletion

**Analytics application**: Social commerce attribution modeling

### Scenario 5: The "Loyal Customer vs. Deal Hunter"
Segment users by promo code usage rate and repeat purchase rate.
- High repeat + no promo = Brand loyal (high CLV)
- No repeat + always uses promo = Deal hunter (low CLV)

**ML application**: CLV prediction + segment-targeted promotions

### Scenario 6: The "Search Reveals Demand" Pattern
Users search "tẩy trang dior" but there is no makeup remover in the catalog. This zero-result search data = direct product sourcing signal.

**Analytics application**: Catalog expansion recommendation system

---

## 13. UPGRADE ROADMAP

### Phase 0 — Foundation (Current State) ✅ DONE
- User auth, product listing, cart, orders, favorites
- Firebase Realtime Database
- Basic order tracking

---

### Phase 1 — Event Instrumentation (2-3 weeks) 🔥 DO THIS FIRST

**Goal**: Turn the app into a data-generating machine

1. Implement `EventTracker` singleton in Android
2. Add events: session_started, product_viewed, add_to_cart, checkout_started, order_placed
3. Store events to Firebase `events/` node (batched)
4. Add product view dwell time tracking
5. Add search event with query + result count
6. Track checkout funnel steps

**Deliverable**: Raw behavioral event log in Firebase
**CV Value**: "Implemented in-app behavioral event tracking system generating 50+ event types"

---

### Phase 2 — Review System (1-2 weeks)

1. Add review submission screen on product detail page
2. Add "Đánh giá sản phẩm" button in order history (after order = "delivered")
3. Store reviews to Firebase `reviews/` node
4. Calculate and update `averageRating` on product when review is submitted
5. Display real reviews on product page

**Deliverable**: User-generated review dataset for NLP
**CV Value**: "Built review collection system producing Vietnamese-language dataset for sentiment analysis"

---

### Phase 3 — Promotions & Inventory (1-2 weeks)

1. Add promo code field at checkout
2. Create `promotions/` node with test codes
3. Add inventory count display ("Còn X sản phẩm")
4. Add low-stock alert ("Chỉ còn 3 sản phẩm!")

**Deliverable**: Price sensitivity data, scarcity behavior signals
**CV Value**: "Implemented promotion attribution tracking for ROI analysis"

---

### Phase 4 — User Enrichment (1 week)

1. Add skin type / concerns onboarding after registration
2. Add profile completion prompt
3. Add age range selection

**Deliverable**: User feature vectors for segmentation and personalization
**CV Value**: "Designed user profiling system for personalization pipeline"

---

### Phase 5 — Analytics Layer (3-4 weeks)

1. Export Firebase data to BigQuery (manual or automated)
2. Build star schema in BigQuery
3. Create SQL queries for all KPIs in Section 7
4. Connect Looker Studio (free) to BigQuery for dashboards
5. Create dashboards: Sales Performance, Funnel, Retention, Product Analytics

**Deliverable**: Working BI dashboard from real (simulated) data
**CV Value**: "Built end-to-end analytics pipeline from mobile app to BI dashboard using Firebase + BigQuery + Looker Studio"

---

### Phase 6 — ML Models (4-6 weeks)

1. Build collaborative filtering recommender (Python, implicit library)
2. Train Vietnamese sentiment classifier on review data (PhoBERT or underthesea)
3. Implement RFM segmentation (pandas + scikit-learn)
4. Build churn predictor (logistic regression or XGBoost)
5. Return recommendations to app (Firebase → app)

**Deliverable**: Working ML models with real training data from Phase 1-4
**CV Value**: "End-to-end ML pipeline: data collection → feature engineering → model training → serving to mobile app"

---

### Phase 7 — External Data Integration (3-4 weeks)

1. Scrape Shopee/Lazada for price comparison data
2. Scrape TikTok comments for cosmetic product sentiment
3. Scrape Reddit (r/AsianBeauty) for ingredient discussions
4. Join with internal data for enriched analysis

**Deliverable**: Multi-source ecommerce intelligence dataset
**CV Value**: "Integrated external market intelligence with internal behavioral data for competitive analysis"

---

## 14. PRIORITIZED RECOMMENDATIONS

### 🔴 MUST HAVE

| # | Recommendation | Business Value | DA/AI Value | Effort |
|---|---|---|---|---|
| 1 | Implement event tracking (Section 3) | Critical | Critical | Medium |
| 2 | Add product review & rating system | High | Critical | Medium |
| 3 | Add session tracking | High | Critical | Low |
| 4 | Track product view + dwell time | High | Critical | Low |
| 5 | Track search events with query | High | High | Low |
| 6 | Store zero-result searches | Medium | High | Very Low |
| 7 | Enrich order schema (add promo, method) | High | High | Low |

### 🟡 SHOULD HAVE

| # | Recommendation | Business Value | DA/AI Value | Effort |
|---|---|---|---|---|
| 8 | Add promo code system | High | High | Medium |
| 9 | User profile enrichment (skin type, etc.) | High | High | Low |
| 10 | Inventory/stock display | Medium | Medium | Low |
| 11 | Firebase → BigQuery ETL pipeline | Critical | Critical | Medium |
| 12 | BI dashboard (Looker Studio) | High | High | Medium |
| 13 | RFM segmentation analysis | High | High | Medium |
| 14 | User-item interaction matrix | Medium | Critical | Medium |
| 15 | "Recently Viewed" section in app | Medium | High | Low |

### 🟢 NICE TO HAVE

| # | Recommendation | Business Value | DA/AI Value | Effort |
|---|---|---|---|---|
| 16 | Vietnamese sentiment classifier | Medium | Very High | High |
| 17 | Collaborative filtering recommender | High | Very High | High |
| 18 | A/B test infrastructure | High | High | High |
| 19 | Price history tracking | Medium | High | Low |
| 20 | Shopee/Lazada price scraping | High | High | High |
| 21 | TikTok/Reddit sentiment integration | Medium | Very High | High |
| 22 | Demand forecasting model | Medium | High | High |
| 23 | Notification system + open tracking | High | High | High |
| 24 | Customer support / returns simulation | Low | Medium | Medium |
| 25 | Cross-sell / bundle recommendations | High | High | Medium |

---

## 15. FINAL SCORECARD

| Dimension | Current | After Phase 1-2 | After Phase 3-5 | After Phase 6-7 |
|---|---|---|---|---|
| Business Realism | 5.5/10 | 6.5/10 | 8/10 | 9/10 |
| Analytics Maturity | 2/10 | 6/10 | 8.5/10 | 9.5/10 |
| AI Readiness | 1.5/10 | 4/10 | 7/10 | 9.5/10 |
| Portfolio Impact | 4/10 | 6/10 | 8/10 | 9.5/10 |
| Dataset Quality | 1/10 | 5/10 | 8/10 | 9/10 |

---

## 💡 Key Insights for Your Portfolio

### What Makes This Different From Every Other Student Project

Most student ecommerce apps are **CRUD apps with a shopping cart**. They store data but never analyze it.

Your competitive advantage is:

1. **You thought about data collection, not just data storage**
2. **You designed for analytics from day one** (event tracking, schema design)
3. **You understand the ecommerce funnel** at a data level, not just a UX level
4. **You can speak the language** of product analysts, BI engineers, and ML engineers simultaneously

### The One Thing to Say in Interviews

> *"I built a mobile ecommerce app that is also a behavioral data collection platform. Every user interaction — from product views to search queries to checkout abandonment — is captured as structured events and fed into an analytics pipeline that produces funnel metrics, cohort retention curves, and features for recommendation and churn prediction models. The app is designed to generate the same type of data that Shopee and Lazada use to power their AI systems."*

---

**Document Version**: 1.0  
**Created**: May 15, 2026  
**Author**: Senior Product & Data Analyst Audit  
**Project**: Nhóm Nhung — Cosmetics E-Commerce App  
**Purpose**: Data Analytics / AI Analytics Portfolio Upgrade Roadmap

---
*End of Audit Document*
