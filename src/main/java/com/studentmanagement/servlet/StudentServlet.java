package com.studentmanagement.servlet;

import com.studentmanagement.dao.DepartmentDAO;
import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Department;
import com.studentmanagement.model.Role;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/students", "/dashboard"})
public class StudentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StudentServlet.class.getName());
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final StudentDAO studentDAO = new StudentDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/dashboard".equals(request.getServletPath())) {
            showDashboard(request, response);
            return;
        }

        String action = request.getParameter("action");
        if ("new".equals(action)) {
            renderStudentForm(request, response, new Student(), false, null);
        } else if ("edit".equals(action) || "view".equals(action)) {
            int id = parseId(request.getParameter("id"));
            if (id < 1) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                Student student = studentDAO.findStudentById(id);
                if (student == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                if ("edit".equals(action)) {
                    renderStudentForm(request, response, student, true, null);
                } else {
                    request.setAttribute("student", student);
                    request.getRequestDispatcher("/student-details.jsp").forward(request, response);
                }
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Unable to load student record", exception);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                request.setAttribute("errorMessage", "Student information could not be loaded. Check the database configuration.");
                request.getRequestDispatcher("/student-details.jsp").forward(request, response);
            }
        } else {
            showStudents(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            deleteStudent(request, response);
        } else if ("add".equals(action) || "update".equals(action)) {
            saveStudent(request, response, "update".equals(action));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown student action.");
        }
    }

    private void showStudents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = trim(request.getParameter("search"));
        String departmentFilter = trim(request.getParameter("departmentId"));
        Integer departmentId = null;
        boolean unassignedOnly = "unassigned".equals(departmentFilter);
        boolean invalidDepartmentFilter = false;
        if (!unassignedOnly && departmentFilter != null && !departmentFilter.isEmpty()) {
            int parsedDepartmentId = parseId(departmentFilter);
            if (parsedDepartmentId > 0) {
                departmentId = parsedDepartmentId;
            } else {
                invalidDepartmentFilter = true;
            }
        }
        request.setAttribute("search", search == null ? "" : search);
        request.setAttribute("selectedDepartmentId", departmentId);
        request.setAttribute("selectedUnassigned", unassignedOnly);
        consumeFlash(request);
        try {
            request.setAttribute("students", invalidDepartmentFilter
                    ? java.util.Collections.emptyList()
                    : studentDAO.findStudents(search, departmentId, unassignedOnly));
            request.setAttribute("departments", departmentDAO.findAllDepartments());
            if (invalidDepartmentFilter) {
                request.setAttribute("errorMessage", "Choose a valid department filter or select all departments.");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load student directory", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("databaseUnavailable", true);
            request.setAttribute("students", java.util.Collections.emptyList());
            request.setAttribute("departments", java.util.Collections.emptyList());
            request.setAttribute("errorMessage", "Student records could not be loaded. Check the database configuration.");
        }
        request.getRequestDispatcher("/students.jsp").forward(request, response);
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        consumeFlash(request);
        UserAccount currentUser = (UserAccount) request.getAttribute("currentUser");
        if (currentUser.getRole() == Role.STUDENT) {
            try {
                Student ownRecord = currentUser.getStudentId() == null
                        ? null : studentDAO.findStudentById(currentUser.getStudentId());
                if (ownRecord == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    request.setAttribute("errorMessage", "No active student record is linked to this account. Contact an administrator.");
                } else {
                    request.setAttribute("student", ownRecord);
                }
            } catch (SQLException exception) {
                LOGGER.log(Level.SEVERE, "Unable to load the signed-in student's profile", exception);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                request.setAttribute("errorMessage", "Your student profile could not be loaded. Try again later.");
            }
            request.getRequestDispatcher("/student-dashboard.jsp").forward(request, response);
            return;
        }
        try {
            request.setAttribute("studentCount", studentDAO.countStudents());
            request.setAttribute("departmentCount", departmentDAO.countDepartments());
            request.setAttribute("recentStudents", studentDAO.findRecentStudents(5));
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load dashboard", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("databaseUnavailable", true);
            request.setAttribute("errorMessage", "Dashboard data could not be loaded. Check the database configuration.");
            request.setAttribute("studentCount", 0);
            request.setAttribute("departmentCount", 0);
            request.setAttribute("recentStudents", java.util.Collections.emptyList());
        }
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    private void renderStudentForm(HttpServletRequest request, HttpServletResponse response,
            Student student, boolean editing, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("student", student);
        request.setAttribute("editing", editing);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        try {
            request.setAttribute("departments", departmentDAO.findAllDepartments());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to load departments for student form", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("databaseUnavailable", true);
            request.setAttribute("departments", java.util.Collections.emptyList());
            request.setAttribute("errorMessage", "Departments could not be loaded. Check the database configuration.");
        }
        request.getRequestDispatcher(editing ? "/edit-student.jsp" : "/add-student.jsp")
                .forward(request, response);
    }

    private void saveStudent(HttpServletRequest request, HttpServletResponse response,
            boolean editing) throws ServletException, IOException {
        Student student = readStudent(request);
        if (editing) {
            int id = parseId(request.getParameter("id"));
            if (id < 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid student ID is required.");
                return;
            }
            student.setId(id);
        }

        String validationError;
        try {
            validationError = validateStudent(student);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to validate selected department", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            renderStudentForm(request, response, student, editing,
                    "The student could not be saved because the database is unavailable.");
            return;
        }
        if (validationError != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            renderStudentForm(request, response, student, editing, validationError);
            return;
        }

        try {
            boolean saved = editing ? studentDAO.updateStudent(student) : studentDAO.addStudent(student);
            if (!saved) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderStudentForm(request, response, student, editing,
                        "That student record no longer exists. Return to the directory and try again.");
                return;
            }
            request.getSession().setAttribute("flashSuccess",
                    editing ? "Student changes were saved." : "Student was added successfully.");
            response.sendRedirect(request.getContextPath() + "/students");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to save student record", exception);
            String sqlState = exception.getSQLState();
            String message;
            if ("23505".equals(sqlState)) {
                message = "That email address is already in use. Email matching ignores letter case; use a different address.";
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else if ("23503".equals(sqlState)) {
                message = "The selected department is no longer available. Choose another department.";
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                message = "Student changes could not be saved. Check the database configuration and try again.";
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            renderStudentForm(request, response, student, editing, message);
        }
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parseId(request.getParameter("id"));
        if (id < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid student ID is required.");
            return;
        }
        try {
            if (studentDAO.deleteStudent(id)) {
                request.getSession().setAttribute("flashSuccess", "Student was deleted.");
            } else {
                request.getSession().setAttribute("flashError", "That student record no longer exists.");
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to delete student record", exception);
            request.getSession().setAttribute("flashError", "Student could not be deleted. Check the database configuration.");
        }
        response.sendRedirect(request.getContextPath() + "/students");
    }

    private Student readStudent(HttpServletRequest request) {
        Student student = new Student();
        student.setName(trim(request.getParameter("name")));
        student.setEmail(trim(request.getParameter("email")));
        student.setPhone(trim(request.getParameter("phone")));
        student.setGender(trim(request.getParameter("gender")));
        student.setDateOfBirth(trim(request.getParameter("dateOfBirth")));
        student.setAddress(trim(request.getParameter("address")));
        String departmentId = trim(request.getParameter("departmentId"));
        student.setDepartmentId(departmentId == null || departmentId.isEmpty()
                ? null : parseId(departmentId));
        return student;
    }

    private String validateStudent(Student student) throws SQLException {
        if (isBlank(student.getName()) || student.getName().length() > 100) {
            return "Enter a student name between 1 and 100 characters.";
        }
        if (isBlank(student.getEmail()) || student.getEmail().length() > 150
                || !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            return "Enter a valid email address of 150 characters or fewer.";
        }
        if (student.getPhone() != null && student.getPhone().length() > 20) {
            return "Phone number must be 20 characters or fewer.";
        }
        if (student.getGender() != null && !student.getGender().isEmpty()
                && !java.util.Arrays.asList("Male", "Female", "Other")
                        .contains(student.getGender())) {
            return "Choose one of the listed gender options.";
        }
        if (student.getDateOfBirth() != null && !student.getDateOfBirth().isEmpty()) {
            try {
                if (LocalDate.parse(student.getDateOfBirth()).isAfter(LocalDate.now())) {
                    return "Date of birth cannot be in the future.";
                }
            } catch (DateTimeParseException exception) {
                return "Enter a valid date of birth.";
            }
        }
        if (student.getDepartmentId() != null
                && (student.getDepartmentId() < 1 || !departmentDAO.exists(student.getDepartmentId()))) {
            return "Choose an existing department or leave it unassigned.";
        }
        return null;
    }

    private void consumeFlash(HttpServletRequest request) {
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
