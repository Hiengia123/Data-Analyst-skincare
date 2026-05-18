import pandas as pd
import re

def extract_color(name: str) -> str:
    """Rule-based extraction of color from product name"""
    if not isinstance(name, str):
        return "Unknown"
        
    name_lower = name.lower()
    
    # Simple rule-based dictionary
    colors = ["đen", "trắng", "xanh", "đỏ", "vàng", "tím", "hồng", "cam", "nâu", "xám"]
    
    for color in colors:
        # Check if color exists as a standalone word
        if re.search(rf'\b{color}\b', name_lower):
            return color
            
    return "Unknown"

def transform_products(df: pd.DataFrame) -> pd.DataFrame:
    """Transform products data and extract colors"""
    print("⏳ Transforming products...")
    try:
        if df.empty:
            return df
            
        df["product_id"] = pd.to_numeric(df["product_id"], errors="coerce").fillna(0).astype(int)
        df["name"] = df["name"].astype(str)
        
        # Apply the color extraction rule
        df["color"] = df["name"].apply(extract_color)

        print(f"✅ Transformed {len(df)} products.")
        return df
    except Exception as e:
        print(f"❌ Error transforming products: {e}")
        return pd.DataFrame()
