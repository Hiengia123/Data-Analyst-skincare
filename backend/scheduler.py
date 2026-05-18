"""
scheduler.py  —  Standalone APScheduler process for incremental ETL
Run independently from FastAPI:

    python scheduler.py

This process:
- Runs the incremental ETL every 2 minutes
- Never blocks the FastAPI server
- Logs every batch to console + etl_sync_log table
- Gracefully handles errors without crashing
"""
import sys
import os
import logging
from datetime import datetime

# ── Make project imports work regardless of CWD ──────────────────────────────
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

# ── Logging: console + rotating file ──────────────────────────────────────────
LOG_DIR = os.path.join(os.path.dirname(__file__), "logs")
os.makedirs(LOG_DIR, exist_ok=True)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s",
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler(
            os.path.join(LOG_DIR, f"etl_{datetime.now().strftime('%Y%m%d')}.log"),
            encoding="utf-8",
        ),
    ],
)
log = logging.getLogger("scheduler")

# ── Import after logging so library logs are captured ─────────────────────────
# pyrefly: ignore [missing-import]
from apscheduler.schedulers.blocking import BlockingScheduler
# pyrefly: ignore [missing-import]
from apscheduler.events import EVENT_JOB_EXECUTED, EVENT_JOB_ERROR

from etl.pipeline import run_incremental

INTERVAL_MINUTES = 2


def etl_job():
    """APScheduler job — runs the incremental pipeline."""
    log.info(f"--- Scheduler triggered ETL job ---")
    result = run_incremental(full=False)
    if result.get("status") == "success":
        log.info(
            f"[OK] Batch done — orders={result.get('orders', 0)} "
            f"items={result.get('items', 0)} "
            f"in {result.get('elapsed', 0):.2f}s"
        )
    else:
        log.error(f"[FAIL] Batch failed — {result.get('error')}")


def on_job_executed(event):
    log.debug(f"Job executed: {event.job_id} at {event.scheduled_run_time}")


def on_job_error(event):
    log.error(f"Job ERROR: {event.job_id} — {event.exception}")


if __name__ == "__main__":
    log.info("=" * 55)
    log.info("  Firebase Incremental ETL Scheduler")
    log.info(f"  Interval : every {INTERVAL_MINUTES} minutes")
    log.info(f"  Log dir  : {LOG_DIR}")
    log.info("=" * 55)

    # ── Run immediately on startup ─────────────────────────────────────────────
    log.info("Running initial full-load on startup ...")
    try:
        result = run_incremental(full=True)
        log.info(f"Initial load done: {result}")
    except Exception as e:
        log.error(f"Initial load FAILED: {e}", exc_info=True)

    # ── Start scheduler ───────────────────────────────────────────────────────
    scheduler = BlockingScheduler(timezone="Asia/Ho_Chi_Minh")
    scheduler.add_job(
        etl_job,
        trigger="interval",
        minutes=INTERVAL_MINUTES,
        id="firebase_incremental_etl",
        name="Firebase Incremental ETL",
        max_instances=1,            # prevent overlapping runs
        misfire_grace_time=60,      # allow up to 60s late start
        replace_existing=True,
    )
    scheduler.add_listener(on_job_executed, EVENT_JOB_EXECUTED)
    scheduler.add_listener(on_job_error,    EVENT_JOB_ERROR)

    log.info(f"Scheduler started. Next run in {INTERVAL_MINUTES} minutes. Press Ctrl+C to stop.")
    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        log.info("Scheduler stopped by user.")
