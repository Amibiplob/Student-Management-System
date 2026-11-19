package com.studentmanagement.servlet;

import com.studentmanagement.dao.UserDAO;
import com.studentmanagement.model.Role;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.UserAccount;
import com.studentmanagement.security.SecurityFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("new".equals(action)) {
            UserAccount user = new UserAccount();
            user.setRole(Role.ADMIN);
            renderPage(request, response, user, false, null);
        } else if ("edit".equals(action)) {
            int id = parseId(request.getParameter("id"));
            if (id < 1) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                UserAccount user = userDAO.findById(id);
                if (user == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                renderPage(request, response, user, true, null);
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Unable to load user account", exception);
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "User accounts could not be loaded. Check the database configuration.");
            }
        } else {
            renderPage(request, response, null, false, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("create".equals(action) || "update".equals(action)) {
            saveUser(request, response, "update".equals(action));
        } else if ("activate".equals(action) || "deactivate".equals(action)) {
            setActive(request, response, "activate".equals(action));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown account action.");
        }
    }

    private void saveUser(HttpServletRequest request, HttpServletResponse response, boolean editing)
            throws ServletException, IOException {
        UserAccount actor = (UserAccount) request.getAttribute(SecurityFilter.REQUEST_USER);
        UserAccount user = readUser(request);
        int id = editing ? parseId(request.getParameter("id")) : 0;
        if (editing && id < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid account ID is required.");
            return;
        }
        user.setId(id);
        if (editing && actor.getId() == id && user.getRole() != Role.ADMIN) {
            renderPage(request, response, user, true,
                    "You cannot remove administrator access from your own account.");
            return;
        }

        String validationError;
        try {
            validationError = validate(user, request.getParameter("password"), editing);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to validate linked student record", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            renderPage(request, response, user, editing,
                    "The account could not be saved because the database is unavailable.");
            return;
        }
        if (validationError != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderPage(request, response, user, editing, validationError);
            return;
        }

        String password = request.getParameter("password");
        String passwordValue = password == null || password.isEmpty() ? null : password;
        try {
            boolean saved = editing ? userDAO.update(user, passwordValue)
                    : userDAO.create(user, passwordValue);
            if (!saved) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderPage(request, response, user, editing,
                        "That account no longer exists. Return to the account list and try again.");
                return;
            }
            request.getSession().setAttribute("userFlashSuccess",
                    editing ? "Account changes were saved." : "The account was created.");
            response.sendRedirect(request.getContextPath() + "/users");
        } catch (UserDAO.LastAdministratorException exception) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            renderPage(request, response, user, editing,
                    "At least one active administrator account must remain.");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to save user account", exception);
            String message;
            if ("23505".equals(exception.getSQLState())) {
                message = "That email is already in use or the student record is linked to another account.";
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else if ("23503".equals(exception.getSQLState())) {
                message = "The selected student record is no longer available.";
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                message = "The account could not be saved. Check the database configuration and try again.";
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            renderPage(request, response, user, editing, message);
        }
    }

    private String validate(UserAccount user, String password, boolean editing) throws SQLException {
        if (isBlank(user.getFullName()) || user.getFullName().length() > 150) {
            return "Enter a full name between 1 and 150 characters.";
        }
        if (isBlank(user.getEmail()) || user.getEmail().length() > 150
                || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return "Enter a valid email address of 150 characters or fewer.";
        }
        if (user.getRole() == null) {
            return "Choose ADMIN, TEACHER, or STUDENT.";
        }
        if (!editing && (password == null || password.isEmpty())) {
            return "Enter an initial password for this account.";
        }
        if (user.getRole() == Role.STUDENT) {
            if (user.getStudentId() == null || user.getStudentId() < 1) {
                return "Select the existing student record for this STUDENT account.";
            }
            if (!userDAO.isStudentAvailable(user.getStudentId(), editing ? user.getId() : null)) {
                return "That student record is unavailable or already linked to another account.";
            }
        } else if (user.getStudentId() != null) {
            return "Only STUDENT accounts can be linked to a student record.";
        }
        return null;
    }

    private void setActive(HttpServletRequest request, HttpServletResponse response, boolean active)
            throws IOException {
        int id = parseId(request.getParameter("id"));
        if (id < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid account ID is required.");
            return;
        }
        UserAccount actor = (UserAccount) request.getAttribute(SecurityFilter.REQUEST_USER);
        if (!active && actor.getId() == id) {
            request.getSession().setAttribute("userFlashError", "You cannot deactivate your own account.");
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }
        try {
            boolean changed = userDAO.setActive(id, active);
            request.getSession().setAttribute(changed ? "userFlashSuccess" : "userFlashError",
                    changed ? (active ? "Account reactivated." : "Account deactivated.")
                            : "That account no longer exists.");
        } catch (UserDAO.LastAdministratorException exception) {
            request.getSession().setAttribute("userFlashError",
                    "At least one active administrator account must remain.");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to change account status", exception);
            request.getSession().setAttribute("userFlashError",
                    "Account status could not be changed. Check the database configuration.");
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response,
            UserAccount editingUser, boolean editing, String errorMessage)
            throws ServletException, IOException {
        if (editingUser != null) {
            request.setAttribute("editingUser", editingUser);
        }
        request.setAttribute("editing", editing);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        try {
            request.setAttribute("users", userDAO.findAll());
            request.setAttribute("studentOptions", userDAO.findAvailableStudents(
                    editingUser == null ? null : editingUser.getId()));
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load user management page", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("databaseUnavailable", true);
            request.setAttribute("users", Collections.emptyList());
            request.setAttribute("studentOptions", Collections.emptyList());
            if (errorMessage == null) {
                request.setAttribute("errorMessage", "User accounts could not be loaded. Check the database configuration.");
            }
        }
        consumeFlash(request);
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }

    private void consumeFlash(HttpServletRequest request) {
        Object success = request.getSession().getAttribute("userFlashSuccess");
        Object error = request.getSession().getAttribute("userFlashError");
        if (success != null) {
            request.setAttribute("successMessage", success);
            request.getSession().removeAttribute("userFlashSuccess");
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.getSession().removeAttribute("userFlashError");
        }
    }

    private UserAccount readUser(HttpServletRequest request) {
        UserAccount user = new UserAccount();
        user.setFullName(trim(request.getParameter("fullName")));
        user.setEmail(trim(request.getParameter("email")));
        String roleValue = trim(request.getParameter("role"));
        try {
            user.setRole(roleValue == null ? null : Role.valueOf(roleValue.toUpperCase(java.util.Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            user.setRole(null);
        }
        String studentId = trim(request.getParameter("studentId"));
        if (user.getRole() == Role.STUDENT) {
            user.setStudentId(studentId == null || studentId.isEmpty() ? null : parseId(studentId));
        }
        return user;
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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
