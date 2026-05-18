import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

const fmt = (v) =>
  new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(v ?? 0);

const truncate = (str = '', max = 24) =>
  str.length > max ? str.slice(0, max) + '…' : str;

const COLOURS = [
  '#2f6f5e', '#3b82f6', '#d97706', '#ef4444',
  '#8b5cf6', '#ec4899', '#14b8a6', '#f97316',
  '#6366f1', '#059669',
];

const TopProductsChart = ({ data }) => {
  if (!data || !data.products || data.products.length === 0)
    return <p className="text-muted" style={{ padding: '32px', textAlign: 'center' }}>Chưa có dữ liệu sản phẩm</p>;

  const products = data.products;

  const options = {
    indexAxis: 'y',
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#16324f',
        titleColor: '#f8fafc',
        bodyColor: '#cbd5e1',
        borderColor: '#334155',
        borderWidth: 1,
        padding: 12,
        callbacks: {
          title(ctx) {
            return products[ctx[0].dataIndex]?.title ?? '';
          },
          label(ctx) {
            const p = products[ctx.dataIndex];
            return [
              `  Doanh thu : ${fmt(p.total_revenue)}`,
              `  Số lượng   : ${p.total_qty}`,
              `  Đơn hàng   : ${p.total_orders}`,
              `  Thương hiệu: ${p.category_title}`,
            ];
          },
        },
      },
    },
    scales: {
      x: {
        grid: { color: 'rgba(0,0,0,0.05)' },
        ticks: {
          color: '#5b6b7e',
          font: { size: 11 },
          callback(value) {
            return new Intl.NumberFormat('vi-VN', {
              notation: 'compact',
              maximumFractionDigits: 1,
            }).format(value);
          },
        },
      },
      y: {
        grid: { display: false },
        ticks: {
          color: '#435468',
          font: { size: 11 },
        },
      },
    },
  };

  const chartData = {
    labels: products.map((p) => truncate(p.title)),
    datasets: [
      {
        label: 'Doanh thu (VNĐ)',
        data: products.map((p) => parseFloat(p.total_revenue ?? 0)),
        backgroundColor: products.map((_, i) => COLOURS[i % COLOURS.length] + 'cc'),
        borderColor:     products.map((_, i) => COLOURS[i % COLOURS.length]),
        borderWidth: 1.5,
        borderRadius: 5,
        borderSkipped: false,
        barThickness: 20,
      },
    ],
  };

  return (
    <div className="chart-wrap chart-wrap-tall">
      <Bar options={options} data={chartData} />
    </div>
  );
};

export default TopProductsChart;
