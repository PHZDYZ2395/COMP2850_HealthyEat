/**
 * Homepage logic: renders hero carousel, featured recipes, and popular foods.
 * Depends on main.js for API requests and navigation.
 * @author COMP2850 Team
 */

document.addEventListener('DOMContentLoaded', function() {
    loadHeroCarousel();
    loadFeaturedRecipes();
    loadPopularFoods();
});

async function loadHeroCarousel() {
    try {
        const response = await apiRequest(`${API_BASE}/knowledge/latest`);
        if (!response.ok) return;
        const articles = await response.json();
        if (articles.length === 0) {
            document.getElementById('heroCarousel').style.display = 'none';
            return;
        }

        const indicators = document.getElementById('carouselIndicators');
        const inner = document.getElementById('carouselInner');

        indicators.innerHTML = articles.map((_, i) =>
            `<button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="${i}" ${i === 0 ? 'class="active" aria-current="true"' : ''} aria-label="Slide ${i + 1}"></button>`
        ).join('');

        inner.innerHTML = articles.map((article, i) => `
            <div class="carousel-item ${i === 0 ? 'active' : ''}">
                <div class="carousel-caption-custom" style="
                    background: linear-gradient(135deg, #198754 0%, #20c997 100%);
                    min-height: 400px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                ">
                    <div class="container">
                        <div class="row align-items-center">
                            <div class="col-md-6 text-white">
                                <span class="badge bg-light text-success mb-2">${escapeHtml(article.category)}</span>
                                <h2 class="fw-bold mb-3">${escapeHtml(article.title)}</h2>
                                <p class="mb-4">${escapeHtml(article.summary)}</p>
                                <a href="knowledge-article.html?id=${article.id}" class="btn btn-light btn-lg">
                                    <i class="fas fa-book-open"></i> Read Article
                                </a>
                            </div>
                            <div class="col-md-6 d-none d-md-block">
                                <img src="${article.imageUrl || getDefaultImage('knowledge')}" alt="${escapeHtml(article.title)}"
                                     class="img-fluid rounded shadow" style="max-height: 350px; object-fit: cover;">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');

        new bootstrap.Carousel(document.getElementById('heroCarousel'), { interval: 5000 });
    } catch (error) {
        console.error('Failed to load carousel:', error);
    }
}

async function loadFeaturedRecipes() {
    try {
        const response = await apiRequest(`${API_BASE}/recipes?size=6`);
        if (!response.ok) return;
        const data = await response.json();
        const container = document.getElementById('featuredRecipes');
        container.innerHTML = data.content.map(recipe => `
            <div class="col-md-6 col-lg-4">
                <div class="card h-100 recipe-card shadow-sm" onclick="window.location.href='recipe-detail.html?id=${recipe.id}'">
                    <img src="${recipe.imageUrl || getDefaultImage('recipe')}" class="card-img-top recipe-card-img" alt="${escapeHtml(recipe.title)}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <span class="badge bg-success">${escapeHtml(recipe.category)}</span>
                            <small class="text-muted"><i class="fas fa-star text-warning"></i> ${(recipe.averageRating || 0).toFixed(1)} (${recipe.ratingCount})</small>
                        </div>
                        <h5 class="card-title text-truncate">${escapeHtml(recipe.title)}</h5>
                        <p class="card-text text-muted small">${escapeHtml(recipe.description).substring(0, 80)}...</p>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <small class="text-muted"><i class="fas fa-clock"></i> ${recipe.prepTime} min</small>
                            <small class="text-muted"><i class="fas fa-fire"></i> ${recipe.calories} kcal</small>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Failed to load recipes:', error);
    }
}

async function loadPopularFoods() {
    try {
        const response = await apiRequest(`${API_BASE}/food-database/popular`);
        if (!response.ok) return;
        const foods = await response.json();
        const container = document.getElementById('popularFoods');
        container.innerHTML = foods.map(food => `
            <div class="col-6 col-md-3">
                <div class="card food-card shadow-sm h-100">
                    <div class="card-body text-center">
                        <img src="${food.imageUrl || getDefaultImage('food')}" class="food-card-img" alt="${escapeHtml(food.name)}">
                        <h6 class="card-title mt-2 text-truncate">${escapeHtml(food.name)}</h6>
                        <span class="badge bg-warning text-dark">${food.caloriesPer100g} kcal/100g</span>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Failed to load popular foods:', error);
    }
}
