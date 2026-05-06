/**
 * Knowledge article detail page logic: displays full article, ratings, and comments.
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

let articleId = null;

document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    articleId = params.get('id');
    if (!articleId) { window.location.href = '/knowledge.html'; return; }

    loadArticle();
    loadRating();
    loadComments();

    document.getElementById('submitRating')?.addEventListener('click', submitRating);
    document.getElementById('submitComment')?.addEventListener('click', submitComment);
});

async function loadArticle() {
    try {
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}`);
        if (!response.ok) throw new Error('Article not found');
        const article = await response.json();
        renderArticle(article);
    } catch (error) {
        console.error(error);
    }
}

function renderArticle(article) {
    document.getElementById('articleContent').innerHTML = `
        <div class="card shadow-sm">
            <img src="${article.imageUrl || getDefaultImage('knowledge')}" class="card-img-top article-detail-img" alt="${escapeHtml(article.title)}">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h2 class="card-title mb-0">${escapeHtml(article.title)}</h2>
                    <span class="badge bg-info fs-6">${escapeHtml(article.category)}</span>
                </div>
                <p class="text-muted mb-4">${escapeHtml(article.summary)}</p>
                <div class="article-body">${article.content}</div>
                <hr>
                <div class="text-muted">
                    <small>By ${escapeHtml(article.authorName)} &middot; Published ${new Date(article.createdAt).toLocaleDateString()}</small>
                </div>
            </div>
        </div>
    `;
}

async function loadRating() {
    try {
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}/rating`);
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
                <h5 class="card-title"><i class="fas fa-star text-warning"></i> Rate This Article</h5>
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
                        <button class="btn btn-info btn-sm ms-2" id="submitRating">Submit</button>
                    </div>
                    ${data.userRating ? `<small class="text-muted mt-1 d-block">You rated this ${data.userRating} star(s)</small>` : ''}
                ` : '<p class="text-muted mb-0"><a href="login.html">Login</a> to rate this article.</p>'}
            </div>
        </div>
    `;
    document.getElementById('submitRating')?.addEventListener('click', submitRating);
}

async function submitRating() {
    const rating = parseInt(document.getElementById('userRating').value);
    try {
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}/rate`, {
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
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}/comments`);
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
                <h5 class="card-title"><i class="fas fa-comments text-info"></i> Comments (${comments.length})</h5>
                ${user ? `
                    <div class="mb-3">
                        <textarea class="form-control" id="commentText" rows="3" placeholder="Write your comment..."></textarea>
                        <button class="btn btn-info btn-sm mt-2" id="submitComment">Post Comment</button>
                    </div>
                ` : '<p class="text-muted"><a href="login.html">Login</a> to comment.</p>'}
                <div id="commentsList">
                    ${comments.length === 0 ? '<p class="text-muted">No comments yet.</p>' :
                        comments.map(c => `
                            <div class="comment-item border-bottom pb-2 mb-2">
                                <div class="d-flex justify-content-between">
                                    <strong><i class="fas fa-user-circle text-info"></i> ${escapeHtml(c.userName)}</strong>
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
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}/comments`, {
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
        const response = await apiRequest(`${API_BASE}/knowledge/${articleId}/comments/${commentId}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete');
        loadComments();
    } catch (error) {
        alert(error.message);
    }
}
