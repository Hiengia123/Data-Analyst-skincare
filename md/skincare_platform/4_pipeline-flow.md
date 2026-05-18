# Luồng ETL & NLP Pipeline

Tài liệu này mô tả chi tiết luồng dữ liệu từ nguồn đến Data Warehouse, làm nổi bật cách tích hợp AI NLP vào pipeline.

## 1. Extract (E — Trích Xuất)

**Scripts:** Các script Python độc lập theo từng nguồn (`extract_reddit.py`, `extract_lazada.py`, `extract_firebase.py`).
**Thực thi:** Được kích hoạt hàng ngày qua APScheduler hoặc OS Cron.
**Các bước thực hiện:**
1.  Kết nối đến API / Scrape website.
2.  Chỉ lấy dữ liệu tăng thêm (incremental) — ví dụ: `created_at > last_run_timestamp`.
3.  Lưu dữ liệu thô dưới dạng JSON hoặc Parquet vào thư mục `data/raw/YYYY-MM-DD/` trên máy local. (Đây đóng vai trò như một Data Lake / bản backup).

## 2. Làm Giàu Dữ Liệu với AI & Transform (T)

Đây là bước phức tạp nhất trong một nền tảng Market Intelligence.

**Script:** `enrich_text_nlp.py`
**Các bước thực hiện:**
1.  Đọc các file text thô mới (comment Reddit, review sản phẩm).
2.  Làm sạch text (xóa thẻ HTML, URL, emoji thừa).
3.  **Batching cho AI:** Nhóm comment thành các batch 20-50 (tùy vào giới hạn context của LLM) để tiết kiệm chi phí API.
4.  **Prompt Engineering:**
    Gửi prompt đến Gemini/OpenAI:
    ```text
    Analyze the following list of skincare product reviews.
    Return a JSON array exactly matching this schema:
    [
      {
        "id": "review_id",
        "sentiment": "Positive|Neutral|Negative",
        "sentiment_score": -1.0 to 1.0,
        "products_mentioned": ["brand name product"],
        "pain_points": ["drying", "expensive", "acne trigger"],
        "ingredients_praised": ["hyaluronic acid"]
      }
    ]
    Reviews:
    [Review Data Inserted Here]
    ```
5.  **Validation:** Parse kết quả từ LLM dùng thư viện `pydantic` hoặc `json`. Xử lý các trường hợp LLM trả về JSON sai format (hallucination).
6.  **Transform DataFrames:** Gộp kết quả JSON từ NLP với Pandas DataFrame gốc.

## 3. Load (L — Nạp Dữ Liệu)

**Script:** `load_warehouse.py`
**Các bước thực hiện:**
1.  Kết nối đến PostgreSQL dùng SQLAlchemy.
2.  **Upsert Dimensions:** Insert người dùng mới, sản phẩm mới, từ khóa mới vào `dim_users`, `dim_products`, `dim_keywords`. Lấy về các `key` đã được tạo.
3.  **Insert Facts:** Map các key vào dữ liệu fact và insert vào `fact_ecommerce_sales`, `fact_social_mentions`, và `fact_mention_keywords`.
4.  Dùng `ON CONFLICT DO UPDATE` (Upsert) để đảm bảo không có bản ghi trùng lặp.

## 4. Serving & Tự Động Hóa (FastAPI + n8n)

1.  FastAPI kết nối đến PostgreSQL.
2.  React Dashboard gọi FastAPI để lấy dữ liệu tại `/api/trends/ingredients` hoặc `/api/products/sentiment`.
3.  **Tích hợp n8n Webhook:**
    *   FastAPI có một background task chạy sau khi ETL hoàn thành.
    *   Background task chạy truy vấn SQL: `SELECT COUNT(*) FROM fact_mention_keywords k JOIN dim_keywords dk ON... WHERE dk.keyword = 'breakout' AND date > yesterday`.
    *   Nếu count > ngưỡng, FastAPI gửi POST request đến n8n webhook URL.
    *   n8n nhận webhook, định dạng thông báo đẹp, và gửi thông báo Discord/Telegram đến nhóm: 🚨 *Tăng đột biến phản hồi tiêu cực về 'breakout' cho Sản phẩm X.*
