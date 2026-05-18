import React, { useState } from 'react';
import { Search, SlidersHorizontal, BarChart2 } from 'lucide-react';
import { fetchProductSearch } from '../services/api';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const PRODUCT_TYPES = [
  { value: '', label: 'Tất cả loại' },
  { value: 'son', label: 'Son' },
  { value: 'sua_rua_mat', label: 'Sữa rửa mặt' },
  { value: 'kem_chong_nang', label: 'Kem chống nắng' },
];

const CATEGORIES = [
  { value: '', label: 'Tất cả thương hiệu' },
  { value: 'chanel', label: 'Chanel' },
  { value: 'dior', label: 'Dior' },
  { value: 'mac', label: 'M.A.C' },
  { value: 'rare', label: 'Rare Beauty' },
];

const ProductSearch = () => {
  const [filters, setFilters] = useState({
    name: '', product_type: '', category_id: '',
    min_price: '', max_price: '', sort_by: 'total_revenue',
  });
  const [results, setResults]   = useState(null);
  const [loading, setLoading]   = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await fetchProductSearch(filters);
      setResults(data);
      setSearched(true);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const totalRevenue = results?.products?.reduce((s, p) => s + p.total_revenue, 0) ?? 0;
  const totalQty     = results?.products?.reduce((s, p) => s + p.total_qty, 0) ?? 0;

  return (
    <div>
      <form className="search-bar" onSubmit={handleSearch}>
        <div className="search-fields">
          <div className="search-field">
            <label>Tên sản phẩm</label>
            <input placeholder="VD: Son Chanel…" value={filters.name}
              onChange={e => setFilters({ ...filters, name: e.target.value })} />
          </div>
          <div className="search-field">
            <label>Thương hiệu</label>
            <select value={filters.category_id}
              onChange={e => setFilters({ ...filters, category_id: e.target.value })}>
              {CATEGORIES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
            </select>
          </div>
          <div className="search-field">
            <label>Loại sản phẩm</label>
            <select value={filters.product_type}
              onChange={e => setFilters({ ...filters, product_type: e.target.value })}>
              {PRODUCT_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </div>
          <div className="search-field">
            <label>Giá tối thiểu (₫)</label>
            <input type="number" placeholder="0" value={filters.min_price}
              onChange={e => setFilters({ ...filters, min_price: e.target.value })} />
          </div>
          <div className="search-field">
            <label>Giá tối đa (₫)</label>
            <input type="number" placeholder="5000000" value={filters.max_price}
              onChange={e => setFilters({ ...filters, max_price: e.target.value })} />
          </div>
          <div className="search-field">
            <label>Sắp xếp theo</label>
            <select value={filters.sort_by}
              onChange={e => setFilters({ ...filters, sort_by: e.target.value })}>
              <option value="total_revenue">Doanh thu</option>
              <option value="total_qty">Số lượng bán</option>
              <option value="total_orders">Số đơn hàng</option>
              <option value="average_rating">Đánh giá</option>
              <option value="title">Tên sản phẩm</option>
            </select>
          </div>
        </div>

        <div className="search-actions">
          <button type="submit" className="btn-search" disabled={loading}>
            <Search size={16} />{loading ? 'Đang tìm…' : 'Tìm kiếm'}
          </button>
          <button type="button" className="btn-reset" onClick={() =>
            setFilters({ name: '', product_type: '', category_id: '', min_price: '', max_price: '', sort_by: 'total_revenue' })
          }>
            <SlidersHorizontal size={14} /> Đặt lại
          </button>
        </div>
      </form>

      {/* Summary metrics */}
      {searched && results && (
        <div className="search-summary-bar">
          <span><strong>{results.count}</strong> sản phẩm</span>
          <span>Tổng doanh thu: <strong className="text-green">{fmt(totalRevenue)}</strong></span>
          <span>Tổng SL: <strong>{totalQty.toLocaleString()}</strong></span>
        </div>
      )}

      {/* Results */}
      {!searched && (
        <div className="search-placeholder">
          <BarChart2 size={40} />
          <p>Nhập bộ lọc và nhấn <strong>Tìm kiếm</strong> để phân tích sản phẩm từ Data Warehouse.</p>
        </div>
      )}

      {searched && results && (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Thương hiệu</th>
                <th>Loại</th>
                <th className="text-right">Giá</th>
                <th className="text-right">Đánh giá</th>
                <th className="text-right">SL bán</th>
                <th className="text-right">Doanh thu</th>
                <th className="text-right">Đơn hàng</th>
              </tr>
            </thead>
            <tbody>
              {results.products.map(p => (
                <tr key={p.product_id}>
                  <td><strong>{p.title}</strong></td>
                  <td><span className="color-chip">{p.category_title}</span></td>
                  <td className="text-muted">{p.product_type}</td>
                  <td className="text-right">{fmt(p.price)}</td>
                  <td className="text-right">⭐ {p.average_rating} <span style={{fontSize: '0.7rem', color: '#6b7280'}}>({p.total_ratings})</span></td>
                  <td className="text-right">{p.total_qty}</td>
                  <td className="text-right text-green">{fmt(p.total_revenue)}</td>
                  <td className="text-right">{p.total_orders}</td>
                </tr>
              ))}
              {results.products.length === 0 && (
                <tr><td colSpan="8" className="table-empty">Không tìm thấy sản phẩm</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ProductSearch;
