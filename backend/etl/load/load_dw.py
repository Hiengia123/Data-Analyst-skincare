"""
Data Warehouse loader with UPSERT (INSERT ... ON CONFLICT DO UPDATE)
to handle incremental loads and prevent duplicate records.
"""
from sqlalchemy import create_engine, text, inspect
import pandas as pd
import os
from dotenv import load_dotenv

load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), ".env"))

def get_dw_engine():
    return create_engine(os.getenv("DW_DB_URL"))


# ─── Primary key definitions ────────────────────────────────────────
TABLE_PK = {
    "fact_orders":      "order_id",
    "fact_order_items": "order_item_id",
    "dim_products":     "product_id",
    "dim_users":        "user_id",
}


def _ensure_table(engine, table_name: str, df: pd.DataFrame, pk: str):
    """Create the table if it does not exist; add PK if missing."""
    inspector = inspect(engine)

    if not inspector.has_table(table_name):
        # Create from scratch
        df.head(0).to_sql(table_name, engine, if_exists="fail", index=False)
        with engine.connect() as conn:
            conn.execute(text(f'ALTER TABLE {table_name} ADD PRIMARY KEY ("{pk}")'))
            conn.commit()
        print(f"   Created table '{table_name}' with PK on '{pk}'")
        return

    # Table exists — check if PK constraint is present
    pk_info = inspector.get_pk_constraint(table_name)
    if not pk_info or not pk_info.get("constrained_columns"):
        # No PK exists — need to deduplicate then add constraint
        print(f"   Adding PK '{pk}' to existing table '{table_name}'...")
        with engine.connect() as conn:
            # Remove duplicate rows keeping the latest
            conn.execute(text(f"""
                DELETE FROM {table_name} a
                USING {table_name} b
                WHERE a.ctid < b.ctid
                  AND a."{pk}" = b."{pk}"
            """))
            conn.execute(text(f'ALTER TABLE {table_name} ADD PRIMARY KEY ("{pk}")'))
            conn.commit()
        print(f"   PK added to '{table_name}'")

    # Schema evolution — add missing columns
    existing_cols = {col["name"] for col in inspector.get_columns(table_name)}
    new_cols = set(df.columns) - existing_cols
    if new_cols:
        dtype_map = {
            "int64": "BIGINT", "int32": "INTEGER", "float64": "DOUBLE PRECISION",
            "object": "TEXT", "datetime64[ns]": "TIMESTAMP", "bool": "BOOLEAN",
        }
        with engine.connect() as conn:
            for col in new_cols:
                pg_type = dtype_map.get(str(df[col].dtype), "TEXT")
                conn.execute(text(f'ALTER TABLE {table_name} ADD COLUMN "{col}" {pg_type}'))
                print(f"   Added column '{col}' ({pg_type}) to '{table_name}'")
            conn.commit()


def _upsert_rows(engine, table_name: str, df: pd.DataFrame, pk: str):
    """
    INSERT rows; on conflict with the primary key, UPDATE all non-PK columns.
    PostgreSQL-specific ON CONFLICT DO UPDATE.
    """
    cols = list(df.columns)
    non_pk = [c for c in cols if c != pk]
    col_list    = ", ".join([f'"{c}"' for c in cols])
    val_list    = ", ".join([f":{c}" for c in cols])
    update_list = ", ".join([f'"{c}" = EXCLUDED."{c}"' for c in non_pk])

    sql = f"""
        INSERT INTO {table_name} ({col_list})
        VALUES ({val_list})
        ON CONFLICT ("{pk}") DO UPDATE SET {update_list}
    """
    stmt = text(sql)

    rows = df.to_dict(orient="records")
    with engine.connect() as conn:
        conn.execute(stmt, rows)
        conn.commit()


def load_table(table_name: str, df: pd.DataFrame, mode: str = "upsert"):
    """
    Load a single dataframe into the DW.
    mode: 'upsert' (incremental-safe) or 'replace' (full reload).
    """
    if df.empty:
        print(f"   SKIP  '{table_name}' — empty dataframe")
        return 0

    engine = get_dw_engine()
    pk = TABLE_PK.get(table_name)

    if mode == "replace" or pk is None:
        df.to_sql(table_name, engine, if_exists="replace", index=False)
        print(f"   REPLACE  '{table_name}' -> {len(df)} rows")
    else:
        _ensure_table(engine, table_name, df, pk)
        _upsert_rows(engine, table_name, df, pk)
        print(f"   UPSERT   '{table_name}' -> {len(df)} rows (PK={pk})")

    return len(df)


def load_all(tables: dict, mode: str = "upsert") -> dict:
    """
    Load multiple tables into the Data Warehouse.
    Returns a dict of {table_name: rows_loaded}.
    """
    print("-- LOAD PHASE -----------------------------------------------")
    stats = {}
    for table_name, df in tables.items():
        try:
            stats[table_name] = load_table(table_name, df, mode=mode)
        except Exception as e:
            print(f"   ERROR loading '{table_name}': {e}")
            stats[table_name] = -1
    return stats
