# Kiến Trúc Hệ Thống

Tài liệu này mô tả kiến trúc kỹ thuật cho nền tảng Social Commerce & Skincare Market Intelligence.

## Sơ Đồ Kiến Trúc Tổng Quan

```text
[ NGUỒN DỮ LIỆU ]
      |
      ├── Firebase (Dữ liệu App)   --[Listener/Batch]-->
      ├── Reddit API (Mạng xã hội) --[API Fetch]------->  [ EXTRACT LAYER ] (Python)
      ├── Lazada (E-commerce)      --[Scraper]--------->    (Raw JSON / Parquet)
      └── TikTok (Social/TM Điện Tử) --[Scraper]------->
                                                               |
                                                               v
[ LÀM GIÀU DỮ LIỆU VỚI AI ]                          [ TRANSFORM LAYER ] (Python/Pandas)
      |                                                        |
      ├── Gemini / OpenAI API  <--[Batch Prompts]-------------|  (Làm sạch, Deduplication)
      |   (Trích xuất Sentiment, Pain Points, Keywords)       |  (Chuẩn hóa cấu trúc)
      |                                                        |
                                                               v
                                                          [ LOAD LAYER ]
                                                               |
                                                               v
[ DATA WAREHOUSE ]                                      [ PostgreSQL ]
      |                                                 Schema: Star Schema
      |                                                 (Facts & Dimensions)
      v
[ SERVING LAYER ]                                       [ FastAPI Backend ]
      |                                                  (Endpoints, ML Model inference)
      |                                                        |
      v                                                        v
[ TIÊU THỤ DỮ LIỆU ]      [ React Dashboard ]          [ n8n Automation Engine ]
                            (Trực quan hóa)              (Cảnh báo -> Discord/Telegram)
```

---

## Tech Stack & Lý Do Lựa Chọn

### 1. Extract & Transform
*   **Python 3.10+**: Tiêu chuẩn ngành cho data engineering, web scraping và NLP. Có hệ sinh thái thư viện lớn nhất.
*   **Pandas / Polars**: Xử lý và biến đổi dữ liệu nhanh, hiệu quả về bộ nhớ. Polars phù hợp khi dữ liệu lớn hơn.
*   **BeautifulSoup / Playwright**: Playwright được ưu tiên cho scraping hiện đại (Lazada/TikTok) vì xử lý tốt các trang render bằng JavaScript và có khả năng vượt qua bảo vệ bot tốt hơn Selenium.
*   **PRAW**: Thư viện Python chính thức cho Reddit API, ổn định và tự động xử lý rate-limiting.

### 2. Lớp AI / NLP
*   **LLM API (Gemini / OpenAI / Claude)**:
    *   *Tại sao không tự huấn luyện model?* Huấn luyện BERT/RoBERTa cho Sentiment Analysis đòi hỏi tài nguyên tính toán khổng lồ, bộ dữ liệu đã gán nhãn và công sức bảo trì liên tục. LLM cung cấp khả năng phân loại zero-shot ngay lập tức với độ chính xác cao.
    *   *Cách tiếp cận:* Gửi các batch comment đến LLM kèm theo prompt yêu cầu schema JSON nghiêm ngặt (ví dụ: "Phân tích 50 review này. Trả về JSON array gồm sentiment, 2 pain point chính, và sản phẩm được đề cập").

### 3. Data Warehouse
*   **PostgreSQL**:
    *   *Tại sao?* Cực kỳ ổn định và đáng tin cậy. Hỗ trợ cột JSONB (hoàn hảo để lưu output NLP linh hoạt trước khi chuẩn hóa hoàn toàn). Xử lý dễ dàng hàng chục gigabyte dữ liệu phân tích. Đây là bước đệm lý tưởng trước khi cần chuyển sang Snowflake hoặc BigQuery.

### 4. Serving & Backend
*   **FastAPI**:
    *   *Tại sao?* Hiệu năng cao, hỗ trợ async gốc (lý tưởng cho các truy vấn database đồng thời), tự động tạo tài liệu Swagger. Vì dùng Python nên có thể tích hợp trực tiếp ML model vào API sau này mà không cần đổi ngôn ngữ.

### 5. Frontend
*   **React + Vite**: Thời gian build nhanh, hệ sinh thái component phong phú.
*   **Chart.js / Recharts**: Render trendline, biểu đồ tròn sentiment, và word cloud.

### 6. Tự Động Hóa Workflow
*   **n8n**:
    *   *Tại sao?* Công cụ mã nguồn mở, xây dựng workflow bằng giao diện kéo thả (node-based). Tốt hơn nhiều so với việc viết thủ công các script Python cho việc gửi email, gửi tin nhắn Discord, hoặc gọi API bên ngoài.
    *   *Vị trí trong kiến trúc:* Đóng vai trò là lớp "hành động". FastAPI phát hiện bất thường → Kích hoạt n8n Webhook → n8n định dạng và gửi thông báo đến Slack/Discord.

---

## Chiến Lược: Batch vs. Realtime

Đối với Market Intelligence, **Batch processing thường là đủ và rẻ hơn nhiều** so với Realtime.

*   **Chiến lược hiện tại (Micro-batch / Daily Batch):** Chạy scraper và gọi API mỗi 6, 12 hoặc 24 giờ. Gửi dữ liệu mới đến LLM theo lô (bulk).
*   **Tại sao chưa cần Realtime?**
    1.  Xu hướng mạng xã hội (ví dụ: một thành phần skincare bùng nổ viral) mất nhiều ngày để hình thành, không phải vài milli giây.
    2.  Gọi LLM API theo Realtime cho từng comment một cực kỳ tốn kém và chậm.
    3.  Realtime yêu cầu hạ tầng phức tạp (Kafka / Spark Streaming).

*Ngoại lệ:* Dữ liệu từ Firebase App có thể dùng Realtime Listener để đồng bộ *trạng thái ứng dụng*, nhưng việc phân tích dữ liệu đó vẫn có thể thực hiện theo batch.
