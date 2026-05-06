/**
 * Food database page logic: renders food list with search, filter, and pagination.
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

let currentPage = 0;

document.addEventListener('DOMContentLoaded', function() {
    loadFoods();
    document.getElementById('searchBtn').addEventListener('click', () => { currentPage = 0; loadFoods(); });
    document.getElementById('searchInput').addEventListener('keypress', (e) => { if (e.key === 'Enter') { currentPage = 0; loadFoods(); } });
    document.getElementById('categoryFilter').addEventListener('change', () => { currentPage = 0; loadFoods(); });
});

async function loadFoods() {
    const keyword = document.getElementById('searchInput').value.trim() || null;
    const category = document.getElementById('categoryFilter').value || null;
    const params = new URLSearchParams();
    params.set('page', currentPage);
    params.set('size', '12');
    if (keyword) params.set('keyword', keyword);
    if (category) params.set('category', category);
    let url = `${API_BASE}/food-database?${params.toString()}`;

    try {
        const response = await apiRequest(url);
        if (!response.ok) throw new Error('Failed to load foods');
        const data = await response.json();
        renderFoods(data);
        renderPagination(data);
    } catch (error) {
        console.error(error);
    }
}

function renderFoods(data) {
    const container = document.getElementById('foodList');
    if (data.content.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No foods found.</p>';
        return;
    }

    container.innerHTML = data.content.map(food => `
        <div class="col-md-6 col-lg-3">
            <div class="card food-item-card shadow-sm h-100">
                <img src="${food.imageUrl || getDefaultImage('food')}" class="card-img-top food-item-img" alt="${escapeHtml(food.name)}">
                <div class="card-body">
                    <span class="badge bg-warning text-dark mb-2">${escapeHtml(food.category)}</span>
                    <h5 class="card-title text-truncate">${escapeHtml(food.name)}</h5>
                    <div class="mb-2">
                        <span class="badge bg-danger">${food.caloriesPer100g} kcal</span>
                    </div>
                    <small class="text-muted d-block">
                        ${food.protein ? `Protein: ${food.protein}g` : ''}
                        ${food.carbs ? ` | Carbs: ${food.carbs}g` : ''}
                        ${food.fat ? ` | Fat: ${food.fat}g` : ''}
                    </small>
                </div>
            </div>
        </div>
    `).join('');
}

function renderPagination(data) {
    const container = document.getElementById('pagination');
    if (data.totalPages <= 1) { container.innerHTML = ''; return; }

    let html = '<ul class="pagination justify-content-center">';
    html += `<li class="page-item ${currentPage === 0 ? 'disabled' : ''}"><a class="page-link" href="#" onclick="goToPage(${currentPage - 1}); return false;">Previous</a></li>`;
    for (let i = 0; i < data.totalPages; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}"><a class="page-link" href="#" onclick="goToPage(${i}); return false;">${i + 1}</a></li>`;
    }
    html += `<li class="page-item ${currentPage === data.totalPages - 1 ? 'disabled' : ''}"><a class="page-link" href="#" onclick="goToPage(${currentPage + 1}); return false;">Next</a></li>`;
    html += '</ul>';
    container.innerHTML = html;
}

function goToPage(page) {
    currentPage = page;
    loadFoods();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
