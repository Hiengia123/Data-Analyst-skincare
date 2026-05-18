# 🏗️ Backend Batch ETL Pipeline — Full Documentation

> **Project:** WooCommerce Data Warehouse  
> **Stack:** Python · pandas · SQLAlchemy · PostgreSQL · FastAPI  
> **Last updated:** 2026-05-04

---

## Table of Contents

1. [Before Refactor (Old System)](#1-before-refactor-old-system)
2. [After Refactor (New System)](#2-after-refactor-new-system)
3. [New ETL Structure](#3-new-etl-structure)
4. [Data Model (Data Warehouse)](#4-data-model-data-warehouse)
5. [Data Flow (End-to-End)](#5-data-flow-end-to-end)
6. [Key Improvements](#6-key-improvements)
7. [Color & Size Handling](#7-color--size-handling)
8. [Incremental Loading & Deduplication](#8-incremental-loading--deduplication)
9. [Scheduling (Current State)](#9-scheduling-current-state)
10. [Future Improvements](#10-future-improvements)

---

## 1. Before Refactor (Old System)

### How the old pipeline worked

The original project was a **single-pass script** with everything tightly coupled:

```
src/
  extract_db.py      ← extracted orders, returned a raw DataFrame
  transform_data.py  ← cleaned that single DataFrame
  load_db.py         ← dumped it into PostgreSQL
  run_etl.py         ← glue script that imported df directly
```

The entire data flow looked like this:

```python
# OLD run_etl.py (simplified)
from extract_db import df          # imports a module-level variable!
from transform_data import clean   # transforms the same df
from load_db import load           # pushes to a single table

clean(df)
load(df, "fact_orders")
```

**Only one table existed:** `fact_orders` — containing just `order_id`, `order_date`, `total_amount`, `day`, and `month`.

### Problems with the old system

| Problem | Why it matters |
|---|---|
| **Tightly coupled** | `extract_db.py` executed SQL on import — you couldn't call it with different parameters or reuse it |
| **Not modular** | All extraction logic lived in one file; adding a new entity (products, users) meant modifying existing code |
| **Single table** | Without `order_items`, you cannot know *what* was sold, only *how much* — making product analytics impossible |
| **No incremental load** | Every run re-downloaded the entire dataset and replaced the table (`if_exists="replace"`) |
| **No deduplication** | If the pipeline crashed mid-run and you re-ran it, you could end up with duplicate rows |
| **No scheduling** | The script was a one-shot command — no automated daily execution |

---

## 2. After Refactor (New System)

### Architecture overview

The new system follows the classic **ETL (Extract → Transform → Load)** pattern used in production data warehouses:

```
┌──────────────────────────────────────────────────────────┐
│                    WooCommerce (MySQL)                    │
│   wp_wc_orders · wp_woocommerce_order_items · wp_posts   │
│                      wp_users                            │
└────────────────────────┬─────────────────────────────────┘
                         │
                    EXTRACT LAYER
                    (4 modules)
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                   Raw DataFrames                         │
│   orders_raw · order_items_raw · products_raw · users    │
└────────────────────────┬─────────────────────────────────┘
                         │
                   TRANSFORM LAYER
                   (4 modules)
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                 Clean DataFrames                         │
│   orders_clean · items_clean · products_clean · users    │
└────────────────────────┬─────────────────────────────────┘
                         │
                    LOAD LAYER
               (UPSERT / REPLACE)
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│              PostgreSQL Data Warehouse                   │
│   fact_orders · fact_order_items · dim_products · dim_u…  │
└────────────────────────┬─────────────────────────────────┘
                         │
                    FastAPI reads
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                  React Dashboard                         │
│          Charts · KPIs · Tables · Analytics              │
└──────────────────────────────────────────────────────────┘
```

### Key design decisions

- **One function per entity per layer** — `extract_orders()`, `transform_orders()`, etc.
- **Functions, not module-level variables** — nothing executes on import
- **Fact tables use UPSERT** — safe for incremental and re-runs
- **Dimension tables use REPLACE** — they are small and may change (product names, user data)
- **State tracking** — `last_run.json` enables incremental extraction

---

## 3. New ETL Structure

```
backend/
├── .env                          ← DB credentials (WC_DB_URL, DW_DB_URL)
├── app/
│   └── main.py                   ← FastAPI application
│
└── etl/
    ├── run_etl.py                ← Pipeline orchestrator (CLI entry point)
    ├── state.py                  ← Tracks last_run timestamp
    ├── last_run.json             ← Auto-generated state file
    │
    ├── extract/
    │   ├── extract_orders.py     ← Extracts from wp_wc_orders
    │   ├── extract_order_items.py← Extracts from wp_woocommerce_order_items + itemmeta
    │   ├── extract_products.py   ← Extracts from wp_posts
    │   └── extract_users.py      ← Extracts from wp_users
    │
    ├── transform/
    │   ├── transform_orders.py   ← Date parsing, day/month extraction
    │   ├── transform_order_items.py ← Type casting for qty, price, product_id
    │   ├── transform_products.py ← Color extraction from product name
    │   └── transform_users.py    ← Datetime casting
    │
    └── load/
        └── load_dw.py            ← UPSERT loader with PK management
```

### Purpose of each folder

| Folder | Responsibility |
|---|---|
| `extract/` | **Read** raw data from the WooCommerce MySQL database. Each file queries one entity. Supports a `since` parameter for incremental extraction. |
| `transform/` | **Clean and enrich** the raw DataFrames. Type casting, date parsing, derived columns (day, month, color). Pure Python — no database access. |
| `load/` | **Write** clean DataFrames into PostgreSQL. Handles table creation, primary key constraints, and UPSERT logic. |
| `run_etl.py` | **Orchestrator** — calls extract → transform → load in sequence. Manages CLI arguments (`--full`, `--schedule`). |
| `state.py` | **State manager** — reads/writes `last_run.json` to know when the pipeline last executed. |

---

## 4. Data Model (Data Warehouse)

The Data Warehouse uses a **star schema** with 2 fact tables and 2 dimension tables:

```
              ┌────────────────┐
              │  dim_products  │
              │────────────────│
              │ product_id (PK)│
              │ name           │
              │ color          │
              └───────┬────────┘
                      │
                      │ product_id
                      │
┌───────────────┐     │     ┌──────────────────┐
│  fact_orders  │     │     │ fact_order_items  │
│───────────────│     │     │──────────────────│
│ order_id (PK) │◄────┼────►│ order_item_id(PK)│
│ order_date    │     │     │ order_id (FK)    │
│ total_amount  │     │     │ product_id (FK)  │
│ day           │     │     │ quantity         │
│ month         │     │     │ price            │
└───────────────┘     │     └──────────────────┘
                      │
              ┌───────┴────────┐
              │   dim_users    │
              │────────────────│
              │ user_id (PK)   │
              │ name           │
              │ created_at     │
              └────────────────┘
```

### `fact_orders`

The **core transactional table**. Each row is one WooCommerce order.

| Column | Type | Description |
|---|---|---|
| `order_id` | INT (PK) | Unique order identifier from WooCommerce |
| `order_date` | TIMESTAMP | When the order was placed (`date_created_gmt`) |
| `total_amount` | FLOAT | Total order value in VND |
| `day` | DATE | Derived: `order_date` truncated to date (for daily analytics) |
| `month` | TEXT | Derived: `"2025-11"` format (for monthly analytics) |

**Used by:** `/summary`, `/orders`, `/analytics/monthly`, `/analytics/daily`

---

### `fact_order_items` ⭐ (Critical Table)

This is the **most important table** added in the refactor. Without it, you only know "Order #216 was 355,000₫" — but you don't know *what product was sold*.

| Column | Type | Description |
|---|---|---|
| `order_item_id` | INT (PK) | Unique line-item identifier |
| `order_id` | INT (FK) | Links back to `fact_orders` |
| `product_id` | INT (FK) | Links to `dim_products` |
| `quantity` | INT | Number of units sold |
| `price` | FLOAT | Line total in VND |

**Why it is critical:**

- **Product-level analytics:** "Which product sells the most?" — impossible without this table
- **Revenue attribution:** "How much revenue does each product generate?"
- **Basket analysis:** "What products are commonly bought together?"
- **Joins:** `fact_order_items JOIN dim_products` gives you product names, colors, and sales data in one query

**Data source:** Extracted from `wp_woocommerce_order_items` joined with `wp_woocommerce_order_itemmeta` using pivot logic:

```sql
SELECT
    i.order_item_id,
    i.order_id,
    MAX(CASE WHEN m.meta_key = '_product_id' THEN m.meta_value END) AS product_id,
    MAX(CASE WHEN m.meta_key = '_qty'        THEN m.meta_value END) AS quantity,
    MAX(CASE WHEN m.meta_key = '_line_total'  THEN m.meta_value END) AS price
FROM wp_woocommerce_order_items i
LEFT JOIN wp_woocommerce_order_itemmeta m ON i.order_item_id = m.order_item_id
WHERE i.order_item_type = 'line_item'
GROUP BY i.order_item_id, i.order_id
```

WooCommerce stores item metadata in a **key-value (EAV) table** (`wp_woocommerce_order_itemmeta`), so we use `MAX(CASE WHEN ...)` to pivot it into proper columns.

---

### `dim_products`

Dimension table for the product catalog.

| Column | Type | Description |
|---|---|---|
| `product_id` | INT (PK) | WooCommerce product ID (`wp_posts.ID`) |
| `name` | TEXT | Product name (`wp_posts.post_title`) |
| `color` | TEXT | Extracted from the product name using rule-based Python logic |

**Data source:** `wp_posts WHERE post_type = 'product' AND post_status = 'publish'`

**Load strategy:** Always REPLACE (full reload). Products are a small dataset, and names or attributes may change.

---

### `dim_users`

Dimension table for WordPress/WooCommerce users (customers).

| Column | Type | Description |
|---|---|---|
| `user_id` | INT (PK) | WordPress user ID |
| `name` | TEXT | Display name (`wp_users.display_name`) |
| `created_at` | TIMESTAMP | Registration date (`wp_users.user_registered`) |

**Data source:** `wp_users`

---

## 5. Data Flow (End-to-End)

Here is what happens when you run `python run_etl.py`:

### Step 1 — State Check

```python
last_run = get_last_run()  # reads last_run.json
# Returns "2026-05-04T04:49:18" or None (first run)
```

If `last_run` exists → **INCREMENTAL mode** (only new data).  
If `last_run` is None → **FULL mode** (everything).

### Step 2 — Extract

```
WooCommerce MySQL
    │
    ├── extract_orders(since="2026-05-04...")   → orders_raw DataFrame
    ├── extract_order_items(order_ids=[233])    → items_raw DataFrame
    ├── extract_products()                      → products_raw DataFrame   (always full)
    └── extract_users()                         → users_raw DataFrame      (always full)
```

- **Orders** use `WHERE date_created_gmt > :since` for incremental extraction
- **Order items** filter by `WHERE order_id IN (...)` — only items belonging to new orders
- **Products & users** are always fully extracted (dimension tables are small)

### Step 3 — Transform

```python
orders_clean     = transform_orders(orders_raw)      # datetime parsing, day/month
items_clean      = transform_order_items(items_raw)   # type casting
products_clean   = transform_products(products_raw)   # color extraction
users_clean      = transform_users(users_raw)         # datetime casting
```

No database access in this step — pure pandas operations.

### Step 4 — Load

```
PostgreSQL Data Warehouse
    │
    ├── fact_orders       ← UPSERT (ON CONFLICT order_id DO UPDATE)
    ├── fact_order_items  ← UPSERT (ON CONFLICT order_item_id DO UPDATE)
    ├── dim_products      ← REPLACE (full reload)
    └── dim_users         ← REPLACE (full reload)
```

### Step 5 — Save State

```python
save_last_run(run_start)  # writes to last_run.json
```

### Step 6 — FastAPI serves the data

```
React Dashboard
    │
    ├── GET /summary              → KPI cards
    ├── GET /orders               → Orders table (paginated)
    ├── GET /analytics/monthly    → Monthly revenue chart
    ├── GET /analytics/daily      → Daily revenue chart
    ├── GET /analytics/top-products → Top products bar chart
    ├── GET /analytics/products-by-color → Color doughnut chart
    ├── GET /products             → Product catalog table
    └── GET /order_items          → Order items table
```

---

## 6. Key Improvements

### Modular Design

| Old | New |
|---|---|
| 1 extract file for everything | 4 separate extract modules, one per entity |
| 1 transform file | 4 separate transform modules |
| 1 load file with `if_exists="replace"` | Smart loader with UPSERT + REPLACE strategies |

### Scalability

Adding a new entity (e.g., `coupons`) now requires:
1. Create `extract/extract_coupons.py`
2. Create `transform/transform_coupons.py`
3. Add `"dim_coupons": coupons_clean` to the pipeline
4. Done — no existing code modified

### Separation of Concerns

- **Extract:** Only talks to WooCommerce MySQL — knows nothing about transformations
- **Transform:** Only receives and returns DataFrames — knows nothing about databases
- **Load:** Only talks to PostgreSQL — knows nothing about where the data came from

### Reusable Functions

```python
# You can now call these independently
orders = extract_orders(since="2026-01-01")
products = extract_products()
```

Each function is self-contained with its own error handling and can be used in Jupyter notebooks, scripts, or tests.

### ML / Analytics Readiness

With `fact_order_items` + `dim_products`, you can now build:
- **Sales forecasting** (time series on daily revenue)
- **Product recommendations** (collaborative filtering on items bought together)
- **Customer segmentation** (RFM analysis using orders + users)
- **Inventory prediction** (quantity trends per product)

---

## 7. Color & Size Handling

### The Problem: Unstructured Data

WooCommerce stores product information in a way that makes analytics difficult:

- **Product name** is a free-text field: `"Áo thun nam đen"`, `"Quần kaki xanh"`
- **Color** is not stored in a separate column — it is embedded in the name
- **Size** is stored in `wp_postmeta` under a serialized PHP array (`_product_attributes`)

This is a classic example of **dirty data** in e-commerce.

### How Color is Extracted

The `transform_products.py` module uses a **rule-based Python function**:

```python
COLORS = ["đen", "trắng", "xanh", "đỏ", "vàng", "tím", "hồng", "cam", "nâu", "xám"]

def extract_color(name: str) -> str:
    name_lower = name.lower()
    for color in COLORS:
        if re.search(rf'\b{color}\b', name_lower):
            return color
    return "Unknown"
```

**Examples:**

| Product Name | Extracted Color |
|---|---|
| `"Áo thun nam đen"` | `đen` |
| `"Quần kaki xanh dương"` | `xanh` |
| `"Giày da nâu"` | `nâu` |
| `"Phụ kiện thời trang"` | `Unknown` |

### Why "Rule-Based" and not ML?

- The dataset is small (31 products)
- Vietnamese color words are well-defined
- A simple regex match achieves nearly 100% accuracy
- No training data needed

### Future: Size Extraction

Size data lives in `wp_postmeta.meta_value` under the key `_product_attributes`. The value is a **serialized PHP array** that looks like:

```
a:1:{s:6:"pa_size";a:6:{s:4:"name";s:7:"pa_size";s:5:"value";s:0:"";...}}
```

Parsing this requires either:
- A PHP unserialize library for Python (`phpserialize`)
- Or querying the `wp_term_relationships` table to get WooCommerce attribute terms

This is a planned future improvement.

---

## 8. Incremental Loading & Deduplication

### Incremental Extraction

Instead of downloading the entire WooCommerce database every run, the pipeline uses **timestamp-based incremental extraction**:

```python
# state.py manages the checkpoint
last_run = get_last_run()  # "2026-05-04T04:49:18" or None

# extract_orders.py uses it as a filter
if since:
    query = "SELECT ... FROM wp_wc_orders WHERE date_created_gmt > :since"
```

**Result:** If you have 10,000 orders and only 5 new ones were placed since yesterday, the pipeline extracts only those 5 rows — not all 10,000.

### Deduplication via UPSERT

The load layer uses PostgreSQL's `ON CONFLICT ... DO UPDATE`:

```sql
INSERT INTO fact_orders (order_id, order_date, total_amount, day, month)
VALUES (:order_id, :order_date, :total_amount, :day, :month)
ON CONFLICT (order_id) DO UPDATE SET
    order_date = EXCLUDED.order_date,
    total_amount = EXCLUDED.total_amount,
    day = EXCLUDED.day,
    month = EXCLUDED.month
```

**This guarantees:**
- ✅ New rows are inserted
- ✅ Existing rows are updated (if order data changed)
- ✅ No duplicate `order_id` values ever exist

### Primary Key Auto-Migration

When the pipeline runs for the first time on old tables (created with `if_exists="replace"`), those tables have **no primary key constraint**. The loader automatically:

1. Detects the missing PK
2. Removes any duplicate rows (keeping one copy)
3. Adds the PRIMARY KEY constraint
4. Proceeds with UPSERT

This is a one-time migration that happens transparently.

---

## 9. Scheduling (Current State)

### How it works now

The pipeline uses **APScheduler** (Python library) for automated execution:

```powershell
# One-shot run (auto-detects full vs incremental)
python run_etl.py

# Force full reload (ignore last_run.json)
python run_etl.py --full

# Run once + repeat every 24 hours
python run_etl.py --schedule
```

When `--schedule` is used:
1. The pipeline runs immediately
2. APScheduler sleeps for 24 hours
3. It runs again
4. Repeat forever

### Limitations

| Limitation | Explanation |
|---|---|
| **Requires open terminal** | If you close the PowerShell window, the scheduler stops |
| **No retry on failure** | If the WooCommerce DB is down, the run fails and waits 24h |
| **No alerting** | No email or notification on failure |
| **No web UI** | You cannot see run history from a dashboard |

This is acceptable for development and small-scale production, but not for enterprise-grade systems.

---

## 10. Future Improvements

### 🔧 n8n or Airflow for Scheduling

Replace APScheduler with a dedicated workflow orchestrator:

- **n8n** (recommended for this project size): Visual workflow builder with WooCommerce and PostgreSQL nodes built in. Can schedule, retry, and send alerts.
- **Apache Airflow**: Industry standard for data pipelines. Better for complex DAGs with dependencies.

### 📊 Enhanced Analytics Endpoints

- **Customer lifetime value (CLV):** Aggregate revenue per user over time
- **Cohort analysis:** Group users by registration month, track retention
- **Product affinity:** "Customers who bought X also bought Y"

### 🤖 Machine Learning Integration

With the current data model, you can build:

| Model | Data Required | Table |
|---|---|---|
| Revenue forecasting | Daily revenue time series | `fact_orders` |
| Product recommendations | User × Product matrix | `fact_order_items` + `dim_users` |
| Customer segmentation | RFM features | `fact_orders` + `dim_users` |
| Demand prediction | Product × Date × Quantity | `fact_order_items` |

### 🧩 Additional Dimension Tables

- `dim_coupons` — Track discount usage
- `dim_shipping` — Shipping method analytics
- `dim_payment` — Payment method breakdown
- `dim_time` — Proper time dimension for OLAP-style queries

### 🔐 Production Hardening

- Tighten CORS (replace `*` with actual frontend domain)
- Add API authentication (JWT tokens)
- Server-side pagination with cursor-based `offset`
- Connection pooling for PostgreSQL
- Docker containerization (`docker-compose.yml` already scaffolded)

---

## Quick Reference: CLI Commands

```bash
# Run ETL (auto-detects full vs incremental)
python run_etl.py

# Force full reload
python run_etl.py --full

# Start 24h scheduler
python run_etl.py --schedule

# Start FastAPI backend
python -m uvicorn app.main:app --reload

# Start React frontend
cd frontend && npm run dev
```

---

*This document was generated as part of the WooCommerce Data Warehouse refactoring project.*
