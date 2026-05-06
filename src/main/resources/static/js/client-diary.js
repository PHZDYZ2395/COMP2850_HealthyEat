/**
 * Client diary page logic for professional users.
 * Displays client food entries, allows giving advice, and shows advice history.
 * Depends on auth.js for API requests.
 * @author COMP2850 Team
 */

let clientId = null;

document.addEventListener('DOMContentLoaded', function() {
    const user = checkRole('PROFESSIONAL');
    if (!user) return;

    // Get client ID from URL query string
    const params = new URLSearchParams(window.location.search);
    clientId = params.get('id');
    if (!clientId) {
        window.location.href = '/clients.html';
        return;
    }

    loadClientFoodEntries();
    loadAdviceHistory();

    document.getElementById('adviceForm').addEventListener('submit', handleGiveAdvice);
    document.getElementById('filterDate').addEventListener('change', () => loadClientFoodEntries());
    document.getElementById('clearFilter').addEventListener('click', () => {
        document.getElementById('filterDate').value = '';
        loadClientFoodEntries();
    });
});

/**
 * Load client's food entries from the API.
 */
async function loadClientFoodEntries() {
    const filterDate = document.getElementById('filterDate').value;
    let url = `${API_BASE}/professional/clients/${clientId}/food-entries`;
    if (filterDate) {
        url += `?date=${filterDate}`;
    }

    try {
        const response = await apiRequest(url);
        if (!response.ok) throw new Error('Failed to load entries');
        const entries = await response.json();
        renderFoodEntries(entries);
    } catch (error) {
        console.error(error);
    }
}

/**
 * Render food entries list.
 * @param {Array} entries - food entry objects
 */
function renderFoodEntries(entries) {
    const container = document.getElementById('foodEntriesList');
    if (entries.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No food entries found.</p>';
        return;
    }

    container.innerHTML = entries.map(entry => `
        <div class="food-entry-item ${entry.mealType ? entry.mealType.toLowerCase() : ''}">
            <div>
                <strong>${escapeHtml(entry.foodName)}</strong>
                <span class="badge bg-secondary ms-2">${escapeHtml(entry.mealType || 'N/A')}</span>
                <span class="text-muted ms-2">${entry.portionSize ? escapeHtml(entry.portionSize) : ''}</span>
                <br>
                <small class="text-muted">${entry.entryDate} - ${entry.calories} kcal</small>
            </div>
        </div>
    `).join('');
}

/**
 * Handle giving advice to the client.
 * @param {Event} e - form submit event
 */
async function handleGiveAdvice(e) {
    e.preventDefault();
    const adviceText = document.getElementById('adviceText').value;

    try {
        const response = await apiRequest(`${API_BASE}/professional/clients/${clientId}/advice`, {
            method: 'POST',
            body: JSON.stringify({ adviceText })
        });
        if (!response.ok) throw new Error('Failed to send advice');
        document.getElementById('adviceText').value = '';
        loadAdviceHistory();
        alert('Advice sent successfully!');
    } catch (error) {
        alert(error.message);
    }
}

/**
 * Load advice history for this client.
 */
async function loadAdviceHistory() {
    try {
        const response = await apiRequest(`${API_BASE}/professional/clients/${clientId}/advice`);
        if (!response.ok) throw new Error('Failed to load advice history');
        const adviceList = await response.json();
        renderAdviceHistory(adviceList);
    } catch (error) {
        console.error(error);
    }
}

/**
 * Render advice history list.
 * @param {Array} adviceList - advice objects
 */
function renderAdviceHistory(adviceList) {
    const container = document.getElementById('adviceHistory');
    if (adviceList.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No advice given yet.</p>';
        return;
    }

    container.innerHTML = adviceList.map(advice => `
        <div class="advice-item">
            <small class="text-muted">${new Date(advice.createdAt).toLocaleString()}</small>
            <p class="mb-0 mt-1">${escapeHtml(advice.adviceText)}</p>
        </div>
    `).join('');
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
