/**
 * app.js – Shared utilities: theme, sidebar, loading overlay, toast, pagination
 */

// ── Theme ────────────────────────────────────────────────────────────────────
const Theme = {
    KEY: 'chokhar_theme',
    init() {
        const saved = localStorage.getItem(this.KEY) || 'light';
        this.apply(saved);
        document.getElementById('themeToggle')?.addEventListener('click', () => {
            const next = document.documentElement.classList.contains('dark') ? 'light' : 'dark';
            this.apply(next);
        });
    },
    apply(mode) {
        document.documentElement.classList.toggle('dark', mode === 'dark');
        localStorage.setItem(this.KEY, mode);
        const icon = document.getElementById('themeIcon');
        if (icon) icon.innerHTML = mode === 'dark'
            ? `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707
                M17.657 17.657l-.707-.707M6.343 6.343l-.707-.707M12 6a6 6 0 100 12A6 6 0 0012 6z"/>`
            : `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003
                9.003 0 008.354-5.646z"/>`;
    },
};

// ── Sidebar ───────────────────────────────────────────────────────────────────
const Sidebar = {
    init() {
        const toggle = document.getElementById('sidebarToggle');
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('sidebarOverlay');
        toggle?.addEventListener('click', () => {
            sidebar.classList.toggle('-translate-x-full');
            overlay.classList.toggle('hidden');
        });
        overlay?.addEventListener('click', () => {
            sidebar.classList.add('-translate-x-full');
            overlay.classList.add('hidden');
        });
        // Active link
        const path = location.pathname.split('/').pop() || 'index.html';
        document.querySelectorAll('.nav-link').forEach(a => {
            if (a.getAttribute('href') && a.getAttribute('href').includes(path)) {
                a.classList.add('active');
            }
        });
    },
};

// ── Loading Overlay ───────────────────────────────────────────────────────────
const Loading = {
    show(msg = 'Loading…') {
        let el = document.getElementById('loadingOverlay');
        if (!el) {
            el = document.createElement('div');
            el.id = 'loadingOverlay';
            el.className = 'fixed inset-0 z-50 flex flex-col items-center justify-center ' +
                'bg-black/50 modal-backdrop';
            el.innerHTML = `
              <div class="bg-white dark:bg-gray-800 rounded-2xl px-10 py-8 flex flex-col items-center gap-4 shadow-2xl">
                <svg class="spin w-10 h-10 text-amber-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
                </svg>
                <span class="text-gray-700 dark:text-gray-200 font-medium" id="loadingMsg">${msg}</span>
              </div>`;
            document.body.appendChild(el);
        } else {
            document.getElementById('loadingMsg').textContent = msg;
            el.classList.remove('hidden');
        }
    },
    hide() {
        document.getElementById('loadingOverlay')?.classList.add('hidden');
    },
};

// ── Toast (using SweetAlert2 mixin) ─────────────────────────────────────────
const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
});

function toast(icon, title) { Toast.fire({ icon, title }); }

// ── Confirm Delete ───────────────────────────────────────────────────────────
async function confirmDelete(name) {
    const result = await Swal.fire({
        title: 'Delete?',
        text: `Remove "${name}"? This cannot be undone.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#b45309',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Yes, delete',
        cancelButtonText: 'Cancel',
    });
    return result.isConfirmed;
}

// ── Pagination helper ─────────────────────────────────────────────────────────
function renderPagination(containerId, currentPage, totalPages, onPageChange) {
    const c = document.getElementById(containerId);
    if (!c) return;
    c.innerHTML = '';
    if (totalPages <= 1) return;

    const btn = (label, page, disabled = false) => {
        const b = document.createElement('button');
        b.textContent = label;
        b.disabled = disabled;
        b.className = `px-3 py-1 rounded-lg text-sm font-medium transition ${
            disabled
                ? 'bg-gray-200 dark:bg-gray-700 text-gray-400 cursor-not-allowed'
                : 'bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200 hover:bg-amber-200 dark:hover:bg-amber-800 cursor-pointer'
        }`;
        if (!disabled) b.addEventListener('click', () => onPageChange(page));
        return b;
    };

    c.appendChild(btn('« Prev', currentPage - 1, currentPage === 0));
    for (let i = 0; i < totalPages; i++) {
        const b = btn(i + 1, i, false);
        if (i === currentPage) {
            b.className = 'px-3 py-1 rounded-lg text-sm font-medium bg-amber-600 text-white cursor-default';
            b.disabled = true;
        }
        c.appendChild(b);
    }
    c.appendChild(btn('Next »', currentPage + 1, currentPage >= totalPages - 1));
}

// ── Init on every page ────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    Theme.init();
    Sidebar.init();
});
