/**
 * Food Diary page logic.
 * Handles food diary CRUD, calorie trends chart, and professional advice display.
 * Depends on main.js for API requests and auth state.
 * @author COMP2850 Team
 */

document.addEventListener('DOMContentLoaded', function() {
    // Set default date to today
    const today = new Date().toISOString().split('T')[0];
    const entryDateEl = document.getElementById('entryDate');
    if (entryDateEl) entryDateEl.value = today;

    // Load initial data
    loadFoodEntries();
    loadAdvice();

    // Setup event listeners
    const addFoodForm = document.getElementById('addFoodForm');
    if (addFoodForm) addFoodForm.addEventListener('submit', handleAddFood);

    const editFoodForm = document.getElementById('editFoodForm');
    if (editFoodForm) editFoodForm.addEventListener('submit', handleEditFood);

    const filterDateEl = document.getElementById('filterDate');
    if (filterDateEl) filterDateEl.addEventListener('change', () => loadFoodEntries());

    const clearFilterEl = document.getElementById('clearFilter');
    if (clearFilterEl) clearFilterEl.addEventListener('click', () => {
        if (filterDateEl) filterDateEl.value = '';
        loadFoodEntries();
    });

    // Load trends when tab is shown
    const trendsTab = document.getElementById('trends-tab');
    if (trendsTab) trendsTab.addEventListener('shown.bs.tab', loadTrends);

    const adviceTab = document.getElementById('advice-tab');
    if (adviceTab) adviceTab.addEventListener('shown.bs.tab', loadAdvice);

    // Setup food name autocomplete
    setupFoodAutocomplete();
});

