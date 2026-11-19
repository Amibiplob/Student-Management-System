package com.studentmanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.security.Security;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Opens PostgreSQL connections using environment values and a local external .env file. */
public final class DBConnection {

    private static final String JDBC_PREFIX = "jdbc:postgresql://";
    private static final String POSTGRESQL_PREFIX = "postgresql://";
    private static final Pattern SQL_STATE = Pattern.compile("[0-9A-Z]{5}");
    private static final Pattern POSTGRES_URL = Pattern.compile(
            "(?i)(?:jdbc:postgresql:|postgresql://)\\S+");
    private static final Pattern CONNECTION_SETTING = Pattern.compile(
            "(?i)\\b(host|port|dbname|database|user|username|password|sslpassword|passfile)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^,;\\s]+)");
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final String[] SSL_SYSTEM_PROPERTIES = {
        "javax.net.ssl.trustStore",
        "javax.net.ssl.trustStoreType",
        "javax.net.ssl.trustStoreProvider",
        "javax.net.ssl.keyStore",
        "javax.net.ssl.keyStoreType",
        "javax.net.ssl.keyStoreProvider",
        "jdk.tls.client.protocols",
        "https.protocols",
        "jsse.enableSNIExtension",
        "com.sun.net.ssl.checkRevocation",
        "jdk.tls.client.enableSessionTicketExtension",
        "jdk.tls.useExtendedMasterSecret",
        "jdk.tls.ephemeralDHKeySize",
        "jdk.tls.namedGroups",
        "jdk.tls.client.cipherSuites",
        "jdk.tls.server.protocols",
        "jdk.tls.server.cipherSuites",
        "jdk.tls.client.SignatureSchemes"
    };
    private static final String[] SSL_SECURITY_PROPERTIES = {
        "ssl.TrustManagerFactory.algorithm",
        "ssl.KeyManagerFactory.algorithm",
        "jdk.tls.disabledAlgorithms",
        "jdk.certpath.disabledAlgorithms"
    };

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String configuredUrl = requiredSetting("DB_URL");
        ConnectionSettings settings = parseConnectionSettings(configuredUrl);
        String user = settings.user != null ? settings.user : requiredSetting("DB_USER");
        String password = settings.password != null ? settings.password : requiredSetting("DB_PASSWORD");
        try {
            // Tomcat may initialize DriverManager before the webapp class loader is visible to it.
            // Loading from this webapp's loader registers the WAR-scoped JDBC driver explicitly.
            Class.forName("org.postgresql.Driver", true, DBConnection.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            SQLException failure = new SQLException("The PostgreSQL JDBC driver is unavailable.");
            logConnectionFailure(failure, settings, configuredUrl, user, password);
            throw failure;
        }
        try {
            return DriverManager.getConnection(settings.jdbcUrl, user, password);
        } catch (SQLException exception) {
            // Do not retain the driver's message or URL: callers may log SQLExceptions.
            String sqlState = findSqlState(exception);
            String detail = classifyFailure(exception);
            logConnectionFailure(exception, settings, configuredUrl, user, password);
            String diagnostic = "Database connection failed: " + detail
                    + (sqlState == null ? "" : " SQLSTATE " + sqlState + ".");
            throw new SQLException(diagnostic,
                    sqlState, exception.getErrorCode());
        }
    }

