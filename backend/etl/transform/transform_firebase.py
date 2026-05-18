"""
transform_firebase.py
Transforms raw Firebase dicts into clean Pandas DataFrames
ready for loading into the PostgreSQL Data Warehouse.
"""
from datetime import datetime, timezone
import pandas as pd


def _ts_to_dt(ms_timestamp) -> datetime | None:
    """Convert a Firebase millisecond timestamp to a UTC datetime."""
    if not ms_timestamp:
        return None
    try:
        return datetime.fromtimestamp(int(ms_timestamp) / 1000, tz=timezone.utc)
    except Exception:
        return None


# ─── Products ─────────────────────────────────────────────────────────────────

def transform_products(items_raw: dict, stats_raw: dict) -> pd.DataFrame:
    """
    items_raw: { product_id: { title, price, categoryId, productType, ... } }
    stats_raw: { product_id: { totalRatings, totalStars } }
    Returns dim_products DataFrame.
    """
    rows = []
    for pid, item in items_raw.items():
        # Compute real ratings from productStats
        stats = stats_raw.get(pid, {})
        total_ratings = int(stats.get("totalRatings", 0))
        total_stars   = int(stats.get("totalStars", 0))
        average_rating = round(total_stars / total_ratings, 1) if total_ratings > 0 else 0.0

        rows.append({
            "product_id":    pid,
            "title":         item.get("title", ""),
            "category_id":   item.get("categoryId", ""),
            "category_title":item.get("categoryTitle", ""),
            "product_type":  item.get("productType", ""),
            "price":         float(item.get("price", 0)),
            "average_rating": average_rating,
            "total_ratings": total_ratings,
            "total_stars":   total_stars,
            "show_recommend":bool(item.get("showRecommend", False)),
            "capacity":      item.get("capacity", ""),
            "weight":        item.get("weight", ""),
            "image_url":     item.get("image", ""),
            "description":   item.get("description", ""),
        })
    df = pd.DataFrame(rows)
    return df

# ─── Ratings ──────────────────────────────────────────────────────────────────

def transform_ratings(ratings_raw: dict) -> pd.DataFrame:
    """
    ratings_raw: { product_id: { user_id: { stars, createdAt, ... } } }
    Returns fact_ratings DataFrame.
    """
    rows = []
    for pid, user_ratings in ratings_raw.items():
        for uid, rating_data in user_ratings.items():
            rows.append({
                "rating_id":  f"{pid}_{uid}",
                "product_id": pid,
                "user_id":    uid,
                "stars":      int(rating_data.get("stars", 0)),
                "created_at": _ts_to_dt(rating_data.get("createdAt")),
            })
    return pd.DataFrame(rows)


# ─── Users ────────────────────────────────────────────────────────────────────

def transform_users(users_raw: dict) -> pd.DataFrame:
    """
    users_raw: { uid: { name, email, phone, provider, createdAt } }
    Returns dim_users DataFrame.
    """
    rows = []
    for uid, u in users_raw.items():
        rows.append({
            "user_id":    uid,
            "name":       u.get("name", ""),
            "email":      (u.get("email", "") or "").lower().strip(),
            "phone":      u.get("phone", ""),
            "provider":   u.get("provider", "email"),
            "created_at": _ts_to_dt(u.get("createdAt")),
        })
    df = pd.DataFrame(rows)
    return df


# ─── Orders & Order Items ──────────────────────────────────────────────────────

def transform_orders(orders_raw: dict) -> tuple[pd.DataFrame, pd.DataFrame]:
    """
    orders_raw: { order_id: { userId, createdAt, totalPrice,
                               status, paymentMethod, items: {...} } }
    Returns (fact_orders_df, fact_order_items_df).
    """
    order_rows = []
    item_rows  = []

    for oid, order in orders_raw.items():
        created_at = _ts_to_dt(order.get("createdAt"))
        order_rows.append({
            "order_id":       oid,
            "user_id":        order.get("userId", ""),
            "created_at":     created_at,
            "order_date":     created_at.date() if created_at else None,
            "total_price":    float(order.get("totalPrice", 0)),
            "status":         order.get("status", ""),
            "payment_method": order.get("paymentMethod", ""),
            "city":           order.get("shippingAddress", {}).get("city", ""),
            "cancel_reason":  order.get("cancelReason", ""),
        })

        for item_key, item in (order.get("items") or {}).items():
            item_rows.append({
                "order_item_id": item_key,
                "order_id":      oid,
                "product_id":    item.get("productId", ""),
                "title":         item.get("title", ""),
                "quantity":      int(item.get("quantity", 1)),
                "unit_price":    float(item.get("price", 0)),
                "line_total":    float(item.get("price", 0)) * int(item.get("quantity", 1)),
                "selected_capacity": item.get("selectedCapacity", ""),
                "selected_color":    item.get("selectedColor", ""),
                "selected_weight":   item.get("selectedWeight", ""),
            })

    orders_df = pd.DataFrame(order_rows)
    items_df  = pd.DataFrame(item_rows)
    return orders_df, items_df
