<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

</div> <!-- End .dashboard-wrapper -->

<!-- Modals Inclusion -->
<jsp:include page="/WEB-INF/jsp/common/session_modal.jsp" />

<!-- Toast Messages -->
<c:if test="${not empty param.success}">
    <div class="toast-msg">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
        <c:choose>
            <c:when test="${param.success == 'session_logged'}">Session logged successfully!</c:when>
            <c:when test="${param.success == 'session_updated'}">Session updated successfully!</c:when>
            <c:when test="${param.success == 'session_deleted'}">Session deleted.</c:when>
            <c:when test="${param.success == 'skill_created'}">New skill added!</c:when>
            <c:when test="${param.success == 'skill_updated'}">Skill updated!</c:when>
            <c:when test="${param.success == 'skill_deleted'}">Skill deleted.</c:when>
            <c:otherwise>Action completed successfully.</c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div class="toast-msg" style="background-color: #DC2626;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <span><c:out value="${param.error}" /></span>
    </div>
</c:if>

<script src="${pageContext.request.contextPath}/static/js/app.js"></script>
</body>
</html>
