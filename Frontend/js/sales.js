/**
 * sales.js – CRUD logic for the Sales page
 */
let currentPage = 0;
let pageSize    = 10;
let totalPages  = 0;
let allProducts = [];

// ── Pre-load products for items dropdown ──────────────────────────────────────
async function loadProductOptions() {
    try {
        const res = await API.products.getAll(0, 200);
        allProducts = (res.data || []).filter(p => p.isAvailable);
    } catch { /* silent */ }
}

// ── Load table ────────────────────────────────────────────────────────────────
async function loadSales(page = 0) {
    currentPage = page;
    const tbody = document.getElementById('salesTableBody');
    tbody.innerHTML = `<tr><td colspan="9" class="py-10 text-center">
      <div class="inline-flex items-center gap-2 text-gray-400">
        <svg class="spin w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
        </svg> Loading…
      </div></td></tr>`;

    try {
        const res  = await API.sales.getAll(page, pageSize);
        const data = res.data || [];
        totalPages = Math.ceil((res.totalElements || 0) / pageSize);
        document.getElementById('totalCount').textContent = res.totalElements ?? 0;

        if (data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" class="py-10 text-center text-gray-400">No sales found.</td></tr>`;
        } else {
            tbody.innerHTML = data.map((s, i) => `
              <tr class="hover:bg-amber-50 dark:hover:bg-gray-800/50 transition">
                <td class="px-5 py-3.5 text-gray-400">${page * pageSize + i + 1}</td>
                <td class="px-5 py-3.5">
                  <span class="font-mono text-xs bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 px-2 py-0.5 rounded">${s.saleCode}</span>
                </td>
                <td class="px-5 py-3.5 font-medium">${s.customerName || '—'}</td>
                <td class="px-5 py-3.5 text-right">${(s.totalAmount || 0).toLocaleString('my-MM')} K</td>
                <td class="px-5 py-3.5 text-right text-red-500 dark:text-red-400">${(s.discountAmount || 0).toLocaleString('my-MM')} K</td>
                <td class="px-5 py-3.5 text-right font-bold text-green-700 dark:text-green-400">${(s.netAmount || 0).toLocaleString('my-MM')} K</td>
                <td class="px-5 py-3.5 text-center">
                  <span class="px-2 py-0.5 rounded-full text-xs font-medium ${payBadge(s.paymentType)}">${s.paymentType || '—'}</span>
                </td>
                <td class="px-5 py-3.5 text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">${formatDate(s.createdAt)}</td>
                <td class="px-5 py-3.5">
                  <div class="flex items-center justify-center gap-2">
                    <button onclick="viewSale('${s.saleCode}')"
                      class="p-1.5 rounded-lg bg-green-50 dark:bg-green-900/40 text-green-600 dark:text-green-400 hover:bg-green-100 dark:hover:bg-green-900 transition" title="View">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                    </button>
                    <button onclick="deleteSale('${s.saleCode}')"
                      class="p-1.5 rounded-lg bg-red-50 dark:bg-red-900/40 text-red-600 dark:text-red-400 hover:bg-red-100 dark:hover:bg-red-900 transition" title="Delete">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    </button>
                  </div>
                </td>
              </tr>`).join('');
        }
        renderPagination('paginationContainer', currentPage, totalPages, loadSales);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="9" class="py-10 text-center text-red-500">${e.message}</td></tr>`;
    }
}

// ── View sale detail ──────────────────────────────────────────────────────────
async function viewSale(code) {
    Loading.show('Loading sale…');
    try {
        const s = await API.sales.find(code);
        document.getElementById('detailTitle').textContent = `Sale: ${s.saleCode}`;
        document.getElementById('detailBody').innerHTML = `
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="bg-gray-50 dark:bg-gray-800 rounded-xl p-3">
              <p class="text-xs text-gray-400 mb-0.5">Customer</p>
              <p class="font-semibold text-gray-800 dark:text-gray-100">${s.customerName || 'Walk-in'}</p>
            </div>
            <div class="bg-gray-50 dark:bg-gray-800 rounded-xl p-3">
              <p class="text-xs text-gray-400 mb-0.5">Payment</p>
              <p class="font-semibold text-gray-800 dark:text-gray-100">${s.paymentType || '—'}</p>
            </div>
            <div class="bg-gray-50 dark:bg-gray-800 rounded-xl p-3">
              <p class="text-xs text-gray-400 mb-0.5">Date</p>
              <p class="font-semibold text-gray-800 dark:text-gray-100">${formatDate(s.createdAt)}</p>
            </div>
            <div class="bg-amber-50 dark:bg-amber-900/40 rounded-xl p-3">
              <p class="text-xs text-amber-600 dark:text-amber-400 mb-0.5">Net Amount</p>
              <p class="font-bold text-amber-800 dark:text-amber-300">${(s.netAmount || 0).toLocaleString('my-MM')} K</p>
            </div>
          </div>
          <div>
            <p class="text-sm font-semibold text-gray-600 dark:text-gray-400 mb-2">Items</p>
            <div class="rounded-xl overflow-hidden border border-gray-100 dark:border-gray-800">
              <table class="w-full text-xs">
                <thead class="bg-gray-50 dark:bg-gray-800 text-gray-400">
                  <tr>
                    <th class="px-4 py-2 text-left">Product</th>
                    <th class="px-4 py-2 text-right">Price</th>
                    <th class="px-4 py-2 text-right">Qty</th>
                    <th class="px-4 py-2 text-right">Sub</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-50 dark:divide-gray-800 text-gray-700 dark:text-gray-300">
                  ${(s.items || []).map(item => `
                    <tr>
                      <td class="px-4 py-2">${item.productName || 'Product'}</td>
                      <td class="px-4 py-2 text-right">${(item.unitPrice || 0).toLocaleString('my-MM')} K</td>
                      <td class="px-4 py-2 text-right">${item.quantity}</td>
                      <td class="px-4 py-2 text-right font-semibold">${(item.subAmount || 0).toLocaleString('my-MM')} K</td>
                    </tr>`).join('') || '<tr><td colspan="4" class="px-4 py-3 text-center text-gray-400">No items</td></tr>'}
                </tbody>
              </table>
            </div>
          </div>`;
        document.getElementById('detailModal').classList.remove('hidden');
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
}

// ── Delete ────────────────────────────────────────────────────────────────────
async function deleteSale(code) {
    const ok = await confirmDelete(code);
    if (!ok) return;
    Loading.show('Deleting…');
    try {
        await API.sales.delete(code);
        toast('success', `Sale "${code}" deleted`);
        loadSales(currentPage);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
}

// ── Sale Items builder ────────────────────────────────────────────────────────
let itemCount = 0;

function addItemRow(productId = '', qty = 1) {
    itemCount++;
    const id  = itemCount;
    const row = document.createElement('div');
    row.id        = `item-row-${id}`;
    row.className = 'flex items-center gap-2';
    row.innerHTML = `
      <select onchange="updateSummary()" data-role="product"
        class="flex-1 border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 rounded-xl px-3 py-2 text-sm text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-500">
        <option value="">— Product —</option>
        ${allProducts.map(p => `<option value="${p.productId}" data-price="${p.price}" ${p.productId == productId ? 'selected' : ''}>${p.productName} (${(p.price||0).toLocaleString('my-MM')} K)</option>`).join('')}
      </select>
      <input type="number" min="1" value="${qty}" onchange="updateSummary()" data-role="qty"
        class="w-20 border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 rounded-xl px-3 py-2 text-sm text-gray-800 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-amber-500" placeholder="Qty"/>
      <button type="button" onclick="document.getElementById('item-row-${id}').remove(); updateSummary();"
        class="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
      </button>`;
    document.getElementById('itemsContainer').appendChild(row);
    updateSummary();
}

function updateSummary() {
    let total    = 0;
    const discount = parseFloat(document.getElementById('inputDiscount').value) || 0;
    document.querySelectorAll('#itemsContainer > div').forEach(row => {
        const sel = row.querySelector('[data-role="product"]');
        const qty = parseFloat(row.querySelector('[data-role="qty"]').value) || 0;
        const opt = sel.selectedOptions[0];
        const price = opt ? parseFloat(opt.dataset.price || 0) : 0;
        total += price * qty;
    });
    document.getElementById('summaryTotal').textContent    = total.toLocaleString('my-MM') + ' K';
    document.getElementById('summaryDiscount').textContent = discount.toLocaleString('my-MM') + ' K';
    document.getElementById('summaryNet').textContent      = Math.max(0, total - discount).toLocaleString('my-MM') + ' K';
}

// ── Modal helpers ─────────────────────────────────────────────────────────────
function openModal() {
    document.getElementById('itemsContainer').innerHTML = '';
    itemCount = 0;
    document.getElementById('saleForm').reset();
    document.getElementById('editCode').value = '';
    document.getElementById('modalTitle').textContent = 'New Sale';
    addItemRow();
    updateSummary();
    document.getElementById('saleModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('saleModal').classList.add('hidden');
}

// ── Form submit ───────────────────────────────────────────────────────────────
document.getElementById('saleForm').addEventListener('submit', async e => {
    e.preventDefault();
    const items = [];
    let total = 0;
    document.querySelectorAll('#itemsContainer > div').forEach(row => {
        const sel   = row.querySelector('[data-role="product"]');
        const qty   = parseInt(row.querySelector('[data-role="qty"]').value) || 0;
        const opt   = sel.selectedOptions[0];
        const price = opt ? parseFloat(opt.dataset.price || 0) : 0;
        if (sel.value && qty > 0) {
            items.push({ productId: parseInt(sel.value), quantity: qty, unitPrice: price, subAmount: price * qty });
            total += price * qty;
        }
    });

    if (items.length === 0) { toast('warning', 'Add at least one item'); return; }

    const discount = parseFloat(document.getElementById('inputDiscount').value) || 0;
    const body = {
        customerName:   document.getElementById('inputCustomer').value.trim() || 'Walk-in',
        paymentType:    document.getElementById('inputPayment').value,
        totalAmount:    total,
        discountAmount: discount,
        netAmount:      Math.max(0, total - discount),
        items,
    };

    Loading.show('Saving sale…');
    try {
        await API.sales.create(body);
        toast('success', 'Sale recorded!');
        closeModal();
        loadSales(0);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
});

// ── Helpers ───────────────────────────────────────────────────────────────────
function payBadge(type) {
    const m = { Cash: 'bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300',
                KPay: 'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300',
                WavePay: 'bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300',
                Card: 'bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-300' };
    return m[type] || 'bg-gray-100 text-gray-600';
}

function formatDate(ts) {
    if (!ts) return '—';
    return new Date(ts).toLocaleString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

// ── Search by Sale Code ───────────────────────────────────────────────────────
let isSearchMode = false;

async function searchBySaleCode() {
    const code = document.getElementById('searchSaleCode').value.trim();
    if (!code) { clearSearch(); return; }

    const clearBtn = document.getElementById('btnClearSearch');
    clearBtn.classList.remove('hidden');

    const tbody = document.getElementById('salesTableBody');
    tbody.innerHTML = `<tr><td colspan="9" class="py-10 text-center">
      <div class="inline-flex items-center gap-2 text-gray-400">
        <svg class="spin w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
        </svg> Searching…
      </div></td></tr>`;

    try {
        const s = await API.sales.find(code);
        isSearchMode = true;
        document.getElementById('totalCount').textContent = '1 (search result)';
        document.getElementById('paginationContainer').innerHTML = '';

        tbody.innerHTML = `
          <tr class="hover:bg-amber-50 dark:hover:bg-gray-800/50 transition">
            <td class="px-5 py-3.5 text-gray-400">1</td>
            <td class="px-5 py-3.5">
              <span class="font-mono text-xs bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 px-2 py-0.5 rounded">${s.saleCode}</span>
            </td>
            <td class="px-5 py-3.5 font-medium">${s.customerName || '—'}</td>
            <td class="px-5 py-3.5 text-right">${(s.totalAmount || 0).toLocaleString('my-MM')} K</td>
            <td class="px-5 py-3.5 text-right text-red-500 dark:text-red-400">${(s.discountAmount || 0).toLocaleString('my-MM')} K</td>
            <td class="px-5 py-3.5 text-right font-bold text-green-700 dark:text-green-400">${(s.netAmount || 0).toLocaleString('my-MM')} K</td>
            <td class="px-5 py-3.5 text-center">
              <span class="px-2 py-0.5 rounded-full text-xs font-medium ${payBadge(s.paymentType)}">${s.paymentType || '—'}</span>
            </td>
            <td class="px-5 py-3.5 text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">${formatDate(s.createdAt)}</td>
            <td class="px-5 py-3.5">
              <div class="flex items-center justify-center gap-2">
                <button onclick="viewSale('${s.saleCode}')"
                  class="p-1.5 rounded-lg bg-green-50 dark:bg-green-900/40 text-green-600 dark:text-green-400 hover:bg-green-100 dark:hover:bg-green-900 transition" title="View">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                </button>
                <button onclick="deleteSale('${s.saleCode}')"
                  class="p-1.5 rounded-lg bg-red-50 dark:bg-red-900/40 text-red-600 dark:text-red-400 hover:bg-red-100 dark:hover:bg-red-900 transition" title="Delete">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                </button>
              </div>
            </td>
          </tr>`;
    } catch (e) {
        isSearchMode = true;
        document.getElementById('totalCount').textContent = '0 (search result)';
        document.getElementById('paginationContainer').innerHTML = '';
        tbody.innerHTML = `<tr><td colspan="9" class="py-10 text-center">
          <div class="flex flex-col items-center gap-2 text-gray-400">
            <svg class="w-10 h-10 text-gray-300 dark:text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
            <p class="text-sm">No sale found for code "<strong class="text-gray-600 dark:text-gray-300">${code}</strong>"</p>
          </div></td></tr>`;
    }
}

function clearSearch() {
    isSearchMode = false;
    document.getElementById('searchSaleCode').value = '';
    document.getElementById('btnClearSearch').classList.add('hidden');
    loadSales(0);
}

// ── Event listeners ───────────────────────────────────────────────────────────
document.getElementById('btnAdd').addEventListener('click', openModal);
document.getElementById('btnCloseModal').addEventListener('click', closeModal);
document.getElementById('btnCancel').addEventListener('click', closeModal);
document.getElementById('saleModal').addEventListener('click', e => { if (e.target === e.currentTarget) closeModal(); });
document.getElementById('btnCloseDetail').addEventListener('click', () => document.getElementById('detailModal').classList.add('hidden'));
document.getElementById('detailModal').addEventListener('click', e => { if (e.target === e.currentTarget) document.getElementById('detailModal').classList.add('hidden'); });
document.getElementById('btnAddItem').addEventListener('click', () => addItemRow());
document.getElementById('inputDiscount').addEventListener('input', updateSummary);
document.getElementById('pageSizeSelect').addEventListener('change', e => { pageSize = +e.target.value; loadSales(0); });

// Search event listeners
document.getElementById('searchSaleCode').addEventListener('keydown', e => {
    if (e.key === 'Enter') { e.preventDefault(); searchBySaleCode(); }
});
document.getElementById('searchSaleCode').addEventListener('input', e => {
    const val = e.target.value.trim();
    document.getElementById('btnClearSearch').classList.toggle('hidden', !val);
    if (!val && isSearchMode) clearSearch();
});
document.getElementById('btnClearSearch').addEventListener('click', clearSearch);

// ── Boot ──────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    await loadProductOptions();
    loadSales(0);
});