async function loadFoodEntries() {
    const filterDate = document.getElementById('filterDate')?.value;
    let url = `${API_BASE}/food-entries`;
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

function renderFoodEntries(entries) {
    const container = document.getElementById('foodEntriesList');
    if (!container) return;
    if (entries.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No food entries found.</p>';
        return;
    }

    container.innerHTML = entries.map(entry => `
        <div class="food-entry-item ${entry.mealType ? entry.mealType.toLowerCase() : ''} d-flex justify-content-between align-items-center">
            <div>
                <strong>${escapeHtml(entry.foodName)}</strong>
                <span class="badge bg-secondary ms-2">${escapeHtml(entry.mealType || 'N/A')}</span>
                <span class="text-muted ms-2">${entry.portionSize ? escapeHtml(entry.portionSize) : ''}</span>
                <br>
                <small class="text-muted">${entry.entryDate} - ${entry.calories} kcal</small>
                ${entry.notes ? `<br><small class="text-info"><i class="fas fa-sticky-note"></i> ${escapeHtml(entry.notes)}</small>` : ''}
            </div>
            <div>
                <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditModal(${entry.id}, '${escapeHtml(entry.foodName)}', '${escapeHtml(entry.portionSize || '')}', ${entry.calories}, '${escapeHtml(entry.mealType || '')}', '${entry.entryDate}', '${escapeHtml(entry.notes || '')}')">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteEntry(${entry.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
}

async function handleAddFood(e) {
    e.preventDefault();
    const data = {
        foodName: document.getElementById('foodName').value,
        portionSize: document.getElementById('portionSize').value || null,
        calories: parseInt(document.getElementById('calories').value),
        mealType: document.getElementById('mealType').value,
        date: document.getElementById('entryDate').value,
        notes: document.getElementById('foodNotes').value || null
    };

    try {
        const response = await apiRequest(`${API_BASE}/food-entries`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error('Failed to add entry');
        document.getElementById('addFoodForm').reset();
        document.getElementById('entryDate').value = new Date().toISOString().split('T')[0];
        loadFoodEntries();
    } catch (error) {
        alert(error.message);
    }
}

function openEditModal(id, foodName, portionSize, calories, mealType, entryDate, notes) {
    document.getElementById('editEntryId').value = id;
    document.getElementById('editFoodName').value = foodName;
    document.getElementById('editPortionSize').value = portionSize;
    document.getElementById('editCalories').value = calories;
    document.getElementById('editMealType').value = mealType || 'Breakfast';
    document.getElementById('editEntryDate').value = entryDate;
    document.getElementById('editNotes').value = notes || '';
    const modal = new bootstrap.Modal(document.getElementById('editFoodModal'));
    modal.show();
}

async function handleEditFood(e) {
    e.preventDefault();
    const id = document.getElementById('editEntryId').value;
    const data = {
        foodName: document.getElementById('editFoodName').value,
        portionSize: document.getElementById('editPortionSize').value || null,
        calories: parseInt(document.getElementById('editCalories').value),
        mealType: document.getElementById('editMealType').value,
        date: document.getElementById('editEntryDate').value,
        notes: document.getElementById('editNotes').value || null
    };

    try {
        const response = await apiRequest(`${API_BASE}/food-entries/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error('Failed to update entry');
        bootstrap.Modal.getInstance(document.getElementById('editFoodModal')).hide();
        loadFoodEntries();
    } catch (error) {
        alert(error.message);
    }
}

async function deleteEntry(id) {
    if (!confirm('Are you sure you want to delete this entry?')) return;
    try {
        const response = await apiRequest(`${API_BASE}/food-entries/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete entry');
        loadFoodEntries();
    } catch (error) {
        alert(error.message);
    }
}

async function loadTrends() {
    try {
        const response = await apiRequest(`${API_BASE}/food-entries/trends`);
        if (!response.ok) throw new Error('Failed to load trends');
        const trends = await response.json();
        renderTrendsChart(trends);
    } catch (error) {
        console.error(error);
    }
}

function renderTrendsChart(trends) {
    const ctx = document.getElementById('trendsChart').getContext('2d');
    if (window.trendsChartInstance) {
        window.trendsChartInstance.destroy();
    }

    const dates = [];
    for (let i = 6; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        dates.push(d.toISOString().split('T')[0]);
    }

    const caloriesMap = {};
    trends.forEach(t => { caloriesMap[t.date] = t.totalCalories; });
    const caloriesData = dates.map(d => caloriesMap[d] || 0);

    window.trendsChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dates,
            datasets: [{
                label: 'Total Calories',
                data: caloriesData,
                borderColor: '#198754',
                backgroundColor: 'rgba(25, 135, 84, 0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

async function loadAdvice() {
    try {
        const response = await apiRequest(`${API_BASE}/advice`);
        if (!response.ok) throw new Error('Failed to load advice');
        const adviceList = await response.json();
        renderAdvice(adviceList);
    } catch (error) {
        console.error(error);
    }
}

function renderAdvice(adviceList) {
    const container = document.getElementById('adviceList');
    if (!container) return;
    if (adviceList.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No advice yet.</p>';
        return;
    }

    container.innerHTML = adviceList.map(advice => `
        <div class="advice-item">
            <div class="d-flex justify-content-between">
                <strong><i class="fas fa-user-md text-info"></i> ${escapeHtml(advice.professionalName)}</strong>
                <small class="text-muted">${new Date(advice.createdAt).toLocaleString()}</small>
            </div>
            <p class="mb-0 mt-2">${escapeHtml(advice.adviceText)}</p>
        </div>
    `).join('');
}

function setupFoodAutocomplete() {
    const input = document.getElementById('foodName');
    const dropdown = document.getElementById('foodAutocompleteList');
    if (!input || !dropdown) return;

    let selectedIndex = -1;
    let currentSuggestions = [];
    let debounceTimer = null;

    input.addEventListener('input', function() {
        clearTimeout(debounceTimer);
        const keyword = input.value.trim();
        debounceTimer = setTimeout(() => searchFoods(keyword), 200);
    });

    input.addEventListener('keydown', function(e) {
        if (currentSuggestions.length === 0) return;
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            selectedIndex = Math.min(selectedIndex + 1, currentSuggestions.length - 1);
            highlightItem(dropdown);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            selectedIndex = Math.max(selectedIndex - 1, 0);
            highlightItem(dropdown);
        } else if (e.key === 'Enter' && selectedIndex >= 0) {
            e.preventDefault();
            selectSuggestion(currentSuggestions[selectedIndex]);
        } else if (e.key === 'Escape') {
            hideDropdown(dropdown);
        }
    });

    input.addEventListener('focusout', function() {
        setTimeout(() => hideDropdown(dropdown), 150);
    });

    async function searchFoods(keyword) {
        try {
            const url = keyword
                ? `${API_BASE}/food-database/search?keyword=${encodeURIComponent(keyword)}`
                : `${API_BASE}/food-database/search`;
            const response = await apiRequest(url);
            if (!response.ok) return;
            currentSuggestions = await response.json();
            selectedIndex = -1;
            renderDropdown(currentSuggestions, dropdown);
        } catch (error) {
            console.error('Failed to search foods:', error);
        }
    }

    function renderDropdown(suggestions, dropdown) {
        if (suggestions.length === 0) {
            dropdown.innerHTML = '<li class="dropdown-empty">No matching foods</li>';
            dropdown.classList.add('show');
            return;
        }
        dropdown.innerHTML = suggestions.map(function(s, i) {
            return '<li data-index="' + i + '">' + escapeHtml(s.name) + ' <span class="cal-badge">' + s.caloriesPer100g + ' kcal</span></li>';
        }).join('');

        dropdown.querySelectorAll('li[data-index]').forEach(function(li) {
            li.addEventListener('mousedown', function(e) {
                e.preventDefault();
                var idx = parseInt(li.getAttribute('data-index'));
                selectSuggestion(suggestions[idx]);
            });
        });

        dropdown.classList.add('show');
    }

    function highlightItem(dropdown) {
        dropdown.querySelectorAll('li[data-index]').forEach(function(li, i) {
            li.classList.toggle('active', i === selectedIndex);
        });
    }

    function selectSuggestion(suggestion) {
        input.value = suggestion.name;
        document.getElementById('calories').value = suggestion.caloriesPer100g;
        hideDropdown(dropdown);
    }

    function hideDropdown(dropdown) {
        dropdown.classList.remove('show');
        selectedIndex = -1;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
