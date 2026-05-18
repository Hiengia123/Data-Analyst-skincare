"""
WooCommerce Batch ETL Pipeline
================================
- Supports FULL and INCREMENTAL modes
- Tracks state via last_run.json
- Handles duplicates via PostgreSQL UPSERT
- Scheduled every 24 hours via APScheduler
- Full logging with timing

Usage:
    python run_etl.py              # One-shot run (auto-detects full vs incremental)
    python run_etl.py --full       # Force full reload
    python run_etl.py --schedule   # Run once then repeat every 24h
"""
import sys
import os
import argparse
from datetime import datetime, timezone

# Add backend/ to sys.path
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from etl.extract.extract_orders import extract_orders
from etl.extract.extract_order_items import extract_order_items
from etl.extract.extract_products import extract_products
from etl.extract.extract_users import extract_users

from etl.transform.transform_orders import transform_orders
from etl.transform.transform_order_items import transform_order_items
from etl.transform.transform_products import transform_products
from etl.transform.transform_users import transform_users

from etl.load.load_dw import load_all
from etl.state import get_last_run, save_last_run


# ─── Pipeline ───────────────────────────────────────────────────────

def run_pipeline(force_full: bool = False):
    """Execute one ETL cycle."""
    run_start = datetime.now(timezone.utc)
    last_run = get_last_run()
    is_incremental = (last_run is not None) and (not force_full)

    print("=" * 60)
    print(f"  ETL RUN  |  {run_start.strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"  MODE     |  {'INCREMENTAL (since ' + last_run + ')' if is_incremental else 'FULL RELOAD'}")
    print("=" * 60)

    row_stats = {}

    # ── 1. EXTRACT ─────────────────────────────────────────────────
    print("\n-- EXTRACT PHASE --------------------------------------------")

    since = last_run if is_incremental else None
    orders = extract_orders(since=since)

    # For incremental order items: only fetch items for new orders
    new_order_ids = list(orders["order_id"]) if not orders.empty else None
    if is_incremental and new_order_ids:
        order_items = extract_order_items(order_ids=new_order_ids)
    else:
        order_items = extract_order_items()

    # Dimension tables: always full load (small tables, may change)
    products = extract_products()
    users = extract_users()

    # ── 2. TRANSFORM ───────────────────────────────────────────────
    print("\n-- TRANSFORM PHASE ------------------------------------------")

    orders_clean = transform_orders(orders)
    order_items_clean = transform_order_items(order_items)
    products_clean = transform_products(products)
    users_clean = transform_users(users)

    # ── 3. LOAD ────────────────────────────────────────────────────
    # Fact tables: UPSERT (handles incremental + dedup)
    # Dim tables: REPLACE (small, always full refresh)
    tables = {
        "fact_orders": orders_clean,
        "fact_order_items": order_items_clean,
    }
    dim_tables = {
        "dim_products": products_clean,
        "dim_users": users_clean,
    }

    print()
    fact_stats = load_all(tables, mode="upsert")
    dim_stats = load_all(dim_tables, mode="replace")
    row_stats = {**fact_stats, **dim_stats}

    # ── 4. SAVE STATE ──────────────────────────────────────────────
    save_last_run(run_start)

    # ── 5. SUMMARY ─────────────────────────────────────────────────
    run_end = datetime.now(timezone.utc)
    elapsed = (run_end - run_start).total_seconds()

    print("\n" + "=" * 60)
    print("  ETL RUN COMPLETE")
    print(f"  Started  : {run_start.strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"  Finished : {run_end.strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"  Duration : {elapsed:.1f}s")
    print(f"  Rows loaded:")
    for tbl, cnt in row_stats.items():
        status = f"{cnt} rows" if cnt >= 0 else "FAILED"
        print(f"    {tbl:24s} {status}")
    print("=" * 60 + "\n")


# ─── Scheduler ──────────────────────────────────────────────────────

def start_scheduler():
    """Run the pipeline immediately, then repeat every 24 hours."""
    # pyrefly: ignore [missing-import]
    from apscheduler.schedulers.blocking import BlockingScheduler

    print("[SCHEDULER] Starting — pipeline will run every 24 hours.")
    print("[SCHEDULER] Press Ctrl+C to stop.\n")

    # Run immediately on startup
    run_pipeline()

    scheduler = BlockingScheduler()
    scheduler.add_job(run_pipeline, "interval", hours=24, id="etl_daily")

    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        print("\n[SCHEDULER] Stopped.")


# ─── CLI entry ──────────────────────────────────────────────────────

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="WooCommerce Batch ETL Pipeline")
    parser.add_argument("--full", action="store_true", help="Force full reload (ignore last_run)")
    parser.add_argument("--schedule", action="store_true", help="Run with 24h APScheduler loop")
    args = parser.parse_args()

    if args.schedule:
        start_scheduler()
    else:
        run_pipeline(force_full=args.full)