"""Extract order items — supports incremental loads by filtering on order_id."""
import pandas as pd
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ".env"))

def get_wc_engine():
    return create_engine(os.getenv("WC_DB_URL"))

def extract_order_items(order_ids: list | None = None) -> pd.DataFrame:
    """
    Extract order items from WooCommerce db.
    If `order_ids` is provided, only fetch items belonging to those orders (incremental).
    """
    mode = "INCREMENTAL" if order_ids else "FULL"
    print(f"   [{mode}] Extracting order items...")
    try:
        engine = get_wc_engine()

        base_query = """
        SELECT 
            i.order_item_id,
            i.order_id,
            MAX(CASE WHEN m.meta_key = '_product_id' THEN m.meta_value END) AS product_id,
            MAX(CASE WHEN m.meta_key = '_qty' THEN m.meta_value END) AS quantity,
            MAX(CASE WHEN m.meta_key = '_line_total' THEN m.meta_value END) AS price
        FROM wp_woocommerce_order_items i
        LEFT JOIN wp_woocommerce_order_itemmeta m ON i.order_item_id = m.order_item_id
        WHERE i.order_item_type = 'line_item'
        """

        if order_ids and len(order_ids) > 0:
            # Build parameterised IN clause
            placeholders = ", ".join([f":oid_{i}" for i in range(len(order_ids))])
            query = base_query + f" AND i.order_id IN ({placeholders})"
            query += " GROUP BY i.order_item_id, i.order_id"
            params = {f"oid_{i}": int(oid) for i, oid in enumerate(order_ids)}
            df = pd.read_sql(text(query), engine, params=params)
        else:
            query = base_query + " GROUP BY i.order_item_id, i.order_id"
            df = pd.read_sql(text(query), engine)

        print(f"   Extracted {len(df)} order items.")
        return df
    except Exception as e:
        print(f"   ERROR extracting order items: {e}")
        return pd.DataFrame()
