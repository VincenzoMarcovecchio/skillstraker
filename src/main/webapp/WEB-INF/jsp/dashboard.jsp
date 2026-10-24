<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <!-- Optional Database Alert if MySQL is not yet started -->
    <c:if test="${not empty errorMessage}">
        <div style="background-color: #FEF3C7; border: 1px solid #F59E0B; color: #92400E; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 13px; display: flex; align-items: center; gap: 8px;">
            <span>⚠️</span>
            <span>${errorMessage}</span>
        </div>
    </c:if>

    <!-- Header Greeting matching design -->
    <header class="header-greeting">
        <h1 class="greeting-title">${stats.greeting}</h1>
        <p class="greeting-subtitle">
            <c:choose>
                <c:when test="${stats.skillsPracticedTodayCount > 0}">
                    You've practiced ${stats.skillsPracticedTodayCount} skill${stats.skillsPracticedTodayCount > 1 ? 's' : ''} today. Keep it up!
                </c:when>
                <c:otherwise>
                    No sessions logged yet today. Ready to practice?
                </c:otherwise>
            </c:choose>
        </p>
    </header>

    <!-- Main 2-Column Dashboard Grid -->
    <div class="dashboard-grid">

        <!-- Left Column: Most Practiced Card -->
        <aside class="most-practiced-card">
            <div class="badge-tag">MOST PRACTICED</div>
            <h2 class="skill-title">
                <c:out value="${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.name : 'Spanish'}" />
            </h2>

            <!-- Circular Donut Progress Ring -->
            <div class="donut-container">
                <c:set var="pct" value="${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.weeklyPercentage : 75}" />
                <c:if test="${pct > 100}"><c:set var="pct" value="100" /></c:if>
                <c:set var="dashoffset" value="${100 - pct}" />

                <svg viewBox="0 0 36 36" class="circular-chart">
                    <path class="circle-bg"
                          d="M18 2.0845
                             a 15.9155 15.9155 0 0 1 0 31.831
                             a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <path class="circle-fill"
                          stroke-dasharray="${pct}, 100"
                          d="M18 2.0845
                             a 15.9155 15.9155 0 0 1 0 31.831
                             a 15.9155 15.9155 0 0 1 0 -31.831" />
                    <text x="18" y="17.5" class="donut-label-val">${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.weeklyPercentage : 75}%</text>
                    <text x="18" y="22.5" class="donut-label-sub">of weekly goal</text>
                </svg>
            </div>

            <!-- Two Column Stats (Total Hours & Streak) -->
            <div class="stats-two-col">
                <div class="stat-item">
                    <div class="stat-item-val">
                        ${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.totalHoursFormatted : '47.5h'}
                    </div>
                    <div class="stat-item-lbl">Total hours</div>
                </div>
                <div class="stat-item">
                    <div class="stat-item-val">
                        <span style="color: #EA580C;">🔥</span>
                        ${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.dayStreak : 12}
                    </div>
                    <div class="stat-item-lbl">Day streak</div>
                </div>
            </div>

            <!-- Card Bottom Note -->
            <div class="card-footer-note">
                Last session: ${not empty stats.mostPracticedSkill ? stats.mostPracticedSkill.lastSessionFormatted : 'Yesterday — 45 min'}
            </div>
        </aside>

        <!-- Right Column: Top Mini Cards & Practice Activity Heatmap -->
        <main class="right-content">

            <!-- 3 Mini Skill Cards -->
            <div class="mini-skills-row">
                <c:forEach var="mini" items="${stats.miniSkills}">
                    <div class="mini-skill-card">
                        <div class="skill-header">
                            <span class="color-dot" style="background-color: ${mini.colorHex};"></span>
                            <span class="skill-name-sm"><c:out value="${mini.name}" /></span>
                        </div>
                        <div class="skill-hours-large">${mini.totalHoursFormatted}</div>
                        <div class="skill-streak-text">
                            <c:if test="${mini.dayStreak > 0}">
                                <span>🔥</span>
                            </c:if>
                            ${mini.dayStreak}-day streak
                        </div>
                    </div>
                </c:forEach>

                <!-- Fallback display if database is just initialized -->
                <c:if test="${empty stats.miniSkills}">
                    <div class="mini-skill-card">
                        <div class="skill-header">
                            <span class="color-dot" style="background-color: #7C3AED;"></span>
                            <span class="skill-name-sm">Guitar</span>
                        </div>
                        <div class="skill-hours-large">32h</div>
                        <div class="skill-streak-text">🔥 8-day streak</div>
                    </div>
                    <div class="mini-skill-card">
                        <div class="skill-header">
                            <span class="color-dot" style="background-color: #2563EB;"></span>
                            <span class="skill-name-sm">TypeScript</span>
                        </div>
                        <div class="skill-hours-large">18h</div>
                        <div class="skill-streak-text">🔥 3-day streak</div>
                    </div>
                    <div class="mini-skill-card">
                        <div class="skill-header">
                            <span class="color-dot" style="background-color: #DC2626;"></span>
                            <span class="skill-name-sm">Cooking</span>
                        </div>
                        <div class="skill-hours-large">12h</div>
                        <div class="skill-streak-text">5-day streak</div>
                    </div>
                </c:if>
            </div>

            <!-- Practice Activity Heatmap Card -->
            <section class="heatmap-card">
                <div class="heatmap-header">
                    <h3 class="heatmap-title">Practice Activity</h3>
                    <span class="heatmap-period">Last 18 weeks</span>
                </div>

                <div class="heatmap-container">
                    <div class="heatmap-grid">
                        <c:forEach var="col" items="${stats.heatmapColumns}">
                            <c:forEach var="day" items="${col}">
                                <div class="heatmap-cell cell-level-${day.level}"
                                     title="${day.tooltip}"
                                     data-date="${day.dateStr}"
                                     data-mins="${day.minutes}"></div>
                            </c:forEach>
                        </c:forEach>
                    </div>
                </div>

                <!-- Heatmap Legend -->
                <div class="heatmap-legend">
                    <span>Less</span>
                    <span class="legend-cell cell-level-0"></span>
                    <span class="legend-cell cell-level-1"></span>
                    <span class="legend-cell cell-level-2"></span>
                    <span class="legend-cell cell-level-3"></span>
                    <span class="legend-cell cell-level-4"></span>
                    <span>More</span>
                </div>
            </section>

        </main>
    </div>

    <!-- Recent Sessions Section matching bottom of design -->
    <section class="recent-sessions-card">
        <div class="sessions-header">
            <h3 class="sessions-title">Recent Sessions</h3>
            <a href="${pageContext.request.contextPath}/sessions" class="link-view-all">View all</a>
        </div>

        <div class="sessions-list">
            <c:forEach var="session" items="${stats.recentSessions}">
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
                        <span class="session-date">${session.relativeDateLabel}</span>
                        <div class="session-actions">
                            <button type="button" class="btn-icon" title="Edit session"
                                    onclick="openEditSessionModal(${session.id}, ${session.skillId}, ${session.durationMinutes}, '<c:out value="${session.notes}" />', '${session.sessionDate}')">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                            </button>
                            <a href="${pageContext.request.contextPath}/sessions/delete?id=${session.id}&redirect=/dashboard"
                               class="btn-icon delete" title="Delete session"
                               onclick="return confirm('Are you sure you want to delete this session?');">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"></polyline>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                </svg>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty stats.recentSessions}">
                <div class="session-row" style="justify-content: center; color: var(--text-muted); font-size: 13px;">
                    No recent sessions logged yet. Click "+ Log Session" above to start!
                </div>
            </c:if>
        </div>
    </section>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
