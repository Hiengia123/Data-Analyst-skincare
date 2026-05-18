"""
main.py  —  FastAPI backend for the Skincare / Firebase Analytics Dashboard
Data Warehouse: PostgreSQL  (populated by run_etl.py / scheduler.py from Firebase)
"""
import sys
import os
from fastapi import FastAPI, HTTPException, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import create_engine, text
from pydantic import BaseModel
import hashlib, secrets
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), "..", ".env"))

# Allow importing etl.* modules when FastAPI is run from /backend
_backend_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _backend_dir not in sys.path:
    sys.path.insert(0, _backend_dir)

app = FastAPI(title="Skincare Analytics API — Firebase Edition", version="4.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─── DB ─────────────────────────────────────────────────────────────────────

def get_engine():
    url = os.getenv("DW_DB_URL")
    if not url:
        raise RuntimeError("DW_DB_URL not set")
    return create_engine(url)

# ─── AUTH ───────────────────────────────────────────────────────────────────

DEMO_USERS = {
    "admin@minshop.vn": {
        "password_hash": hashlib.sha256("admin123".encode()).hexdigest(),
        "name": "Admin", "role": "admin",
    }
}
TOKENS = {}

class LoginRequest(BaseModel):
    email: str
    password: str

@app.post("/auth/login")
def login(req: LoginRequest):
    user = DEMO_USERS.get(req.email)
    if not user or hashlib.sha256(req.password.encode()).hexdigest() != user["password_hash"]:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = secrets.token_hex(32)
    TOKENS[token] = {"email": req.email, "name": user["name"], "role": user["role"]}
    return {"token": token, "user": {"email": req.email, "name": user["name"], "role": user["role"]}}

@app.get("/auth/me")
def auth_me(token: str = ""):
    user = TOKENS.get(token)
    if not user:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    return user

# ─── ROOT ────────────────────────────────────────────────────────────────────

@app.get("/")
def root():
    return {"message": "Skincare Analytics API (Firebase Edition) running"}

# ─── KPI SUMMARY ─────────────────────────────────────────────────────────────

@app.get("/summary")
def get_summary():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            row = conn.execute(text("""
                SELECT
                    COUNT(DISTINCT o.order_id)                          AS total_orders,
                    SUM(o.total_price)                                  AS total_revenue,
                    AVG(o.total_price)                                  AS avg_order_value,
                    MAX(o.total_price)                                  AS max_order,
                    COUNT(DISTINCT o.user_id)                           AS total_customers,
                    SUM(CASE WHEN o.status='cancelled' THEN 1 ELSE 0 END)::INT AS cancelled_orders
                FROM fact_orders o
            """)).fetchone()
        return dict(row._mapping)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ORDERS ──────────────────────────────────────────────────────────────────

@app.get("/orders")
def get_orders(limit: int = 50, offset: int = 0):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT o.order_id, o.user_id, u.name AS customer_name, u.email,
                       o.created_at, o.order_date, o.total_price, o.status,
                       o.payment_method, o.city
                FROM fact_orders o
                LEFT JOIN dim_users u ON o.user_id = u.user_id
                ORDER BY o.created_at DESC
                LIMIT :lim OFFSET :off
            """), {"lim": limit, "off": offset})
            data = [dict(r._mapping) for r in rows]
            total = conn.execute(text("SELECT COUNT(*) FROM fact_orders")).scalar()
        return {"count": total, "orders": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ORDER DRILL-DOWN ─────────────────────────────────────────────────────────

@app.get("/orders/{order_id}/items")
def get_order_detail(order_id: str):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            order = conn.execute(text("""
                SELECT o.*, u.name AS customer_name, u.email
                FROM fact_orders o
                LEFT JOIN dim_users u ON o.user_id = u.user_id
                WHERE o.order_id = :oid
            """), {"oid": order_id}).fetchone()
            if not order:
                raise HTTPException(status_code=404, detail="Order not found")
            items = conn.execute(text("""
                SELECT i.*, p.title AS product_title, p.category_title, p.image_url
                FROM fact_order_items i
                LEFT JOIN dim_products p ON i.product_id = p.product_id
                WHERE i.order_id = :oid
            """), {"oid": order_id})
            item_rows = [dict(r._mapping) for r in items]
        return {"order": dict(order._mapping), "items": item_rows}
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── PRODUCTS ────────────────────────────────────────────────────────────────

@app.get("/products")
def get_products(limit: int = 100, offset: int = 0):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT * FROM dim_products ORDER BY average_rating DESC LIMIT :lim OFFSET :off
            """), {"lim": limit, "off": offset})
            data = [dict(r._mapping) for r in rows]
            total = conn.execute(text("SELECT COUNT(*) FROM dim_products")).scalar()
        return {"count": total, "products": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── PRODUCT DRILL-DOWN ───────────────────────────────────────────────────────

@app.get("/products/{product_id}/details")
def get_product_detail(product_id: str):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            product = conn.execute(text(
                "SELECT * FROM dim_products WHERE product_id = :pid"
            ), {"pid": product_id}).fetchone()
            if not product:
                raise HTTPException(status_code=404, detail="Product not found")
            sales = conn.execute(text("""
                SELECT COALESCE(SUM(quantity),0) AS total_qty,
                       COALESCE(SUM(line_total),0) AS total_revenue,
                       COUNT(DISTINCT order_id) AS total_orders
                FROM fact_order_items WHERE product_id = :pid
            """), {"pid": product_id}).fetchone()
            recent = conn.execute(text("""
                SELECT i.order_id, o.created_at, o.status, i.quantity, i.unit_price,
                       i.line_total, i.selected_capacity, i.selected_color
                FROM fact_order_items i
                JOIN fact_orders o ON o.order_id = i.order_id
                WHERE i.product_id = :pid
                ORDER BY o.created_at DESC LIMIT 10
            """), {"pid": product_id})
            recent_rows = [dict(r._mapping) for r in recent]
        return {
            "product": dict(product._mapping),
            "sales": dict(sales._mapping),
            "recent_orders": recent_rows,
        }
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: MONTHLY REVENUE ──────────────────────────────────────────────

@app.get("/analytics/monthly")
def get_monthly():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT TO_CHAR(order_date, 'YYYY-MM') AS month,
                       COUNT(*) AS orders,
                       SUM(total_price) AS revenue
                FROM fact_orders
                WHERE status != 'cancelled'
                GROUP BY month ORDER BY month
            """))
            data = [dict(r._mapping) for r in rows]
        return {
            "labels": [r["month"] for r in data],
            "datasets": [
                {"label": "Revenue", "data": [float(r["revenue"]) for r in data]},
                {"label": "Orders",  "data": [int(r["orders"])  for r in data]},
            ]
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: DAILY REVENUE ────────────────────────────────────────────────

@app.get("/analytics/daily")
def get_daily():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT order_date::TEXT AS day, SUM(total_price) AS revenue
                FROM fact_orders WHERE status != 'cancelled'
                GROUP BY order_date ORDER BY order_date
            """))
            data = [dict(r._mapping) for r in rows]
        return {"labels": [r["day"] for r in data], "data": [float(r["revenue"]) for r in data]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: TOP PRODUCTS BY REVENUE ──────────────────────────────────────

@app.get("/analytics/top-products")
def get_top_products(limit: int = 10):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT p.product_id, p.title, p.category_title, p.product_type,
                       p.average_rating, p.total_ratings, p.total_stars, p.image_url,
                       COALESCE(SUM(i.quantity),0)   AS total_qty,
                       COALESCE(SUM(i.line_total),0) AS total_revenue,
                       COUNT(DISTINCT i.order_id)    AS total_orders
                FROM dim_products p
                LEFT JOIN fact_order_items i ON p.product_id = i.product_id
                LEFT JOIN fact_orders o ON o.order_id = i.order_id AND o.status != 'cancelled'
                GROUP BY p.product_id, p.title, p.category_title, p.product_type, p.average_rating, p.total_ratings, p.total_stars, p.image_url
                ORDER BY total_revenue DESC
                LIMIT :lim
            """), {"lim": limit})
            data = [dict(r._mapping) for r in rows]
        for r in data:
            r["total_qty"]     = int(r["total_qty"])
            r["total_revenue"] = float(r["total_revenue"])
            r["total_orders"]  = int(r["total_orders"])
        return {"products": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: PRODUCTS BY CATEGORY ─────────────────────────────────────────

@app.get("/analytics/products-by-category")
def get_by_category():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT category_title, COUNT(*) AS count
                FROM dim_products GROUP BY category_title ORDER BY count DESC
            """))
            data = [dict(r._mapping) for r in rows]
        return {"labels": [r["category_title"] for r in data], "data": [int(r["count"]) for r in data]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: PRODUCTS BY TYPE ─────────────────────────────────────────────

@app.get("/analytics/products-by-type")
def get_by_type():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT product_type, COUNT(*) AS count
                FROM dim_products GROUP BY product_type ORDER BY count DESC
            """))
            data = [dict(r._mapping) for r in rows]
        TYPE_LABELS = {
            "son": "Son", "sua_rua_mat": "Sua rua mat",
            "kem_chong_nang": "Kem chong nang",
        }
        return {
            "labels": [TYPE_LABELS.get(r["product_type"], r["product_type"]) for r in data],
            "data": [int(r["count"]) for r in data],
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: ORDER STATUS ─────────────────────────────────────────────────

@app.get("/analytics/order-status")
def get_order_status():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT status, COUNT(*) AS count, SUM(total_price) AS revenue
                FROM fact_orders GROUP BY status ORDER BY count DESC
            """))
            data = [dict(r._mapping) for r in rows]
        for r in data:
            r["count"]   = int(r["count"])
            r["revenue"] = float(r["revenue"] or 0)
        return {"statuses": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── ANALYTICS: REVENUE BY CITY ──────────────────────────────────────────────

@app.get("/analytics/revenue-by-city")
def get_revenue_by_city():
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT COALESCE(NULLIF(city,''), 'Unknown') AS city,
                       COUNT(*) AS orders, SUM(total_price) AS revenue
                FROM fact_orders WHERE status != 'cancelled'
                GROUP BY city ORDER BY revenue DESC LIMIT 10
            """))
            data = [dict(r._mapping) for r in rows]
        for r in data:
            r["orders"]  = int(r["orders"])
            r["revenue"] = float(r["revenue"])
        return {"cities": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── PRODUCT SEARCH (ANALYTICAL) ─────────────────────────────────────────────

@app.get("/products/search")
def search_products(
    name: str | None = None,
    product_type: str | None = None,
    category_id: str | None = None,
    min_price: float | None = None,
    max_price: float | None = None,
    sort_by: str = "total_revenue",
    limit: int = 50,
):
    allowed_sorts = {"total_revenue", "total_qty", "total_orders", "title", "average_rating"}
    if sort_by not in allowed_sorts:
        sort_by = "total_revenue"
    try:
        engine = get_engine()
        conditions, params = [], {"limit": limit}
        if name:
            conditions.append("p.title ILIKE :name"); params["name"] = f"%{name}%"
        if product_type:
            conditions.append("p.product_type = :pt"); params["pt"] = product_type
        if category_id:
            conditions.append("p.category_id = :cat"); params["cat"] = category_id
        if min_price is not None:
            conditions.append("p.price >= :min_p"); params["min_p"] = min_price
        if max_price is not None:
            conditions.append("p.price <= :max_p"); params["max_p"] = max_price
        where = ("WHERE " + " AND ".join(conditions)) if conditions else ""
        sql = f"""
            SELECT p.product_id, p.title, p.category_title, p.product_type,
                   p.price, p.average_rating, p.total_ratings, p.total_stars, p.image_url,
                   COALESCE(SUM(i.quantity),0)   AS total_qty,
                   COALESCE(SUM(i.line_total),0) AS total_revenue,
                   COUNT(DISTINCT i.order_id)    AS total_orders
            FROM dim_products p
            LEFT JOIN fact_order_items i ON p.product_id = i.product_id
            {where}
            GROUP BY p.product_id, p.title, p.category_title, p.product_type, p.price, p.average_rating, p.total_ratings, p.total_stars, p.image_url
            ORDER BY {sort_by} DESC LIMIT :limit
        """
        with engine.connect() as conn:
            rows = conn.execute(text(sql), params)
            data = [dict(r._mapping) for r in rows]
        for r in data:
            r["total_qty"]     = int(r["total_qty"])
            r["total_revenue"] = float(r["total_revenue"])
            r["total_orders"]  = int(r["total_orders"])
        return {"count": len(data), "products": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── RECOMMENDATIONS ──────────────────────────────────────────────────────────

@app.get("/recommendations")
def get_recommendations(limit: int = 5):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT p.product_id, p.title, p.category_title, p.product_type,
                       p.price, p.average_rating, p.total_ratings, p.total_stars, p.image_url, p.show_recommend,
                       COALESCE(SUM(i.quantity),0)   AS total_qty,
                       COUNT(DISTINCT i.order_id)    AS total_orders,
                       COALESCE(SUM(i.line_total),0) AS total_revenue,
                       (COALESCE(SUM(i.quantity),0)*2 + COUNT(DISTINCT i.order_id)*3
                        + COALESCE(p.average_rating, 0)*5) AS score
                FROM dim_products p
                LEFT JOIN fact_order_items i ON p.product_id = i.product_id
                GROUP BY p.product_id, p.title, p.category_title, p.product_type,
                         p.price, p.average_rating, p.total_ratings, p.total_stars, p.image_url, p.show_recommend
                ORDER BY score DESC LIMIT :lim
            """), {"lim": limit})
            data = [dict(r._mapping) for r in rows]
        for i, r in enumerate(data):
            r["total_qty"]     = int(r["total_qty"])
            r["total_orders"]  = int(r["total_orders"])
            r["total_revenue"] = float(r["total_revenue"])
            r["score"]         = float(r["score"])
            if i == 0:
                r["reason"] = "San pham ban chay nhat"
            elif r["total_orders"] >= 3:
                r["reason"] = "Duoc dat hang nhieu"
            elif float(r["average_rating"] or 0) >= 4.8:
                r["reason"] = "Danh gia cao nhat"
            else:
                r["reason"] = "Duoc de xuat"
        return {"recommendations": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── CUSTOMERS / USERS ────────────────────────────────────────────────────────

@app.get("/users")
def get_users(limit: int = 100, offset: int = 0):
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT u.*, COUNT(o.order_id) AS total_orders,
                       COALESCE(SUM(o.total_price),0) AS total_spent
                FROM dim_users u
                LEFT JOIN fact_orders o ON u.user_id = o.user_id
                GROUP BY u.user_id ORDER BY total_spent DESC
                LIMIT :lim OFFSET :off
            """), {"lim": limit, "off": offset})
            data = [dict(r._mapping) for r in rows]
            total = conn.execute(text("SELECT COUNT(*) FROM dim_users")).scalar()
        for r in data:
            r["total_orders"] = int(r["total_orders"])
            r["total_spent"]  = float(r["total_spent"])
        return {"count": total, "users": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── AUTOMATION ───────────────────────────────────────────────────────────────

class AutomationRequest(BaseModel):
    action: str = "send-email"
    target: str = ""

@app.post("/automation/send-email")
def automation_send_email(req: AutomationRequest):
    return {
        "status": "success",
        "message": f"Email queued for '{req.target or 'all customers'}'",
        "note": "Connect to n8n webhook for real automation.",
    }

@app.post("/automation/trigger")
def automation_trigger(req: AutomationRequest):
    return {
        "status": "success",
        "message": f"Workflow '{req.action}' triggered",
        "note": "Connect to n8n webhook for real automation.",
    }

# ─── ETL Status & Control ─────────────────────────────────────────────────────

@app.get("/etl/status")
def get_etl_status():
    """Return last ETL run info + recent batch stats for the dashboard."""
    try:
        from etl.sync_meta import get_sync_status
        return get_sync_status()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/etl/history")
def get_etl_history(limit: int = 20):
    """Return recent ETL run history."""
    try:
        engine = get_engine()
        with engine.connect() as conn:
            rows = conn.execute(text("""
                SELECT run_id, started_at, finished_at, duration_seconds,
                       status, orders_synced, products_synced, users_synced,
                       items_synced, error_message, is_full_load
                FROM etl_sync_log
                ORDER BY started_at DESC LIMIT :lim
            """), {"lim": limit})
            data = [dict(r._mapping) for r in rows]
        for r in data:
            r["orders_synced"]   = int(r["orders_synced"] or 0)
            r["products_synced"] = int(r["products_synced"] or 0)
            r["users_synced"]    = int(r["users_synced"] or 0)
            r["items_synced"]    = int(r["items_synced"] or 0)
        return {"runs": data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/etl/trigger")
def trigger_etl(background_tasks: BackgroundTasks, full: bool = False):
    """Manually trigger one ETL batch (runs in background thread)."""
    def _run():
        try:
            from etl.pipeline import run_incremental
            run_incremental(full=full)
        except Exception as e:
            import logging
            logging.getLogger("etl.manual").error(f"Manual trigger failed: {e}")
    background_tasks.add_task(_run)
    return {"status": "queued", "message": f"ETL batch queued (full={full})"}