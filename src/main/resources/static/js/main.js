/**
 * Global navigation and authentication state management.
 * Renders the nav bar menu items and auth section based on login status.
 * All nav menu items are defined centrally in NAV_ITEMS.
 * Used by all frontend pages.
 * @author COMP2850 Team
 */

const API_BASE = '/api';

// Central navigation menu configuration.
// Add, remove, or reorder menu items here and all pages update automatically.
const NAV_ITEMS = [
    { href: 'index.html', label: 'Home', icon: 'fas fa-home' },
    { href: 'recipes.html', label: 'Recipes', icon: 'fas fa-book-open' },
    { href: 'knowledge.html', label: 'Knowledge', icon: 'fas fa-lightbulb' },
    { href: 'food-database.html', label: 'Food Database', icon: 'fas fa-database' },
    { href: 'food-diary.html', label: 'Food Diary', icon: 'fas fa-clipboard-list' },
];

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function clearToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
}

function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

function setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
}

async function apiRequest(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return fetch(url, { ...options, headers });
}

function redirectByRole(user) {
    switch (user.role) {
        case 'SUBSCRIBER': window.location.href = '/dashboard.html'; break;
        case 'PROFESSIONAL': window.location.href = '/clients.html'; break;
        case 'ADMIN': window.location.href = '/admin.html'; break;
        default: window.location.href = '/index.html';
    }
}

function logout() {
    clearToken();
    window.location.href = '/index.html';
}

function requireAuth() {
    const user = getUser();
    if (!user || !getToken()) {
        window.location.href = '/login.html';
        return null;
    }
    return user;
}

function checkRole(requiredRole) {
    const user = requireAuth();
    if (user && user.role !== requiredRole) {
        redirectByRole(user);
    }
    return user;
}

// Initialize navigation bar on DOMContentLoaded
document.addEventListener('DOMContentLoaded', function() {
    const navMenu = document.getElementById('navMenu');
    if (navMenu) {
        renderNavMenu(navMenu);
    }

    const navAuth = document.getElementById('navAuth');
    if (navAuth) {
        renderNavAuth(navAuth);
    }

    // Setup logout button
    document.getElementById('logoutBtn')?.addEventListener('click', logout);
});

function renderNavMenu(container) {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    container.innerHTML = NAV_ITEMS.map(function(item) {
        var activeClass = item.href === currentPage ? ' active' : '';
        return '<li class="nav-item"><a class="nav-link' + activeClass + '" href="' + item.href + '">'
            + '<i class="' + item.icon + '"></i> ' + item.label + '</a></li>';
    }).join('');
}

function renderNavAuth(container) {
    var user = getUser();
    var token = getToken();
    if (user && token) {
        container.innerHTML =
            '<li class="nav-item"><a class="nav-link" href="' + getRoleLink(user.role) + '"><i class="fas fa-user"></i> My Panel</a></li>'
            + '<li class="nav-item"><a class="nav-link" href="#" id="logoutBtn"><i class="fas fa-sign-out-alt"></i> Logout</a></li>';
        document.getElementById('logoutBtn')?.addEventListener('click', function(e) { e.preventDefault(); logout(); });
    } else {
        container.innerHTML =
            '<li class="nav-item"><a class="nav-link" href="login.html"><i class="fas fa-sign-in-alt"></i> Login</a></li>'
            + '<li class="nav-item"><a class="nav-link" href="register.html"><i class="fas fa-user-plus"></i> Register</a></li>';
    }
}

function getRoleLink(role) {
    switch (role) {
        case 'SUBSCRIBER': return '/dashboard.html';
        case 'PROFESSIONAL': return '/clients.html';
        case 'ADMIN': return '/admin.html';
        default: return '/index.html';
    }
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function renderStars(rating, size = 'sm') {
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    let html = '';
    for (let i = 0; i < full; i++) html += '<i class="fas fa-star text-warning"></i>';
    if (half) html += '<i class="fas fa-star-half-alt text-warning"></i>';
    for (let i = 0; i < empty; i++) html += '<i class="far fa-star text-warning"></i>';
    return html;
}

function getDefaultImage(type) {
    const images = {
        recipe: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop',
        knowledge: 'https://images.unsplash.com/photo-1505576399279-0d754c0ce141?w=400&h=300&fit=crop',
        food: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop'
    };
    return images[type] || images.food;
}
