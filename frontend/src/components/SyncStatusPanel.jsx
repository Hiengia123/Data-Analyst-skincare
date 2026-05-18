import React, { useEffect, useState, useCallback } from 'react';
import {
  Activity, CheckCircle, XCircle, Clock, Zap,
  RefreshCw, Play, BarChart2, Database,
} from 'lucide-react';
import { fetchEtlStatus, fetchEtlHistory, triggerEtl } from '../services/api';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const fmtDate = (d) => {
  if (!d) return '—';
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit',
  }).format(new Date(d));
};

const STATUS_STYLES = {
  success: { colour: '#065f46', bg: '#d1fae5', icon: CheckCircle },
  failed:  { colour: '#991b1b', bg: '#fee2e2', icon: XCircle },
  running: { colour: '#92400e', bg: '#fef3c7', icon: RefreshCw },
};

const SyncStatusPanel = () => {
  const [status,   setStatus]   = useState(null);
  const [history,  setHistory]  = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [triggering, setTriggering] = useState(false);
  const [message,  setMessage]  = useState('');

  const load = useCallback(async () => {
    try {
      const [s, h] = await Promise.all([fetchEtlStatus(), fetchEtlHistory(10)]);
      setStatus(s);
      setHistory(h.runs || []);
    } catch (err) {
      console.error('ETL status error:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
    const interval = setInterval(load, 30_000); // refresh every 30s
    return () => clearInterval(interval);
  }, [load]);

  const handleTrigger = async (full = false) => {
    setTriggering(true);
    setMessage('');
    try {
      const res = await triggerEtl(full);
      setMessage(res.message || 'ETL queued');
      setTimeout(load, 3000); // reload status after 3s
    } catch {
      setMessage('Trigger failed');
    } finally {
      setTriggering(false);
    }
  };

  const lastRun   = status?.last_run;
  const statusMeta = STATUS_STYLES[lastRun?.status] || STATUS_STYLES.running;
  const StatusIcon = statusMeta.icon;

  const successCount = status?.recent_1h?.success ?? 0;
  const failedCount  = status?.recent_1h?.failed  ?? 0;

  return (
    <div className="sync-panel">

      {/* ── Top KPIs ── */}
      <div className="sync-kpis">
        <div className="sync-kpi">
          <Database size={16} />
          <div>
            <strong>{lastRun?.orders_synced ?? 0}</strong>
            <span>Đơn đồng bộ</span>
          </div>
        </div>
        <div className="sync-kpi">
          <Activity size={16} />
          <div>
            <strong>{lastRun?.products_synced ?? 0}</strong>
            <span>Sản phẩm</span>
          </div>
        </div>
        <div className="sync-kpi">
          <BarChart2 size={16} />
          <div>
            <strong style={{ color: '#065f46' }}>{successCount}</strong>
            <span>Thành công / 1h</span>
          </div>
        </div>
        <div className="sync-kpi">
          <XCircle size={16} />
          <div>
            <strong style={{ color: failedCount > 0 ? '#dc2626' : undefined }}>{failedCount}</strong>
            <span>Lỗi / 1h</span>
          </div>
        </div>
      </div>

      {/* ── Last run info ── */}
      {lastRun && (
        <div className="sync-last-run">
          <div className="sync-last-header">
            <span
              className="sync-status-badge"
              style={{ background: statusMeta.bg, color: statusMeta.colour }}
            >
              <StatusIcon size={12} />
              {lastRun.status}
            </span>
            <span className="text-muted" style={{ fontSize: '0.8rem' }}>
              Run ID: <code>{lastRun.run_id}</code>
            </span>
            {lastRun.is_full_load && (
              <span className="status-chip status-pending" style={{ fontSize: '0.72rem' }}>Full Load</span>
            )}
          </div>
          <div className="sync-details">
            <div><Clock size={12} />Bắt đầu: {fmtDate(lastRun.started_at)}</div>
            <div><Clock size={12} />Kết thúc: {fmtDate(lastRun.finished_at)}</div>
            <div><Zap  size={12} />Thời gian: {lastRun.duration_seconds ? `${parseFloat(lastRun.duration_seconds).toFixed(2)}s` : '—'}</div>
          </div>
          {lastRun.error_message && (
            <div className="sync-error">{lastRun.error_message}</div>
          )}
          <div className="sync-state">
            <span>Lần đồng bộ cuối: <strong>{status?.last_sync_time ? fmtDate(status.last_sync_time) : '—'}</strong></span>
          </div>
        </div>
      )}

      {!lastRun && !loading && (
        <div className="sync-empty">
          <Activity size={32} />
          <p>Chưa có lần đồng bộ nào. Nhấn <strong>Chạy ngay</strong> để bắt đầu.</p>
        </div>
      )}

      {/* ── Manual trigger buttons ── */}
      <div className="sync-actions">
        <button
          className="btn-sync"
          onClick={() => handleTrigger(false)}
          disabled={triggering}
        >
          <RefreshCw size={14} className={triggering ? 'animate-spin' : ''} />
          {triggering ? 'Đang chạy…' : 'Incremental Sync'}
        </button>
        <button
          className="btn-sync btn-sync-full"
          onClick={() => handleTrigger(true)}
          disabled={triggering}
        >
          <Play size={14} />
          Full Load
        </button>
      </div>
      {message && <p className="sync-message">{message}</p>}

      {/* ── Run history table ── */}
      {history.length > 0 && (
        <div className="table-container" style={{ marginTop: 8 }}>
          <table>
            <thead>
              <tr>
                <th>Run ID</th>
                <th>Bắt đầu</th>
                <th>Thời gian</th>
                <th>Trạng thái</th>
                <th className="text-right">Đơn</th>
                <th className="text-right">Items</th>
                <th className="text-right">SP</th>
                <th>Loại</th>
              </tr>
            </thead>
            <tbody>
              {history.map(run => {
                const sm = STATUS_STYLES[run.status] || STATUS_STYLES.running;
                const SI = sm.icon;
                return (
                  <tr key={run.run_id}>
                    <td><code style={{ fontSize: '0.76rem' }}>{run.run_id}</code></td>
                    <td className="text-muted" style={{ fontSize: '0.8rem', whiteSpace: 'nowrap' }}>
                      {fmtDate(run.started_at)}
                    </td>
                    <td className="text-muted" style={{ fontSize: '0.8rem' }}>
                      {run.duration_seconds ? `${parseFloat(run.duration_seconds).toFixed(1)}s` : '—'}
                    </td>
                    <td>
                      <span style={{
                        display: 'inline-flex', alignItems: 'center', gap: 4,
                        padding: '2px 8px', borderRadius: 12,
                        fontSize: '0.76rem', fontWeight: 600,
                        background: sm.bg, color: sm.colour,
                      }}>
                        <SI size={11} />{run.status}
                      </span>
                    </td>
                    <td className="text-right">{run.orders_synced}</td>
                    <td className="text-right">{run.items_synced}</td>
                    <td className="text-right">{run.products_synced}</td>
                    <td>
                      <span className="text-muted" style={{ fontSize: '0.75rem' }}>
                        {run.is_full_load ? 'Full' : 'Incr.'}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default SyncStatusPanel;
