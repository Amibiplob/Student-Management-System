package com.studentmanagement.servlet;

import com.studentmanagement.dao.DepartmentDAO;
import com.studentmanagement.model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/departments")
public class DepartmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DepartmentServlet.class.getName());
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int id = parseId(request.getParameter("id"));
            if (id < 1) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                Department department = departmentDAO.findDepartmentById(id);
                if (department == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("department", department);
                request.setAttribute("editing", true);
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Unable to load department", exception);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                request.setAttribute("errorMessage", "Department information could not be loaded. Check the database configuration.");
            }
        }
        loadPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            deleteDepartment(request, response);
        } else if ("add".equals(action) || "update".equals(action)) {
            saveDepartment(request, response, "update".equals(action));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown department action.");
        }
    }

    private void saveDepartment(HttpServletRequest request, HttpServletResponse response,
            boolean editing) throws ServletException, IOException {
        Department department = new Department(trim(request.getParameter("name")),
                trim(request.getParameter("description")));
        if (editing) {
            int id = parseId(request.getParameter("id"));
            if (id < 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid department ID is required.");
                return;
            }
            department.setId(id);
        }

        String validationError = validate(department);
        if (validationError != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderPage(request, response, department, editing, validationError);
            return;
        }

        try {
            boolean saved = editing ? departmentDAO.updateDepartment(department)
                    : departmentDAO.addDepartment(department);
            if (!saved) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderPage(request, response, department, editing,
                        "That department no longer exists. Return to the department list and try again.");
                return;
            }
            request.getSession().setAttribute("flashSuccess",
                    editing ? "Department changes were saved." : "Department was added successfully.");
            response.sendRedirect(request.getContextPath() + "/departments");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to save department", exception);
            String message = "23505".equals(exception.getSQLState())
                    ? "A department with that name already exists. Name matching ignores letter case."
                    : "Department changes could not be saved. Check the database configuration and try again.";
            response.setStatus("23505".equals(exception.getSQLState())
                    ? HttpServletResponse.SC_CONFLICT : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            renderPage(request, response, department, editing, message);
        }
    }

    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parseId(request.getParameter("id"));
        if (id < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid department ID is required.");
            return;
        }
        try {
            if (departmentDAO.deleteDepartment(id)) {
                request.getSession().setAttribute("flashSuccess", "Department was deleted.");
            } else {
                request.getSession().setAttribute("flashError", "That department no longer exists.");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.INFO, "Department delete was rejected", exception);
            String message = "23503".equals(exception.getSQLState())
                    ? "This department still has students. Move or delete those student records before deleting it."
                    : "Department could not be deleted. Check the database configuration and try again.";
            request.getSession().setAttribute("flashError", message);
        }
        response.sendRedirect(request.getContextPath() + "/departments");
    }

    private void loadPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object success = request.getSession().getAttribute("flashSuccess");
        Object error = request.getSession().getAttribute("flashError");
        if (success != null) {
            request.setAttribute("successMessage", success);
            request.getSession().removeAttribute("flashSuccess");
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.getSession().removeAttribute("flashError");
        }
        try {
            List<Department> departments = departmentDAO.findAllDepartments();
            request.setAttribute("departments", departments);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load departments", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("databaseUnavailable", true);
            request.setAttribute("departments", Collections.emptyList());
            request.setAttribute("errorMessage", "Departments could not be loaded. Check the database configuration.");
        }
        request.getRequestDispatcher("/departments.jsp").forward(request, response);
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response,
            Department department, boolean editing, String message)
            throws ServletException, IOException {
        request.setAttribute("department", department);
        request.setAttribute("editing", editing);
        request.setAttribute("errorMessage", message);
        loadPage(request, response);
    }

    private String validate(Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()
                || department.getName().length() > 100) {
            return "Enter a department name between 1 and 100 characters.";
        }
        return null;
    }

    private static int parseId(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
