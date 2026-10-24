package com.skilltrack.servlet;

import com.skilltrack.dao.SkillDAO;
import com.skilltrack.dto.SkillStat;
import com.skilltrack.model.Skill;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "SkillServlet", urlPatterns = {"/skills", "/skills/*"})
public class SkillServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SkillServlet.class.getName());

    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if ("/delete".equals(pathInfo)) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    skillDAO.delete(Long.parseLong(idParam));
                }
                response.sendRedirect(request.getContextPath() + "/skills?success=skill_deleted");
                return;
            }

            List<Skill> skills = skillDAO.findAll();
            List<SkillStat> statsList = new ArrayList<>();
            for (Skill s : skills) {
                statsList.add(skillDAO.getSkillStat(s));
            }

            request.setAttribute("skills", skills);
            request.setAttribute("statsList", statsList);
            request.getRequestDispatcher("/WEB-INF/jsp/skills.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in SkillServlet GET: " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/dashboard?error=" + e.getMessage());
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
                Long id = Long.parseLong(request.getParameter("id"));
                skillDAO.delete(id);
                response.sendRedirect(request.getContextPath() + "/skills?success=skill_deleted");
            } else if ("edit".equalsIgnoreCase(action) || "update".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(request.getParameter("id"));
                String name = request.getParameter("name").trim();
                String category = request.getParameter("category");
                String colorHex = request.getParameter("colorHex");
                double weeklyGoalHours = Double.parseDouble(request.getParameter("weeklyGoalHours"));

                Skill skill = new Skill(id, name, category, colorHex, weeklyGoalHours);
                skillDAO.update(skill);
                response.sendRedirect(request.getContextPath() + "/skills?success=skill_updated");
            } else {
                // Create
                String name = request.getParameter("name").trim();
                String category = request.getParameter("category");
                String colorHex = request.getParameter("colorHex");
                double weeklyGoalHours = Double.parseDouble(request.getParameter("weeklyGoalHours"));

                Skill skill = new Skill(null, name, category, colorHex, weeklyGoalHours);
                skillDAO.create(skill);
                response.sendRedirect(request.getContextPath() + "/skills?success=skill_created");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in SkillServlet POST: " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/skills?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
