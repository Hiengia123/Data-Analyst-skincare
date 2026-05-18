import React from 'react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

const PALETTE = [
  '#2f6f5e', '#3b82f6', '#d97706', '#ef4444', '#8b5cf6',
  '#ec4899', '#14b8a6', '#f97316', '#6366f1', '#a3a3a3',
];

const ColorBreakdownChart = ({ data }) => {
  if (!data || !data.labels || data.labels.length === 0)
    return <p className="text-muted">No color data</p>;

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '62%',
    plugins: {
      legend: {
        position: 'right',
        labels: {
          color: '#435468',
          boxWidth: 12,
          boxHeight: 12,
          usePointStyle: true,
          pointStyle: 'circle',
          padding: 14,
          font: { size: 12 },
        },
      },
      tooltip: {
        backgroundColor: '#16324f',
        titleColor: '#f8fafc',
        bodyColor: '#f8fafc',
        borderColor: '#9fb7cb',
        borderWidth: 1,
      },
    },
  };

  const chartData = {
    labels: data.labels,
    datasets: [
      {
        data: data.data,
        backgroundColor: data.labels.map((_, i) => PALETTE[i % PALETTE.length]),
        borderWidth: 2,
        borderColor: '#ffffff',
      },
    ],
  };

  return (
    <div className="chart-wrap">
      <Doughnut options={options} data={chartData} />
    </div>
  );
};

export default ColorBreakdownChart;
