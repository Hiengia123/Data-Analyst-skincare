"""
pipeline.py  —  Incremental ETL Pipeline (core logic, no scheduling)
Called by the scheduler and by run_etl.py CLI.

Flow:
  1. Read last_sync_ms from etl_sync_state
  2. Extract only NEW orders (createdAt > last_sync_ms)
  3. Extract full products + users (small datasets, always sync)
  4. Transform → DataFrames
  5. Upsert into PostgreSQL (ON CONFLICT DO UPDATE)
  6. Update last_sync_ms to max(createdAt) in this batch
  7. Write run log to etl_sync_log
"""
import sys
import os
import uuid
import logging
from datetime import datetime, timezone

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from etl.extract.extract_firebase import (
    extract_items, extract_orders, extract_users,
    extract_product_stats, extract_ratings
)
from etl.transform.transform_firebase import (
    transform_products, transform_users, transform_orders, transform_ratings
)
from etl.load.load_firebase import (
    get_engine, load_products, load_users, load_orders, load_ratings
)
from etl.sync_meta import (
    ensure_meta_schema,
    get_last_sync_ms, save_last_sync_ms,
    log_run_start, log_run_success, log_run_failure,
)

log = logging.getLogger("etl.pipeline")


def run_incremental(full: bool = False) -> dict:
    """
    Execute one ETL batch.

    Args:
        full: if True, ignore last_sync_ms and pull everything.

    Returns:
        dict with sync statistics.
    """
    run_id   = str(uuid.uuid4())[:12]
    started  = datetime.now(timezone.utc)
    stats    = {"orders": 0, "products": 0, "users": 0, "items": 0}
    last_order_ts_ms = None

    log.info("=" * 50)
    log.info(f"[RUN {run_id}] Incremental ETL started  {'(FULL LOAD)' if full else ''}")
    log.info("=" * 50)

    # ── Ensure metadata schema exists ─────────────────────────────────────────
    try:
        ensure_meta_schema()
    except Exception as e:
        log.warning(f"Could not create meta schema: {e}")

    # ── Log run start ─────────────────────────────────────────────────────────
    try:
        log_run_start(run_id, is_full=full)
    except Exception as e:
        log.warning(f"Could not log run start: {e}")

    try:
        # ── Determine incremental cutoff ──────────────────────────────────────
        last_sync_ms = None if full else get_last_sync_ms()
        if last_sync_ms:
            cutoff_dt = datetime.fromtimestamp(last_sync_ms / 1000, tz=timezone.utc)
            log.info(f"Incremental mode: fetching orders after {cutoff_dt.isoformat()}")
        else:
            log.info("Full load mode: fetching all orders")

        # ── EXTRACT ───────────────────────────────────────────────────────────
        log.info("EXTRACT: Firebase Realtime DB ...")
        orders_raw  = extract_orders(last_sync_ms=last_sync_ms)
        items_raw   = extract_items()     # always full — 15 products
        users_raw   = extract_users()     # always full — small set
        stats_raw   = extract_product_stats()
        ratings_raw = extract_ratings()

        log.info(
            f"  Fetched: {len(orders_raw)} orders | "
            f"{len(items_raw)} products | {len(users_raw)} users"
        )

        # ── TRANSFORM ─────────────────────────────────────────────────────────
        log.info("TRANSFORM: cleaning DataFrames ...")
        products_df         = transform_products(items_raw, stats_raw)
        users_df            = transform_users(users_raw)
        orders_df, items_df = transform_orders(orders_raw)
        ratings_df          = transform_ratings(ratings_raw)

        # ── Compute new last_sync_ms (max createdAt in batch) ─────────────────
        if not orders_df.empty and "created_at" in orders_df.columns:
            max_ts = orders_df["created_at"].max()
            if max_ts is not None and str(max_ts) != "NaT":
                # Convert back to ms for Firebase incremental queries
                last_order_ts_ms = int(max_ts.timestamp() * 1000)

        # ── LOAD ──────────────────────────────────────────────────────────────
        log.info("LOAD: upserting into PostgreSQL ...")
        engine = get_engine()

        load_users(users_df, engine)
        load_products(products_df, engine)
        load_orders(orders_df, items_df, engine)
        load_ratings(ratings_df, engine)

        # ── Update stats ──────────────────────────────────────────────────────
        stats = {
            "orders":   len(orders_df),
            "products": len(products_df),
            "users":    len(users_df),
            "items":    len(items_df),
        }

        # ── Persist new last_sync_ms ──────────────────────────────────────────
        if last_order_ts_ms:
            save_last_sync_ms(last_order_ts_ms)
            log.info(f"  State saved: last_sync_ms = {last_order_ts_ms}")
        elif full or last_sync_ms is None:
            # First run completed — save current time as baseline
            save_last_sync_ms(int(started.timestamp() * 1000))

        # ── Log success ───────────────────────────────────────────────────────
        try:
            log_run_success(run_id, stats, last_order_ts_ms)
        except Exception as e:
            log.warning(f"Could not log run success: {e}")

        elapsed = (datetime.now(timezone.utc) - started).total_seconds()
        log.info(
            f"[RUN {run_id}] DONE in {elapsed:.2f}s — "
            f"orders={stats['orders']} products={stats['products']} "
            f"users={stats['users']} items={stats['items']}"
        )
        return {"run_id": run_id, "status": "success", "elapsed": elapsed, **stats}

    except Exception as exc:
        log.error(f"[RUN {run_id}] FAILED: {exc}", exc_info=True)
        try:
            log_run_failure(run_id, str(exc))
        except Exception:
            pass
        return {"run_id": run_id, "status": "failed", "error": str(exc)}
