import React, { useEffect, useState } from 'react';
import { Mail, ShoppingBag } from 'lucide-react';
import { fetchUsers } from '../services/api';

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(v ?? 0);

const UsersPanel = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers()
      .then(d => setUsers(d.users))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading-block">Đang tải khách hàng…</div>;

  return (
    <div className="table-container">
      <table>
        <thead>
          <tr>
            <th>Tên</th>
            <th>Email</th>
            <th>Provider</th>
            <th className="text-right">Đơn hàng</th>
            <th className="text-right">Tổng chi tiêu</th>
          </tr>
        </thead>
        <tbody>
          {users.map(u => (
            <tr key={u.user_id}>
              <td><strong>{u.name || '—'}</strong></td>
              <td className="text-muted">
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <Mail size={13} />{u.email}
                </div>
              </td>
              <td><span className="month-chip">{u.provider}</span></td>
              <td className="text-right">
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 4 }}>
                  <ShoppingBag size={13} />{u.total_orders}
                </div>
              </td>
              <td className="text-right text-green">{fmt(u.total_spent)}</td>
            </tr>
          ))}
          {users.length === 0 && (
            <tr><td colSpan="5" className="table-empty">Không có khách hàng</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default UsersPanel;
