"""Extract orders — supports incremental loads via `since` timestamp."""
import pandas as pd
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

# Load .env from backend root
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ".env"))

def get_wc_engine():
    url = os.getenv("WC_DB_URL")
    if not url:
        raise RuntimeError("WC_DB_URL not set")
    return create_engine(url)

def extract_orders(since: str | None = None) -> pd.DataFrame:
    """
    Extract orders from WooCommerce db.
    If `since` is provided (ISO string), only fetch orders created after that timestamp.
    """
    mode = "INCREMENTAL" if since else "FULL"
    print(f"   [{mode}] Extracting orders...")
    try:
        engine = get_wc_engine()

        if since:
            query = """
            SELECT 
                id AS order_id,
                date_created_gmt AS order_date,
                total_amount,
                billing_email,
                customer_id
            FROM wp_wc_orders
            WHERE type = 'shop_order'
              AND date_created_gmt > :since
            ORDER BY date_created_gmt
            """
            df = pd.read_sql(text(query), engine, params={"since": since})
        else:
            query = """
            SELECT 
                id AS order_id,
                date_created_gmt AS order_date,
                total_amount,
                billing_email,
                customer_id
            FROM wp_wc_orders
            WHERE type = 'shop_order'
            ORDER BY date_created_gmt
            """
            df = pd.read_sql(text(query), engine)

        print(f"   Extracted {len(df)} orders.")
        return df
    except Exception as e:
        print(f"   ERROR extracting orders: {e}")
        return pd.DataFrame()
