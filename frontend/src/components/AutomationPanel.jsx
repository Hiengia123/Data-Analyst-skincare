import React, { useState } from 'react';
import { Mail, Zap, CheckCircle, AlertCircle } from 'lucide-react';
import { sendAutomation } from '../services/api';

const AutomationPanel = () => {
  const [status, setStatus] = useState(null); // { type: 'success'|'error', message }
  const [sending, setSending] = useState(false);

  const handleAction = async (action) => {
    setSending(true);
    setStatus(null);
    try {
      const res = await sendAutomation(action);
      setStatus({ type: 'success', message: res.message });
    } catch (err) {
      setStatus({ type: 'error', message: err.response?.data?.detail || 'Action failed' });
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="auto-panel">
      <p className="auto-desc">
        Trigger n8n workflows to send emails or run automated processes.
      </p>

      <div className="auto-actions">
        <button
          className="auto-btn auto-btn-email"
          onClick={() => handleAction('send-email')}
          disabled={sending}
        >
          <Mail size={18} />
          <div>
            <strong>Send promotion email</strong>
            <span>Notify all customers about offers</span>
          </div>
        </button>

        <button
          className="auto-btn auto-btn-workflow"
          onClick={() => handleAction('trigger-workflow')}
          disabled={sending}
        >
          <Zap size={18} />
          <div>
            <strong>Trigger workflow</strong>
            <span>Run automated data processing</span>
          </div>
        </button>
      </div>

      {status && (
        <div className={`auto-status auto-status-${status.type}`}>
          {status.type === 'success' ? <CheckCircle size={16} /> : <AlertCircle size={16} />}
          {status.message}
        </div>
      )}
    </div>
  );
};

export default AutomationPanel;
