import pandas as pd

def transform_order_items(df: pd.DataFrame) -> pd.DataFrame:
    """Transform order items data"""
    print("⏳ Transforming order items...")
    try:
        if df.empty:
            return df
            
        df["quantity"] = pd.to_numeric(df["quantity"], errors="coerce").fillna(0).astype(int)
        df["price"] = pd.to_numeric(df["price"], errors="coerce").fillna(0.0)
        df["product_id"] = pd.to_numeric(df["product_id"], errors="coerce").fillna(0).astype(int)
        df["order_id"] = pd.to_numeric(df["order_id"], errors="coerce").fillna(0).astype(int)

        print(f"✅ Transformed {len(df)} order items.")
        return df
    except Exception as e:
        print(f"❌ Error transforming order items: {e}")
        return pd.DataFrame()
