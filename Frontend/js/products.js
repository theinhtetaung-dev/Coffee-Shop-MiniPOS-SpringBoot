/**
 * products.js – CRUD logic for the Products page
 */
let currentPage = 0;
let pageSize    = 10;
let totalPages  = 0;
let allCategories = [];

// ── Pre-load category dropdown ────────────────────────────────────────────────
async function loadCategoryOptions() {
    try {
        const res = await API.categories.getAll(0, 100);
        allCategories = res.data || [];
        const sel = document.getElementById('inputCategory');
        allCategories.forEach(c => {
            const opt = document.createElement('option');
            opt.value       = c.categoryId;
            opt.textContent = c.categoryName;
            sel.appendChild(opt);
        });
    } catch { /* silent */ }
}

function categoryName(id) {
    const c = allCategories.find(x => x.categoryId === id);
    return c ? c.categoryName : `Cat-${id}`;
}

// ── Load table ────────────────────────────────────────────────────────────────
async function loadProducts(page = 0) {
    currentPage = page;
    const tbody = document.getElementById('productTableBody');
    tbody.innerHTML = `<tr><td colspan="7" class="py-10 text-center">
      <div class="inline-flex items-center gap-2 text-gray-400">
        <svg class="spin w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
        </svg> Loading…
      </div></td></tr>`;

    try {
        const res  = await API.products.getAll(page, pageSize);
        const data = res.data || [];
        totalPages = Math.ceil((res.totalElements || 0) / pageSize);
        document.getElementById('totalCount').textContent = res.totalElements ?? 0;

        if (data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-10 text-center text-gray-400">No products found.</td></tr>`;
        } else {
            tbody.innerHTML = data.map((p, i) => `
              <tr class="hover:bg-amber-50 dark:hover:bg-gray-800/50 transition">
                <td class="px-5 py-3.5 text-gray-400">${page * pageSize + i + 1}</td>
                <td class="px-5 py-3.5">
                  <span class="font-mono text-xs bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 px-2 py-0.5 rounded">${p.productCode}</span>
                </td>
                <td class="px-5 py-3.5 font-medium">${p.productName}</td>
                <td class="px-5 py-3.5 font-semibold text-amber-700 dark:text-amber-400">${(p.price || 0).toLocaleString('my-MM')} K</td>
                <td class="px-5 py-3.5 text-gray-500 dark:text-gray-400">${categoryName(p.categoryId)}</td>
                <td class="px-5 py-3.5 text-center">
                  ${p.isAvailable
                    ? `<span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300">✓ Yes</span>`
                    : `<span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300">✕ No</span>`}
                </td>
                <td class="px-5 py-3.5">
                  <div class="flex items-center justify-center gap-2">
                    <button onclick="openEditModal('${p.productCode}')"
                      class="p-1.5 rounded-lg bg-blue-50 dark:bg-blue-900/40 text-blue-600 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-900 transition" title="Edit">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    </button>
                    <button onclick="deleteProduct('${p.productCode}', '${p.productName}')"
                      class="p-1.5 rounded-lg bg-red-50 dark:bg-red-900/40 text-red-600 dark:text-red-400 hover:bg-red-100 dark:hover:bg-red-900 transition" title="Delete">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    </button>
                  </div>
                </td>
              </tr>`).join('');
        }
        renderPagination('paginationContainer', currentPage, totalPages, loadProducts);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" class="py-10 text-center text-red-500">${e.message}</td></tr>`;
    }
}

// ── Modal helpers ─────────────────────────────────────────────────────────────
function openModal(title = 'Add Product') {
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('productModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('productModal').classList.add('hidden');
    document.getElementById('productForm').reset();
    document.getElementById('editCode').value = '';
}

function openEditModal(code) {
    Loading.show('Fetching product…');
    API.products.find(code)
        .then(p => {
            document.getElementById('editCode').value       = p.productCode;
            document.getElementById('inputName').value      = p.productName;
            document.getElementById('inputPrice').value     = p.price;
            document.getElementById('inputCategory').value  = p.categoryId;
            document.getElementById('inputDesc').value      = p.description || '';
            document.getElementById('inputAvailable').checked = p.isAvailable ?? true;
            openModal('Edit Product');
        })
        .catch(e => toast('error', e.message))
        .finally(() => Loading.hide());
}

// ── Delete ────────────────────────────────────────────────────────────────────
async function deleteProduct(code, name) {
    const ok = await confirmDelete(name);
    if (!ok) return;
    Loading.show('Deleting…');
    try {
        await API.products.delete(code);
        toast('success', `"${name}" deleted`);
        loadProducts(currentPage);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
}

// ── Form submit ───────────────────────────────────────────────────────────────
document.getElementById('productForm').addEventListener('submit', async e => {
    e.preventDefault();
    const code = document.getElementById('editCode').value;
    const body = {
        productName:  document.getElementById('inputName').value.trim(),
        price:        parseFloat(document.getElementById('inputPrice').value),
        categoryId:   parseInt(document.getElementById('inputCategory').value),
        description:  document.getElementById('inputDesc').value.trim(),
        isAvailable:  document.getElementById('inputAvailable').checked,
    };
    Loading.show(code ? 'Updating…' : 'Creating…');
    try {
        if (code) {
            await API.products.update({ ...body, productCode: code });
            toast('success', 'Product updated!');
        } else {
            await API.products.create(body);
            toast('success', 'Product created!');
        }
        closeModal();
        loadProducts(currentPage);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
});

// ── Event listeners ───────────────────────────────────────────────────────────
document.getElementById('btnAdd').addEventListener('click', () => openModal('Add Product'));
document.getElementById('btnCloseModal').addEventListener('click', closeModal);
document.getElementById('btnCancel').addEventListener('click', closeModal);
document.getElementById('productModal').addEventListener('click', e => { if (e.target === e.currentTarget) closeModal(); });
document.getElementById('pageSizeSelect').addEventListener('change', e => { pageSize = +e.target.value; loadProducts(0); });

// ── Boot ──────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    await loadCategoryOptions();
    loadProducts(0);
});
