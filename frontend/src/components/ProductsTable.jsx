import React from 'react';
import { Star } from 'lucide-react';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const ProductsTable = ({ products, onProductClick }) => {
  return (
    <div className="table-container">
      <table>
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th>Thương hiệu</th>
            <th>Loại</th>
            <th className="text-right">Giá</th>
            <th className="text-right">Đánh giá</th>
            <th>Gợi ý</th>
          </tr>
        </thead>
        <tbody>
          {products.map(p => (
            <tr key={p.product_id} className={onProductClick ? 'clickable-row' : ''} onClick={() => onProductClick?.(p.product_id)}>
              <td>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  {p.image_url && (
                    <img src={p.image_url} alt={p.title}
                      style={{ width: 36, height: 36, borderRadius: 6, objectFit: 'cover', flexShrink: 0 }} />
                  )}
                  <strong style={{ fontSize: '0.85rem' }}>{p.title}</strong>
                </div>
              </td>
              <td><span className="color-chip">{p.category_title}</span></td>
              <td className="text-muted" style={{ fontSize: '0.82rem' }}>{p.product_type}</td>
              <td className="text-right text-green">{fmt(p.price)}</td>
              <td className="text-right">
                <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 3 }}>
                  <Star size={12} className="star-gold" />{p.average_rating}
                  <span style={{ fontSize: '0.7rem', color: '#6b7280' }}>({p.total_ratings})</span>
                </span>
              </td>
              <td>
                {p.show_recommend ? (
                  <span className="status-chip status-delivered" style={{ fontSize: '0.72rem' }}>Gợi ý</span>
                ) : (
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.72rem' }}>—</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductsTable;
