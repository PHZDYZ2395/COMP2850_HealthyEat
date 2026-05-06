/**
 * Recipe detail page logic: displays full recipe, ratings, and comments.
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

let recipeId = null;

document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    recipeId = params.get('id');
    if (!recipeId) { window.location.href = '/recipes.html'; return; }

    loadRecipe();
    loadRating();
    loadComments();

    document.getElementById('submitRating')?.addEventListener('click', submitRating);
    document.getElementById('submitComment')?.addEventListener('click', submitComment);
});

async function loadRecipe() {
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}`);
        if (!response.ok) throw new Error('Recipe not found');
        const recipe = await response.json();
        renderRecipe(recipe);
        renderRecipeInfo(recipe);
    } catch (error) {
        console.error(error);
    }
}

function renderRecipe(recipe) {
    document.getElementById('recipeContent').innerHTML = `
        <div class="card shadow-sm">
            <img src="${recipe.imageUrl || getDefaultImage('recipe')}" class="card-img-top recipe-detail-img" alt="${escapeHtml(recipe.title)}">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h2 class="card-title mb-0">${escapeHtml(recipe.title)}</h2>
                    <span class="badge bg-success fs-6">${escapeHtml(recipe.category)}</span>
                </div>
                <p class="text-muted">${escapeHtml(recipe.description)}</p>
                <hr>
                <h4><i class="fas fa-list text-success"></i> Ingredients</h4>
                <div class="ingredients-list">${formatIngredients(recipe.ingredients)}</div>
                <hr>
                <h4><i class="fas fa-fire text-success"></i> Instructions</h4>
                <div class="instructions-list">${formatInstructions(recipe.instructions)}</div>
                <div class="text-muted mt-3">
                    <small>By ${escapeHtml(recipe.createdByName)} &middot; ${new Date(recipe.createdAt).toLocaleDateString()}</small>
                </div>
            </div>
        </div>
    `;
}

function renderRecipeInfo(recipe) {
    document.getElementById('recipeInfo').innerHTML = `
        <div class="d-flex justify-content-between mb-3">
            <span><i class="fas fa-clock text-muted"></i> Prep Time</span>
            <strong>${recipe.prepTime} min</strong>
        </div>
        <div class="d-flex justify-content-between mb-3">
            <span><i class="fas fa-utensils text-muted"></i> Servings</span>
            <strong>${recipe.servings}</strong>
        </div>
        <div class="d-flex justify-content-between mb-3">
            <span><i class="fas fa-fire text-muted"></i> Calories</span>
            <strong>${recipe.calories} kcal</strong>
        </div>
        <div class="d-flex justify-content-between mb-3">
            <span><i class="fas fa-signal text-muted"></i> Difficulty</span>
            <strong>${escapeHtml(recipe.difficulty)}</strong>
        </div>
    `;
}

function formatIngredients(text) {
    return text.split('\n').filter(l => l.trim()).map(l => `<div class="mb-1"><i class="fas fa-check text-success me-2"></i>${escapeHtml(l.trim())}</div>`).join('');
}

function formatInstructions(text) {
    return text.split('\n').filter(l => l.trim()).map((l, i) => `<div class="mb-2"><strong>Step ${i + 1}:</strong> ${escapeHtml(l.trim())}</div>`).join('');
}

async function loadRating() {
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}/rating`);
        if (!response.ok) return;
        const data = await response.json();
        renderRating(data);
    } catch (error) {
        console.error(error);
    }
}

function renderRating(data) {
    const user = getUser();
    document.getElementById('ratingSection').innerHTML = `
        <div class="card shadow-sm">
            <div class="card-body">
                <h5 class="card-title"><i class="fas fa-star text-warning"></i> Rate This Recipe</h5>
                <div class="d-flex align-items-center mb-3">
                    <div class="me-3 fs-4">${renderStars(data.averageRating, 'lg')}</div>
                    <span class="text-muted">${data.averageRating.toFixed(1)} (${data.ratingCount} ratings)</span>
                </div>
                ${user ? `
                    <div class="d-flex align-items-center">
                        <span class="me-2">Your rating:</span>
                        <select class="form-select w-auto" id="userRating">
                            <option value="1">1 Star</option>
                            <option value="2">2 Stars</option>
                            <option value="3">3 Stars</option>
                            <option value="4">4 Stars</option>
                            <option value="5" ${data.userRating === 5 ? 'selected' : ''}>5 Stars</option>
                        </select>
                        <button class="btn btn-success btn-sm ms-2" id="submitRating">Submit</button>
                    </div>
                    ${data.userRating ? `<small class="text-muted mt-1 d-block">You rated this ${data.userRating} star(s)</small>` : ''}
                ` : '<p class="text-muted mb-0"><a href="login.html">Login</a> to rate this recipe.</p>'}
            </div>
        </div>
    `;
    document.getElementById('submitRating')?.addEventListener('click', submitRating);
}

async function submitRating() {
    const rating = parseInt(document.getElementById('userRating').value);
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}/rate`, {
            method: 'POST',
            body: JSON.stringify({ rating })
        });
        if (!response.ok) throw new Error('Failed to rate');
        loadRating();
    } catch (error) {
        alert(error.message);
    }
}

async function loadComments() {
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}/comments`);
        if (!response.ok) return;
        const comments = await response.json();
        renderComments(comments);
    } catch (error) {
        console.error(error);
    }
}

function renderComments(comments) {
    const user = getUser();
    const container = document.getElementById('commentSection');
    container.innerHTML = `
        <div class="card shadow-sm">
            <div class="card-body">
                <h5 class="card-title"><i class="fas fa-comments text-success"></i> Comments (${comments.length})</h5>
                ${user ? `
                    <div class="mb-3">
                        <textarea class="form-control" id="commentText" rows="3" placeholder="Write your comment..."></textarea>
                        <button class="btn btn-success btn-sm mt-2" id="submitComment">Post Comment</button>
                    </div>
                ` : '<p class="text-muted"><a href="login.html">Login</a> to comment.</p>'}
                <div id="commentsList">
                    ${comments.length === 0 ? '<p class="text-muted">No comments yet.</p>' :
                        comments.map(c => `
                            <div class="comment-item border-bottom pb-2 mb-2">
                                <div class="d-flex justify-content-between">
                                    <strong><i class="fas fa-user-circle text-success"></i> ${escapeHtml(c.userName)}</strong>
                                    <small class="text-muted">${new Date(c.createdAt).toLocaleString()}</small>
                                </div>
                                <p class="mb-0 mt-1">${escapeHtml(c.commentText)}</p>
                                ${user && user.id === c.userId ? `<button class="btn btn-sm text-danger" onclick="deleteComment(${c.id})"><i class="fas fa-trash"></i></button>` : ''}
                            </div>
                        `).join('')
                    }
                </div>
            </div>
        </div>
    `;
    document.getElementById('submitComment')?.addEventListener('click', submitComment);
}

async function submitComment() {
    const text = document.getElementById('commentText').value.trim();
    if (!text) return alert('Please enter a comment');
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}/comments`, {
            method: 'POST',
            body: JSON.stringify({ commentText: text })
        });
        if (!response.ok) throw new Error('Failed to post comment');
        document.getElementById('commentText').value = '';
        loadComments();
    } catch (error) {
        alert(error.message);
    }
}

async function deleteComment(commentId) {
    if (!confirm('Delete this comment?')) return;
    try {
        const response = await apiRequest(`${API_BASE}/recipes/${recipeId}/comments/${commentId}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete');
        loadComments();
    } catch (error) {
        alert(error.message);
    }
}
