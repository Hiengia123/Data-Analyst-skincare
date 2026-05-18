"""
Extract users — merges TWO sources:
  1. wp_users        → registered accounts (user_id, email, name, created_at)
  2. wp_wc_orders    → guest checkouts via billing_email (customer_id = 0)

Always full load (dimension table).
"""
import pandas as pd
from sqlalchemy import create_engine, text
import os
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ".env"))

def get_wc_engine():
    return create_engine(os.getenv("WC_DB_URL"))


def _extract_registered_users(engine) -> pd.DataFrame:
    """Source 1: Users with WordPress accounts."""
    query = """
    SELECT 
        ID        AS user_id,
        user_email AS email,
        display_name AS name,
        user_registered AS created_at
    FROM wp_users
    """
    df = pd.read_sql(text(query), engine)
    df["source"] = "account"
    return df


def _extract_guest_emails(engine) -> pd.DataFrame:
    """
    Source 2: Guest checkout emails from wp_wc_orders.
    Only picks emails where customer_id = 0 (no account)
    OR emails that don't match any wp_users.user_email.
    """
    query = """
    SELECT DISTINCT
        billing_email AS email
    FROM wp_wc_orders
    WHERE type = 'shop_order'
      AND billing_email IS NOT NULL
      AND billing_email != ''
      AND customer_id = 0
    """
    df = pd.read_sql(text(query), engine)
    df["source"] = "order"
    return df


def extract_users() -> pd.DataFrame:
    """
    Extract users from BOTH wp_users and wp_wc_orders.
    Returns a combined DataFrame with columns:
        user_id | email | name | created_at | source
    """
    print("   [FULL] Extracting users (accounts + guest emails)...")
    try:
        engine = get_wc_engine()

        # Source 1: registered accounts
        accounts = _extract_registered_users(engine)
        print(f"   Source 1 (accounts): {len(accounts)} users")

        # Source 2: guest checkouts
        guests = _extract_guest_emails(engine)
        print(f"   Source 2 (guest orders): {len(guests)} unique emails")

        return {"accounts": accounts, "guests": guests}

    except Exception as e:
        print(f"   ERROR extracting users: {e}")
        return {"accounts": pd.DataFrame(), "guests": pd.DataFrame()}
