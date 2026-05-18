"""
Transform users — merge accounts + guest emails, deduplicate by email.

Output schema:
    user_id    INT     (auto-generated for guests: negative IDs)
    email      TEXT    (lowercase, trimmed)
    name       TEXT
    created_at TIMESTAMP
    source     TEXT    ('account' or 'order')
"""
import pandas as pd


def transform_users(raw: dict) -> pd.DataFrame:
    """
    Merge registered accounts and guest emails into a single dim_users table.
    - Accounts keep their original user_id
    - Guests get auto-generated negative IDs (to avoid collisions)
    - Deduplication by email (accounts win over guests)
    """
    print("   Transforming users...")
    try:
        accounts = raw.get("accounts", pd.DataFrame())
        guests = raw.get("guests", pd.DataFrame())

        # ── Clean accounts ──────────────────────────────────────────
        if not accounts.empty:
            accounts["user_id"] = pd.to_numeric(accounts["user_id"], errors="coerce").fillna(0).astype(int)
            accounts["email"] = accounts["email"].astype(str).str.strip().str.lower()
            accounts["name"] = accounts["name"].astype(str)
            accounts["created_at"] = pd.to_datetime(accounts["created_at"], errors="coerce")
            accounts["source"] = "account"

        # ── Clean guests ────────────────────────────────────────────
        if not guests.empty:
            guests["email"] = guests["email"].astype(str).str.strip().str.lower()

            # Remove guest emails that already exist in accounts
            if not accounts.empty:
                account_emails = set(accounts["email"].tolist())
                guests = guests[~guests["email"].isin(account_emails)]

            if not guests.empty:
                # Generate negative IDs so they never clash with real user_ids
                guests["user_id"] = range(-1, -len(guests) - 1, -1)
                guests["name"] = "Guest"
                guests["created_at"] = pd.NaT
                guests["source"] = "order"

        # ── Combine ─────────────────────────────────────────────────
        combined = pd.concat([accounts, guests], ignore_index=True)

        # Drop rows with empty/null emails
        combined = combined[combined["email"].notna() & (combined["email"] != "") & (combined["email"] != "nan")]

        # Final dedup by email (keep account over guest)
        combined = combined.sort_values("source")  # 'account' sorts before 'order'
        combined = combined.drop_duplicates(subset=["email"], keep="first")
        combined = combined.reset_index(drop=True)

        # Ensure column order
        combined = combined[["user_id", "email", "name", "created_at", "source"]]

        print(f"   Transformed {len(combined)} users ({len(combined[combined['source']=='account'])} accounts, {len(combined[combined['source']=='order'])} guests)")
        return combined

    except Exception as e:
        print(f"   ERROR transforming users: {e}")
        return pd.DataFrame()
