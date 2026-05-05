/**
 * Authentication utility module for HealthyEat.
 * Handles token storage, API calls with auth headers, role-based redirects, and login/register forms.
 * Used by all frontend pages for authentication.
 * @author COMP2850 Team
 */

const API_BASE = '/api';

// used GitHub Copilot to generate lines 14-30

/**
 * Get the stored JWT token from localStorage.
 * @returns {string|null} JWT token or null if not found
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Store JWT token in localStorage.
 * @param {string} token - JWT token
 */
function setToken(token) {
    localStorage.setItem('token', token);
}

/**
 * Remove JWT token from localStorage.
 */
function clearToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
}

/**
 * Get stored user info from localStorage.
 * @returns {object|null} user object or null
 */
function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Store user info in localStorage.
 * @param {object} user - user object
 */
function setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
}

/**
 * Make an authenticated API request.
 * @param {string} url - API endpoint
 * @param {object} options - fetch options
 * @returns {Promise<Response>} fetch response
 */
async function apiRequest(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return fetch(url, {
        ...options,
        headers
    });
}

/**
 * Check if user is authenticated, redirect to login if not.
 * @returns {object|null} user object or null
 */
function requireAuth() {
    const user = getUser();
    if (!user || !getToken()) {
        window.location.href = '/login.html';
        return null;
    }
    return user;
}

/**
 * Redirect user to appropriate page based on role.
 * @param {object} user - user object with role property
 */
function redirectByRole(user) {
    switch (user.role) {
        case 'SUBSCRIBER':
            window.location.href = '/dashboard.html';
            break;
        case 'PROFESSIONAL':
            window.location.href = '/clients.html';
            break;
        case 'ADMIN':
            window.location.href = '/admin.html';
            break;
        default:
            window.location.href = '/login.html';
    }
}

/**
 * Check role and redirect if mismatch.
 * @param {string} requiredRole - required role (SUBSCRIBER/PROFESSIONAL/ADMIN)
 */
function checkRole(requiredRole) {
    const user = requireAuth();
    if (user && user.role !== requiredRole) {
        redirectByRole(user);
    }
    return user;
}

/**
 * Handle logout.
 */
function logout() {
    clearToken();
    window.location.href = '/login.html';
}

// Initialize on DOMContentLoaded
document.addEventListener('DOMContentLoaded', function() {
    // Setup logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }

    // Display user name if element exists
    const userNameEl = document.getElementById('userName');
    if (userNameEl) {
        const user = getUser();
        if (user) {
            userNameEl.textContent = user.fullName;
        }
    }

    // Setup login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Setup subscriber registration form
    const subscriberForm = document.getElementById('subscriberForm');
    if (subscriberForm) {
        subscriberForm.addEventListener('submit', handleSubscriberRegister);
    }

    // Setup professional registration form
    const professionalForm = document.getElementById('professionalForm');
    if (professionalForm) {
        professionalForm.addEventListener('submit', handleProfessionalRegister);
    }
});

/**
 * Handle login form submission.
 * @param {Event} e - form submit event
 */
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorAlert = document.getElementById('errorAlert');

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error || 'Login failed');
        }
        const data = await response.json();
        setToken(data.token);
        setUser(data.user);
        redirectByRole(data.user);
    } catch (error) {
        errorAlert.textContent = error.message;
        errorAlert.classList.remove('d-none');
    }
}

/**
 * Handle subscriber registration form submission.
 * @param {Event} e - form submit event
 */
async function handleSubscriberRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('subFullName').value;
    const email = document.getElementById('subEmail').value;
    const password = document.getElementById('subPassword').value;
    const errorAlert = document.getElementById('subErrorAlert');

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullName, email, password })
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error || 'Registration failed');
        }
        const data = await response.json();
        setToken(data.token);
        setUser(data.user);
        redirectByRole(data.user);
    } catch (error) {
        errorAlert.textContent = error.message;
        errorAlert.classList.remove('d-none');
    }
}

/**
 * Handle professional registration form submission.
 * @param {Event} e - form submit event
 */
async function handleProfessionalRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('profFullName').value;
    const email = document.getElementById('profEmail').value;
    const password = document.getElementById('profPassword').value;
    const errorAlert = document.getElementById('profErrorAlert');

    try {
        const response = await fetch(`${API_BASE}/auth/register/professional`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullName, email, password })
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error || 'Registration failed');
        }
        alert('Registration successful. Your account needs admin approval before you can login.');
        window.location.href = '/login.html';
    } catch (error) {
        errorAlert.textContent = error.message;
        errorAlert.classList.remove('d-none');
    }
}
