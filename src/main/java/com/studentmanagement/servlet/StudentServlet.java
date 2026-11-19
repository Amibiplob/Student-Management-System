package com.studentmanagement.servlet;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("search");
        List<Student> students;

        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentDAO.searchStudents(keyword.trim());
        } else {
            students = studentDAO.getAllStudents();
        }

        request.setAttribute("students", students);
        request.getRequestDispatcher("/students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                Student student = readStudent(request);
                studentDAO.addStudent(student);

            } else if ("update".equals(action)) {
                Student student = readStudent(request);
                student.setId(Integer.parseInt(request.getParameter("id")));
                studentDAO.updateStudent(student);

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                studentDAO.deleteStudent(id);
            }

            response.sendRedirect(request.getContextPath() + "/students");
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid student ID or department ID.", e);
        } catch (Exception e) {
            throw new ServletException("Unable to process student request.", e);
        }
    }

    private Student readStudent(HttpServletRequest request) {
        String departmentValue = request.getParameter("departmentId");
        int departmentId = (departmentValue == null
                || departmentValue.trim().isEmpty())
                ? 0 : Integer.parseInt(departmentValue);

        return new Student(
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("phone"),
                request.getParameter("gender"),
                request.getParameter("dateOfBirth"),
                request.getParameter("address"),
                departmentId
        );
    }
}
