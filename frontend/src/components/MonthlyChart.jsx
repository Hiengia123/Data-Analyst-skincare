import React from 'react';
import {
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  Title,
  Tooltip,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const MonthlyChart = ({ data }) => {
  if (!data || !data.labels) return <p className="text-muted">No data available</p>;

  const formatCurrency = (value) =>
    new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(value);

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        align: 'end',
        labels: {
          color: '#435468',
          boxWidth: 10,
          boxHeight: 10,
          usePointStyle: true,
          pointStyle: 'circle',
          padding: 18,
        },
      },
      tooltip: {
        backgroundColor: '#16324f',
        titleColor: '#f8fafc',
        bodyColor: '#f8fafc',
        borderColor: '#9fb7cb',
        borderWidth: 1,
        callbacks: {
          label(context) {
            let label = context.dataset.label || '';
            if (label) {
              label += ': ';
            }

            if (context.parsed.y !== null) {
              label += context.dataset.label === 'Revenue'
                ? formatCurrency(context.parsed.y)
                : context.parsed.y;
            }

            return label;
          },
        },
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          color: '#5b6b7e',
          font: {
            size: 11,
          },
        },
      },
      yRevenue: {
        position: 'left',
        grid: {
          color: '#e2e8f0',
          drawBorder: false,
        },
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
      yOrders: {
        position: 'right',
        grid: {
          display: false,
          drawBorder: false,
        },
        ticks: {
          color: '#7a4b16',
          precision: 0,
        },
      },
    },
  };

  const chartData = {
    labels: data.labels,
    datasets: [
      {
        label: 'Revenue',
        data: data.datasets[0].data,
        type: 'bar',
        backgroundColor: '#2f6f5e',
        borderRadius: 6,
        borderSkipped: false,
        yAxisID: 'yRevenue',
      },
      {
        label: 'Orders',
        data: data.datasets[1].data,
        type: 'line',
        borderColor: '#d97706',
        backgroundColor: '#d97706',
        yAxisID: 'yOrders',
        tension: 0.35,
        pointRadius: 3,
        pointHoverRadius: 4,
      },
    ],
  };

  return (
    <div className="chart-wrap">
      <Bar options={options} data={chartData} />
    </div>
  );
};

export default MonthlyChart;
