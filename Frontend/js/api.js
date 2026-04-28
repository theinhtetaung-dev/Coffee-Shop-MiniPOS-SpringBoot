/**
 * api.js – Base API configuration for Cho Khar POS
 */

const API = {
    BASE: 'http://localhost:8080/api',

    async request(method, path, body = null) {
        const opts = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body) opts.body = JSON.stringify(body);

        const res = await fetch(`${API.BASE}${path}`, opts);
        const data = await res.json().catch(() => null);
        if (!res.ok) {
            const msg = data?.message || data?.error || `Error ${res.status}`;
            throw new Error(msg);
        }
        return data;
    },

    get:    (path)       => API.request('GET',    path),
    post:   (path, body) => API.request('POST',   path, body),
    put:    (path, body) => API.request('PUT',    path, body),
    delete: (path, body) => API.request('DELETE', path, body),

    // --- Categories ---
    categories: {
        getAll:   (page, size) => API.get(`/categories?page=${page}&size=${size}`),
        create:   (body)       => API.post('/categories', body),
        find:     (code)       => API.post('/categories/find', { categoryCode: code }),
        update:   (body)       => API.put('/categories', body),
        delete:   (code)       => API.delete('/categories', { categoryCode: code }),
    },

    // --- Products ---
    products: {
        getAll:   (page, size) => API.get(`/products?page=${page}&size=${size}`),
        create:   (body)       => API.post('/products', body),
        find:     (code)       => API.post('/products/find', { productCode: code }),
        update:   (body)       => API.put('/products', body),
        delete:   (code)       => API.delete('/products', { productCode: code }),
    },

    // --- Sales ---
    sales: {
        getAll:   (page, size) => API.get(`/sales?page=${page}&size=${size}`),
        create:   (body)       => API.post('/sales', body),
        find:     (code)       => API.post('/sales/find', { saleCode: code }),
        update:   (body)       => API.put('/sales', body),
        delete:   (code)       => API.delete('/sales', { saleCode: code }),
    },
};
