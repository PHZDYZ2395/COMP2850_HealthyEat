/**
 * Recipes page logic: renders recipe list with search, filter, and pagination.
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

let currentPage = 0;

document.addEventListener('DOMContentLoaded', function() {
    loadRecipes();
    document.getElementById('searchBtn').addEventListener('click', () => { currentPage = 0; loadRecipes(); });
    document.getElementById('searchInput').addEventListener('keypress', (e) => { if (e.key === 'Enter') { currentPage = 0; loadRecipes(); } });
    document.getElementById('categoryFilter').addEventListener('change', () => { currentPage = 0; loadRecipes(); });
});

async function loadRecipes() {
    const keyword = document.getElementById('searchInput').value.trim() || null;
    const category = document.getElementById('categoryFilter').value || null;
    const params = new URLSearchParams();
    params.set('page', currentPage);
    params.set('size', '12');
    if (keyword) params.set('keyword', keyword);
    if (category) params.set('category', category);
    let url = `${API_BASE}/recipes?${params.toString()}`;

    try {
        const response = await apiRequest(url);
        if (!response.ok) throw new Error('Failed to load recipes');
        const data = await response.json();
        renderRecipes(data);
        renderPagination(data);
    } catch (error) {
        console.error(error);
    }
}

function renderRecipes(data) {
    const container = document.getElementById('recipeList');
    if (data.content.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No recipes found.</p>';
        return;
    }

    container.innerHTML = data.content.map(recipe => `
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 recipe-card shadow-sm" onclick="window.location.href='recipe-detail.html?id=${recipe.id}'">
                <img src="${recipe.imageUrl || getDefaultImage('recipe')}" class="card-img-top recipe-card-img" alt="${escapeHtml(recipe.title)}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <span class="badge bg-success">${escapeHtml(recipe.category)}</span>
                        <small class="text-muted">${renderStars(recipe.averageRating || 0)} (${recipe.ratingCount})</small>
                    </div>
                    <h5 class="card-title text-truncate">${escapeHtml(recipe.title)}</h5>
                    <p class="card-text text-muted small">${escapeHtml(recipe.description).substring(0, 100)}...</p>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <small class="text-muted"><i class="fas fa-clock"></i> ${recipe.prepTime} min</small>
                        <small class="text-muted"><i class="fas fa-fire"></i> ${recipe.calories} kcal</small>
                        <small class="text-muted"><i class="fas fa-utensils"></i> ${recipe.servings} servings</small>
                    </div>
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
    loadRecipes();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
