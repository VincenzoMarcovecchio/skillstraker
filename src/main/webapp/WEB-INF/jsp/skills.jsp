<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <div>
            <h1 class="greeting-title">Manage Skills</h1>
            <p class="greeting-subtitle">Customize your practice skills, color themes, and weekly hour goals</p>
        </div>
        <button type="button" class="btn-primary" onclick="openModal('addSkillModal')">
            + Add New Skill
        </button>
    </div>

    <!-- Skills Grid -->
    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(310px, 1fr)); gap: 20px; margin-bottom: 28px;">
        <c:forEach var="stat" items="${statsList}">
            <div class="mini-skill-card" style="padding: 24px; position: relative;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <span class="color-dot" style="width: 14px; height: 14px; background-color: ${stat.colorHex};"></span>
                        <h3 style="font-size: 18px; font-weight: 700; color: #111827;"><c:out value="${stat.name}" /></h3>
                    </div>
                    <div class="session-actions">
                        <button type="button" class="btn-icon" title="Edit Skill"
                                onclick="openEditSkillModal(${stat.id}, '<c:out value="${stat.name}" />', 'General', '${stat.colorHex}', ${stat.weeklyGoalHours})">
                            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                            </svg>
                        </button>
                        <a href="${pageContext.request.contextPath}/skills/delete?id=${stat.id}"
                           class="btn-icon delete" title="Delete Skill"
                           onclick="return confirm('Are you sure you want to delete this skill and all its practice history?');">
                            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="3 6 5 6 21 6"></polyline>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            </svg>
                        </a>
                    </div>
                </div>

                <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 14px;">
                    <div>
                        <div style="font-size: 26px; font-weight: 700; color: #111827;">${stat.totalHoursFormatted}</div>
                        <div style="font-size: 12px; color: var(--text-muted);">Total practice time</div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 14px; font-weight: 600; color: #374151;">${stat.weeklyGoalHours} hrs/wk</div>
                        <div style="font-size: 12px; color: var(--text-muted);">Weekly target</div>
                    </div>
                </div>

                <div style="background: #F3F4F6; height: 8px; border-radius: 4px; overflow: hidden; margin-bottom: 12px;">
                    <div style="background-color: ${stat.colorHex}; height: 100%; width: ${stat.weeklyPercentage > 100 ? 100 : stat.weeklyPercentage}%; border-radius: 4px;"></div>
                </div>

                <div style="display: flex; justify-content: space-between; font-size: 12px; color: var(--text-secondary);">
                    <span>${stat.weeklyPercentage}% achieved this week</span>
                    <span>🔥 ${stat.dayStreak}-day streak</span>
                </div>
            </div>
        </c:forEach>
    </div>

<!-- Add Skill Modal -->
<div class="modal-overlay" id="addSkillModal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">+ Add New Skill</h3>
            <button type="button" class="modal-close" onclick="closeModal('addSkillModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/skills/create" method="POST">
            <input type="hidden" name="action" value="create">
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="newSkillName">Skill Name</label>
                    <input type="text" class="form-control" id="newSkillName" name="name" placeholder="e.g. Piano, French, Chess" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="newSkillCategory">Category</label>
                    <input type="text" class="form-control" id="newSkillCategory" name="category" placeholder="e.g. Music, Language, Tech" value="General">
                </div>
                <div class="form-group">
                    <label class="form-label" for="newSkillColor">Skill Color</label>
                    <input type="color" class="form-control" id="newSkillColor" name="colorHex" value="#00875A" style="height: 42px; padding: 2px;">
                </div>
                <div class="form-group">
                    <label class="form-label" for="newSkillGoal">Weekly Goal (Hours)</label>
                    <input type="number" step="0.5" class="form-control" id="newSkillGoal" name="weeklyGoalHours" value="5.0" min="0.5" required>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeModal('addSkillModal')">Cancel</button>
                <button type="submit" class="btn-primary">Add Skill</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Skill Modal -->
<div class="modal-overlay" id="editSkillModal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Edit Skill</h3>
            <button type="button" class="modal-close" onclick="closeModal('editSkillModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/skills/edit" method="POST">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="id" id="editSkillId">
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="editSkillName">Skill Name</label>
                    <input type="text" class="form-control" id="editSkillName" name="name" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="editSkillCategory">Category</label>
                    <input type="text" class="form-control" id="editSkillCategory" name="category">
                </div>
                <div class="form-group">
                    <label class="form-label" for="editSkillColor">Skill Color</label>
                    <input type="color" class="form-control" id="editSkillColor" name="colorHex" style="height: 42px; padding: 2px;">
                </div>
                <div class="form-group">
                    <label class="form-label" for="editSkillGoal">Weekly Goal (Hours)</label>
                    <input type="number" step="0.5" class="form-control" id="editSkillGoal" name="weeklyGoalHours" min="0.5" required>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeModal('editSkillModal')">Cancel</button>
                <button type="submit" class="btn-primary">Update Skill</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
