import React, { useEffect, useState, useCallback } from 'react';
import {
  ArrowUpCircle, CreditCard, RefreshCw, ShoppingCart, Store, TrendingUp,
  Package, BarChart3, Calendar, Users, Search, Sparkles, Zap, LogOut,
  Tag, XCircle, Activity,
} from 'lucide-react';
import {
  fetchSummary, fetchMonthlyAnalytics, fetchDailyAnalytics,
  fetchOrders, fetchProducts, fetchTopProducts, fetchByCategory,
  fetchByType, fetchOrderStatus, fetchRevenueByCity,
} from '../services/api';
import KPICard            from '../components/KPICard';
import MonthlyChart       from '../components/MonthlyChart';
import DailyRevenueChart  from '../components/DailyRevenueChart';
import TopProductsChart   from '../components/TopProductsChart';
import ColorBreakdownChart from '../components/ColorBreakdownChart';
import OrdersTable        from '../components/OrdersTable';
import ProductsTable      from '../components/ProductsTable';
import ProductSearch      from '../components/ProductSearch';
import RecommendationPanel from '../components/RecommendationPanel';
import AutomationPanel    from '../components/AutomationPanel';
import DrillDownModal     from '../components/DrillDownModal';
import UsersPanel         from '../components/UsersPanel';
import SyncStatusPanel    from '../components/SyncStatusPanel';

