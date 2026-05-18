# Social Commerce & Skincare Market Intelligence Platform
## Lộ Trình Tổng Thể Dự Án

Lộ trình này đưa bạn từ một người mới bắt đầu xây dựng batch pipeline cơ bản trở thành một Senior Data Engineer triển khai nền tảng Market Intelligence được hỗ trợ bởi AI.

Dự án được chia thành **3 giai đoạn lớn**. **Không được bỏ qua giai đoạn nào.** Over-engineering (thiết kế quá phức tạp từ đầu) là lý do số 1 khiến các dự án dữ liệu thất bại.

---

### GIAI ĐOẠN 1: MVP (Minimum Viable Pipeline — Pipeline Khả Dụng Tối Thiểu)
**Mục tiêu:** Xây dựng một batch pipeline ổn định, đáng tin cậy và tự động hóa cho dữ liệu có cấu trúc. Chưa cần AI, chưa cần Realtime. Tập trung thiết lập nền tảng cốt lõi trước.

*   **Bước 1.1: Thiết lập môi trường & kiến trúc**
    *   Khởi tạo cấu trúc thư mục theo dạng Monorepo.
    *   Cài đặt và cấu hình PostgreSQL trên máy local.
*   **Bước 1.2: Tích hợp Firebase (Dữ liệu từ ứng dụng)**
    *   Kết nối đến Firebase Realtime Database.
    *   Trích xuất dữ liệu có cấu trúc (người dùng, tương tác sản phẩm cơ bản, danh sách yêu thích).
*   **Bước 1.3: Tích hợp Reddit API (Cơ bản)**
    *   Dùng PRAW (Python Reddit API Wrapper) để lấy các bài viết nổi bật hàng ngày từ `r/SkincareAddiction`, `r/AsianBeauty`.
    *   Lưu dữ liệu thô dưới dạng JSON.
*   **Bước 1.4: Xây dựng ETL Pipeline cốt lõi**
    *   Viết các script Python/Pandas để làm sạch dữ liệu từ Firebase & Reddit.
    *   Load vào Data Warehouse PostgreSQL ban đầu (theo mô hình Star Schema).
*   **Bước 1.5: API & Dashboard MVP**
    *   FastAPI backend phục vụ các chỉ số cơ bản (sản phẩm được thảo luận nhiều nhất, tăng trưởng người dùng).
    *   React Dashboard với các biểu đồ đơn giản.
*   **Bước 1.6: Lên lịch tự động (Scheduling)**
    *   Dùng `APScheduler` hoặc OS Cron để chạy batch ETL tự động mỗi 24 giờ.

**Giá trị kinh doanh:** Chứng minh bạn có thể di chuyển dữ liệu từ các nguồn hiện đại (Firebase, API) vào Data Warehouse và hiển thị trực quan.
**Đánh đổi:** Dữ liệu trễ 24 giờ. Chưa có phân tích văn bản sâu. Chỉ theo dõi "khối lượng" thảo luận, chưa phân tích "cảm xúc" (sentiment).

---

### GIAI ĐOẠN 2: Trung Cấp (Market Intelligence & AI)
**Mục tiêu:** Bổ sung web scraping, NLP được cung cấp bởi LLM, và tự động hóa quy trình.

*   **Bước 2.1: Web Scraping (Dữ liệu E-commerce)**
    *   Xây dựng scraper cho Lazada / Shopee (sử dụng Selenium/Playwright hoặc Scrapy).
    *   Thu thập giá sản phẩm, đánh giá sao, và review cơ bản của các thương hiệu skincare hàng đầu.
*   **Bước 2.2: Tích hợp LLM API (Lớp "AI")**
    *   Tích hợp OpenAI / Gemini API vào bước Transform.
    *   **Workflow:** Đưa comment Reddit thô / review Lazada vào LLM → Nhận về JSON có cấu trúc (Sentiment: Positive/Negative, Pain Points: ["da khô", "giá cao"], Thành phần được đề cập: ["niacinamide"]).
    *   **Kiểm soát chi phí:** Chỉ xử lý 100 comment hàng đầu mỗi ngày. Gửi theo Batch.
*   **Bước 2.3: Mở rộng Data Warehouse**
    *   Cập nhật schema PostgreSQL để chứa `fact_sentiment`, `dim_keywords`, `dim_pain_points`.
*   **Bước 2.4: Tự động hóa với n8n (Cảnh báo)**
    *   Triển khai n8n trên máy local bằng Docker.
    *   Xây dựng workflow: Nếu một sản phẩm đột ngột nhận >50 lượt đề cập tiêu cực, gửi cảnh báo Discord/Telegram cho nhóm "Product Team".
*   **Bước 2.5: Dashboard nâng cao**
    *   Bổ sung Word Cloud, Sentiment Trendline và biểu đồ phân phối Pain Point vào React.

**Giá trị kinh doanh:** Chuyển đổi văn bản thô thành thông tin kinh doanh có thể hành động được. Giúp phản ứng chủ động trước các khủng hoảng truyền thông hoặc xu hướng sản phẩm mới nổi.
**Đánh đổi:** LLM API tốn tiền. Scraper có thể bị lỗi nếu layout website thay đổi. Thời gian xử lý tăng đáng kể.

---

### GIAI ĐOẠN 3: Nâng Cao (Realtime & Forecasting)
**Mục tiêu:** Biến hệ thống thành công cụ dự báo và phản ứng theo thời gian thực. Nâng cấp kiến trúc để xử lý quy mô lớn hơn.

*   **Bước 3.1: ML Forecasting**
    *   Dùng dữ liệu lịch sử để dự báo khối lượng thảo luận hoặc doanh số sản phẩm trong tương lai.
    *   Bắt đầu với `Prophet` cho univariate time-series (ví dụ: dự báo số lần đề cập "Retinol" trong tháng tới).
*   **Bước 3.2: Kiến trúc Realtime Listener**
    *   Chuyển từ batch 24 giờ sang micro-batching (theo giờ) hoặc Realtime thực sự cho một số nguồn cụ thể.
    *   Triển khai Firebase Realtime Listeners để nắm bắt hành động người dùng ngay lập tức.
    *   *Tùy chọn:* Giới thiệu Kafka hoặc RabbitMQ làm message broker để xếp hàng các luồng dữ liệu đến.
*   **Bước 3.3: TikTok Shop / Social Listening**
    *   Tích hợp TikTok scraper hoặc API Social Listening của bên thứ ba (nếu ngân sách cho phép).
    *   Ánh xạ xu hướng TikTok với dữ liệu doanh số E-commerce để chứng minh ROI của viral marketing.
*   **Bước 3.4: Triển khai Production**
    *   Dockerize toàn bộ stack (FastAPI, React, ETL runner).
    *   Deploy lên AWS/GCP (EC2, RDS) hoặc Render/Railway.
    *   Thiết lập CI/CD đúng chuẩn qua GitHub Actions.

**Giá trị kinh doanh:** Phân tích dự báo giúp doanh nghiệp nhập hàng *trước khi* xu hướng đạt đỉnh. Cảnh báo Realtime nắm bắt các khoảnh khắc viral ngay lập tức.
**Đánh đổi:** Độ phức tạp hạ tầng cao. Đòi hỏi monitoring chắc chắn (ví dụ: Prometheus/Grafana) để đảm bảo các listener không bị crash.
