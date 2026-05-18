"""
sync_meta.py  —  PostgreSQL Sync Metadata Manager
Tracks every ETL batch run in a dedicated table.
"""
import os
from datetime import datetime, timezone
from sqlalchemy import create_engine, text
from dotenv import load_dotenv

load_dotenv(os.path.join(os.path.dirname(__file__), "..", ".env"))

_DDL_META = """
CREATE TABLE IF NOT EXISTS etl_sync_log (
    id                  SERIAL PRIMARY KEY,
    run_id              TEXT NOT NULL,           -- unique run identifier
    started_at          TIMESTAMPTZ NOT NULL,
    finished_at         TIMESTAMPTZ,
    duration_seconds    NUMERIC(8,2),
    status              TEXT NOT NULL DEFAULT 'running',  -- running | success | failed
    orders_synced       INTEGER DEFAULT 0,
    products_synced     INTEGER DEFAULT 0,
    users_synced        INTEGER DEFAULT 0,
    items_synced        INTEGER DEFAULT 0,
    last_order_ts_ms    BIGINT,                  -- last createdAt ms seen (for incremental)
    error_message       TEXT,
    is_full_load        BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS etl_sync_state (
    key     TEXT PRIMARY KEY,
    value   TEXT NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed state keys if not present
INSERT INTO etl_sync_state (key, value) VALUES
    ('last_sync_ms',   '0'),
    ('last_sync_time', '1970-01-01T00:00:00')
ON CONFLICT (key) DO NOTHING;
"""


def _get_engine():
    url = os.getenv("DW_DB_URL")
    if not url:
        raise RuntimeError("DW_DB_URL not set in .env")
    return create_engine(url)


def ensure_meta_schema():
    """Create etl_sync_log and etl_sync_state tables if they don't exist."""
    engine = _get_engine()
    with engine.begin() as conn:
        conn.execute(text(_DDL_META))


# ─── Sync State (last_sync_ms) ─────────────────────────────────────────────────

def get_last_sync_ms() -> int | None:
    """
    Return the last synced Firebase createdAt ms timestamp.
    Returns None if this is the first run (forces full load).
    """
    try:
        engine = _get_engine()
        with engine.connect() as conn:
            row = conn.execute(
                text("SELECT value FROM etl_sync_state WHERE key = 'last_sync_ms'")
            ).fetchone()
            if row:
                val = int(row[0])
                return val if val > 0 else None
    except Exception:
        pass
    return None


def save_last_sync_ms(ms: int):
    """Persist the most recent order createdAt ms timestamp."""
    engine = _get_engine()
    with engine.begin() as conn:
        conn.execute(text("""
            INSERT INTO etl_sync_state (key, value, updated_at)
            VALUES ('last_sync_ms', :val, NOW())
            ON CONFLICT (key) DO UPDATE SET value = :val, updated_at = NOW()
        """), {"val": str(ms)})
        conn.execute(text("""
            INSERT INTO etl_sync_state (key, value, updated_at)
            VALUES ('last_sync_time', :val, NOW())
            ON CONFLICT (key) DO UPDATE SET value = :val, updated_at = NOW()
        """), {"val": datetime.now(timezone.utc).isoformat()})


# ─── Run Log ──────────────────────────────────────────────────────────────────

def log_run_start(run_id: str, is_full: bool = False) -> None:
    engine = _get_engine()
    with engine.begin() as conn:
        conn.execute(text("""
            INSERT INTO etl_sync_log (run_id, started_at, status, is_full_load)
            VALUES (:rid, NOW(), 'running', :full)
        """), {"rid": run_id, "full": is_full})


def log_run_success(run_id: str, stats: dict, last_order_ts_ms: int | None):
    """
    stats: { orders, products, users, items }
    """
    engine = _get_engine()
    with engine.begin() as conn:
        conn.execute(text("""
            UPDATE etl_sync_log SET
                finished_at        = NOW(),
                duration_seconds   = EXTRACT(EPOCH FROM (NOW() - started_at)),
                status             = 'success',
                orders_synced      = :orders,
                products_synced    = :products,
                users_synced       = :users,
                items_synced       = :items,
                last_order_ts_ms   = :last_ts
            WHERE run_id = :rid
        """), {
            "rid":      run_id,
            "orders":   stats.get("orders",   0),
            "products": stats.get("products", 0),
            "users":    stats.get("users",    0),
            "items":    stats.get("items",    0),
            "last_ts":  last_order_ts_ms,
        })


def log_run_failure(run_id: str, error: str):
    engine = _get_engine()
    with engine.begin() as conn:
        conn.execute(text("""
            UPDATE etl_sync_log SET
                finished_at      = NOW(),
                duration_seconds = EXTRACT(EPOCH FROM (NOW() - started_at)),
                status           = 'failed',
                error_message    = :err
            WHERE run_id = :rid
        """), {"rid": run_id, "err": error[:2000]})


# ─── Status Query (for FastAPI /etl/status endpoint) ──────────────────────────

def get_sync_status() -> dict:
    """Return the most recent sync log entry + state."""
    try:
        engine = _get_engine()
        with engine.connect() as conn:
            last = conn.execute(text("""
                SELECT run_id, started_at, finished_at, duration_seconds,
                       status, orders_synced, products_synced, users_synced,
                       items_synced, error_message, is_full_load
                FROM etl_sync_log
                ORDER BY started_at DESC LIMIT 1
            """)).fetchone()

            recent = conn.execute(text("""
                SELECT status, COUNT(*) as cnt
                FROM etl_sync_log
                WHERE started_at > NOW() - INTERVAL '1 hour'
                GROUP BY status
            """)).fetchall()

            state = conn.execute(text(
                "SELECT key, value FROM etl_sync_state"
            )).fetchall()

        state_dict  = {r[0]: r[1] for r in state} if state else {}
        recent_dict = {r[0]: int(r[1]) for r in recent} if recent else {}

        return {
            "last_run": dict(last._mapping) if last else None,
            "last_sync_time": state_dict.get("last_sync_time"),
            "last_sync_ms":   int(state_dict.get("last_sync_ms", 0)),
            "recent_1h":      recent_dict,
        }
    except Exception as e:
        return {"error": str(e)}
