"""
extract_firebase.py  —  Firebase Realtime DB extractor (incremental-aware)
Supports full-load and incremental extraction via last_sync_ms timestamp.
"""
import os
# pyrefly: ignore [missing-import]
import firebase_admin
# pyrefly: ignore [missing-import]
from firebase_admin import credentials, db

_SA_KEY = os.path.normpath(os.path.join(
    os.path.dirname(__file__),
    "..", "..", "..",
    "json realtime db",
    "nhung-group-firebase-adminsdk-fbsvc-9542acc298.json",
))
_DB_URL = "https://nhung-group-default-rtdb.asia-southeast1.firebasedatabase.app/"

_app = None


def _init_firebase():
    global _app
    if _app is None:
        cred = credentials.Certificate(_SA_KEY)
        _app = firebase_admin.initialize_app(cred, {"databaseURL": _DB_URL})


# ─── Full extractions ──────────────────────────────────────────────────────────

def extract_items() -> dict:
    """Full /items node — products rarely change so always full."""
    _init_firebase()
    return db.reference("/items").get() or {}


def extract_users() -> dict:
    """Full /users node — user count is small."""
    _init_firebase()
    return db.reference("/users").get() or {}


def extract_categories() -> dict:
    _init_firebase()
    return db.reference("/categories").get() or {}


def extract_product_stats() -> dict:
    _init_firebase()
    return db.reference("/productStats").get() or {}


# ─── Incremental order extraction ─────────────────────────────────────────────

def extract_orders(last_sync_ms: int | None = None) -> dict:
    """
    Return orders from /orders.

    If last_sync_ms is provided, only returns orders whose
    createdAt timestamp (milliseconds) is > last_sync_ms.

    Firebase RTDB doesn't support server-side range queries on arbitrary
    fields without indexes, but our orders are indexed on 'createdAt'
    via rules.json.  We use orderByChild + startAt for efficiency.

    Falls back to full fetch if last_sync_ms is None (first run).
    """
    _init_firebase()
    ref = db.reference("/orders")

    if last_sync_ms is None:
        # First run: full extract
        return ref.get() or {}

    # Incremental: only fetch orders newer than last sync
    # Firebase orderByChild requires an index — see rules.json: .indexOn "createdAt"
    try:
        result = (
            ref.order_by_child("createdAt")
               .start_at(last_sync_ms + 1)
               .get()
        ) or {}
        return result
    except Exception:
        # Fallback: full extract (safe but slower)
        all_orders = ref.get() or {}
        return {
            oid: o for oid, o in all_orders.items()
            if (o.get("createdAt") or 0) > last_sync_ms
        }


def extract_ratings() -> dict:
    """Full /ratings node."""
    _init_firebase()
    return db.reference("/ratings").get() or {}
