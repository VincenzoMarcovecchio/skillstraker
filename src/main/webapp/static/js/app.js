// SkillTrack App Script

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close when clicking overlay backdrop
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal-overlay')) {
        event.target.classList.remove('active');
    }
});

// Duration pill quick selector
function setDuration(mins, inputId) {
    const input = document.getElementById(inputId || 'durationMinutes');
    if (input) {
        input.value = mins;
    }
}

// Open Edit Session Modal
function openEditSessionModal(id, skillId, duration, notes, dateStr) {
    document.getElementById('editSessionId').value = id;
    document.getElementById('editSkillId').value = skillId;
    document.getElementById('editDurationMinutes').value = duration;
    document.getElementById('editNotes').value = notes;
    document.getElementById('editSessionDate').value = dateStr;
    openModal('editSessionModal');
}

// Open Edit Skill Modal
function openEditSkillModal(id, name, category, colorHex, goal) {
    document.getElementById('editSkillId').value = id;
    document.getElementById('editSkillName').value = name;
    document.getElementById('editSkillCategory').value = category;
    document.getElementById('editSkillColor').value = colorHex;
    document.getElementById('editSkillGoal').value = goal;
    openModal('editSkillModal');
}

// Auto dismiss toast after 4s
document.addEventListener('DOMContentLoaded', function() {
    const toast = document.querySelector('.toast-msg');
    if (toast) {
        setTimeout(() => {
            toast.style.transition = 'opacity 0.5s ease';
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 500);
        }, 4000);
    }
});
