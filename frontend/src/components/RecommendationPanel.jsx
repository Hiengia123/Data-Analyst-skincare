import React, { useEffect, useState } from 'react';
import { Star, Sparkles, TrendingUp, ShoppingCart, Package } from 'lucide-react';
import { fetchRecommendations } from '../services/api';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const RecommendationPanel = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRecommendations(6)
      .then((d) => setItems(d.recommendations))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading-block">Đang tải gợi ý sản phẩm…</div>;

  return (
    <div className="rec-panel">
      {items.map((item, i) => (
        <div className="rec-card" key={item.product_id}>
          {/* Rank & image */}
          <div className="rec-rank">
            <Star size={14} className={i === 0 ? 'star-gold' : ''} />
            <span>#{i + 1}</span>
          </div>

          {item.image_url && (
            <img
              src={item.image_url}
              alt={item.title}
              style={{
                width: 48, height: 48, borderRadius: 8, objectFit: 'cover',
                flexShrink: 0, border: '1px solid rgba(0,0,0,0.08)',
              }}
            />
          )}

          {/* Main info */}
          <div className="rec-body">
            <strong className="rec-name">{item.title}</strong>
            <div className="rec-meta">
              <span className="color-chip">{item.category_title}</span>
              <span className="color-chip" style={{ background: '#e8f4ff', color: '#1d4ed8' }}>
                {item.product_type}
              </span>
              <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                <Star size={11} className="star-gold" />{item.average_rating}
                <span style={{ fontSize: '0.7rem', color: '#6b7280' }}>({item.total_ratings})</span>
              </span>
            </div>
            <div className="rec-stats">
              <span><ShoppingCart size={12} /> {item.total_orders} đơn</span>
              <span><Package size={12} /> {item.total_qty} bán</span>
              <span className="text-green"><TrendingUp size={12} /> {fmt(item.total_revenue)}</span>
            </div>
            <p className="rec-reason">
              <Sparkles size={12} />
              {item.reason}
            </p>
          </div>

          {/* Score badge */}
          <div className="rec-score">
            <span className="score-badge">{Math.round(item.score)}</span>
            <span className="score-label">score</span>
          </div>
        </div>
      ))}
      {items.length === 0 && (
        <p className="text-muted" style={{ textAlign: 'center', padding: '24px' }}>
          Chưa có đủ dữ liệu để gợi ý sản phẩm.
        </p>
      )}
    </div>
  );
};

export default RecommendationPanel;
