import pandas as pd

def transform_orders(df: pd.DataFrame) -> pd.DataFrame:
    """Transform orders data"""
    print("⏳ Transforming orders...")
    try:
        if df.empty:
            return df
            
        df = df.dropna(subset=["order_date", "total_amount"])
        df["order_date"] = pd.to_datetime(df["order_date"], errors="coerce")
        df["total_amount"] = pd.to_numeric(df["total_amount"], errors="coerce")
        df = df.dropna(subset=["order_date", "total_amount"])

        df["day"] = df["order_date"].dt.date
        df["month"] = df["order_date"].dt.to_period("M").astype(str)

        # Normalize billing email
        if "billing_email" in df.columns:
            df["billing_email"] = df["billing_email"].astype(str).str.strip().str.lower()
        if "customer_id" in df.columns:
            df["customer_id"] = pd.to_numeric(df["customer_id"], errors="coerce").fillna(0).astype(int)

        print(f"✅ Transformed {len(df)} orders.")
        return df
    except Exception as e:
        print(f"❌ Error transforming orders: {e}")
        return pd.DataFrame()
