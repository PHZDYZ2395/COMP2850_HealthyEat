/**
 * Shared content management logic for recipes, knowledge articles, and food database.
 * Used by both admin panel (admin.html) and professional panel (clients.html).
 * Depends on main.js for API requests.
 * @author COMP2850 Team
 */

// ==================== Image Upload ====================

async function uploadImage(fileInputId, urlInputId) {
    const fileInput = document.getElementById(fileInputId);
    if (!fileInput.files.length) return alert('Please select a file first');

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const response = await fetch(`${API_BASE}/admin/upload-image`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${getToken()}` },
            body: formData
        });
        if (!response.ok) throw new Error('Upload failed');
        const data = await response.json();
        document.getElementById(urlInputId).value = data.url;
        fileInput.value = '';
        alert('Image uploaded successfully!');
    } catch (err) { alert(err.message); }
}

// ==================== Recipe Management ====================

async function loadRecipes() {
    try {
        const r = await apiRequest(`${API_BASE}/recipes?size=100`);
        if (!r.ok) return;
        const data = await r.json();
        renderRecipes(data.content);
    } catch (e) { console.error(e); }
}

function renderRecipes(recipes) {
    const tbody = document.getElementById('recipesTableBody');
    if (!tbody) return;
    if (!recipes.length) { tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No recipes.</td></tr>'; return; }
    tbody.innerHTML = recipes.map(r => `
        <tr>
            <td>${r.id}</td>
            <td><img src="${r.imageUrl || getDefaultImage('recipe')}" alt="" style="width:60px;height:40px;object-fit:cover;border-radius:4px;"></td>
            <td>${escapeHtml(r.title)}</td>
            <td><span class="badge bg-success">${escapeHtml(r.category)}</span></td>
            <td>${r.calories}</td>
            <td>${r.prepTime} min</td>
            <td>
                <button class="btn btn-sm btn-outline-primary me-1" onclick="openRecipeModal(${r.id})"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteRecipe(${r.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function openRecipeModal(id) {
    document.getElementById('recipeForm').reset();
    document.getElementById('recipeId').value = '';
    document.getElementById('recipeModalLabel').textContent = 'Add Recipe';

    if (id) {
        document.getElementById('recipeModalLabel').textContent = 'Edit Recipe';
        fetch(`${API_BASE}/recipes/${id}`).then(r => r.json()).then(r => {
            document.getElementById('recipeId').value = r.id;
            document.getElementById('recipeTitle').value = r.title;
            document.getElementById('recipeCategory').value = r.category;
            document.getElementById('recipeDifficulty').value = r.difficulty;
            document.getElementById('recipePrepTime').value = r.prepTime;
            document.getElementById('recipeServings').value = r.servings;
            document.getElementById('recipeCalories').value = r.calories;
            document.getElementById('recipeDescription').value = r.description;
            document.getElementById('recipeIngredients').value = r.ingredients;
            document.getElementById('recipeInstructions').value = r.instructions;
            document.getElementById('recipeImageUrl').value = r.imageUrl || '';
        });
    }
    new bootstrap.Modal(document.getElementById('recipeModal')).show();
}

async function saveRecipe() {
    const id = document.getElementById('recipeId').value;
    const body = {
        title: document.getElementById('recipeTitle').value,
        category: document.getElementById('recipeCategory').value,
        difficulty: document.getElementById('recipeDifficulty').value,
        prepTime: parseInt(document.getElementById('recipePrepTime').value),
        servings: parseInt(document.getElementById('recipeServings').value),
        calories: parseInt(document.getElementById('recipeCalories').value),
        description: document.getElementById('recipeDescription').value,
        ingredients: document.getElementById('recipeIngredients').value,
        instructions: document.getElementById('recipeInstructions').value,
        imageUrl: document.getElementById('recipeImageUrl').value || null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/admin/recipes/${id}` : `${API_BASE}/admin/recipes`;
        const r = await apiRequest(url, { method, body: JSON.stringify(body) });
        if (r.ok) {
            bootstrap.Modal.getInstance(document.getElementById('recipeModal')).hide();
            loadRecipes();
        }
    } catch (e) { alert(e.message); }
}

async function deleteRecipe(id) {
    if (!confirm('Delete this recipe?')) return;
    try {
        const r = await apiRequest(`${API_BASE}/admin/recipes/${id}`, { method: 'DELETE' });
        if (r.ok) loadRecipes();
    } catch (e) { alert(e.message); }
}

// ==================== Knowledge Management ====================

async function loadKnowledge() {
    try {
        const r = await apiRequest(`${API_BASE}/knowledge?size=100`);
        if (!r.ok) return;
        const data = await r.json();
        renderKnowledge(data.content);
    } catch (e) { console.error(e); }
}

function renderKnowledge(articles) {
    const tbody = document.getElementById('knowledgeTableBody');
    if (!tbody) return;
    if (!articles.length) { tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No articles.</td></tr>'; return; }
    tbody.innerHTML = articles.map(a => `
        <tr>
            <td>${a.id}</td>
            <td><img src="${a.imageUrl || getDefaultImage('knowledge')}" alt="" style="width:60px;height:40px;object-fit:cover;border-radius:4px;"></td>
            <td>${escapeHtml(a.title)}</td>
            <td><span class="badge bg-info">${escapeHtml(a.category)}</span></td>
            <td><span class="badge bg-${a.published ? 'success' : 'secondary'}">${a.published ? 'Yes' : 'No'}</span></td>
            <td><small>${new Date(a.createdAt).toLocaleDateString()}</small></td>
            <td>
                <button class="btn btn-sm btn-outline-primary me-1" onclick="openKnowledgeModal(${a.id})"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteKnowledge(${a.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function openKnowledgeModal(id) {
    document.getElementById('knowledgeForm').reset();
    document.getElementById('knowledgeId').value = '';
    document.getElementById('knowledgeModalLabel').textContent = 'Add Article';
    document.getElementById('knowledgePublished').checked = true;

    if (id) {
        document.getElementById('knowledgeModalLabel').textContent = 'Edit Article';
        fetch(`${API_BASE}/knowledge/${id}`).then(r => r.json()).then(a => {
            document.getElementById('knowledgeId').value = a.id;
            document.getElementById('knowledgeTitle').value = a.title;
            document.getElementById('knowledgeCategory').value = a.category;
            document.getElementById('knowledgeSummary').value = a.summary;
            document.getElementById('knowledgeContent').value = a.content;
            document.getElementById('knowledgePublished').checked = a.published;
            document.getElementById('knowledgeImageUrl').value = a.imageUrl || '';
        });
    }
    new bootstrap.Modal(document.getElementById('knowledgeModal')).show();
}

async function saveKnowledge() {
    const id = document.getElementById('knowledgeId').value;
    const body = {
        title: document.getElementById('knowledgeTitle').value,
        category: document.getElementById('knowledgeCategory').value,
        summary: document.getElementById('knowledgeSummary').value,
        content: document.getElementById('knowledgeContent').value,
        imageUrl: document.getElementById('knowledgeImageUrl').value || null,
        published: document.getElementById('knowledgePublished').checked
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/admin/knowledge/${id}` : `${API_BASE}/admin/knowledge`;
        const r = await apiRequest(url, { method, body: JSON.stringify(body) });
        if (r.ok) {
            bootstrap.Modal.getInstance(document.getElementById('knowledgeModal')).hide();
            loadKnowledge();
        }
    } catch (e) { alert(e.message); }
}

async function deleteKnowledge(id) {
    if (!confirm('Delete this article?')) return;
    try {
        const r = await apiRequest(`${API_BASE}/admin/knowledge/${id}`, { method: 'DELETE' });
        if (r.ok) loadKnowledge();
    } catch (e) { alert(e.message); }
}

// ==================== Food DB Management ====================

async function loadFoodDB() {
    try {
        const r = await apiRequest(`${API_BASE}/food-database?size=100`);
        if (!r.ok) return;
        const data = await r.json();
        renderFoodDB(data.content);
    } catch (e) { console.error(e); }
}

function renderFoodDB(foods) {
    const tbody = document.getElementById('fooddbTableBody');
    if (!tbody) return;
    if (!foods.length) { tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No foods.</td></tr>'; return; }
    tbody.innerHTML = foods.map(f => `
        <tr>
            <td>${f.id}</td>
            <td><img src="${f.imageUrl || getDefaultImage('food')}" alt="" style="width:60px;height:40px;object-fit:cover;border-radius:4px;"></td>
            <td>${escapeHtml(f.name)}</td>
            <td><span class="badge bg-warning text-dark">${escapeHtml(f.category)}</span></td>
            <td>${f.caloriesPer100g}</td>
            <td>${f.protein ?? '-'}g</td>
            <td>
                <button class="btn btn-sm btn-outline-primary me-1" onclick="openFoodModal(${f.id})"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteFood(${f.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function openFoodModal(id) {
    document.getElementById('foodForm').reset();
    document.getElementById('foodId').value = '';
    document.getElementById('foodModalLabel').textContent = 'Add Food';

    if (id) {
        document.getElementById('foodModalLabel').textContent = 'Edit Food';
        fetch(`${API_BASE}/food-database/${id}`).then(r => r.json()).then(f => {
            document.getElementById('foodId').value = f.id;
            document.getElementById('foodName').value = f.name;
            document.getElementById('foodCategory').value = f.category;
            document.getElementById('foodCalories').value = f.caloriesPer100g;
            document.getElementById('foodProtein').value = f.protein || '';
            document.getElementById('foodCarbs').value = f.carbs || '';
            document.getElementById('foodFat').value = f.fat || '';
            document.getElementById('foodFiber').value = f.fiber || '';
            document.getElementById('foodImageUrl').value = f.imageUrl || '';
        });
    }
    new bootstrap.Modal(document.getElementById('foodModal')).show();
}

async function saveFood() {
    const id = document.getElementById('foodId').value;
    const body = {
        name: document.getElementById('foodName').value,
        category: document.getElementById('foodCategory').value,
        caloriesPer100g: parseInt(document.getElementById('foodCalories').value),
        protein: document.getElementById('foodProtein').value ? parseFloat(document.getElementById('foodProtein').value) : null,
        carbs: document.getElementById('foodCarbs').value ? parseFloat(document.getElementById('foodCarbs').value) : null,
        fat: document.getElementById('foodFat').value ? parseFloat(document.getElementById('foodFat').value) : null,
        fiber: document.getElementById('foodFiber').value ? parseFloat(document.getElementById('foodFiber').value) : null,
        imageUrl: document.getElementById('foodImageUrl').value || null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/admin/food-database/${id}` : `${API_BASE}/admin/food-database`;
        const r = await apiRequest(url, { method, body: JSON.stringify(body) });
        if (r.ok) {
            bootstrap.Modal.getInstance(document.getElementById('foodModal')).hide();
            loadFoodDB();
        }
    } catch (e) { alert(e.message); }
}

async function deleteFood(id) {
    if (!confirm('Delete this food item?')) return;
    try {
        const r = await apiRequest(`${API_BASE}/admin/food-database/${id}`, { method: 'DELETE' });
        if (r.ok) loadFoodDB();
    } catch (e) { alert(e.message); }
}
