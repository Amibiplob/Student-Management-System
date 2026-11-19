package com.studentmanagement.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Reads process environment values and a local, external .env file. */
public final class EnvironmentConfig {

    private static final Pattern VALID_KEY = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static Path loadedFile;
    private static final Map<String, String> FILE_VALUES = loadFileValues();

    private EnvironmentConfig() {
    }

    /** Process environment values take precedence over values from .env. */
    public static String get(String name) {
        String processValue = System.getenv(name);
        if (processValue != null && !processValue.trim().isEmpty()) {
            return processValue;
        }
        return FILE_VALUES.get(name);
    }

    /** Returns a safe description of the source selected for a setting, never its value. */
    public static String sourceOf(String name) {
        String processValue = System.getenv(name);
        if (processValue != null && !processValue.trim().isEmpty()) {
            return "process environment variable " + name;
        }
        if (FILE_VALUES.containsKey(name) && loadedFile != null) {
            return loadedFile.toString();
        }
        return "not configured";
    }

    private static Map<String, String> loadFileValues() {
        String configuredPath = System.getProperty("studentmanagement.env");
        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            configuredPath = System.getenv("STUDENT_MANAGEMENT_ENV_FILE");
        }
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            final Path path;
            try {
                path = Paths.get(configuredPath.trim()).toAbsolutePath().normalize();
            } catch (RuntimeException exception) {
                return Collections.emptyMap();
            }
            if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
                return Collections.emptyMap();
            }
            return readFile(path);
        }

        Set<Path> candidates = new LinkedHashSet<>();
        addProjectEnvironmentFile(candidates, getCodeLocation());
        addProjectEnvironmentFile(candidates, Paths.get(System.getProperty("user.dir", ".")));
        addEnvironmentDirectory(candidates, System.getProperty("catalina.base"));
        addEnvironmentDirectory(candidates, System.getProperty("catalina.home"));
        addEnvironmentDirectory(candidates, System.getenv("CATALINA_BASE"));
        addEnvironmentDirectory(candidates, System.getenv("CATALINA_HOME"));

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                if (!Files.isReadable(candidate)) {
                    return Collections.emptyMap();
                }
                return readFile(candidate);
            }
        }
        return Collections.emptyMap();
    }

    private static void addEnvironmentDirectory(Set<Path> candidates, String directory) {
        if (directory != null && !directory.trim().isEmpty()) {
            try {
                candidates.add(Paths.get(directory).toAbsolutePath().normalize().resolve(".env"));
            } catch (RuntimeException ignored) {
                // Ignore malformed optional launcher paths and keep looking elsewhere.
            }
        }
    }

    private static Path getCodeLocation() {
        try {
            return Paths.get(EnvironmentConfig.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException | RuntimeException ignored) {
            return null;
        }
    }

    private static void addProjectEnvironmentFile(Set<Path> candidates, Path start) {
        if (start == null) {
            return;
        }
        Path directory;
        try {
            directory = Files.isDirectory(start) ? start : start.getParent();
        } catch (RuntimeException exception) {
            return;
        }
        for (int depth = 0; directory != null && depth < 12; depth++) {
            Path normalized = directory.toAbsolutePath().normalize();
            if (Files.exists(normalized.resolve("pom.xml")) || Files.exists(normalized.resolve(".git"))) {
                candidates.add(normalized.resolve(".env"));
                return;
            }
            directory = directory.getParent();
        }
    }

    private static Map<String, String> readFile(Path path) {
        Map<String, String> values = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    line = stripBom(line);
                    firstLine = false;
                }
                parseLine(line, values);
            }
        } catch (IOException exception) {
            return Collections.emptyMap();
        }
        loadedFile = path.toAbsolutePath().normalize();
        return Collections.unmodifiableMap(values);
    }

    private static void parseLine(String line, Map<String, String> values) {
        String candidate = line.trim();
        if (candidate.isEmpty() || candidate.startsWith("#")) {
            return;
        }
        if (candidate.startsWith("export") && candidate.length() > 6
                && Character.isWhitespace(candidate.charAt(6))) {
            candidate = candidate.substring(7).trim();
        }

        int equals = candidate.indexOf('=');
        if (equals < 1) {
            return;
        }
        String key = candidate.substring(0, equals).trim();
        if (!VALID_KEY.matcher(key).matches()) {
            return;
        }
        String value = parseValue(candidate.substring(equals + 1).trim());
        if (value != null) {
            values.put(key, value);
        }
    }

    private static String parseValue(String raw) {
        if (raw.isEmpty()) {
            return "";
        }
        char quote = raw.charAt(0);
        if (quote == '\'' || quote == '"') {
            StringBuilder value = new StringBuilder();
            boolean escaped = false;
            int closing = -1;
            for (int index = 1; index < raw.length(); index++) {
                char current = raw.charAt(index);
                if (quote == '"' && escaped) {
                    value.append(unescapeDoubleQuoted(current));
                    escaped = false;
                } else if (quote == '"' && current == '\\') {
                    escaped = true;
                } else if (current == quote) {
                    closing = index;
                    break;
                } else {
                    value.append(current);
                }
            }
            if (closing < 0 || escaped) {
                return null;
            }
            String suffix = raw.substring(closing + 1).trim();
            return suffix.isEmpty() || suffix.startsWith("#") ? value.toString() : null;
        }

        for (int index = 0; index < raw.length(); index++) {
            if (raw.charAt(index) == '#' && (index == 0 || Character.isWhitespace(raw.charAt(index - 1)))) {
                return raw.substring(0, index).trim();
            }
        }
        return raw.trim();
    }

    private static char unescapeDoubleQuoted(char value) {
        if (value == 'n') {
            return '\n';
        }
        if (value == 'r') {
            return '\r';
        }
        return value;
    }

    private static String stripBom(String value) {
        return !value.isEmpty() && value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }
}
