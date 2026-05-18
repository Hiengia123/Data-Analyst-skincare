import React from 'react';

const KPICard = ({ title, value, note, icon: Icon, tone }) => {
  return (
    <div className={`kpi-card tone-${tone}`}>
      <div className="kpi-card-head">
        <div>
          <p className="kpi-title">{title}</p>
          <p className="kpi-value">{value}</p>
        </div>
        <div className="kpi-icon-wrap">
          <Icon size={18} />
        </div>
      </div>
      <p className="kpi-note">{note}</p>
    </div>
  );
};

export default KPICard;
