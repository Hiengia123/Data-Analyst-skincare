PostgreSQL
    ↓
train.py
    ↓
saved model
    ↓
FastAPI /forecast
    ↓
React chart

## **1. Data Source (Data Source)**
*   **Nguồn dữ liệu**: Cơ sở dữ liệu **PostgreSQL** (được đổ từ WooCommerce).
*   **Dữ liệu đầu vào**: Bảng `fact_order_items` với các cột:
    *   `order_item_date`: Ngày tạo đơn hàng.
    *   `product_id`: ID sản phẩm.
    *   `product_name`: Tên sản phẩm.
    *   `product_color`: Màu sắc sản phẩm (đã được xử lý).
    *   `quantity`: Số lượng.
    *   `price`: Giá bán.

## **2. Training (train.py)**
*   **Script**: `backend/scraper/train.py`.
*   **Mô hình**: **ARIMA (Autoregressive Integrated Moving Average)**.
*   **Chu kỳ huấn luyện**: Tự động mỗi **24 giờ** (hoặc khi script được chạy thủ công).
*   **Quá trình**: 
    1.  Đọc dữ liệu lịch sử từ PostgreSQL.
    2.  Xử lý chuỗi thời gian (làm sạch dữ liệu ngày, tính tổng doanh thu theo ngày/tháng).
    3.  Huấn luyện mô hình ARIMA cho từng `product_id`.
    4.  Lưu mô hình đã huấn luyện vào file `.pkl` trong thư mục `models/`.
    *   *Lưu ý*: Chỉ huấn luyện lại khi phát hiện có sản phẩm mới hoặc dữ liệu mới quan trọng.

## **3. Model Storage (Saved Model)**
*   **Vị trí**: Thư mục `backend/scraper/models/`.
*   **Định dạng**: File **pickle** ( `.pkl` ).
*   **Tên file**: `model_<product_id>.pkl`.
*   **Mục đích**: Chứa các mô hình ARIMA đã được huấn luyện sẵn cho từng sản phẩm.

## **4. Prediction API (FastAPI)**
*   **Endpoint**: `GET /forecast`.
*   **Chức năng**: Dự đoán doanh thu cho tương lai.
*   **Input**: Số ngày cần dự đoán (`days`).
*   **Output**: 
    ```json
    {
        "next_month": {
            "predicted_revenue": 12345.67,
            "product_forecasts": [
                {
                    "product_id": "123",
                    "product_name": "Product A",
                    "predicted_revenue": 5000.00
                },
                {
                    "product_id": "456",
                    "product_name": "Product B",
                    "predicted_revenue": 7345.67
                }
            ]
        }
    }
    ```
*   **Cách hoạt động**: 
    1.  Tải mô hình ARIMA đã lưu cho từng sản phẩm.
    2.  Thực hiện dự đoán cho số ngày yêu cầu.
    3.  Trả về kết quả cho frontend.

## **5. Frontend (React + Chart.js)**
*   **Thành phần**: Tab "Forecast" (Dự đoán).
*   **Thư viện**: **Chart.js**.
*   **Cách hoạt động**: 
    1.  Gọi API `/forecast` khi người dùng mở tab này.
    2.  Nhận dữ liệu dự đoán.
    3.  Vẽ biểu đồ **bar chart** hiển thị dự đoán doanh thu cho tháng tiếp theo, với mỗi thanh là một sản phẩm.