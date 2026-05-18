import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
);

const DailyRevenueChart = ({ data }) => {
  if (!data || !data.labels || data.labels.length === 0)
    return <p className="text-muted">No daily data</p>;

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#16324f',
        titleColor: '#f8fafc',
        bodyColor: '#f8fafc',
        borderColor: '#9fb7cb',
        borderWidth: 1,
        callbacks: {
          label(context) {
            return `Revenue: ${new Intl.NumberFormat('vi-VN', {
              style: 'currency',
              currency: 'VND',
              maximumFractionDigits: 0,
            }).format(context.parsed.y)}`;
          },
        },
      },
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: '#5b6b7e', font: { size: 10 }, maxTicksLimit: 12 },
      },
      y: {
        grid: { color: '#e2e8f0', drawBorder: false },
        ticks: {
          color: '#5b6b7e',
          callback(value) {
            return new Intl.NumberFormat('vi-VN', {
              notation: 'compact',
              maximumFractionDigits: 1,
            }).format(value);
          },
        },
      },
    },
    interaction: { intersect: false, mode: 'index' },
  };

  const chartData = {
    labels: data.labels,
    datasets: [
      {
        data: data.data,
        borderColor: '#2f6f5e',
        backgroundColor: 'rgba(47, 111, 94, 0.08)',
        fill: true,
        tension: 0.35,
        pointRadius: 3,
        pointHoverRadius: 6,
        pointBackgroundColor: '#2f6f5e',
        borderWidth: 2,
      },
    ],
  };

  return (
    <div className="chart-wrap">
      <Line options={options} data={chartData} />
    </div>
  );
};

export default DailyRevenueChart;
