/**
 * dashboard.js – Logic for index.html dashboard
 */
document.addEventListener('DOMContentLoaded', async () => {

    // ── Clock ────────────────────────────────────────────────────────────────
    const clockEl = document.getElementById('liveClock');
    const dateEl  = document.getElementById('dateStr');
    const greetEl = document.getElementById('greeting');

    function tick() {
        const now = new Date();
        const h = now.getHours();
        if (clockEl) clockEl.textContent = now.toLocaleTimeString('en-US', { hour12: true });
        if (dateEl)  dateEl.textContent  = now.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
        if (greetEl) {
            const g = h < 12 ? 'Good Morning ☕' : h < 17 ? 'Good Afternoon 🌤' : 'Good Evening 🌙';
            greetEl.textContent = g;
        }
    }
    tick();
    setInterval(tick, 1000);

    // ── Fetch stats ──────────────────────────────────────────────────────────
    Loading.show('Loading dashboard…');
    try {
        const [sales, products, categories] = await Promise.all([
            API.sales.getAll(0, 100),
            API.products.getAll(0, 1),
            API.categories.getAll(0, 1),
        ]);

        // Total
        document.getElementById('statTotalSales').textContent = sales.totalElements ?? sales.data?.length ?? 0;
        document.getElementById('statProducts').textContent   = products.totalElements ?? 0;
        document.getElementById('statCategories').textContent = categories.totalElements ?? 0;

        // Revenue = sum of net_amount
        const revenue = (sales.data || []).reduce((s, x) => s + (x.netAmount || 0), 0);
        document.getElementById('statRevenue').textContent = revenue.toLocaleString('my-MM') + ' K';

        // ── Recent sales table ─────────────────────────────────────────────
        const recent = (sales.data || []).slice(0, 8);
        const tbody  = document.getElementById('recentSalesBody');
        if (recent.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="py-6 text-center text-gray-400">No sales yet.</td></tr>`;
        } else {
            tbody.innerHTML = recent.map(s => `
              <tr class="hover:bg-amber-50 dark:hover:bg-gray-800 transition">
                <td class="py-3 pr-4"><span class="font-mono text-xs bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 px-2 py-0.5 rounded">${s.saleCode}</span></td>
                <td class="py-3 pr-4">${s.customerName || '—'}</td>
                <td class="py-3 pr-4 font-semibold">${(s.netAmount || 0).toLocaleString('my-MM')} K</td>
                <td class="py-3 pr-4">
                  <span class="px-2 py-0.5 rounded-full text-xs font-medium ${payBadge(s.paymentType)}">${s.paymentType || '—'}</span>
                </td>
                <td class="py-3 text-gray-400 text-xs">${formatDate(s.createdAt)}</td>
              </tr>`).join('');
        }

        // ── Bar chart (last 7 days) ───────────────────────────────────────
        buildBarChart(sales.data || []);

        // ── Donut chart (payment types) ───────────────────────────────────
        buildDonutChart(sales.data || []);

    } catch (e) {
        toast('error', 'Failed to load dashboard: ' + e.message);
    } finally {
        Loading.hide();
    }
});

// ── Helpers ──────────────────────────────────────────────────────────────────
function payBadge(type) {
    const m = { Cash: 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300',
                KPay: 'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300',
                WavePay: 'bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300' };
    return m[type] || 'bg-gray-100 text-gray-600';
}

function formatDate(ts) {
    if (!ts) return '—';
    return new Date(ts).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function last7Labels() {
    const labels = [];
    for (let i = 6; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        labels.push(d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' }));
    }
    return labels;
}

function buildBarChart(sales) {
    const labels   = last7Labels();
    const counts   = new Array(7).fill(0);
    const revenues = new Array(7).fill(0);
    const today    = new Date(); today.setHours(23, 59, 59, 999);

    sales.forEach(s => {
        const d = new Date(s.createdAt);
        for (let i = 0; i < 7; i++) {
            const day = new Date();
            day.setDate(today.getDate() - (6 - i));
            if (d.toDateString() === day.toDateString()) {
                counts[i]++;
                revenues[i] += s.netAmount || 0;
            }
        }
    });

    const isDark = document.documentElement.classList.contains('dark');
    const gc = document.getElementById('salesChart').getContext('2d');
    new Chart(gc, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: 'Net Revenue (K)',
                data: revenues,
                backgroundColor: 'rgba(180,83,9,0.75)',
                borderRadius: 8,
                borderSkipped: false,
            }, {
                label: 'Orders',
                data: counts,
                backgroundColor: 'rgba(251,191,36,0.5)',
                borderRadius: 8,
                borderSkipped: false,
                yAxisID: 'y1',
            }]
        },
        options: {
            responsive: true,
            interaction: { mode: 'index' },
            plugins: { legend: { labels: { color: isDark ? '#e5e7eb' : '#374151' } } },
            scales: {
                x: { ticks: { color: isDark ? '#9ca3af' : '#6b7280' }, grid: { display: false } },
                y: { ticks: { color: isDark ? '#9ca3af' : '#6b7280' } },
                y1: { position: 'right', ticks: { color: isDark ? '#9ca3af' : '#6b7280' }, grid: { display: false } },
            }
        }
    });
}

function buildDonutChart(sales) {
    const map = {};
    sales.forEach(s => {
        const t = s.paymentType || 'Other';
        map[t] = (map[t] || 0) + 1;
    });
    const isDark = document.documentElement.classList.contains('dark');
    const gc = document.getElementById('paymentChart').getContext('2d');
    new Chart(gc, {
        type: 'doughnut',
        data: {
            labels: Object.keys(map),
            datasets: [{
                data: Object.values(map),
                backgroundColor: ['#b45309','#f59e0b','#8b5cf6','#10b981','#3b82f6'],
                borderWidth: 0,
            }]
        },
        options: {
            responsive: true,
            cutout: '65%',
            plugins: {
                legend: { position: 'bottom', labels: { color: isDark ? '#e5e7eb' : '#374151', padding: 14 } }
            }
        }
    });
}
