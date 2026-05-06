/**
 * Knowledge page logic: renders article list with search, filter, and pagination.
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

let currentPage = 0;

document.addEventListener('DOMContentLoaded', function() {
    loadKnowledge();
    document.getElementById('searchBtn').addEventListener('click', () => { currentPage = 0; loadKnowledge(); });
    document.getElementById('searchInput').addEventListener('keypress', (e) => { if (e.key === 'Enter') { currentPage = 0; loadKnowledge(); } });
    document.getElementById('categoryFilter').addEventListener('change', () => { currentPage = 0; loadKnowledge(); });
});

async function loadKnowledge() {
    const keyword = document.getElementById('searchInput').value.trim() || null;
    const category = document.getElementById('categoryFilter').value || null;
    const params = new URLSearchParams();
    params.set('page', currentPage);
    params.set('size', '12');
    if (keyword) params.set('keyword', keyword);
    if (category) params.set('category', category);
    let url = `${API_BASE}/knowledge?${params.toString()}`;

    try {
        const response = await apiRequest(url);
        if (!response.ok) throw new Error('Failed to load articles');
        const data = await response.json();
        renderKnowledge(data);
        renderPagination(data);
    } catch (error) {
        console.error(error);
    }
}

function renderKnowledge(data) {
    const container = document.getElementById('knowledgeList');
    if (data.content.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No articles found.</p>';
        return;
    }

    container.innerHTML = data.content.map(article => `
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 knowledge-card shadow-sm" onclick="window.location.href='knowledge-article.html?id=${article.id}'">
                <img src="${article.imageUrl || getDefaultImage('knowledge')}" class="card-img-top knowledge-card-img" alt="${escapeHtml(article.title)}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <span class="badge bg-info">${escapeHtml(article.category)}</span>
                        <small class="text-muted">${renderStars(article.averageRating || 0)} (${article.ratingCount})</small>
                    </div>
                    <h5 class="card-title text-truncate">${escapeHtml(article.title)}</h5>
                    <p class="card-text text-muted small">${escapeHtml(article.summary).substring(0, 100)}...</p>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <small class="text-muted"><i class="fas fa-user"></i> ${escapeHtml(article.authorName)}</small>
                        <small class="text-muted"><i class="fas fa-calendar"></i> ${new Date(article.createdAt).toLocaleDateString()}</small>
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
    loadKnowledge();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
