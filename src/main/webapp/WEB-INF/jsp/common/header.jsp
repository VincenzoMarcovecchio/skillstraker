<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SkillTrack - Practice Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

<!-- Background decorative doodles matching screenshot -->
<div class="bg-doodles">
    <!-- Top Right Squiggle -->
    <svg class="doodle-squiggles-top" width="140" height="90" viewBox="0 0 140 90" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M10 20 L40 50 L70 20 L100 50 L130 20" stroke="#FCA5A5" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M50 70 L80 70" stroke="#FCA5A5" stroke-width="4" stroke-linecap="round"/>
    </svg>

    <!-- Left Dots Matrix -->
    <svg class="doodle-dots-left" width="40" height="130" viewBox="0 0 40 130" fill="none" xmlns="http://www.w3.org/2000/svg">
        <c:forEach var="r" begin="0" end="7">
            <circle cx="10" cy="${15 + r * 15}" r="4" fill="#38BDF8"/>
            <circle cx="28" cy="${15 + r * 15}" r="4" fill="#38BDF8"/>
        </c:forEach>
    </svg>

    <!-- Bottom Right Subtle Arc -->
    <svg class="doodle-circle-bottom" width="180" height="180" viewBox="0 0 180 180" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="90" cy="90" r="75" stroke="#E0E7FF" stroke-width="4" stroke-dasharray="10 10"/>
    </svg>
</div>

<!-- Main Dashboard Container -->
<div class="dashboard-wrapper">

    <!-- Top Navigation Bar -->
    <nav class="top-navbar">
        <a href="${pageContext.request.contextPath}/" class="brand-logo">
            <span class="brand-icon">
                <!-- SkillTrack Green Hexagon Icon -->
                <svg width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="2" y="2" width="24" height="24" rx="8" fill="#00875A"/>
                    <path d="M14 7L20 10.5V17.5L14 21L8 17.5V10.5L14 7Z" stroke="white" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M14 14L20 10.5M14 14V21M14 14L8 10.5" stroke="white" stroke-width="2"/>
                </svg>
            </span>
            <span class="brand-text">SkillTrack</span>
        </a>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/" class="nav-link ${pageContext.request.servletPath == '/' || pageContext.request.servletPath == '/dashboard' ? 'active' : ''}">Dashboard</a>
            <a href="${pageContext.request.contextPath}/sessions" class="nav-link ${pageContext.request.servletPath.startsWith('/sessions') ? 'active' : ''}">Sessions</a>
            <a href="${pageContext.request.contextPath}/skills" class="nav-link ${pageContext.request.servletPath.startsWith('/skills') ? 'active' : ''}">Skills</a>
        </div>

        <div class="nav-actions">
            <button type="button" class="btn-primary" onclick="openModal('logSessionModal')">
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                    <line x1="7" y1="2" x2="7" y2="12"></line>
                    <line x1="2" y1="7" x2="12" y2="7"></line>
                </svg>
                Log Session
            </button>
            <div class="user-avatar" title="Jordan">JD</div>
        </div>
    </nav>
