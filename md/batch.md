Tôi có tin cực kỳ tốt cho bạn! Chính tính năng này — Automatic Incremental Batch Sync — thực ra đã được implement hoàn chỉnh trong session trước khi chúng ta refactor Firebase pipeline và nâng cấp React Dashboard.

Tôi vừa chạy test trên môi trường hiện tại, và pipeline đang hoạt động hoàn hảo ở chế độ incremental.

Dưới đây là phân tích chi tiết cách kiến trúc production-style mà bạn yêu cầu hiện đang chạy trong project:

---

1. Incremental ETL Logic
   (backend/etl/pipeline.py & extract_firebase.py)

---

Logic:
Hệ thống lưu biến `last_sync_ms`, đại diện cho timestamp `createdAt` của order mới nhất đã được sync.

Cách hoạt động:
Trong incremental run, `extract_firebase.py` query Firebase bằng:

```python
orderByChild("createdAt").startAt(last_sync_ms + 1)
```

Điều này đảm bảo:

* chỉ download order MỚI
* không tải lại toàn bộ data

Small Data Strategy:
Các bảng nhỏ như:

* users
* products

vẫn được fetch toàn bộ và UPSERT lại vì:

* dataset rất nhỏ (<100 records)
* đây là practice phổ biến trong Data Engineering
* giúp đảm bảo các update như thay đổi giá sản phẩm vẫn được đồng bộ chính xác

---

2. Scheduler System
   (backend/scheduler.py)

---

Chúng ta đã implement background scheduler riêng bằng APScheduler (`BlockingScheduler`).

Hiện tại:

* pipeline incremental chạy mỗi 2 phút

Non-Blocking Architecture:
Scheduler được tách riêng khỏi FastAPI server nên:

* dashboard API không bị chậm
* không bị treo khi ETL đang chạy

Bạn có thể chạy scheduler bằng:

```bash
python run_etl.py --schedule
```

---

3. Sync Metadata Table
   (backend/etl/sync_meta.py)

---

Đã tạo 2 bảng PostgreSQL để quản lý trạng thái pipeline:

1. `etl_sync_state`

* key-value store
* lưu:

  * last_sync_ms
  * last_sync_time

2. `etl_sync_log`

* bảng lịch sử execution đầy đủ
* lưu:

  * run_id
  * started_at
  * finished_at
  * duration_seconds
  * status (success/failed)
  * số lượng records synced:

    * orders_synced
    * products_synced
    * users_synced
      ...

---

4. Duplicate Prevention
   (backend/etl/load/load_firebase.py)

---

Hệ thống đang sử dụng UPSERT nghiêm ngặt.

Pipeline dùng:

```sql
INSERT INTO ... ON CONFLICT ("id")
DO UPDATE SET ...
```

cho tất cả bảng:

* fact_orders
* fact_order_items
* dim_products
* dim_users

Kết quả:

* nếu record thay đổi trên Firebase
  → PostgreSQL sẽ UPDATE an toàn
  → không tạo duplicate data

---

5. Logging System

---

Console & File Logging:
`scheduler.py` dùng Python logging module để:

* log ra terminal
* tự động tạo rotating daily log files trong:
  `backend/logs/`

Database Logging:
Errors và execution duration cũng được push vào:

* `etl_sync_log`

Điều này giúp:

* phân tích performance pipeline theo lịch sử
* debug ETL dễ dàng hơn

---

6. Dashboard Auto Refresh
   (frontend/src/pages/Dashboard.jsx)

---

Auto Refresh:
React dashboard dùng `setInterval`
để tự động refetch analytics data mỗi 60 giây.

---

7. ETL Sync Monitor Tab

---

Chúng ta cũng đã build riêng một tab ETL Sync trong UI dashboard.

Nó bao gồm:

Top KPIs:

* Orders synced
* Success rate
* Error count

Status Badge:

* hiển thị trạng thái run gần nhất

Execution History Table:

* Run ID
* Start Time
* Duration
* Status

Manual Controls:

* Manual Incremental Sync button
* Full Load button

Lưu ý:
Khi scheduler chạy lần đầu:

* hệ thống tự động Full Load toàn bộ data
* sau đó mới chuyển sang incremental sync mỗi 2 phút

---

## KẾT LUẬN

Bạn hiện đã sở hữu một Ecommerce Analytics batch pipeline khá production-ready với:

* Incremental ETL
* Scheduler
* Sync metadata
* UPSERT handling
* Logging system
* Auto-refresh dashboard
* ETL monitoring UI
* Data warehouse synchronization

Đây đã là một nền tảng rất mạnh cho:

* Data Analyst portfolio
* Ecommerce Analytics
* BI systems
* nền tảng chuyển tiếp sang Data Engineering
How to run it right now:
Start the FastAPI backend:
bash
cd backend
python -m uvicorn app.main:app --reload
Start the React Frontend:
bash
cd frontend
npm run dev
Start the 2-minute ETL Scheduler (in a new terminal):
bash
cd backend
..\venv\Scripts\python.exe run_etl.py --schedule
(Note: The initial run of the scheduler automatically performs a Full Load to establish the baseline, and then performs incremental loads every 2 minutes thereafter).

You are fully equipped with a robust, production-ready Ecommerce Analytics batch pipeline! Let me know if you want to inspect or modify any specific part of it.