manage_woocommerce/
│
├── backend/                # 🔥 FastAPI + ETL + ML
│   ├── app/
│   │   ├── main.py
│   │   ├── api/
│   │   ├── services/
│   │   ├── models/
│   │   └── core/
│   │
│   ├── etl/
│   │   ├── extract/
│   │   ├── transform/
│   │   ├── load/
│   │   └── run_etl.py
│   │
│   ├── ml/
│   │   ├── train.py
│   │   ├── predict.py
│   │   └── models/
│   │
│   ├── scripts/
│   ├── requirements.txt
│   ├── .env
│   └── venv/              # (không commit)
│
├── frontend/              # 🔥 React + Chart.js
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/      # gọi API
│   │   │   └── api.js
│   │   └── App.jsx
│   │
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── data/                  # (optional)
│   ├── raw/
│   ├── processed/
│   └── dw/
│
├── docker/
│   └── docker-compose.yml
│
└── README.md