# 🌊 Data Flow: WooCommerce → UI
[Historical - batch]
WooCommerce (DB/API)
        ↓
ETL (Python - schedule)
        ↓
Data Warehouse (PostgreSQL)

[Realtime - incremental]
Shop/TS backend (hoặc webhook Woo)
        ↓
Ingest API (FastAPI)  ──(option)──> Queue (Redis/RabbitMQ)
        ↓
Data Warehouse (PostgreSQL)
[Serving]
FastAPI (analytics API)
        ↓
React + Chart.js (UI) . 

> **For beginners** — a simple explanation of how data moves through this system.

---

## Overview

This system automatically collects order data from a WooCommerce store, cleans it up, saves it to a database, and displays it as charts and tables on a dashboard.

Think of it like a **data pipeline** — raw data enters one end, and beautiful analytics come out the other. Every 24 hours, the pipeline runs automatically and the dashboard updates with the latest data.

---

## Simple Diagram

```
┌─────────────────────┐
│   WooCommerce       │  ← Customers place orders here
│   (MySQL Database)  │
└──────────┬──────────┘
           │  SQL queries
           ▼
┌─────────────────────┐
│   ETL Pipeline      │  ← Python script runs every 24h
│   (Python + pandas) │
└──────────┬──────────┘
           │  Clean data
           ▼
┌─────────────────────┐
│   Data Warehouse    │  ← Organised tables for analytics
│   (PostgreSQL)      │
└──────────┬──────────┘
           │  SQL queries
           ▼
┌─────────────────────┐
│   FastAPI Backend   │  ← Serves data as JSON via API
│   (Python)          │
└──────────┬──────────┘
           │  HTTP requests
           ▼
┌─────────────────────┐
│   React Dashboard   │  ← Charts, KPIs, and tables
│   (Chart.js)        │
└─────────────────────┘
```

---

## Step-by-Step Explanation

---

### Step 1 — WooCommerce (Data Source)

WooCommerce is the online store. When a customer buys something, WooCommerce saves the order into its own **MySQL database**.

The data lives in tables like:

| WooCommerce Table | What it contains |
|---|---|
| `wp_wc_orders` | Every order (date, total amount) |
| `wp_woocommerce_order_items` | What products were in each order |
| `wp_posts` | Product catalog (names, descriptions) |
| `wp_users` | Customer accounts |

> **Problem:** This raw data is messy — dates are in the wrong format, colors are buried inside product names, and metadata is split across multiple tables.

---

### Step 2 — ETL Pipeline (Python)

**ETL** stands for **Extract, Transform, Load**. It is a Python script that runs every 24 hours.

#### 🔵 Extract
The pipeline connects to WooCommerce's MySQL database and pulls data using SQL.

```
extract_orders()       → reads wp_wc_orders
extract_order_items()  → reads wp_woocommerce_order_items
extract_products()     → reads wp_posts
extract_users()        → reads wp_users
```

Only **new records** are pulled each time (incremental loading) — so it is fast even with large datasets.

#### 🟡 Transform
The raw data is cleaned and enriched using **pandas** (a Python data library):

- Convert text dates → proper datetime format
- Add `day` and `month` columns (for grouping in charts)
- Extract `color` from product names (e.g. `"Áo đen"` → `"đen"`)
- Cast prices and quantities to the correct number types

#### 🟢 Load
The clean data is saved into PostgreSQL (the Data Warehouse), using **UPSERT** — meaning:
- New orders are **inserted**
- Existing orders that changed are **updated**
- No duplicates are ever created

---

### Step 3 — Data Warehouse (PostgreSQL)

The Data Warehouse stores clean, well-structured data ready for analytics.

It contains 4 tables:

```
fact_orders          → one row per order
fact_order_items     → one row per product line in each order
dim_products         → product catalog with extracted color
dim_users            → customer list
```

> **Why two databases?**  
> WooCommerce's MySQL is the **operational database** — it is optimised for running the store.  
> PostgreSQL is the **analytical database** — it is optimised for running reports and charts.  
> Mixing both would slow down your store.

---

### Step 4 — FastAPI Backend (API Layer)

FastAPI is a Python web server. It reads data from the Data Warehouse and exposes it as **API endpoints** — URLs that return JSON data.

| Endpoint | What it returns |
|---|---|
| `GET /summary` | Total orders, total revenue, average basket |
| `GET /orders` | Paginated list of recent orders |
| `GET /analytics/monthly` | Revenue and order count grouped by month |
| `GET /analytics/daily` | Revenue grouped by day |
| `GET /analytics/top-products` | Best-selling products by revenue |
| `GET /analytics/products-by-color` | Product count grouped by color |

The UI never touches the database directly — it always goes through FastAPI.

---

### Step 5 — React Dashboard (UI)

The React frontend is what you see in the browser. It:

1. Calls the FastAPI endpoints using **Axios** (a JavaScript HTTP library)
2. Receives JSON data
3. Displays it using **Chart.js** (for charts) and plain HTML tables

The dashboard has 3 tabs:

| Tab | What you see |
|---|---|
| **Overview** | Monthly revenue bar chart + Daily revenue line chart |
| **Products** | Top products bar chart + Color doughnut chart + Product table |
| **Orders** | Paginated orders table |

---

## Example: Full Flow of One Sale

Here is what happens when a customer buys a product:

```
1. Customer clicks "Buy" on the WooCommerce store
   └─ WooCommerce saves the order to MySQL (wp_wc_orders)

2. 24 hours later, ETL pipeline runs automatically
   ├─ EXTRACT: reads new orders from MySQL since last run
   ├─ TRANSFORM: parses dates, extracts product color
   └─ LOAD: upserts the new order into PostgreSQL (fact_orders)

3. You open the React dashboard
   ├─ Dashboard calls GET /summary
   ├─ FastAPI queries SELECT SUM(total_amount) FROM fact_orders
   └─ Dashboard shows updated revenue KPI card

4. You click the "Products" tab
   ├─ Dashboard calls GET /analytics/top-products
   ├─ FastAPI runs a JOIN between fact_order_items and dim_products
   └─ Dashboard shows a bar chart of best-selling products
```

---

## Key Concepts (Quick Reference)

| Term | Simple meaning |
|---|---|
| **ETL** | A script that moves and cleans data from one place to another |
| **Data Warehouse** | A database built for analytics, not for running apps |
| **Incremental load** | Only loading *new* data, not everything every time |
| **UPSERT** | Insert if new, update if already exists — no duplicates |
| **API endpoint** | A URL that returns data in JSON format |
| **Dimension table** | A lookup table (`dim_products`, `dim_users`) |
| **Fact table** | A table with measurable events (`fact_orders`, `fact_order_items`) |

---

*Part of the WooCommerce Data Warehouse project documentation.*