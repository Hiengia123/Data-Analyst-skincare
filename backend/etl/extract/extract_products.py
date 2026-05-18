"""Extract products — always full load (dimension table)."""
import pandas as pd
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ".env"))

def get_wc_engine():
    return create_engine(os.getenv("WC_DB_URL"))

def extract_products() -> pd.DataFrame:
    """
    Extract products from WooCommerce db.
    Always full load — dimension tables are small and may change names/colors.
    """
    print("   [FULL] Extracting products...")
    try:
        engine = get_wc_engine()
        query = """
        SELECT 
            ID AS product_id,
            post_title AS name
        FROM wp_posts
        WHERE post_type = 'product' AND post_status = 'publish'
        """
        df = pd.read_sql(text(query), engine)
        print(f"   Extracted {len(df)} products.")
        return df
    except Exception as e:
        print(f"   ERROR extracting products: {e}")
        return pd.DataFrame()
