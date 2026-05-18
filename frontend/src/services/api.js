import axios from 'axios';

const API_BASE_URL = 'http://127.0.0.1:8000';

export const api = axios.create({ baseURL: API_BASE_URL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) config.params = { ...config.params, token };
  return config;
});

// ─── Core KPIs ──────────────────────────────────────────────────────────────
export const fetchSummary        = async () => (await api.get('/summary')).data;
export const fetchMonthlyAnalytics = async () => (await api.get('/analytics/monthly')).data;
export const fetchDailyAnalytics   = async () => (await api.get('/analytics/daily')).data;

// ─── Orders ─────────────────────────────────────────────────────────────────
export const fetchOrders       = async (limit = 50, offset = 0) =>
  (await api.get('/orders', { params: { limit, offset } })).data;
export const fetchOrderDetail  = async (orderId) =>
  (await api.get(`/orders/${orderId}/items`)).data;
export const fetchOrderStatus  = async () => (await api.get('/analytics/order-status')).data;
export const fetchRevenueByCity = async () => (await api.get('/analytics/revenue-by-city')).data;

// ─── Products ───────────────────────────────────────────────────────────────
export const fetchProducts       = async (limit = 100, offset = 0) =>
  (await api.get('/products', { params: { limit, offset } })).data;
export const fetchTopProducts    = async (limit = 10) =>
  (await api.get('/analytics/top-products', { params: { limit } })).data;
export const fetchByCategory     = async () => (await api.get('/analytics/products-by-category')).data;
export const fetchByType         = async () => (await api.get('/analytics/products-by-type')).data;
export const fetchProductDetail  = async (productId) =>
  (await api.get(`/products/${productId}/details`)).data;

// ─── Product Search ──────────────────────────────────────────────────────────
export const fetchProductSearch = async (filters = {}) => {
  const params = Object.fromEntries(
    Object.entries(filters).filter(([, v]) => v !== '' && v !== null && v !== undefined)
  );
  return (await api.get('/products/search', { params })).data;
};

// ─── Users ──────────────────────────────────────────────────────────────────
export const fetchUsers = async (limit = 100, offset = 0) =>
  (await api.get('/users', { params: { limit, offset } })).data;

// ─── AI / Automation ─────────────────────────────────────────────────────────
export const fetchRecommendations = async (limit = 5) =>
  (await api.get('/recommendations', { params: { limit } })).data;
export const sendAutomation = async (action, target = '') => {
  const url = action === 'send-email' ? '/automation/send-email' : '/automation/trigger';
  return (await api.post(url, { action, target })).data;
};

// ─── Auth ────────────────────────────────────────────────────────────────────
export const loginUser = async (email, password) =>
  (await api.post('/auth/login', { email, password })).data;

// ─── ETL / Sync status ───────────────────────────────────────────────────────
export const fetchEtlStatus  = async () => (await api.get('/etl/status')).data;
export const fetchEtlHistory = async (limit = 15) =>
  (await api.get('/etl/history', { params: { limit } })).data;
export const triggerEtl = async (full = false) =>
  (await api.post('/etl/trigger', null, { params: { full } })).data;

