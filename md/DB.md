WooCommerce
   ↓
ETL (Python)
   ↓
PostgreSQL

Tables:
- product
product (
  product_id,
  name,
  color,
  created_at
)
- user
user (
  user_id,
  name,
  created_at
)
- orders
orders (
  order_id,
  user_id,
  total_amount,
  status,
  created_at
)
- order_items  ⭐ MUST
order_items (
  order_item_id,
  order_id,
  product_id,
  quantity,
  price
)
- product_attributes (optional)
product_attributes (
  product_id,
  color,
  size
)