package com.skilltrack.servlet;

import com.skilltrack.dao.SessionDAO;
import com.skilltrack.dao.SkillDAO;
import com.skilltrack.model.PracticeSession;
import com.skilltrack.model.Skill;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SessionServlet", urlPatterns = {"/sessions", "/sessions/*"})
public class SessionServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SessionServlet.class.getName());

    private final SessionDAO sessionDAO = new SessionDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if ("/delete".equals(pathInfo)) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    sessionDAO.delete(Long.parseLong(idParam));
                }
                String redirect = cleanRedirectPath(request.getParameter("redirect"), request);
                response.sendRedirect(request.getContextPath() + redirect + "?success=session_deleted");
                return;
            }

            // List sessions view
            String skillIdParam = request.getParameter("skillId");
            List<PracticeSession> sessions;
            if (skillIdParam != null && !skillIdParam.isBlank()) {
                sessions = sessionDAO.findBySkill(Long.parseLong(skillIdParam));
                request.setAttribute("selectedSkillId", Long.parseLong(skillIdParam));
            } else {
                sessions = sessionDAO.findAll();
            }

            List<Skill> skills = skillDAO.findAll();
            request.setAttribute("sessions", sessions);
            request.setAttribute("skills", skills);
            request.setAttribute("todayDate", LocalDate.now().toString());

            request.getRequestDispatcher("/WEB-INF/jsp/sessions.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in SessionServlet GET: " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        if (action == null && pathInfo != null) {
            action = pathInfo.replace("/", "");
        }

        try {
            if ("delete".equalsIgnoreCase(action)) {
                handleDelete(request, response);
            } else if ("edit".equalsIgnoreCase(action) || "update".equalsIgnoreCase(action)) {
                handleUpdate(request, response);
            } else {
                // Default: create
                handleCreate(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling session POST: " + e.getMessage(), e);
            String redirect = cleanRedirectPath(request.getParameter("redirect"), request);
            response.sendRedirect(request.getContextPath() + redirect + "?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long skillId = Long.parseLong(request.getParameter("skillId"));
        int durationMinutes = Integer.parseInt(request.getParameter("durationMinutes"));
        String notes = request.getParameter("notes");
        String dateStr = request.getParameter("sessionDate");
        LocalDate sessionDate = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();

        PracticeSession session = new PracticeSession();
        session.setSkillId(skillId);
        session.setDurationMinutes(durationMinutes);
        session.setNotes(notes != null ? notes.trim() : "");
        session.setSessionDate(sessionDate);

        sessionDAO.create(session);

        String redirect = cleanRedirectPath(request.getParameter("redirect"), request);
        response.sendRedirect(request.getContextPath() + redirect + "?success=session_logged");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        Long skillId = Long.parseLong(request.getParameter("skillId"));
        int durationMinutes = Integer.parseInt(request.getParameter("durationMinutes"));
        String notes = request.getParameter("notes");
        String dateStr = request.getParameter("sessionDate");
        LocalDate sessionDate = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();

        PracticeSession session = new PracticeSession();
        session.setId(id);
        session.setSkillId(skillId);
        session.setDurationMinutes(durationMinutes);
        session.setNotes(notes != null ? notes.trim() : "");
        session.setSessionDate(sessionDate);

        sessionDAO.update(session);

        String redirect = cleanRedirectPath(request.getParameter("redirect"), request);
        response.sendRedirect(request.getContextPath() + redirect + "?success=session_updated");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        sessionDAO.delete(id);

        String redirect = cleanRedirectPath(request.getParameter("redirect"), request);
        response.sendRedirect(request.getContextPath() + redirect + "?success=session_deleted");
    }

    private String cleanRedirectPath(String redirect, HttpServletRequest request) {
        if (redirect == null || redirect.isBlank()) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                if (referer.contains("/skills")) return "/skills";
                if (referer.contains("/sessions")) return "/sessions";
            }
            return "/dashboard";
        }
        if (redirect.contains("skills")) return "/skills";
        if (redirect.contains("sessions")) return "/sessions";
        if (redirect.contains("dashboard") || redirect.equals("/")) return "/dashboard";
        if (redirect.startsWith("/WEB-INF/")) return "/dashboard";
        return redirect;
    }
}
