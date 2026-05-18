"""
load_firebase.py
Drops old WooCommerce tables, creates new Firebase-sourced schema,
and upserts cleaned DataFrames into PostgreSQL Data Warehouse.
"""
import os
from sqlalchemy import create_engine, text
from dotenv import load_dotenv
import pandas as pd

load_dotenv(os.path.join(os.path.dirname(__file__), "..", "..", ".env"))

_DDL = """
-- ── Drop old WooCommerce tables if they exist ─────────────────────────────
DROP TABLE IF EXISTS fact_ratings       CASCADE;
DROP TABLE IF EXISTS fact_order_items CASCADE;
DROP TABLE IF EXISTS fact_orders      CASCADE;
DROP TABLE IF EXISTS dim_products     CASCADE;
DROP TABLE IF EXISTS dim_users        CASCADE;

-- ── New Firebase-sourced schema ───────────────────────────────────────────

CREATE TABLE IF NOT EXISTS dim_users (
    user_id    TEXT PRIMARY KEY,
    name       TEXT,
    email      TEXT,
    phone      TEXT,
    provider   TEXT,
    created_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS dim_products (
    product_id    TEXT PRIMARY KEY,
    title         TEXT,
    category_id   TEXT,
    category_title TEXT,
    product_type  TEXT,
    price         NUMERIC(12,2),
    average_rating NUMERIC(3,1),
    total_ratings INTEGER DEFAULT 0,
    total_stars   INTEGER DEFAULT 0,
    show_recommend BOOLEAN,
    capacity      TEXT,
    weight        TEXT,
    image_url     TEXT,
    description   TEXT
);

CREATE TABLE IF NOT EXISTS fact_orders (
    order_id       TEXT PRIMARY KEY,
    user_id        TEXT REFERENCES dim_users(user_id) ON DELETE SET NULL,
    created_at     TIMESTAMPTZ,
    order_date     DATE,
    total_price    NUMERIC(14,2),
    status         TEXT,
    payment_method TEXT,
    city           TEXT,
    cancel_reason  TEXT
);

CREATE TABLE IF NOT EXISTS fact_order_items (
    order_item_id     TEXT PRIMARY KEY,
    order_id          TEXT REFERENCES fact_orders(order_id) ON DELETE CASCADE,
    product_id        TEXT REFERENCES dim_products(product_id) ON DELETE SET NULL,
    title             TEXT,
    quantity          INTEGER,
    unit_price        NUMERIC(12,2),
    line_total        NUMERIC(14,2),
    selected_capacity TEXT,
    selected_color    TEXT,
    selected_weight   TEXT
);

CREATE TABLE IF NOT EXISTS fact_ratings (
    rating_id     TEXT PRIMARY KEY,
    product_id    TEXT REFERENCES dim_products(product_id) ON DELETE CASCADE,
    user_id       TEXT REFERENCES dim_users(user_id) ON DELETE SET NULL,
    stars         INTEGER,
    created_at    TIMESTAMPTZ
);
"""


def get_engine():
    url = os.getenv("DW_DB_URL")
    if not url:
        raise RuntimeError("DW_DB_URL not set in .env")
    return create_engine(url)


def recreate_schema(engine):
    """Drop old WooCommerce tables and create the new Firebase schema."""
    with engine.begin() as conn:
        conn.execute(text(_DDL))
    print("  [OK] Schema recreated (old WooCommerce tables dropped).")


def _upsert_df(df: pd.DataFrame, table: str, pk: str, engine):
    """Generic upsert: INSERT ... ON CONFLICT (pk) DO UPDATE."""
    if df.empty:
        return
    cols = list(df.columns)
    col_list   = ", ".join(f'"{c}"' for c in cols)
    val_list   = ", ".join(f":{c}" for c in cols)
    update_set = ", ".join(
        f'"{c}" = EXCLUDED."{c}"' for c in cols if c != pk
    )
    sql = text(f"""
        INSERT INTO {table} ({col_list})
        VALUES ({val_list})
        ON CONFLICT ("{pk}") DO UPDATE SET {update_set}
    """)
    records = df.where(df.notna(), None).to_dict(orient="records")
    with engine.begin() as conn:
        conn.execute(sql, records)
    print(f"  [OK] {table}: upserted {len(records)} rows.")


def load_products(df: pd.DataFrame, engine):
    _upsert_df(df, "dim_products", "product_id", engine)


def load_users(df: pd.DataFrame, engine):
    _upsert_df(df, "dim_users", "user_id", engine)


def load_orders(orders_df: pd.DataFrame, items_df: pd.DataFrame, engine):
    _upsert_df(orders_df, "fact_orders",      "order_id",      engine)
    _upsert_df(items_df,  "fact_order_items", "order_item_id", engine)

def load_ratings(df: pd.DataFrame, engine):
    _upsert_df(df, "fact_ratings", "rating_id", engine)
