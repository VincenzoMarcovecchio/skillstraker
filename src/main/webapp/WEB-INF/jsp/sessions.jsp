<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <div>
            <h1 class="greeting-title">All Practice Sessions</h1>
            <p class="greeting-subtitle">View, filter, and manage your complete practice history</p>
        </div>
        <button type="button" class="btn-primary" onclick="openModal('logSessionModal')">
            + Log Session
        </button>
    </div>

    <!-- Filter Bar -->
    <div style="display: flex; gap: 12px; margin-bottom: 20px; align-items: center; background: #FFFFFF; padding: 14px 18px; border-radius: var(--radius-md); border: 1px solid var(--border-color);">
        <span style="font-size: 13px; font-weight: 600; color: #4B5563;">Filter by Skill:</span>
        <a href="${pageContext.request.contextPath}/sessions"
           class="duration-pill ${empty selectedSkillId ? 'active' : ''}" style="text-decoration: none;">
            All Skills
        </a>
        <c:forEach var="skill" items="${skills}">
            <a href="${pageContext.request.contextPath}/sessions?skillId=${skill.id}"
               class="duration-pill ${selectedSkillId == skill.id ? 'active' : ''}" style="text-decoration: none;">
                <span class="color-dot" style="display: inline-block; vertical-align: middle; background-color: ${skill.colorHex}; margin-right: 4px;"></span>
                ${skill.name}
            </a>
        </c:forEach>
    </div>

    <!-- Sessions Table / List Card -->
    <div class="recent-sessions-card">
        <div class="sessions-header">
            <h3 class="sessions-title">Session History (${sessions.size()} records)</h3>
        </div>

        <div class="sessions-list">
            <c:forEach var="session" items="${sessions}">
                <div class="session-row">
                    <div class="session-left">
                        <div class="session-skill-tag">
                            <span class="color-dot" style="background-color: ${session.skillColorHex};"></span>
                            <span><c:out value="${session.skillName}" /></span>
                        </div>
                        <div class="session-notes" title="<c:out value='${session.notes}' />">
                            <c:out value="${not empty session.notes ? session.notes : 'Practice session'}" />
                        </div>
                    </div>
                    <div class="session-right">
                        <span class="session-duration">${session.formattedDuration}</span>
                        <span class="session-date" style="min-width: 80px;">${session.sessionDate}</span>
                        <div class="session-actions">
                            <button type="button" class="btn-icon" title="Edit session"
                                    onclick="openEditSessionModal(${session.id}, ${session.skillId}, ${session.durationMinutes}, '<c:out value="${session.notes}" />', '${session.sessionDate}')">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                            </button>
                            <a href="${pageContext.request.contextPath}/sessions/delete?id=${session.id}&redirect=/sessions"
                               class="btn-icon delete" title="Delete session"
                               onclick="return confirm('Are you sure you want to delete this session?');">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty sessions}">
                <div class="session-row" style="justify-content: center; color: var(--text-muted); font-size: 13px; padding: 30px 0;">
                    No sessions found. Log your first session!
                </div>
            </c:if>
        </div>
    </div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
