# 🐍 Python Project Setup Guide (FastAPI + Flet + Data Analyst Stack)

## 🎯 Mục tiêu

Tài liệu này giúp bạn:

* Tạo môi trường ảo (venv)
* Cài đặt thư viện
* Chạy FastAPI
* Tránh lỗi môi trường

---

## 📦 1. Tạo project

```bash
mkdir Manage_Woocommerce
cd Manage_Woocommerce
```
---

## 🧪 2. Tạo môi trường ảo (venv)

```bash
python -m venv venv
```

👉 Sau khi chạy xong sẽ có folder:

```
venv/
```

---

## ⚡ 3. Kích hoạt môi trường

### Windows (CMD / PowerShell)

```bash

venv\Scripts\activate```
```
instead of calling uvicorn directly, force it to use your active virtual environment's Python by using the python -m syntax.

Stop your current backend server (Ctrl+C), and run this command inside your backend folder instead:

powershell
python -m uvicorn app.main:app --reload
By prefixing it with python -m, you guarantee that the server boots up using the exact Python interpreter inside your venv, which has all the database libraries properly installed.

Your React dashboard will immediately be able to load the data once the backend boots up!
```

👉 Khi thành công sẽ thấy:

```
(venv) C:\...
```

---

## 📥 4. Cài đặt thư viện

```bash
pip install fastapi uvicorn plotly numpy flet pandas scikit-learn
```

---

## 🧠 5. Kiểm tra cài đặt

```bash
python -c "import fastapi, plotly, numpy, flet; print('OK')"
```

---

## 🚀 6. Tạo FastAPI app

Tạo file: `main.py`

```python
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "Hello FastAPI"}
```

---

## ▶️ 7. Chạy server

```bash
uvicorn main:app --reload
```

Mở trình duyệt:

```
http://127.0.0.1:8000/docs
```

---

## ⚠️ 8. Lỗi thường gặp

### ❌ Không tìm thấy module main

✔ Kiểm tra:

* File có tên đúng: `main.py`
* Không phải `main.py.txt`
* Đang đứng đúng thư mục

---

### ❌ Import lỗi

Chạy:

```bash
python main.py
```

---

### ❌ Quên activate venv

👉 Nếu không có `(venv)` → chưa kích hoạt

---

## 📦 9. Lưu môi trường (rất quan trọng)

```bash
pip freeze > requirements.txt
```

---

## 🔁 10. Cài lại môi trường (khi clone project)

```bash
pip install -r requirements.txt
```

---
## 🧠 11. Nguyên tắc quan trọng

* Mỗi project = 1 venv
* Không dùng global environment
* Luôn có requirements.txt

---

## 🚀 12. Cấu trúc project đề xuất

```
Manage_Woocommerce/
│
├── venv/
├── main.py
├── requirements.txt
├── app/
│   ├── api/
│   ├── services/
│   ├── models/
│
└── ui/
    └── flet_app.py
```

---

## 💡 13. Tips nâng cao

* Dùng `pip list` để xem lib
* Dùng `deactivate` để thoát venv
* Không commit folder `venv/` lên Git

---

## ✅ Kết luận

Nếu làm đúng quy trình này:

* Không lỗi môi trường
* Dễ debug
* Dễ deploy
* Làm việc như dev chuyên nghiệp
