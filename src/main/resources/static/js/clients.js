/**
 * Clients page logic for professional users.
 * Displays list of assigned clients with navigation to client diary.
 * Depends on auth.js for API requests.
 * @author COMP2850 Team
 */

document.addEventListener('DOMContentLoaded', function() {
    const user = checkRole('PROFESSIONAL');
    if (!user) return;
    loadClients();
});

/**
 * Load clients list from the API.
 */
async function loadClients() {
    try {
        const response = await apiRequest(`${API_BASE}/professional/clients`);
        if (!response.ok) throw new Error('Failed to load clients');
        const clients = await response.json();
        renderClients(clients);
    } catch (error) {
        console.error(error);
        document.getElementById('clientsList').innerHTML =
            '<p class="text-danger text-center">Failed to load clients.</p>';
    }
}

/**
 * Render the clients list as cards.
 * @param {Array} clients - client user objects
 */
function renderClients(clients) {
    const container = document.getElementById('clientsList');
    if (clients.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No clients assigned yet.</p>';
        return;
    }

    container.innerHTML = `<div class="row g-3">${clients.map(client => `
        <div class="col-md-6 col-lg-4">
            <div class="card client-card shadow-sm" onclick="window.location.href='/client-diary.html?id=${client.id}&name=${encodeURIComponent(client.fullName)}'">
                <div class="card-body">
                    <div class="d-flex align-items-center">
                        <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center text-white me-3"
                             style="width:50px;height:50px;font-size:1.2rem;">
                            ${client.fullName.charAt(0).toUpperCase()}
                        </div>
                        <div>
                            <h6 class="mb-1">${escapeHtml(client.fullName)}</h6>
                            <small class="text-muted">${escapeHtml(client.email)}</small>
                        </div>
                    </div>
                    <div class="mt-3 text-end">
                        <span class="btn btn-sm btn-outline-primary">
                            <i class="fas fa-book-open"></i> View Diary
                        </span>
                    </div>
                </div>
            </div>
        </div>
    `).join('')}</div>`;
}

/**
 * Escape HTML to prevent XSS.
 * @param {string} str - input string
 * @returns {string} escaped string
 */
function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
