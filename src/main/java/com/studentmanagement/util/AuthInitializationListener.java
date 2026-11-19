package com.studentmanagement.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.EnumSet;
import jakarta.servlet.SessionTrackingMode;

@WebListener
public class AuthInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().getSessionCookieConfig().setHttpOnly(true);
        String secureCookie = System.getenv("SESSION_COOKIE_SECURE");
        event.getServletContext().getSessionCookieConfig().setSecure(
                "true".equalsIgnoreCase(secureCookie) || "1".equals(secureCookie));
        event.getServletContext().setSessionTrackingModes(
                EnumSet.of(SessionTrackingMode.COOKIE));
        new InitialUserSeeder().seed();
    }
}
