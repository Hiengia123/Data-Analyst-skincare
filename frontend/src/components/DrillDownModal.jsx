import React, { useEffect, useState } from 'react';
import {
  X, Package, ShoppingCart, Star, TrendingUp, BarChart2,
  User, Phone, MapPin, CreditCard, Tag, Hash, Clock,
  AlertCircle, XCircle, CheckCircle,
} from 'lucide-react';
import { fetchOrderDetail, fetchProductDetail } from '../services/api';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const fmtDate = (d) => {
  if (!d) return '';
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  }).format(new Date(d));
};

const STATUS_META = {
  pending:   { label: 'Chờ xử lý',   cls: 'status-pending',   icon: Clock },
  delivered: { label: 'Đã giao',     cls: 'status-delivered', icon: CheckCircle },
  cancelled: { label: 'Đã huỷ',     cls: 'status-cancelled', icon: XCircle },
};

/* ─── Order drill-down ─────────────────────────────────────────────── */
const OrderDetail = ({ data }) => {
  const { order, items } = data;
  const statusMeta = STATUS_META[order.status] || { label: order.status, cls: '', icon: AlertCircle };
  const StatusIcon = statusMeta.icon;

  const subtotals = items.reduce((s, i) => s + (i.line_total ?? 0), 0);

  return (
    <div className="modal-body">

      {/* ── Top info grid ── */}
      <div className="dd-info-grid">
        {/* Customer block */}
        <div className="dd-info-block">
          <div className="dd-block-title"><User size={14} />Thông tin khách hàng</div>
          <div className="dd-row"><span>Tên:</span><strong>{order.customer_name || '—'}</strong></div>
          <div className="dd-row"><span>Email:</span><span className="text-muted">{order.email || '—'}</span></div>
          <div className="dd-row"><span>Thành phố:</span><span>{order.city || '—'}</span></div>
        </div>

        {/* Order block */}
        <div className="dd-info-block">
          <div className="dd-block-title"><Hash size={14} />Thông tin đơn hàng</div>
          <div className="dd-row"><span>Mã đơn:</span><span className="order-id">{order.order_id}</span></div>
          <div className="dd-row">
            <span>Ngày đặt:</span><span className="text-muted">{fmtDate(order.created_at)}</span>
          </div>
          <div className="dd-row">
            <span>Thanh toán:</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
              <CreditCard size={13} />{order.payment_method?.toUpperCase()}
            </span>
          </div>
        </div>

        {/* Status block */}
        <div className="dd-info-block">
          <div className="dd-block-title"><TrendingUp size={14} />Trạng thái</div>
          <div className="dd-row">
            <span>Trạng thái:</span>
            <span className={`status-chip ${statusMeta.cls}`}>
              <StatusIcon size={11} style={{ marginRight: 4, verticalAlign: 'middle' }} />
              {statusMeta.label}
            </span>
          </div>
          <div className="dd-row">
            <span>Tổng tiền:</span>
            <strong className="text-green">{fmt(order.total_price)}</strong>
          </div>
          {order.cancel_reason && (
            <div className="dd-row">
              <span>Lý do huỷ:</span>
              <span className="text-muted" style={{ fontSize: '0.82rem' }}>{order.cancel_reason}</span>
            </div>
          )}
        </div>
      </div>

      {/* ── Product items table ── */}
      <div className="dd-section-title">
        <Package size={15} /> Danh sách sản phẩm ({items.length} items)
      </div>
      <div className="table-container" style={{ marginTop: 0 }}>
        <table>
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Phân loại</th>
              <th>Lựa chọn</th>
              <th className="text-right">Đơn giá</th>
              <th className="text-right">SL</th>
              <th className="text-right">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.order_item_id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 9 }}>
                    {item.image_url && (
                      <img src={item.image_url} alt={item.title}
                        style={{ width: 34, height: 34, borderRadius: 6, objectFit: 'cover', flexShrink: 0 }} />
                    )}
                    <div>
                      <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>{item.product_title || item.title}</div>
                      <div className="text-muted" style={{ fontSize: '0.75rem' }}>{item.product_id}</div>
                    </div>
                  </div>
                </td>
                <td>
                  {item.category_title && <span className="color-chip">{item.category_title}</span>}
                </td>
                <td className="text-muted" style={{ fontSize: '0.8rem' }}>
                  {[item.selected_capacity, item.selected_color, item.selected_weight]
                    .filter(Boolean).join(' / ') || '—'}
                </td>
                <td className="text-right">{fmt(item.unit_price)}</td>
                <td className="text-right"><strong>{item.quantity}</strong></td>
                <td className="text-right text-green"><strong>{fmt(item.line_total)}</strong></td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr>
              <td colSpan="4" />
              <td className="text-right text-muted" style={{ fontSize: '0.85rem' }}>Tổng phụ:</td>
              <td className="text-right"><strong className="text-green">{fmt(subtotals)}</strong></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  );
};

