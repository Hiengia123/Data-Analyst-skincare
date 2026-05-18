import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const fmtDate = (d) => {
  if (!d) return '';
  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
  }).format(new Date(d));
};

const STATUS_CLASSES = { pending: 'status-pending', delivered: 'status-delivered', cancelled: 'status-cancelled' };
const STATUS_LABELS  = { pending: 'Chờ xử lý', delivered: 'Đã giao', cancelled: 'Đã huỷ' };

const OrdersTable = ({ orders, total, page, pageSize, onPrev, onNext, onOrderClick }) => {
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const startRow = total === 0 ? 0 : page * pageSize + 1;
  const endRow   = Math.min(total, (page + 1) * pageSize);

  return (
    <div className="table-container">
      <div className="table-toolbar">
        <p>Hiển thị <strong>{startRow}–{endRow}</strong> / <strong>{total}</strong> đơn hàng</p>
      </div>
      <table>
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Ngày đặt</th>
            <th>Thành phố</th>
            <th>Thanh toán</th>
            <th>Trạng thái</th>
            <th className="text-right">Tổng tiền</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(o => (
            <tr key={o.order_id} className={onOrderClick ? 'clickable-row' : ''} onClick={() => onOrderClick?.(o.order_id)}>
              <td><span className="order-id">{o.order_id}</span></td>
              <td>
                <div>{o.customer_name || '—'}</div>
                <div className="text-muted" style={{ fontSize: '0.75rem' }}>{o.email}</div>
              </td>
              <td className="text-muted">{fmtDate(o.created_at)}</td>
              <td className="text-muted">{o.city || '—'}</td>
              <td><span className="month-chip">{o.payment_method}</span></td>
              <td><span className={`status-chip ${STATUS_CLASSES[o.status] || ''}`}>{STATUS_LABELS[o.status] || o.status}</span></td>
              <td className="text-right text-green">{fmt(o.total_price)}</td>
            </tr>
          ))}
          {orders.length === 0 && <tr><td colSpan="7" className="table-empty">Không có đơn hàng</td></tr>}
        </tbody>
      </table>

      {total > 0 && (
        <div className="pagination">
          <button className="btn-icon" onClick={onPrev} disabled={page === 0}><ChevronLeft size={20} /></button>
          <span className="pagination-summary">Trang {page + 1} / {totalPages}</span>
          <button className="btn-icon" onClick={onNext} disabled={page >= totalPages - 1}><ChevronRight size={20} /></button>
        </div>
      )}
    </div>
  );
};

export default OrdersTable;
