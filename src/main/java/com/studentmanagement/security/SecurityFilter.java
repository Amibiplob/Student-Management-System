package com.studentmanagement.security;

import com.studentmanagement.dao.UserDAO;
import com.studentmanagement.model.UserAccount;
import com.studentmanagement.util.CsrfTokens;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST})
public class SecurityFilter implements Filter {

    public static final String SESSION_USER = "authenticatedUser";
    public static final String REQUEST_USER = "currentUser";
    public static final String REQUEST_CSRF = "csrfToken";

    private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) {
        // No mutable per-request state is kept in the filter.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        setSecurityHeaders(response);

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.isEmpty()) {
            path = "/";
        }
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        UserAccount sessionUser = sessionUser(session);
        if ("/".equals(path) || "/index.jsp".equals(path)) {
            response.sendRedirect(request.getContextPath() + (sessionUser == null ? "/login" : "/dashboard"));
            return;
        }
        if ("/login.jsp".equals(path)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if ("/access-denied.jsp".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        if ("/login".equals(path)) {
            if (isMutation(request.getMethod()) && !CsrfTokens.matches(session,
                    request.getParameter("_csrf"))) {
                deny(request, response, "The sign-in form expired. Reload it and try again.");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserAccount currentUser;
        try {
            currentUser = userDAO.findActiveById(sessionUser.getId());
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to validate authenticated session", exception);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Authentication could not be verified. Try again later.");
            return;
        }
        if (currentUser == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        session.setAttribute(SESSION_USER, currentUser);

        if (!AuthorizationPolicy.allows(currentUser, path, request.getMethod(),
                request.getParameter("action"), request.getParameter("id"))) {
            deny(request, response, "Your account does not have permission to open that page or perform that action.");
            return;
        }
        if (isMutation(request.getMethod())
                && !CsrfTokens.matches(session, request.getParameter("_csrf"))) {
            deny(request, response, "Your request expired. Reload the page and try again.");
            return;
        }

        request.setAttribute(REQUEST_USER, currentUser);
        request.setAttribute(REQUEST_CSRF, CsrfTokens.ensure(session));
        response.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No resources to release.
    }

    private UserAccount sessionUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_USER);
        return value instanceof UserAccount ? (UserAccount) value : null;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/assets/") || path.startsWith("/css/")
                || path.startsWith("/js/") || path.startsWith("/images/")
                || path.equals("/favicon.ico") || path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|woff2?)$");
    }

    private boolean isMutation(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method);
    }

    private void deny(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/access-denied.jsp").forward(request, response);
    }

    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        response.setHeader("Cache-Control", "no-store");
    }
}
