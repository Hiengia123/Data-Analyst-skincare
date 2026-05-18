# Cấu Trúc Thư Mục Dự Án

Dự án này được tổ chức theo kiểu **Monorepo** — toàn bộ code (ETL, Backend, Frontend) nằm trong một repository được quản lý version control. Đây là lựa chọn lý tưởng cho kỹ sư làm solo hoặc các nhóm nhỏ đang xây dựng ứng dụng dữ liệu full-stack.

```text
skincare_market_intel/
│
├── data/                       # Data Lake cục bộ (bị ignore bởi Git)
│   ├── raw/                    # Dữ liệu thô JSON/Parquet từ scraper
│   └── processed/              # Dữ liệu đã làm sạch, sẵn sàng load vào DB
│
├── etl/                        # Toàn bộ code cho Data Pipeline
│   ├── extract/
│   │   ├── extract_firebase.py # Lấy dữ liệu từ Firebase (listener/batch)
│   │   ├── extract_reddit.py   # Wrapper cho Reddit API dùng PRAW
│   │   └── extract_lazada.py   # Scraper Lazada dùng Playwright
│   ├── transform/
│   │   ├── clean_data.py       # Các hàm làm sạch dữ liệu bằng Pandas
│   │   └── nlp_enrichment.py   # Gọi LLM API (Gemini/OpenAI) để phân tích NLP
│   ├── load/
│   │   └── load_postgres.py    # Upsert dữ liệu vào PostgreSQL bằng SQLAlchemy
│   ├── run_pipeline.py         # Script điều phối chính (orchestrator)
│   └── config.yaml             # API keys, DB URI, các cấu hình cài đặt
│
├── backend/                    # FastAPI — Serving Layer
│   ├── app/
│   │   ├── main.py             # Khởi tạo FastAPI application
│   │   ├── api/
│   │   │   ├── routes_trends.py    # /api/trends/...
│   │   │   ├── routes_products.py  # /api/products/...
│   │   │   └── routes_alerts.py    # Trigger webhook cho n8n
│   │   ├── models/             # Pydantic schema models (request/response)
│   │   └── database.py         # Quản lý connection pool đến DB
│   └── requirements.txt
│
├── frontend/                   # React Dashboard
│   ├── src/
│   │   ├── components/         # UI có thể tái sử dụng (Chart, Table, Card)
│   │   ├── pages/              # Dashboard, ProductDetail, TrendPage
│   │   ├── services/           # Gọi API backend bằng Axios
│   │   └── App.jsx
│   ├── package.json
│   └── vite.config.js
│
├── ml_models/                  # (Giai đoạn 3) Các model Forecasting
│   ├── notebooks/              # Jupyter Notebooks cho training & khám phá dữ liệu
│   └── predictor.py            # Script inference được FastAPI gọi
│
├── docker-compose.yml          # Khởi động DB, Backend, Frontend trên local
├── README.md
└── .gitignore
```

### Các Nguyên Tắc Thiết Kế Cốt Lõi

1.  **Separation of Concerns (Tách biệt trách nhiệm):** ETL không biết API tồn tại. API không biết scraping hoạt động như thế nào. Chúng chỉ giao tiếp với nhau thông qua database PostgreSQL dùng chung.
2.  **Tính Module hóa:** Bạn có thể dễ dàng thêm `extract_tiktok.py` sau này mà không cần chạm vào code của Reddit.
3.  **Cô lập môi trường:** Mỗi thư mục con (etl, backend) có thể có `requirements.txt` riêng nếu cần, mặc dù dùng chung một virtual environment là ổn khi phát triển local.
