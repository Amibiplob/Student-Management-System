package com.studentmanagement.servlet;

import com.studentmanagement.dao.UserDAO;
import com.studentmanagement.model.UserAccount;
import com.studentmanagement.security.LoginAttemptLimiter;
import com.studentmanagement.security.SecurityFilter;
import com.studentmanagement.util.CsrfTokens;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;
    private static final int TOO_MANY_REQUESTS = 429;
    private static final LoginAttemptLimiter ATTEMPT_LIMITER = new LoginAttemptLimiter();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession(false) != null
                && request.getSession(false).getAttribute(SecurityFilter.SESSION_USER) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        HttpSession session = request.getSession(true);
        request.setAttribute("csrfToken", CsrfTokens.ensure(session));
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String remoteAddress = request.getRemoteAddr();

        if (ATTEMPT_LIMITER.isBlocked(remoteAddress)) {
            response.setStatus(TOO_MANY_REQUESTS);
            showLogin(request, response, email,
                    "Too many sign-in attempts came from this network. Wait 15 minutes and try again.");
            return;
        }

        try {
            UserAccount user = userDAO.authenticate(email, password);
            if (user == null) {
                ATTEMPT_LIMITER.recordFailure(remoteAddress);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                showLogin(request, response, email, "Email or password is incorrect, or the account is inactive.");
                return;
            }
            ATTEMPT_LIMITER.recordSuccess(remoteAddress);

            HttpSession session = request.getSession(true);
            request.changeSessionId();
            Enumeration<String> attributeNames = session.getAttributeNames();
            for (String attributeName : Collections.list(attributeNames)) {
                session.removeAttribute(attributeName);
            }
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute(SecurityFilter.SESSION_USER, user);
            CsrfTokens.rotate(session);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate account", exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            showLogin(request, response, email,
                    "Sign-in is temporarily unavailable. Check the server database configuration and try again.");
        }
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response,
            String email, String message) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        request.setAttribute("csrfToken", CsrfTokens.ensure(session));
        request.setAttribute("email", email == null ? "" : email);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