/* ─── Product drill-down ───────────────────────────────────────────── */
const ProductDetail = ({ data }) => {
  const { product, sales, recent_orders } = data;

  const cancelledCount = recent_orders?.filter(o => o.status === 'cancelled').length ?? 0;
  const cancelRate = recent_orders?.length > 0
    ? ((cancelledCount / recent_orders.length) * 100).toFixed(0)
    : 0;

  return (
    <div className="modal-body">

      {/* ── Product hero ── */}
      <div className="dd-product-hero">
        {product.image_url && (
          <img src={product.image_url} alt={product.title}
            className="dd-product-img" />
        )}
        <div className="dd-product-info">
          <div className="dd-product-brand">
            <span className="color-chip">{product.category_title}</span>
            <span className="color-chip" style={{ background: '#e8f4ff', color: '#1d4ed8' }}>
              {product.product_type}
            </span>
          </div>
          <h3 className="dd-product-name">{product.title}</h3>
          <div className="dd-product-meta">
            <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <Star size={14} className="star-gold" />
              <strong>{product.average_rating}</strong> / 5.0
              <span style={{ fontSize: '0.8rem', color: '#6b7280', marginLeft: 4 }}>({product.total_ratings} lượt đánh giá)</span>
            </span>
            <span className="text-green" style={{ fontWeight: 700, fontSize: '1.05rem' }}>
              {fmt(product.price)}
            </span>
            {product.show_recommend && (
              <span className="status-chip status-delivered" style={{ fontSize: '0.72rem' }}>
                Được gợi ý
              </span>
            )}
          </div>
          {product.description && (
            <p className="dd-product-desc">{product.description}</p>
          )}
        </div>
      </div>

      {/* ── Sales KPI row ── */}
      <div className="drill-kpis drill-kpis-4">
        <div className="drill-kpi">
          <strong className="text-green">{fmt(sales?.total_revenue)}</strong>
          <span><TrendingUp size={12} /> Doanh thu</span>
        </div>
        <div className="drill-kpi">
          <strong>{sales?.total_orders ?? 0}</strong>
          <span><ShoppingCart size={12} /> Đơn hàng</span>
        </div>
        <div className="drill-kpi">
          <strong>{sales?.total_qty ?? 0}</strong>
          <span><Package size={12} /> Số lượng bán</span>
        </div>
        <div className="drill-kpi" style={{ borderColor: cancelRate > 20 ? '#fca5a5' : undefined }}>
          <strong style={{ color: cancelRate > 20 ? '#dc2626' : undefined }}>{cancelRate}%</strong>
          <span><XCircle size={12} /> Tỷ lệ huỷ</span>
        </div>
      </div>

      {/* ── Recent orders table ── */}
      {recent_orders?.length > 0 && (
        <>
          <div className="dd-section-title">
            <BarChart2 size={15} /> Đơn hàng gần nhất
          </div>
          <div className="table-container" style={{ marginTop: 0 }}>
            <table>
              <thead>
                <tr>
                  <th>Mã đơn</th>
                  <th>Ngày đặt</th>
                  <th>Trạng thái</th>
                  <th>Lựa chọn</th>
                  <th className="text-right">SL</th>
                  <th className="text-right">Đơn giá</th>
                  <th className="text-right">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
                {recent_orders.map((o, i) => {
                  const sm = STATUS_META[o.status] || { label: o.status, cls: '' };
                  return (
                    <tr key={i}>
                      <td><span className="order-id">{o.order_id}</span></td>
                      <td className="text-muted" style={{ fontSize: '0.8rem' }}>{fmtDate(o.created_at)}</td>
                      <td><span className={`status-chip ${sm.cls}`}>{sm.label}</span></td>
                      <td className="text-muted" style={{ fontSize: '0.8rem' }}>
                        {[o.selected_capacity, o.selected_color].filter(Boolean).join(' / ') || '—'}
                      </td>
                      <td className="text-right"><strong>{o.quantity}</strong></td>
                      <td className="text-right">{fmt(o.unit_price)}</td>
                      <td className="text-right text-green">{fmt(o.line_total)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
};

/* ─── Modal wrapper ────────────────────────────────────────────────── */
const DrillDownModal = ({ type, id, onClose }) => {
  const [data, setData]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  useEffect(() => {
    setLoading(true); setError(null); setData(null);
    const fetch = type === 'order' ? fetchOrderDetail(id) : fetchProductDetail(id);
    fetch
      .then(setData)
      .catch((e) => setError(e.message || 'Không tải được dữ liệu'))
      .finally(() => setLoading(false));
  }, [type, id]);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        className="modal-content modal-content-wide"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="modal-header">
          <h3>
            {type === 'order'
              ? <><ShoppingCart size={18} /> Đơn hàng {id}</>
              : <><Package size={18} /> Chi tiết sản phẩm</>}
          </h3>
          <button className="btn-icon" onClick={onClose}><X size={18} /></button>
        </div>

        {/* States */}
        {loading && <div className="loading-block" style={{ margin: '32px auto' }}>Đang tải dữ liệu…</div>}
        {error   && (
          <div className="modal-body" style={{ color: '#dc2626', padding: '24px' }}>
            <AlertCircle size={16} style={{ marginRight: 6 }} />{error}
          </div>
        )}

        {/* Content */}
        {!loading && !error && data && type === 'order'   && <OrderDetail   data={data} />}
        {!loading && !error && data && type === 'product' && <ProductDetail data={data} />}
      </div>
    </div>
  );
};

export default DrillDownModal;
