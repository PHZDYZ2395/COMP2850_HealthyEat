/**
 * Dashboard (My Profile) page logic for subscriber users.
 * Handles viewing/editing profile info and changing password.
 * Depends on main.js for API requests and auth state.
 * @author COMP2850 Team
 */

document.addEventListener('DOMContentLoaded', function() {
    const user = checkRole('SUBSCRIBER');
    if (!user) return;

    loadProfile();

    document.getElementById('profileForm').addEventListener('submit', handleUpdateProfile);
    document.getElementById('passwordForm').addEventListener('submit', handleChangePassword);
    document.getElementById('nutritionist-tab').addEventListener('shown.bs.tab', loadNutritionist);
});

async function loadProfile() {
    try {
        const response = await apiRequest(`${API_BASE}/users/me`);
        if (!response.ok) throw new Error('Failed to load profile');
        const data = await response.json();
        document.getElementById('profileFullName').value = data.fullName;
        document.getElementById('profileEmail').value = data.email;
        document.getElementById('profileRole').value = capitalizeRole(data.role);
    } catch (error) {
        console.error(error);
    }
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    const data = {
        fullName: document.getElementById('profileFullName').value,
        email: document.getElementById('profileEmail').value
    };

    try {
        const response = await apiRequest(`${API_BASE}/users/me`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Failed to update profile');
        }
        alert('Profile updated successfully');
    } catch (error) {
        alert(error.message);
    }
}

async function handleChangePassword(e) {
    e.preventDefault();
    const oldPassword = document.getElementById('oldPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        alert('New passwords do not match');
        return;
    }
    if (newPassword.length < 6) {
        alert('Password must be at least 6 characters');
        return;
    }

    try {
        const response = await apiRequest(`${API_BASE}/users/me/password`, {
            method: 'PUT',
            body: JSON.stringify({ oldPassword, newPassword })
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Failed to change password');
        }
        alert('Password changed successfully');
        document.getElementById('passwordForm').reset();
    } catch (error) {
        alert(error.message);
    }
}

function capitalizeRole(role) {
    return role ? role.charAt(0) + role.slice(1).toLowerCase() : '';
}

async function loadNutritionist() {
    try {
        const response = await apiRequest(`${API_BASE}/subscribers/my-professional`);
        if (response.status === 204) {
            await loadAvailableProfessionals();
            return;
        }
        if (!response.ok) throw new Error('Failed to load nutritionist');
        const professional = await response.json();
        renderCurrentNutritionist(professional);
    } catch (error) {
        document.getElementById('nutritionistContent').innerHTML =
            '<p class="text-danger text-center">Failed to load nutritionist info.</p>';
    }
}

function renderCurrentNutritionist(professional) {
    const container = document.getElementById('nutritionistContent');
    container.innerHTML = `
        <div class="card bg-light border-info mb-3">
            <div class="card-body">
                <div class="d-flex align-items-center">
                    <div class="bg-info rounded-circle d-flex align-items-center justify-content-center text-white me-3"
                         style="width:60px;height:60px;font-size:1.5rem;">
                        ${escapeHtml(professional.fullName).charAt(0).toUpperCase()}
                    </div>
                    <div>
                        <h5 class="mb-1">${escapeHtml(professional.fullName)}</h5>
                        <p class="text-muted mb-0">${escapeHtml(professional.email)}</p>
                    </div>
                </div>
            </div>
        </div>
        <div class="d-flex gap-2">
            <button class="btn btn-outline-primary" onclick="loadAvailableProfessionals()">
                <i class="fas fa-exchange-alt"></i> Change Nutritionist
            </button>
            <button class="btn btn-outline-danger" onclick="removeNutritionist(${professional.id})">
                <i class="fas fa-unlink"></i> Remove
            </button>
        </div>
    `;
}

async function loadAvailableProfessionals() {
    const container = document.getElementById('nutritionistContent');
    container.innerHTML = `
        <h6 class="mb-3">Select a Nutritionist</h6>
        <div id="professionalsList" class="row g-3">
            <div class="col-12 text-center"><div class="spinner-border text-info"></div></div>
        </div>
    `;

    try {
        const response = await apiRequest(`${API_BASE}/subscribers/professionals`);
        if (!response.ok) throw new Error('Failed to load nutritionists');
        const professionals = await response.json();
        renderAvailableProfessionals(professionals);
    } catch (error) {
        document.getElementById('professionalsList').innerHTML =
            '<p class="text-danger text-center">Failed to load nutritionists.</p>';
    }
}

function renderAvailableProfessionals(professionals) {
    const container = document.getElementById('professionalsList');
    if (professionals.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No nutritionists available.</p>';
        return;
    }

    container.innerHTML = professionals.map(p => `
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex align-items-center">
                        <div class="bg-info rounded-circle d-flex align-items-center justify-content-center text-white me-3"
                             style="width:50px;height:50px;font-size:1.2rem;">
                            ${escapeHtml(p.fullName).charAt(0).toUpperCase()}
                        </div>
                        <div class="flex-grow-1">
                            <h6 class="mb-1">${escapeHtml(p.fullName)}</h6>
                            <small class="text-muted">${escapeHtml(p.email)}</small>
                        </div>
                    </div>
                    <div class="mt-3 text-end">
                        <button class="btn btn-sm btn-success" onclick="assignNutritionist(${p.id})">
                            <i class="fas fa-plus"></i> Select
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

async function assignNutritionist(professionalId) {
    if (!confirm('Assign this nutritionist? It will replace your current one.')) return;

    try {
        const response = await apiRequest(`${API_BASE}/subscribers/my-professional`, {
            method: 'POST',
            body: JSON.stringify({ professionalId })
        });
        if (!response.ok) throw new Error('Failed to assign nutritionist');
        loadNutritionist();
    } catch (error) {
        alert(error.message);
    }
}

async function removeNutritionist(professionalId) {
    if (!confirm('Remove this nutritionist?')) return;

    try {
        const response = await apiRequest(`${API_BASE}/subscribers/my-professional`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Failed to remove nutritionist');
        loadAvailableProfessionals();
    } catch (error) {
        alert(error.message);
    }
}
