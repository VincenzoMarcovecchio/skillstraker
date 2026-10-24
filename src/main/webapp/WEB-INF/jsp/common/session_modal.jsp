<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="rawForwardPath" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />
<c:set var="redirectTarget" value="/dashboard" />
<c:choose>
    <c:when test="${rawForwardPath == '/skills' || rawForwardPath.contains('skills')}">
        <c:set var="redirectTarget" value="/skills" />
    </c:when>
    <c:when test="${rawForwardPath == '/sessions' || rawForwardPath.contains('sessions')}">
        <c:set var="redirectTarget" value="/sessions" />
    </c:when>
    <c:otherwise>
        <c:set var="redirectTarget" value="/dashboard" />
    </c:otherwise>
</c:choose>

<!-- Log Session Modal -->
<div class="modal-overlay" id="logSessionModal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">+ Log Practice Session</h3>
            <button type="button" class="modal-close" onclick="closeModal('logSessionModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/sessions/create" method="POST">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="redirect" value="${redirectTarget}">
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="skillId">Select Skill</label>
                    <select class="form-control" id="skillId" name="skillId" required>
                        <c:forEach var="skill" items="${skills}">
                            <option value="${skill.id}">${skill.name} (${skill.category})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="durationMinutes">Duration (Minutes)</label>
                    <input type="number" class="form-control" id="durationMinutes" name="durationMinutes" value="45" min="1" max="1440" required>
                    <div class="quick-duration-pills">
                        <button type="button" class="duration-pill" onclick="setDuration(15)">15m</button>
                        <button type="button" class="duration-pill" onclick="setDuration(30)">30m</button>
                        <button type="button" class="duration-pill active" onclick="setDuration(45)">45m</button>
                        <button type="button" class="duration-pill" onclick="setDuration(60)">60m</button>
                        <button type="button" class="duration-pill" onclick="setDuration(90)">90m</button>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="sessionDate">Practice Date</label>
                    <input type="date" class="form-control" id="sessionDate" name="sessionDate" value="${todayDate}" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="notes">Notes / What did you practice?</label>
                    <textarea class="form-control" id="notes" name="notes" rows="3" placeholder="e.g. Practiced past tense conjugations. Struggled with irregular verbs."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeModal('logSessionModal')">Cancel</button>
                <button type="submit" class="btn-primary">Save Session</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Session Modal -->
<div class="modal-overlay" id="editSessionModal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">Edit Practice Session</h3>
            <button type="button" class="modal-close" onclick="closeModal('editSessionModal')">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/sessions/edit" method="POST">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="id" id="editSessionId">
            <input type="hidden" name="redirect" value="${redirectTarget}">
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="editSkillId">Skill</label>
                    <select class="form-control" id="editSkillId" name="skillId" required>
                        <c:forEach var="skill" items="${skills}">
                            <option value="${skill.id}">${skill.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="editDurationMinutes">Duration (Minutes)</label>
                    <input type="number" class="form-control" id="editDurationMinutes" name="durationMinutes" min="1" max="1440" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="editSessionDate">Date</label>
                    <input type="date" class="form-control" id="editSessionDate" name="sessionDate" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="editNotes">Notes</label>
                    <textarea class="form-control" id="editNotes" name="notes" rows="3"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeModal('editSessionModal')">Cancel</button>
                <button type="submit" class="btn-primary">Update Session</button>
            </div>
        </form>
    </div>
</div>
