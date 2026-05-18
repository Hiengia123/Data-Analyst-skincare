# Thiết Kế Cơ Sở Dữ Liệu (Data Warehouse)

Chúng ta sẽ dùng mô hình **Star Schema** trong PostgreSQL. Mô hình này tách biệt các thực thể kinh doanh (Dimension) ra khỏi các sự kiện đo lường được (Fact), giúp các truy vấn phân tích chạy nhanh và dễ viết hơn.

## Quy Ước Đặt Tên

*   `dim_*`: Bảng Dimension — thực thể, thay đổi chậm theo thời gian.
*   `fact_*`: Bảng Fact — sự kiện/giao dịch, tăng trưởng nhanh.

---

## 1. Bảng Dimension

### `dim_users`
Người dùng ứng dụng hoặc tác giả trên mạng xã hội.
*   `user_key` (PK, UUID hoặc Auto-increment)
*   `source_platform` (VARCHAR) — 'firebase', 'reddit', 'lazada'
*   `platform_user_id` (VARCHAR) — ID gốc từ nền tảng nguồn
*   `username` (VARCHAR)
*   `created_at` (TIMESTAMP)

### `dim_products`
Các sản phẩm skincare được theo dõi trên tất cả nền tảng.
*   `product_key` (PK)
*   `brand_name` (VARCHAR) — ví dụ: 'COSRX', 'La Roche-Posay'
*   `product_name` (VARCHAR)
*   `category` (VARCHAR) — ví dụ: 'Cleanser', 'Serum', 'Sunscreen'
*   `main_ingredient` (VARCHAR) — ví dụ: 'Niacinamide', 'Salicylic Acid'

### `dim_keywords` (Cho NLP)
Từ khóa được trích xuất, pain point, hoặc thành phần dưỡng chất.
*   `keyword_key` (PK)
*   `keyword` (VARCHAR) — ví dụ: 'pilling', 'white cast', 'hydrating'
*   `category` (VARCHAR) — 'pain_point', 'benefit', 'ingredient'

### `dim_date`
Bảng thời gian chuẩn dùng cho time-series analysis.
*   `date_key` (PK, INT) — ví dụ: 20260516
*   `full_date` (DATE)
*   `day_of_week` (INT)
*   `month` (INT)
*   `year` (INT)
*   `is_weekend` (BOOLEAN)

---

## 2. Bảng Fact

### `fact_ecommerce_sales` (Lazada / Shopee / App)
Theo dõi các chỉ số doanh số hoặc xếp hạng theo ngày.
*   `sale_id` (PK)
*   `date_key` (FK → dim_date)
*   `product_key` (FK → dim_products)
*   `platform` (VARCHAR)
*   `price_at_time` (DECIMAL) — Giá tại thời điểm thu thập
*   `units_sold_estimate` (INT) — Ước tính số đơn vị bán ra
*   `revenue_estimate` (DECIMAL) — Ước tính doanh thu

### `fact_social_mentions`
Bảng cốt lõi cho Social Listening & kết quả NLP.
*   `mention_id` (PK)
*   `date_key` (FK → dim_date)
*   `product_key` (FK → dim_products)
*   `user_key` (FK → dim_users)
*   `source_url` (TEXT) — URL gốc của comment/bài viết
*   `raw_text` (TEXT) — Nội dung comment/bài viết gốc
*   `sentiment_score` (DECIMAL) — -1.0 (Tiêu cực) đến 1.0 (Tích cực)
*   `sentiment_label` (VARCHAR) — 'Positive', 'Neutral', 'Negative'
*   `engagement_metrics` (JSONB) — ví dụ: `{"upvotes": 150, "replies": 12}`

### `fact_mention_keywords` (Bảng Cầu Nối)
Quan hệ nhiều-nhiều giữa social mention và từ khóa/pain point được trích xuất.
*   `mention_id` (FK → fact_social_mentions)
*   `keyword_key` (FK → dim_keywords)
*   *Khóa chính là composite key (mention_id, keyword_key)*

---

## Xử Lý Output NLP: JSONB vs. Bảng Có Cấu Trúc

Khi gửi batch 50 comment Reddit đến LLM và nhận về JSON, xử lý theo 2 lớp:

**Lớp Staging (Thô):**
Lưu kết quả thô từ LLM vào bảng staging dùng kiểu cột `JSONB`.
*   `staging_nlp.raw_llm_response` (JSONB)
*   *Tại sao?* Nếu ETL bị lỗi khi parse JSON, bạn vẫn giữ được kết quả LLM và không phải tốn tiền API để tạo lại.

**Lớp Data Warehouse (Final):**
Trong bước Load, parse JSONB đó và chuẩn hóa vào `fact_social_mentions` (sentiment) và `fact_mention_keywords` (pain points/thành phần).
*   *Tại sao?* Các công cụ BI và truy vấn SQL phân tích chạy nhanh hơn nhiều trên bảng quan hệ có cấu trúc so với JSON lồng nhau.