const Dashboard = ({ user, onLogout }) => {
  const [summary,     setSummary]     = useState(null);
  const [monthlyData, setMonthlyData] = useState(null);
  const [dailyData,   setDailyData]   = useState(null);
  const [orders,      setOrders]      = useState([]);
  const [totalOrders, setTotalOrders] = useState(0);
  const [products,    setProducts]    = useState([]);
  const [topProducts, setTopProducts] = useState(null);
  const [categoryData, setCategoryData] = useState(null);
  const [typeData,    setTypeData]    = useState(null);
  const [orderStatus, setOrderStatus] = useState(null);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState(null);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [activeTab,   setActiveTab]   = useState('overview');
  const [page,        setPage]        = useState(0);
  const [drillDown,   setDrillDown]   = useState(null);
  const pageSize = 10;

  const loadData = async () => {
    setLoading(true); setError(null);
    try {
      const [sumData, monthData, dailyRes, orderData, productData, topProdRes,
             catData, typeRes, statusData] = await Promise.all([
        fetchSummary(), fetchMonthlyAnalytics(), fetchDailyAnalytics(),
        fetchOrders(pageSize, page * pageSize), fetchProducts(200, 0),
        fetchTopProducts(10), fetchByCategory(), fetchByType(), fetchOrderStatus(),
      ]);
      setSummary(sumData);
      setMonthlyData(monthData);
      setDailyData(dailyRes);
      setOrders(orderData.orders);
      setTotalOrders(orderData.count);
      setProducts(productData.products);
      setTopProducts(topProdRes);
      setCategoryData(catData);
      setTypeData(typeRes);
      setOrderStatus(statusData);
      setLastUpdated(new Date());
    } catch (err) {
      setError('Không thể tải dữ liệu. Kiểm tra FastAPI backend đang chạy.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [page]);

  // ── Auto-refresh every 60 seconds ─────────────────────────────────────────
  useEffect(() => {
    const interval = setInterval(() => {
      loadData();
    }, 60_000);
    return () => clearInterval(interval);
  }, [page]);

  const handleRefresh = () => { setPage(0); loadData(); };

  const fmt = (v) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

  const fmtTime = (v) => {
    if (!v) return 'Chưa refresh';
    return new Intl.DateTimeFormat('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }).format(v);
  };

  const cancelledOrders = orderStatus?.statuses?.find(s => s.status === 'cancelled');
  const cancelledCount  = cancelledOrders?.count ?? 0;

  const kpiCards = [
    { title: 'Tổng đơn hàng',    value: new Intl.NumberFormat().format(summary?.total_orders ?? 0), note: 'Từ Firebase', icon: ShoppingCart, tone: 'blue' },
    { title: 'Doanh thu',         value: fmt(summary?.total_revenue),  note: 'Đơn thành công',       icon: CreditCard,   tone: 'green' },
    { title: 'Đơn trung bình',    value: fmt(summary?.avg_order_value), note: 'Giá trị trung bình',  icon: TrendingUp,   tone: 'amber' },
    { title: 'Khách hàng',        value: new Intl.NumberFormat().format(summary?.total_customers ?? 0), note: 'Unique users', icon: Users, tone: 'purple' },
    { title: 'Đơn huỷ',          value: cancelledCount, note: 'Cần xem lại',   icon: XCircle,      tone: 'rose' },
    { title: 'Đơn lớn nhất',     value: fmt(summary?.max_order),      note: 'Giao dịch cao nhất',   icon: ArrowUpCircle, tone: 'indigo' },
  ];

  const sidebarNav = [
    {
      group: 'Analytics',
      items: [
        { id: 'overview',    label: 'Tổng quan',   icon: BarChart3 },
        { id: 'orders',      label: 'Đơn hàng',    icon: ShoppingCart },
        { id: 'products',    label: 'Sản phẩm',    icon: Package },
        { id: 'customers',   label: 'Khách hàng',  icon: Users },
        { id: 'search',      label: 'Tìm kiếm',    icon: Search },
      ]
    },
    {
      group: 'AI & Insights',
      items: [
        { id: 'ai',          label: 'AI Insights', icon: Sparkles },
      ]
    },
    {
      group: 'Data & ETL',
      items: [
        { id: 'etl',         label: 'ETL Sync',    icon: Activity },
      ]
    },
    {
      group: 'Automation',
      items: [
        { id: 'automation',  label: 'Workflows',   icon: Zap },
      ]
    }
  ];

  const currentTabLabel = sidebarNav.flatMap(g => g.items).find(i => i.id === activeTab)?.label || 'Dashboard';

  return (
    <div className="app-layout">
      {/* SIDEBAR */}
      <aside className="app-sidebar">
        <div className="sidebar-brand">
          <Store size={24} className="brand-icon" />
          <span>Nhung Group</span>
        </div>
        
        <div className="sidebar-navs">
          {sidebarNav.map(group => (
            <div key={group.group} className="nav-group">
              <span className="nav-group-label">{group.group}</span>
              {group.items.map(item => {
                const Icon = item.icon;
                return (
                  <button
                    key={item.id}
                    className={`nav-item ${activeTab === item.id ? 'active' : ''}`}
                    onClick={() => setActiveTab(item.id)}
                  >
                    <Icon size={18} />
                    <span>{item.label}</span>
                  </button>
                );
              })}
            </div>
          ))}
        </div>
        
        {user && (
          <div className="sidebar-footer">
            <div className="user-info">
              <div className="user-avatar">
                {user.name ? user.name.charAt(0).toUpperCase() : 'U'}
              </div>
              <div className="user-details">
                <span className="user-name">{user.name || 'Admin User'}</span>
                <span className="user-email">{user.email}</span>
              </div>
            </div>
            <button className="btn-logout-icon" onClick={onLogout} title="Đăng xuất">
              <LogOut size={16} />
            </button>
          </div>
        )}
      </aside>

      {/* MAIN CONTENT */}
      <main className="app-main">
        <header className="app-header">
          <div className="header-title">
            <h1>{currentTabLabel}</h1>
          </div>
          
          <div className="header-actions">
            <div className="status-panel-mini" title={`Cập nhật: ${fmtTime(lastUpdated)}`}>
              <div className={`status-dot ${error ? 'is-error' : 'is-live'}`} />
              <span>{error ? 'Lỗi kết nối' : 'Đã đồng bộ'}</span>
            </div>
            
            <button className="btn-refresh-icon" onClick={handleRefresh} disabled={loading} title="Làm mới">
              <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
            </button>
          </div>
        </header>

        <div className="app-content">
          {error && <div className="error-banner">{error}</div>}

          {/* KPIs only on Dashboard/Overview */}
          {activeTab === 'overview' && (
            <section className="kpi-grid kpi-grid-6">
              {kpiCards.map((card) => <KPICard key={card.title} {...card} />)}
            </section>
          )}

          {/* ── OVERVIEW ───────────────────────────────────────────────────── */}
          {activeTab === 'overview' && (
            <>
          <section className="content-grid content-grid-2col">
            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Doanh thu theo tháng</span><h2>Xu hướng & đơn hàng</h2></div>
                <div className="panel-meta"><Calendar size={16} /><span>{monthlyData?.labels?.length || 0} tháng</span></div>
              </div>
              {loading && !monthlyData ? <div className="loading-block">Đang tải…</div> : <MonthlyChart data={monthlyData} />}
            </article>
            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Doanh thu theo ngày</span><h2>Revenue by day</h2></div>
              </div>
              {loading && !dailyData ? <div className="loading-block">Đang tải…</div> : <DailyRevenueChart data={dailyData} />}
            </article>
          </section>

          <section className="content-grid content-grid-2col">
            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Trạng thái đơn hàng</span><h2>Order status</h2></div>
                <div className="panel-meta"><ShoppingCart size={16} /></div>
              </div>
              {orderStatus ? (
                <div className="status-list">
                  {orderStatus.statuses.map(s => (
                    <div key={s.status} className="status-row">
                      <span className={`status-chip status-${s.status}`}>{s.status}</span>
                      <span className="status-count">{s.count} đơn</span>
                      <span className="status-rev">{fmt(s.revenue)}</span>
                    </div>
                  ))}
                </div>
              ) : <div className="loading-block">Đang tải…</div>}
            </article>

            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Phân loại sản phẩm</span><h2>Theo thương hiệu</h2></div>
                <div className="panel-meta"><Tag size={16} /></div>
              </div>
              {categoryData ? <ColorBreakdownChart data={categoryData} /> : <div className="loading-block">Đang tải…</div>}
            </article>
          </section>
        </>
      )}

      {/* ── PRODUCTS ───────────────────────────────────────────────────── */}
      {activeTab === 'products' && (
        <>
          {/* Row 1: Product Analytics Table (primary, always visible) */}
          <section className="content-grid">
            <article className="panel">
              <div className="panel-head">
                <div>
                  <span className="panel-label">Product Performance Analytics</span>
                  <h2>Phân tích sản phẩm</h2>
                </div>
                <div className="panel-meta">
                  <Package size={16} />
                  <span>{(topProducts?.products ?? products).length} sản phẩm</span>
                </div>
              </div>

              {/* ── Inline product analytics table — data-analyst style ── */}
              {loading && !topProducts && products.length === 0 ? (
                <div className="loading-block">Đang tải sản phẩm…</div>
              ) : (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>#</th>
                        <th>Sản phẩm</th>
                        <th>Thương hiệu</th>
                        <th>Loại</th>
                        <th className="text-right">Giá</th>
                        <th className="text-right">⭐ Rating</th>
                        <th className="text-right">Doanh thu</th>
                        <th className="text-right">Đơn hàng</th>
                        <th className="text-right">SL bán</th>
                        <th className="text-right">% DT</th>
                        <th>Gợi ý</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        /* Merge topProducts (has revenue/orders) with products (has all items) */
                        const topMap = {};
                        (topProducts?.products ?? []).forEach(p => { topMap[p.product_id] = p; });

                        const allProducts = products.length > 0 ? products : (topProducts?.products ?? []);
                        const totalRevenue = Object.values(topMap).reduce((s, p) => s + (p.total_revenue ?? 0), 0) || 1;

                        return allProducts.map((p, i) => {
                          const stats = topMap[p.product_id] ?? {};
                          const revenue = stats.total_revenue ?? 0;
                          const orders  = stats.total_orders  ?? 0;
                          const qty     = stats.total_qty     ?? 0;
                          const pct     = ((revenue / totalRevenue) * 100).toFixed(1);

                          return (
                            <tr
                              key={p.product_id}
                              className="clickable-row"
                              onClick={() => setDrillDown({ type: 'product', id: p.product_id })}
                            >
                              <td className="text-muted" style={{ fontSize: '0.8rem' }}>{i + 1}</td>
                              <td>
                                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                                  {p.image_url && (
                                    <img src={p.image_url} alt={p.title}
                                      style={{ width: 34, height: 34, borderRadius: 6, objectFit: 'cover', flexShrink: 0 }} />
                                  )}
                                  <span style={{ fontWeight: 600, fontSize: '0.85rem', lineHeight: 1.3 }}>{p.title}</span>
                                </div>
                              </td>
                              <td><span className="color-chip">{p.category_title}</span></td>
                              <td className="text-muted" style={{ fontSize: '0.8rem' }}>{p.product_type}</td>
                              <td className="text-right" style={{ fontWeight: 600 }}>
                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(p.price)}
                              </td>
                              <td className="text-right">
                                <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 3 }}>
                                  <span style={{ color: '#f59e0b', fontSize: '0.9rem' }}>★</span>
                                  <strong>{p.average_rating ?? '—'}</strong>
                                  <span style={{ fontSize: '0.7rem', color: '#6b7280' }}>({p.total_ratings ?? 0})</span>
                                </span>
                              </td>
                              <td className="text-right text-green" style={{ fontWeight: 700 }}>
                                {revenue > 0
                                  ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(revenue)
                                  : <span className="text-muted">—</span>}
                              </td>
                              <td className="text-right"><strong>{orders || '—'}</strong></td>
                              <td className="text-right">{qty || '—'}</td>
                              <td className="text-right">
                                {revenue > 0 ? (
                                  <div style={{ display: 'flex', alignItems: 'center', gap: 6, justifyContent: 'flex-end' }}>
                                    <div style={{
                                      width: 40, height: 6, borderRadius: 3,
                                      background: '#e2e8f0', overflow: 'hidden',
                                    }}>
                                      <div style={{
                                        width: `${Math.min(100, parseFloat(pct) * 5)}%`,
                                        height: '100%',
                                        background: '#2f6f5e',
                                        borderRadius: 3,
                                      }} />
                                    </div>
                                    <span style={{ fontSize: '0.8rem', fontWeight: 600 }}>{pct}%</span>
                                  </div>
                                ) : <span className="text-muted" style={{ fontSize: '0.8rem' }}>—</span>}
                              </td>
                              <td>
                                {p.show_recommend
                                  ? <span className="status-chip status-delivered" style={{ fontSize: '0.7rem' }}>✓ Gợi ý</span>
                                  : <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>—</span>}
                              </td>
                            </tr>
                          );
                        });
                      })()}
                    </tbody>
                  </table>
                  {products.length === 0 && topProducts?.products?.length === 0 && (
                    <p className="table-empty">Không có dữ liệu sản phẩm</p>
                  )}
                </div>
              )}
            </article>
          </section>

          {/* Row 2: Charts */}
          <section className="content-grid content-grid-2col">
            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Doanh thu top sản phẩm</span><h2>Biểu đồ doanh thu</h2></div>
              </div>
              {topProducts ? <TopProductsChart data={topProducts} /> : <div className="loading-block">Đang tải…</div>}
            </article>
            <article className="panel">
              <div className="panel-head">
                <div><span className="panel-label">Phân loại thương hiệu</span><h2>Danh mục sản phẩm</h2></div>
              </div>
              {categoryData ? <ColorBreakdownChart data={categoryData} /> : <div className="loading-block">Đang tải…</div>}
            </article>
          </section>
        </>
      )}

      {/* ── SEARCH ─────────────────────────────────────────────────────── */}
      {activeTab === 'search' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div><span className="panel-label">Analytical Query</span><h2>Tìm kiếm & phân tích sản phẩm</h2></div>
            </div>
            <ProductSearch />
          </article>
        </section>
      )}

      {/* ── ORDERS ─────────────────────────────────────────────────────── */}
      {activeTab === 'orders' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div><span className="panel-label">Đơn hàng Firebase</span><h2>Tất cả đơn hàng</h2></div>
              <div className="panel-meta"><span>{pageSize} / trang</span></div>
            </div>
            {loading && orders.length === 0 ? (
              <div className="loading-block">Đang tải…</div>
            ) : (
              <OrdersTable
                orders={orders} total={totalOrders} page={page} pageSize={pageSize}
                onPrev={() => setPage(p => Math.max(0, p - 1))}
                onNext={() => setPage(p => p + 1)}
                onOrderClick={(id) => setDrillDown({ type: 'order', id })}
              />
            )}
          </article>
        </section>
      )}

      {/* ── CUSTOMERS ──────────────────────────────────────────────────── */}
      {activeTab === 'customers' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div><span className="panel-label">Khách hàng</span><h2>Danh sách & chi tiêu</h2></div>
              <div className="panel-meta"><Users size={16} /></div>
            </div>
            <UsersPanel />
          </article>
        </section>
      )}

      {/* ── AI INSIGHTS ────────────────────────────────────────────────── */}
      {activeTab === 'ai' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div><span className="panel-label">Machine Learning</span><h2>Gợi ý sản phẩm</h2></div>
              <div className="panel-meta"><Sparkles size={16} /><span>Popularity-based</span></div>
            </div>
            <RecommendationPanel />
          </article>
        </section>
      )}

      {/* ── AUTOMATION ─────────────────────────────────────────────────── */}
      {activeTab === 'automation' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div><span className="panel-label">n8n Integration</span><h2>Workflow Automation</h2></div>
              <div className="panel-meta"><Zap size={16} /><span>Ready to connect</span></div>
            </div>
            <AutomationPanel />
          </article>
        </section>
      )}

      {/* ── ETL SYNC ─────────────────────────────────────────────────────── */}
      {activeTab === 'etl' && (
        <section className="content-grid">
          <article className="panel">
            <div className="panel-head">
              <div>
                <span className="panel-label">Incremental Batch Pipeline</span>
                <h2>ETL Sync Monitor</h2>
              </div>
              <div className="panel-meta">
                <Activity size={16} />
                <span>Firebase → PostgreSQL</span>
              </div>
            </div>
            <SyncStatusPanel />
          </article>
        </section>
      )}

      {/* DRILL-DOWN MODAL */}
      {drillDown && (
        <DrillDownModal type={drillDown.type} id={drillDown.id} onClose={() => setDrillDown(null)} />
      )}
        </div>
      </main>
    </div>
  );
};


export default Dashboard;
