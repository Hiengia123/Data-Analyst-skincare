# Lộ Trình Học Tập (NLP & ML Forecasting)

Để xây dựng các tính năng nâng cao của nền tảng này mà không bị choáng ngợp, hãy đi theo lộ trình học tập có cấu trúc dưới đây.

---

## Phần 1: Thành Thạo NLP Qua API (Giai Đoạn Trung Cấp)

Bạn **không cần** học PyTorch hay Transformers để tạo ra giá trị kinh doanh từ dữ liệu văn bản.

1.  **Nền Tảng Prompt Engineering**
    *   *Khái niệm:* Học cách viết prompt ở cấp độ system-level, buộc LLM phải xuất ra JSON có cấu trúc nghiêm ngặt thay vì văn bản hội thoại thông thường.
    *   *Công cụ:* OpenAI API hoặc Google Gemini API.

2.  **Structured Outputs trong Python**
    *   *Khái niệm:* Học `Pydantic`. Đây là tiêu chuẩn ngành để validate JSON được trả về từ API. Hướng dẫn LLM tuân theo một Pydantic schema định sẵn.

3.  **Quản Lý Chi Phí & Rate Limit**
    *   *Khái niệm:* Học cách gửi request theo batch. Học kỹ thuật exponential backoff (dùng thư viện `tenacity`) để xử lý giới hạn tốc độ API một cách uyển chuyển khi xử lý hàng nghìn comment.

---

## Phần 2: ML Forecasting (Giai Đoạn Nâng Cao)

Khi chuyển sang dự báo doanh số hoặc khối lượng xu hướng, hãy tiến dần từ model đơn giản nhất đến phức tạp nhất.

### Cấp 1: Đường Cơ Sở (Bắt Đầu Từ Đây)
*   **Model:** Moving Averages & ARIMA.
*   **Tại sao:** Đây là nền tảng thống kê. Bạn phải hiểu khái niệm Seasonality (tính thời vụ), Trend (xu hướng), và Noise (nhiễu) trước khi dùng AI.
*   **Ứng dụng thực tế:** Làm mượt dữ liệu số lần đề cập trên Reddit theo ngày.

### Cấp 2: Công Cụ Chủ Lực Cho Business
*   **Model:** Meta Prophet (thư viện `prophet`).
*   **Tại sao:** Được thiết kế riêng cho dữ liệu time-series trong kinh doanh. Tự động xử lý missing data, outlier, ngày lễ và cuối tuần. Cực kỳ thân thiện với người mới nhưng đủ mạnh cho production.
*   **Ứng dụng thực tế:** Dự báo tổng doanh số Skincare trong 30 ngày tiếp theo dựa trên dữ liệu lịch sử.

### Cấp 3: Vô Địch Trên Kaggle
*   **Model:** XGBoost (thư viện `xgboost`).
*   **Tại sao:** Là gradient-boosted tree model. Thường là model tốt nhất cho dữ liệu dạng bảng (tabular). Cho phép kết hợp dữ liệu time-series với các external feature (ví dụ: dự báo doanh số dùng *cả* dữ liệu bán hàng lịch sử VÀ điểm sentiment Reddit hiện tại).
*   **Ứng dụng thực tế:** Dự báo nhu cầu sản phẩm có tích hợp chi phí marketing và social sentiment làm feature.

### Cấp 4: Deep Learning (Chỉ Khi Thực Sự Cần)
*   **Model:** LSTM (Long Short-Term Memory) hoặc Time-Series Transformer.
*   **Tại sao tránh:** Cực kỳ phức tạp, đòi hỏi lượng dữ liệu khổng lồ, và dễ bị overfitting.
*   **Kết luận:** Không nên dùng cho đến khi bạn đã chứng minh rằng XGBoost và Prophet không đủ đáp ứng nhu cầu của mình.

---

### Lộ Trình Học ML Có Thể Thực Hiện Ngay

1.  Xem khóa học nhanh 2 giờ về **Time Series Analysis** (hiểu Seasonality & Stationarity).
2.  Đọc **Prophet Quickstart Documentation** (chỉ mất 15 phút để chạy được forecast đầu tiên).
3.  Dùng Jupyter Notebooks trong `ml_models/notebooks/` để train model trên dữ liệu PostgreSQL của mình.
4.  Export model đã train (dùng `joblib` hoặc `pickle`) và load vào FastAPI để phục vụ dự báo cho React frontend.