    /** Summarizes runtime JDBC and SSL settings without exposing connection credentials. */
    public static String runtimeSettingsSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Runtime: Java ").append(System.getProperty("java.version", "unknown"))
                .append("; JDBC driver ").append(jdbcDriverVersion())
                .append("; driver classloader ")
                .append(org.postgresql.Driver.class.getClassLoader() == null
                        ? "bootstrap" : org.postgresql.Driver.class.getClassLoader().getClass().getName());
        for (String property : SSL_SYSTEM_PROPERTIES) {
            summary.append("\nSSL JVM property ").append(property).append('=').append(propertyValue(property));
        }
        for (String property : SSL_SECURITY_PROPERTIES) {
            String value = Security.getProperty(property);
            summary.append("\nSSL security property ").append(property).append('=')
                    .append(value == null || value.isEmpty() ? "<unset>" : value);
        }
        return summary.toString();
    }

    private static String jdbcDriverVersion() {
        Package driverPackage = org.postgresql.Driver.class.getPackage();
        String version = driverPackage == null ? null : driverPackage.getImplementationVersion();
        return version == null || version.isEmpty() ? "unknown" : version;
    }

    private static String propertyValue(String property) {
        String value = System.getProperty(property);
        return value == null || value.isEmpty() ? "<unset>" : value;
    }

    /** Emits structural diagnostics only; raw exception messages may contain connection details. */
    private static void logConnectionFailure(SQLException original, ConnectionSettings settings,
            String configuredUrl, String user, String password) {
        LOGGER.log(Level.SEVERE, "PostgreSQL connection failed; exception messages and credentials omitted.");
        LOGGER.log(Level.SEVERE, "Connection metadata: {0}; sslmode={1}.", new Object[] {
            settings.neon ? "Neon hostname present" : "PostgreSQL hostname present",
            settings.sslMode == null ? "not specified" : settings.sslMode
        });
        LOGGER.severe(runtimeSettingsSummary());

        List<Throwable> chain = collectCauses(original);
        for (int index = 0; index < chain.size(); index++) {
            Throwable throwable = chain.get(index);
            StringBuilder item = new StringBuilder("JDBC exception chain[").append(index)
                    .append("]: class=").append(throwable.getClass().getName());
            if (throwable instanceof SQLException) {
                SQLException sqlException = (SQLException) throwable;
                item.append("; SQLState=").append(sqlException.getSQLState() == null
                        ? "<none>" : sqlException.getSQLState())
                        .append("; vendorErrorCode=").append(sqlException.getErrorCode());
            }
            LOGGER.severe(item.toString());
            String safeMessage = sanitizeDriverMessage(throwable.getMessage(), settings,
                    configuredUrl, user, password);
            if (safeMessage != null) {
                LOGGER.severe("JDBC exception message (connection values redacted): " + safeMessage);
            }
            String messageSummary = safeTlsMessageSummary(throwable.getMessage());
            if (messageSummary != null) {
                LOGGER.severe("JDBC exception safe TLS message category: " + messageSummary);
            }
            if (index == 0) {
                LOGGER.severe("Original JDBC exception stack trace (message omitted):");
                for (StackTraceElement frame : throwable.getStackTrace()) {
                    LOGGER.severe("    at " + frame.toString());
                }
            }
            if (isSslThrowable(throwable)) {
                LOGGER.severe("SSL/TLS stack trace for " + throwable.getClass().getName()
                        + " (exception message omitted):");
                for (StackTraceElement frame : throwable.getStackTrace()) {
                    LOGGER.severe("    at " + frame.toString());
                }
            }
        }
    }

    private static String sanitizeDriverMessage(String message, ConnectionSettings settings,
            String configuredUrl, String user, String password) {
        if (message == null || message.isEmpty()) {
            return null;
        }
        String safe = replaceSecret(message, configuredUrl, "[DB_URL_OMITTED]");
        safe = replaceSecret(safe, settings.jdbcUrl, "[DB_URL_OMITTED]");
        safe = replaceSecret(safe, settings.hostname, "[DB_ENDPOINT_OMITTED]");
        safe = replaceSecret(safe, user, "[DB_USER_OMITTED]");
        safe = replaceSecret(safe, password, "[DB_PASSWORD_OMITTED]");
        safe = POSTGRES_URL.matcher(safe).replaceAll("[DB_URL_OMITTED]");
        safe = CONNECTION_SETTING.matcher(safe).replaceAll("$1=[CONNECTION_VALUE_OMITTED]");
        safe = safe.replaceAll("[\\r\\n\\t]+", " ").trim();
        final int maximumLength = 512;
        return safe.length() > maximumLength ? safe.substring(0, maximumLength) + "..." : safe;
    }

    private static String replaceSecret(String text, String secret, String replacement) {
        return secret == null || secret.isEmpty() ? text : text.replace(secret, replacement);
    }

    /** Maps TLS-related driver messages to fixed labels so raw text can never expose credentials. */
    private static String safeTlsMessageSummary(String message) {
        if (message == null) {
            return null;
        }
        String normalized = POSTGRES_URL.matcher(message).replaceAll("").toLowerCase(Locale.ROOT);
        if (!(normalized.contains("ssl") || normalized.contains("tls")
                || normalized.contains("handshake") || normalized.contains("certificate"))) {
            return null;
        }
        if (normalized.contains("pkix path building failed")
                || normalized.contains("unable to find valid certification path")
                || normalized.contains("trust anchor")) {
            return "TLS certificate trust-path validation failed.";
        }
        if (normalized.contains("no subject alternative") || normalized.contains("no name matching")) {
            return "TLS server certificate hostname verification failed.";
        }
        if (normalized.contains("certificate_expired") || normalized.contains("expired certificate")) {
            return "TLS certificate was reported expired.";
        }
        if (normalized.contains("certificate_unknown")) {
            return "TLS peer returned a certificate_unknown alert.";
        }
        if (normalized.contains("protocol_version") || normalized.contains("protocol version")) {
            return "TLS protocol version was rejected.";
        }
        if (normalized.contains("no appropriate protocol") || normalized.contains("no cipher suites in common")) {
            return "TLS runtime could not select a mutually supported protocol or cipher suite.";
        }
        if (normalized.contains("handshake_failure")) {
            return "TLS peer returned a handshake_failure alert.";
        }
        if (normalized.contains("remote host terminated the handshake")
                || normalized.contains("remote host closed connection during handshake")
                || normalized.contains("peer closed connection during handshake")) {
            return "TLS peer closed the connection during negotiation.";
        }
        if (normalized.contains("connection reset")) {
            return "Network connection reset during TLS negotiation.";
        }
        if (normalized.contains("unexpected end of file") || normalized.contains("eof")) {
            return "TLS peer closed the stream before negotiation completed.";
        }
        return "TLS-related driver message present; raw message omitted.";
    }

    private static boolean isSslThrowable(Throwable throwable) {
        String type = throwable.getClass().getName().toLowerCase(Locale.ROOT);
        return throwable instanceof SSLException || type.contains("ssl") || type.contains("tls");
    }

    /** Reports parsed connection metadata without exposing any endpoint or credential values. */
    public static String configurationSummary() throws SQLException {
        ConnectionSettings settings = parseConnectionSettings(requiredSetting("DB_URL"));
        boolean hasUser = settings.user != null || hasSetting("DB_USER");
        boolean hasPassword = settings.password != null || hasSetting("DB_PASSWORD");
        if (!hasUser) {
            requiredSetting("DB_USER");
        }
        if (!hasPassword) {
            requiredSetting("DB_PASSWORD");
        }

        String hostType = settings.neon ? "Neon hostname" : "PostgreSQL hostname";
        String ssl = settings.sslMode == null ? "not specified" : settings.sslMode;
        int port = settings.port < 0 ? 5432 : settings.port;
        return "DB_URL source: " + EnvironmentConfig.sourceOf("DB_URL") + "\n"
                + "Parsed connection metadata: " + hostType + " "
                + (settings.hostname == null ? "missing" : "present") + "; database name "
                + (settings.databaseNamePresent ? "present" : "missing") + "; port " + port
                + "; username " + (settings.user == null ? "from DB_USER" : "extracted from URI")
                + "; password " + (settings.password == null ? "from DB_PASSWORD" : "extracted from URI")
                + "; sslmode=" + ssl + ".";
    }

    /** Performs a bounded DNS/TCP reachability check without sending database credentials. */
    public static String networkProbeSummary() throws SQLException {
        ConnectionSettings settings = parseConnectionSettings(requiredSetting("DB_URL"));
        if (settings.hostname == null) {
            return "DNS/TCP probe unavailable for this JDBC URL form.";
        }
        int port = settings.port < 0 ? 5432 : settings.port;
        final InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(settings.hostname);
        } catch (UnknownHostException exception) {
            return "DNS resolution failed for the configured database endpoint; TCP was not attempted.";
        }

        boolean timedOut = false;
        boolean refused = false;
        boolean noRoute = false;
        int checked = Math.min(addresses.length, 4);
        for (int index = 0; index < checked; index++) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(addresses[index], port), 2500);
                return "DNS resolved; TCP port " + port + " is reachable.";
            } catch (SocketTimeoutException exception) {
                timedOut = true;
            } catch (ConnectException exception) {
                refused = true;
            } catch (NoRouteToHostException exception) {
                noRoute = true;
            } catch (java.io.IOException exception) {
                noRoute = true;
            }
        }
        if (timedOut) {
            return "DNS resolved; TCP connection to port " + port + " timed out.";
        }
        if (refused) {
            return "DNS resolved; TCP connection to port " + port + " was refused.";
        }
        if (noRoute) {
            return "DNS resolved; TCP connection to port " + port + " failed at the network layer.";
        }
        return "DNS resolved, but no TCP addresses were available for the endpoint.";
    }

    /** Returns only diagnostics created by this class; arbitrary driver text is discarded. */
    public static String sanitizedDiagnostic(SQLException exception) {
        String message = exception.getMessage();
        if (message != null && message.startsWith("Database connection failed: ")) {
            return message;
        }
        if ("The local environment configuration could not be loaded.".equals(message)
                || "DB_URL must be a valid PostgreSQL URI or JDBC PostgreSQL URL.".equals(message)) {
            return message;
        }
        if (message != null && message.startsWith("Required database setting is missing: ")) {
            String setting = message.substring("Required database setting is missing: ".length());
            if ("DB_URL".equals(setting) || "DB_USER".equals(setting) || "DB_PASSWORD".equals(setting)) {
                return message;
            }
        }
        return "Database configuration or connection failed.";
    }

    private static boolean hasSetting(String name) {
        String value = EnvironmentConfig.get(name);
        return value != null && !value.trim().isEmpty();
    }

    private static String classifyFailure(Throwable failure) {
        List<Throwable> causes = collectCauses(failure);
        for (Throwable cause : causes) {
            String type = cause.getClass().getName();
            String message = messageWithoutPostgresUrl(cause.getMessage());
            if (message.contains("no suitable driver found")) {
                return "The PostgreSQL JDBC driver is not registered with the application class loader.";
            }
            if (cause instanceof UnknownHostException || type.endsWith("UnknownHostException")
                    || message.contains("name or service not known")
                    || message.contains("unknown host") || message.contains("unknownhostexception")
                    || message.contains("name resolution")
                    || message.contains("no address associated with hostname")
                    || message.contains("temporary failure in name resolution")
                    || message.contains("could not translate host name")) {
                return "DNS resolution failed for the configured database endpoint.";
            }
        }
        for (Throwable cause : causes) {
            String message = cause.getMessage() == null ? ""
                    : cause.getMessage().toLowerCase(Locale.ROOT);
            if (cause instanceof SocketTimeoutException || message.contains("connection timed out")
                    || message.contains("connect timed out") || message.contains("read timed out")) {
                return "TCP connection timed out while reaching the database endpoint.";
            }
        }
        for (Throwable cause : causes) {
            String message = cause.getMessage() == null ? ""
                    : cause.getMessage().toLowerCase(Locale.ROOT);
            if (cause instanceof ConnectException || message.contains("connection refused")) {
                return "TCP connection was refused by the database endpoint.";
            }
            if (cause instanceof NoRouteToHostException || message.contains("network is unreachable")
                    || message.contains("no route to host")) {
                return "The network has no route to the database endpoint.";
            }
        }
        for (Throwable cause : causes) {
            String type = cause.getClass().getName().toLowerCase(Locale.ROOT);
            String message = messageWithoutPostgresUrl(cause.getMessage());
            if (cause instanceof SSLException || type.contains("ssl") || type.contains("tls")
                    || message.contains("ssl error") || message.contains("ssl handshake")
                    || message.contains("tls handshake") || message.contains("ssl exception")
                    || message.contains("certificate")) {
                return "SSL/TLS negotiation failed for the database connection.";
            }
        }

        String sqlState = findSqlState(failure);
        if (sqlState != null && sqlState.startsWith("28")) {
            return "PostgreSQL rejected the supplied authentication credentials.";
        }
        if ("3D000".equals(sqlState)) {
            return "PostgreSQL rejected the configured database name.";
        }
        if (sqlState != null && !sqlState.startsWith("08")) {
            return "PostgreSQL returned a database error.";
        }
        if (sqlState != null && sqlState.startsWith("08")) {
            return "The connection could not be established; DNS, TCP, or SSL setup failed.";
        }
        return "The connection failed before PostgreSQL accepted the session.";
    }

    private static String messageWithoutPostgresUrl(String message) {
        return message == null ? ""
                : POSTGRES_URL.matcher(message).replaceAll("").toLowerCase(Locale.ROOT);
    }

    private static List<Throwable> collectCauses(Throwable failure) {
        List<Throwable> causes = new ArrayList<>();
        Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Throwable> pending = new ArrayDeque<>();
        if (failure != null) {
            pending.add(failure);
        }
        while (!pending.isEmpty()) {
            Throwable cause = pending.removeFirst();
            if (!seen.add(cause)) {
                continue;
            }
            causes.add(cause);
            if (cause.getCause() != null) {
                pending.addLast(cause.getCause());
            }
            if (cause instanceof SQLException && ((SQLException) cause).getNextException() != null) {
                pending.addLast(((SQLException) cause).getNextException());
            }
            for (Throwable suppressed : cause.getSuppressed()) {
                pending.addLast(suppressed);
            }
        }
        return causes;
    }

    private static String findSqlState(Throwable failure) {
        for (Throwable cause : collectCauses(failure)) {
            if (cause instanceof SQLException) {
                String state = ((SQLException) cause).getSQLState();
                if (state != null && SQL_STATE.matcher(state).matches()) {
                    return state;
                }
            }
        }
        return null;
    }

    private static String requiredSetting(String name) throws SQLException {
        final String value;
        try {
            value = EnvironmentConfig.get(name);
        } catch (IllegalStateException exception) {
            throw new SQLException("The local environment configuration could not be loaded.");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Required database setting is missing: " + name);
        }
        return value;
    }

    private static ConnectionSettings parseConnectionSettings(String configuredUrl) throws SQLException {
        String value = configuredUrl.trim();
        boolean alreadyJdbcUrl = value.regionMatches(true, 0, "jdbc:postgresql:", 0,
                "jdbc:postgresql:".length());
        boolean postgresUri = value.regionMatches(true, 0, POSTGRESQL_PREFIX, 0,
                POSTGRESQL_PREFIX.length());
        if (!alreadyJdbcUrl && !postgresUri) {
            throw invalidUrl();
        }

        String uriText = alreadyJdbcUrl ? value.substring("jdbc:".length()) : value;
        final URI uri;
        try {
            uri = URI.create(uriText);
        } catch (IllegalArgumentException exception) {
            throw invalidUrl();
        }

        // Older PostgreSQL JDBC URL forms are already JDBC URLs; pass them through.
        if (uri.isOpaque()) {
            if (alreadyJdbcUrl) {
                return new ConnectionSettings(value, null, null, null, -1, false, null, false);
            }
            throw invalidUrl();
        }
        if (!"postgresql".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
                || uri.getRawAuthority() == null || uri.getRawFragment() != null) {
            throw invalidUrl();
        }

        String authority = uri.getRawAuthority();
        String embeddedUser = null;
        String embeddedPassword = null;
        String rawUserInfo = uri.getRawUserInfo();
        if (rawUserInfo != null) {
            int separator = rawUserInfo.indexOf(':');
            String rawUser = separator < 0 ? rawUserInfo : rawUserInfo.substring(0, separator);
            if (rawUser.isEmpty()) {
                throw invalidUrl();
            }
            embeddedUser = decodeUriPart(rawUser);
            if (separator >= 0) {
                embeddedPassword = decodeUriPart(rawUserInfo.substring(separator + 1));
            }
            int at = authority.lastIndexOf('@');
            if (at < 0 || at == authority.length() - 1) {
                throw invalidUrl();
            }
            authority = authority.substring(at + 1);
        }

        StringBuilder jdbcUrl = new StringBuilder(JDBC_PREFIX).append(authority);
        if (uri.getRawPath() != null) {
            jdbcUrl.append(uri.getRawPath());
        }
        if (uri.getRawQuery() != null) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }

        boolean neon = isNeonHostname(uri.getHost());
        String safeJdbcUrl = requireNeonSsl(jdbcUrl.toString(), uri.getHost(), uri.getRawQuery());
        String sslMode = queryParameter(safeJdbcUrl, "sslmode");
        return new ConnectionSettings(safeJdbcUrl, embeddedUser, embeddedPassword,
                uri.getHost(), uri.getPort(), uri.getRawPath() != null && uri.getRawPath().length() > 1,
                sslMode, neon);
    }

    private static boolean isNeonHostname(String hostname) {
        return hostname != null && hostname.toLowerCase(Locale.ROOT).endsWith(".neon.tech");
    }

    private static String queryParameter(String url, String requestedName) throws SQLException {
        int queryStart = url.indexOf('?');
        if (queryStart < 0) {
            return null;
        }
        String result = null;
        for (String parameter : url.substring(queryStart + 1).split("&", -1)) {
            int equals = parameter.indexOf('=');
            String name = decodeUriPart(equals < 0 ? parameter : parameter.substring(0, equals));
            if (requestedName.equalsIgnoreCase(name)) {
                result = decodeUriPart(equals < 0 ? "" : parameter.substring(equals + 1));
            }
        }
        return result;
    }

    private static String requireNeonSsl(String jdbcUrl, String hostname, String rawQuery)
            throws SQLException {
        if (!isNeonHostname(hostname)) {
            return jdbcUrl;
        }

        List<String> parameters = new ArrayList<>();
        String secureMode = null;
        if (rawQuery != null && !rawQuery.isEmpty()) {
            for (String parameter : rawQuery.split("&", -1)) {
                int equals = parameter.indexOf('=');
                String rawName = equals < 0 ? parameter : parameter.substring(0, equals);
                if ("sslmode".equalsIgnoreCase(decodeUriPart(rawName))) {
                    String rawValue = equals < 0 ? "" : parameter.substring(equals + 1);
                    String mode = decodeUriPart(rawValue).toLowerCase(Locale.ROOT);
                    if (sslModeStrength(mode) > sslModeStrength(secureMode)) {
                        secureMode = mode;
                    }
                } else {
                    parameters.add(parameter);
                }
            }
        }
        parameters.add("sslmode=" + (secureMode == null ? "require" : secureMode));
        int queryStart = jdbcUrl.indexOf('?');
        String baseUrl = queryStart < 0 ? jdbcUrl : jdbcUrl.substring(0, queryStart);
        return baseUrl + "?" + String.join("&", parameters);
    }

    private static int sslModeStrength(String mode) {
        if ("verify-full".equals(mode)) {
            return 3;
        }
        if ("verify-ca".equals(mode)) {
            return 2;
        }
        if ("require".equals(mode)) {
            return 1;
        }
        return 0;
    }

    private static String decodeUriPart(String value) throws SQLException {
        try {
            // A plus sign is literal in URI userinfo and query components.
            return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8.name());
        } catch (IllegalArgumentException | java.io.UnsupportedEncodingException exception) {
            throw invalidUrl();
        }
    }

    private static SQLException invalidUrl() {
        return new SQLException("DB_URL must be a valid PostgreSQL URI or JDBC PostgreSQL URL.");
    }

    private static final class ConnectionSettings {

        private final String jdbcUrl;
        private final String user;
        private final String password;
        private final String hostname;
        private final int port;
        private final boolean databaseNamePresent;
        private final String sslMode;
        private final boolean neon;

        private ConnectionSettings(String jdbcUrl, String user, String password, String hostname,
                int port, boolean databaseNamePresent, String sslMode, boolean neon) {
            this.jdbcUrl = jdbcUrl;
            this.user = user;
            this.password = password;
            this.hostname = hostname;
            this.port = port;
            this.databaseNamePresent = databaseNamePresent;
            this.sslMode = sslMode;
            this.neon = neon;
        }
    }
}
