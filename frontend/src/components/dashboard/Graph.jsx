import React, { useRef, useEffect, useState, useMemo } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Filler,
} from "chart.js";

ChartJS.register(
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Filler,
);

const Graph = ({ graphData }) => {
  const chartRef = useRef(null);
  const [gradient, setGradient] = useState(null);

  const sortedData = useMemo(() => {
    return [...graphData].sort(
      (a, b) => new Date(a.clickDate) - new Date(b.clickDate),
    );
  }, [graphData]);

  const labels = sortedData.map((item) => {
    const date = new Date(item.clickDate);
    return date.toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "2-digit",
    });
  });

  const clicks = sortedData.map((item) => item.count);

  useEffect(() => {
    const chart = chartRef.current;
    if (!chart) return;

    const ctx = chart.ctx;
    const gradientFill = ctx.createLinearGradient(0, 0, 0, 400);

    gradientFill.addColorStop(0, "rgba(99,102,241,0.4)");
    gradientFill.addColorStop(1, "rgba(99,102,241,0)");

    setGradient(gradientFill);
  }, []);

  const data = {
    labels,
    datasets: [
      {
        label: "Clicks",
        data: clicks,
        borderColor: "#6366F1",
        backgroundColor: gradient,
        fill: true,
        tension: 0.45,
        pointBackgroundColor: "#6366F1",
        pointBorderColor: "#fff",
        pointRadius: 0,
        pointHoverRadius: 6,
        borderWidth: 3,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: {
      mode: "index",
      intersect: false,
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: "#111827",
        padding: 12,
        titleColor: "#fff",
        bodyColor: "#d1d5db",
        borderColor: "#374151",
        borderWidth: 1,
        displayColors: false,
      },
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: "#9CA3AF" },
      },
      y: {
        beginAtZero: true,
        grid: {
          color: "rgba(255,255,255,0.05)",
        },
        ticks: { color: "#9CA3AF" },
      },
    },
  };

  return <Line ref={chartRef} data={data} options={options} />;
};

export default Graph;
