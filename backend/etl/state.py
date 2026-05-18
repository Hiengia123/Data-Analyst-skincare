"""
ETL State Management — tracks last_run timestamp for incremental loads.
Uses a JSON file stored alongside the ETL scripts.
"""
import json
import os
from datetime import datetime, timezone

STATE_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "last_run.json")

def get_last_run() -> str | None:
    """Return the last_run ISO timestamp, or None if first run."""
    if not os.path.exists(STATE_FILE):
        return None
    try:
        with open(STATE_FILE, "r") as f:
            data = json.load(f)
        return data.get("last_run")
    except Exception:
        return None

def save_last_run(timestamp: datetime | None = None):
    """Persist the current run timestamp."""
    ts = (timestamp or datetime.now(timezone.utc)).strftime("%Y-%m-%dT%H:%M:%S")
    with open(STATE_FILE, "w") as f:
        json.dump({"last_run": ts, "updated_at": datetime.now(timezone.utc).isoformat()}, f, indent=2)
    print(f"   State saved: last_run = {ts}")
