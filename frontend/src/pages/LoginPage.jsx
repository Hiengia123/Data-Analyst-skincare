import React, { useState } from 'react';
import { Store, LogIn, AlertCircle } from 'lucide-react';
import { loginUser } from '../services/api';

const LoginPage = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await loginUser(email, password);
      localStorage.setItem('auth_token', data.token);
      localStorage.setItem('auth_user', JSON.stringify(data.user));
      onLogin(data.user);
    } catch (err) {
      setError(err.response?.data?.detail || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <div className="login-header">
          <Store size={32} />
          <h1>Minshop Dashboard</h1>
          <p>Sign in to access analytics</p>
        </div>

        {error && (
          <div className="login-error">
            <AlertCircle size={16} />
            {error}
          </div>
        )}

        <div className="login-field">
          <label>Email</label>
          <input
            type="email"
            placeholder="admin@minshop.vn"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="login-field">
          <label>Password</label>
          <input
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button className="btn-login" type="submit" disabled={loading}>
          <LogIn size={16} />
          {loading ? 'Signing in...' : 'Sign in'}
        </button>

        <p className="login-hint">
          Demo: <strong>admin@minshop.vn</strong> / <strong>admin123</strong>
        </p>
      </form>
    </div>
  );
};

export default LoginPage;
