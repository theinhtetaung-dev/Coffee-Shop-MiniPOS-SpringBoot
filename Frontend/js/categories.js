/**
 * categories.js – CRUD logic for the Categories page
 */
let currentPage = 0;
let pageSize    = 10;
let totalPages  = 0;

// ── Load table ────────────────────────────────────────────────────────────────
async function loadCategories(page = 0) {
    currentPage = page;
    const tbody = document.getElementById('categoryTableBody');
    tbody.innerHTML = `<tr><td colspan="5" class="py-10 text-center">
      <div class="inline-flex items-center gap-2 text-gray-400">
        <svg class="spin w-5 h-5 text-amber-500" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
        </svg> Loading…
      </div></td></tr>`;

    try {
        const res = await API.categories.getAll(page, pageSize);
        const data = res.data || [];
        totalPages = Math.ceil((res.totalElements || 0) / pageSize);
        document.getElementById('totalCount').textContent = res.totalElements ?? 0;

        if (data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="py-10 text-center text-gray-400">No categories found.</td></tr>`;
        } else {
            tbody.innerHTML = data.map((c, i) => `
              <tr class="hover:bg-amber-50 dark:hover:bg-gray-800/50 transition">
                <td class="px-5 py-3.5 text-gray-400">${page * pageSize + i + 1}</td>
                <td class="px-5 py-3.5">
                  <span class="font-mono text-xs bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 px-2 py-0.5 rounded">${c.categoryCode}</span>
                </td>
                <td class="px-5 py-3.5 font-medium">${c.categoryName}</td>
                <td class="px-5 py-3.5 text-gray-500 dark:text-gray-400 truncate max-w-xs">${c.description || '—'}</td>
                <td class="px-5 py-3.5">
                  <div class="flex items-center justify-center gap-2">
                    <button onclick="openEditModal('${c.categoryCode}')"
                      class="p-1.5 rounded-lg bg-blue-50 dark:bg-blue-900/40 text-blue-600 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-900 transition" title="Edit">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    </button>
                    <button onclick="deleteCategory('${c.categoryCode}', '${c.categoryName}')"
                      class="p-1.5 rounded-lg bg-red-50 dark:bg-red-900/40 text-red-600 dark:text-red-400 hover:bg-red-100 dark:hover:bg-red-900 transition" title="Delete">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    </button>
                  </div>
                </td>
              </tr>`).join('');
        }
        renderPagination('paginationContainer', currentPage, totalPages, loadCategories);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="5" class="py-10 text-center text-red-500">${e.message}</td></tr>`;
    }
}

// ── Modal helpers ─────────────────────────────────────────────────────────────
function openModal(title = 'Add Category') {
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('categoryModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('categoryModal').classList.add('hidden');
    document.getElementById('categoryForm').reset();
    document.getElementById('editCode').value = '';
}

function openEditModal(code) {
    Loading.show('Fetching category…');
    API.categories.find(code)
        .then(c => {
            document.getElementById('editCode').value   = c.categoryCode;
            document.getElementById('inputName').value  = c.categoryName;
            document.getElementById('inputDesc').value  = c.description || '';
            openModal('Edit Category');
        })
        .catch(e => toast('error', e.message))
        .finally(() => Loading.hide());
}

// ── Delete ────────────────────────────────────────────────────────────────────
async function deleteCategory(code, name) {
    const ok = await confirmDelete(name);
    if (!ok) return;
    Loading.show('Deleting…');
    try {
        await API.categories.delete(code);
        toast('success', `Category "${name}" deleted`);
        loadCategories(currentPage);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
}

// ── Form submit ───────────────────────────────────────────────────────────────
document.getElementById('categoryForm').addEventListener('submit', async e => {
    e.preventDefault();
    const code = document.getElementById('editCode').value;
    const body = {
        categoryName: document.getElementById('inputName').value.trim(),
        description:  document.getElementById('inputDesc').value.trim(),
    };
    Loading.show(code ? 'Updating…' : 'Creating…');
    try {
        if (code) {
            await API.categories.update({ ...body, categoryCode: code });
            toast('success', 'Category updated!');
        } else {
            await API.categories.create(body);
            toast('success', 'Category created!');
        }
        closeModal();
        loadCategories(currentPage);
    } catch (e) {
        toast('error', e.message);
    } finally {
        Loading.hide();
    }
});

// ── Event listeners ───────────────────────────────────────────────────────────
document.getElementById('btnAddCategory').addEventListener('click', () => openModal('Add Category'));
document.getElementById('btnCloseModal').addEventListener('click', closeModal);
document.getElementById('btnCancelModal').addEventListener('click', closeModal);
document.getElementById('categoryModal').addEventListener('click', e => { if (e.target === e.currentTarget) closeModal(); });
document.getElementById('pageSizeSelect').addEventListener('change', e => { pageSize = +e.target.value; loadCategories(0); });

// ── Boot ──────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => loadCategories(0));
