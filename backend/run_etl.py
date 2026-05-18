"""
run_etl.py  —  Firebase → PostgreSQL batch pipeline (CLI entry point)
Usage:
    python run_etl.py              # one incremental run
    python run_etl.py --full       # force full load (resets incremental state)
    python run_etl.py --schedule   # start scheduler (every 2 min, blocking)
    python run_etl.py --init       # create schema + metadata tables only
"""
import sys
import os
import logging

# ── Logging ───────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)],
)
log = logging.getLogger(__name__)

# ── Path fix ──────────────────────────────────────────────────────────────────
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from etl.pipeline import run_incremental
from etl.sync_meta import ensure_meta_schema
from etl.load.load_firebase import get_engine, recreate_schema


def cmd_init():
    """Create DW schema + ETL metadata tables."""
    log.info("Initialising schema ...")
    engine = get_engine()
    recreate_schema(engine)
    ensure_meta_schema()
    log.info("[DONE] Schema + metadata tables created.")


def cmd_run(full: bool = False):
    result = run_incremental(full=full)
    if result.get("status") == "success":
        log.info(f"[DONE] {result}")
    else:
        log.error(f"[FAIL] {result}")
        sys.exit(1)


def cmd_schedule():
    """Start the blocking APScheduler (every 2 min)."""
    from scheduler import etl_job, on_job_executed, on_job_error, INTERVAL_MINUTES
    # pyrefly: ignore [missing-import]
    from apscheduler.schedulers.blocking import BlockingScheduler
    # pyrefly: ignore [missing-import]
    from apscheduler.events import EVENT_JOB_EXECUTED, EVENT_JOB_ERROR

    log.info(f"Starting scheduler — every {INTERVAL_MINUTES} minutes")
    log.info("Running initial load first ...")
    run_incremental(full=True)

    scheduler = BlockingScheduler(timezone="Asia/Ho_Chi_Minh")
    scheduler.add_job(
        etl_job, "interval", minutes=INTERVAL_MINUTES,
        id="firebase_incremental_etl", max_instances=1, misfire_grace_time=60,
    )
    scheduler.add_listener(on_job_executed, EVENT_JOB_EXECUTED)
    scheduler.add_listener(on_job_error,    EVENT_JOB_ERROR)
    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        log.info("Scheduler stopped.")


if __name__ == "__main__":
    args = sys.argv[1:]
    if "--init" in args:
        cmd_init()
    elif "--schedule" in args:
        cmd_schedule()
    else:
        cmd_run(full="--full" in args)
