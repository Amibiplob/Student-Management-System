package com.studentmanagement.util;

import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokens {

    public static final String SESSION_ATTRIBUTE = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfTokens() {
    }

    public static String ensure(HttpSession session) {
        Object existing = session.getAttribute(SESSION_ATTRIBUTE);
        if (existing instanceof String) {
            return (String) existing;
        }
        return rotate(session);
    }

    public static String rotate(HttpSession session) {
        byte[] token = new byte[32];
        RANDOM.nextBytes(token);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        session.setAttribute(SESSION_ATTRIBUTE, value);
        return value;
    }

    public static boolean matches(HttpSession session, String submitted) {
        if (session == null || submitted == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_ATTRIBUTE);
        if (!(expected instanceof String)) {
            return false;
        }
        return MessageDigest.isEqual(
                ((String) expected).getBytes(StandardCharsets.UTF_8),
                submitted.getBytes(StandardCharsets.UTF_8));
    }
}
