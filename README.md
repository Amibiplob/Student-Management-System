# Student Management System

CampusDesk is a small academic student management application built with JSP, Jakarta Servlets, JDBC, Neon PostgreSQL, and Tailwind CSS. It is a Maven WAR project for Apache Tomcat 10.1 and Java 11. Neon is the only database target; the application does not use local PostgreSQL.

## Features

- List, search, and filter students by department.
- Add, view, edit, and delete student records.
- Validate student fields against the PostgreSQL column sizes and report case-insensitive duplicate email addresses.
- Add, edit, and delete departments; show their student counts and prevent deleting a department that still has students.
- Save students without a department, and filter the directory to show unassigned students.
- Show database-backed dashboard totals and recently created student records.
- Use prepared statements and close JDBC resources with try-with-resources.
- Keep database credentials outside committed source code.
- Authenticate users by comparing plain-text values stored in the existing `app_users.password_hash` column, with session renewal, session expiry, CSRF tokens, and login attempt throttling.
- Enforce ADMIN, TEACHER, and STUDENT permissions on server routes and direct JSP access.
- Let administrators create, edit, deactivate, and reactivate user accounts.

Plain-text passwords are used only for this academic demonstration. Do not expose this configuration as a public service. The app keeps database credentials outside committed source code and the WAR.

## Technology and requirements

- Java 11 JDK
- Maven 3.6 or newer
- Neon PostgreSQL project
- Apache Tomcat 10.1
- NetBeans (optional)

The project uses Jakarta Servlet API 6.0 with `provided` scope, the PostgreSQL JDBC driver, and JSTL for escaped JSP output. No Spring, ORM, or REST framework is used.

## PostgreSQL schema

The application targets this Neon database structure:

| Table | Columns and constraints |
| --- | --- |
| `departments` | `id SERIAL PRIMARY KEY`; `name VARCHAR(100) NOT NULL UNIQUE`; `description TEXT` |
| `students` | `id SERIAL PRIMARY KEY`; `name VARCHAR(100) NOT NULL`; `email VARCHAR(150) NOT NULL UNIQUE`; `phone VARCHAR(20)`; `gender VARCHAR(20)`; `date_of_birth DATE`; `address TEXT`; nullable `department_id INTEGER REFERENCES departments(id) ON DELETE SET NULL`; `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP` |
| `app_users` | `id SERIAL PRIMARY KEY`; `full_name VARCHAR(150) NOT NULL`; `email VARCHAR(150) NOT NULL` with a unique lowercase index; `password_hash VARCHAR(60) NOT NULL`; checked `role`; `is_active BOOLEAN NOT NULL DEFAULT TRUE`; unique nullable `student_id` foreign key; timestamp defaults for `created_at` and `updated_at` |

The `password_hash` name remains for compatibility with the existing table, but this academic demonstration stores and compares plain-text password values in that column.

The schema also has unique indexes `students_email_lower_uq` on `LOWER(email)` and `departments_name_lower_uq` on `LOWER(name)`, plus `students_department_id_idx` on `department_id` and `students_created_at_idx` on `created_at DESC`. The lowercase indexes prevent duplicates that differ only by letter case. Student inserts omit `id`, allowing PostgreSQL's `SERIAL` sequence to generate it.

Students may be unassigned. Deleting a department through this application is blocked while it has students and shows a message asking the user to move or delete them first. The database foreign key remains `ON DELETE SET NULL`, so it can set references to null if a department is deleted outside the application.

`src/main/resources/db/schema.sql` is a **bootstrap script for a brand-new, empty Neon database only**. It creates the departments, students, and app_users tables and their indexes in one transaction. It contains no `DROP`, `TRUNCATE`, or data reset statements. It is not an upgrade script; do not run it against an existing database.

For an existing Neon database, keep its `students` and `departments` tables and records. Before applying any migration, verify the Neon project and current schema. The additive `V1__create_app_users.sql` migration creates only `app_users` and its lowercase-email unique index; it does not alter or remove student or department rows or their schema. Apply it through the Neon SQL editor only if `app_users` is missing, and take a database backup first. Student accounts reference an existing student row through `app_users.student_id`; deleting that student row deletes its linked account through `ON DELETE CASCADE`. The existing `students.department_id` foreign key remains nullable and `ON DELETE SET NULL`. The app prevents department deletion while students are assigned to it.

The application does not execute migrations at startup. `schema.sql` is for a new, empty database; the versioned SQL under `db/migrations` is for existing databases.

For a new Neon database, select an empty database in the Neon project and apply `schema.sql` once through the Neon SQL editor. For an existing database, do not run `schema.sql` or recreate, drop, truncate, or reset tables or records. This project does not apply migrations automatically.

## Database configuration

The app uses Neon through `DB_URL`, `DB_USER`, and `DB_PASSWORD`:

| Variable | Example |
| --- | --- |
| `DB_URL` | Complete Neon connection URI |
| `DB_USER` | `student_app` when the URL does not include a username |
| `DB_PASSWORD` | Set only when the URL does not include a password |

For development, copy `.env.example` to `.env` in the project root and put the Neon connection settings there. The file is ignored by Git. The application reads it directly; it does not depend on a shell sourcing it or on NetBeans inheriting variables from a terminal. The parser supports plain assignments, optional `export`, and quoted values. Process environment variables override matching `.env` values.

For Neon, set `DB_URL` to the complete connection URI copied from the Neon project, including its query parameters such as `sslmode=require`. The app converts `postgresql://...` to JDBC format, removes URI user information from the JDBC URL, and passes the decoded URI username and password separately to the driver. When those credentials are present in the URI, they are authoritative; `DB_USER` and `DB_PASSWORD` are only used when the URL has no embedded credentials. This lets one URI remain the source of truth and prevents old split credentials from overriding it. URI credentials containing reserved characters must be percent-encoded. Neon URLs are required to use SSL; the app preserves a secure SSL mode and adds `sslmode=require` when one is absent or weaker. Other query parameters are preserved.

The loader first honors the JVM property `-Dstudentmanagement.env=/absolute/path/to/.env` or `STUDENT_MANAGEMENT_ENV_FILE`. Otherwise it checks the nearest project root (identified by `pom.xml` or `.git`) from the application classes location and working directory, followed by `$CATALINA_BASE/.env` and `$CATALINA_HOME/.env`. This avoids silently picking up an unrelated `.env` from a parent directory. Keep `.env` outside `src/main/webapp`, the WAR, and source control. The root `.env` is not included in the WAR by Maven.

`SESSION_COOKIE_SECURE` and `DEMO_ACCOUNTS_ENABLED` are Tomcat process settings. Set `SESSION_COOKIE_SECURE=true` when serving over HTTPS and `false` for local HTTP. After confirming the Neon schema, set `DEMO_ACCOUNTS_ENABLED=true` for one application startup, then remove it. The seeder creates or updates these accounts without duplicating users:

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@admin.com` | `123456` |
| TEACHER | `teacher@teacher.com` | `123456` |
| STUDENT | `student@student.com` | `123456` |

The STUDENT account links only to the unique existing **Rahim Ahmed** row with email `rahim@example.com`; seeding never creates or edits a student row. If that row is missing, duplicated, or already linked to another account, setup fails and rolls back the account updates. Startup never applies SQL migrations or `schema.sql`.

## Roles and access

| Role | Permissions |
| --- | --- |
| ADMIN | Dashboard; create, view, edit, and delete student records; manage departments; create, edit, deactivate, reactivate, and reset passwords for user accounts |
| TEACHER | Dashboard; search and view student records; view departments; no student, department, or user-account changes |
| STUDENT | Student dashboard and only the profile linked to that account |

Roles are loaded from PostgreSQL and checked on every protected request. A student profile URL with another student's ID returns an access-denied response. Login sessions expire after 30 minutes of inactivity; the session ID and CSRF token rotate after login. Login attempts are throttled per remote address after repeated failures. Tomcat must be configured with HTTPS and `SESSION_COOKIE_SECURE=true` for a deployed site.

## Build and deploy

The project targets Java 11 and packages the PostgreSQL JDBC driver in the WAR. Maven compilation and packaging do not connect to the database. Build and package with:

```sh
mvn clean package
```

To run the full Maven install lifecycle, use `mvn clean install`. The artifact is `target/StudentManagementSystem.war`. JSPs are compiled by Tomcat when the application is deployed or when a JSP is first requested.

### NetBeans local run

1. Choose **File → Open Project** and select this Maven project directory.
2. Allow NetBeans to load the Maven project and dependencies, then configure a Tomcat 10.1 server.
3. Keep the ignored `.env` in the project root. Maven or an exploded project deployment can find it from the application classes directory. If the server deploys classes outside the project tree, add `-Dstudentmanagement.env=/absolute/path/to/project/.env` to that Tomcat instance's JVM options.
4. Run or deploy the project. The database URI is read server-side; it is not sent to JSP output or browser JavaScript.

### Standalone Tomcat 10.1

Keep `.env` outside the WAR and outside `webapps`. The loader checks `$CATALINA_BASE/.env` and `$CATALINA_HOME/.env` automatically. To use the existing project `.env` without copying it, put this JVM option in Tomcat's protected `bin/setenv.sh` (create the file if needed):

```sh
CATALINA_OPTS="$CATALINA_OPTS -Dstudentmanagement.env=/absolute/path/to/StudentManagementSystem/.env"
```

Set restrictive file permissions on `.env` and `setenv.sh`. For a service manager that uses a protected environment file instead, provide `DB_URL` and, only when needed, `DB_USER` and `DB_PASSWORD` to the Tomcat process. Restart Tomcat after changing its configuration, then deploy the WAR:

```sh
cp target/StudentManagementSystem.war "$CATALINA_BASE/webapps/StudentManagementSystem.war"
```

For a local foreground app server, set `CATALINA_HOME` and `CATALINA_BASE` to the Tomcat 10.1 installation and run:

```sh
"$CATALINA_HOME/bin/catalina.sh" run
```

When `.env` is in the project directory, use the JVM property above if Tomcat's class location and base directory cannot discover it. Do not put credentials in `CATALINA_OPTS`; the property value is only a file path.

## Quick start

Install Java 11, Maven, and Tomcat 10.1. Configure `.env` with the Neon URI, build the WAR with `mvn clean package`, and deploy it to Tomcat. The application connects only to Neon; do not install or configure local PostgreSQL for this project.

## Application URLs

- Sign in: `http://localhost:8080/StudentManagementSystem/login`
- Dashboard: `http://localhost:8080/StudentManagementSystem/dashboard`
- Students: `http://localhost:8080/StudentManagementSystem/students`
- Departments: `http://localhost:8080/StudentManagementSystem/departments`
- Access denied: `http://localhost:8080/StudentManagementSystem/access-denied.jsp`
- User accounts (ADMIN): `http://localhost:8080/StudentManagementSystem/users`

## Troubleshooting

- **Demo accounts were not created or updated:** Confirm the Neon schema, set `DEMO_ACCOUNTS_ENABLED=true` in Tomcat's protected process environment for one startup, then remove it. The setting updates only the three named accounts in `app_users`.
- **The STUDENT demo account could not be linked:** Confirm exactly one existing Rahim Ahmed row has email `rahim@example.com` and that it is not linked to another app account. The seeder never creates or changes a student row.
- **Missing database setting:** Confirm `DB_URL` is in the project's `.env` or Tomcat's environment. `DB_USER` and `DB_PASSWORD` are needed only when the URL does not contain credentials. For a custom file location, set `-Dstudentmanagement.env` and restart Tomcat.
- **Neon connection failure:** Run `com.studentmanagement.util.DBTest` with the application classpath for a sanitized configuration summary, DNS/TCP preflight, and JDBC SQLSTATE diagnosis. If DNS fails, TCP was not attempted; restore DNS resolution before investigating credentials or database settings. Otherwise confirm the Neon compute is available, credentials are current, and the URI retains `sslmode=require` and its other query parameters. Diagnostics never print the URI or database password.
- **Table or column does not exist:** Check the configured Neon database and compare its structure with the schema documented above. `schema.sql` is only for a new, empty database; it is not a repair or migration script for an existing database.
- **Duplicate email or department:** Email and department names are unique without regard to letter case. Choose a different value.
- **Cannot delete a department:** It still has student records. Reassign or delete those records first.
- **Tomcat cannot deploy the WAR:** Confirm Tomcat is version 10.1, inspect its logs, and verify the artifact exists at `target/StudentManagementSystem.war`.
- **Tailwind styles do not load:** The current pages use the Tailwind Play CDN and need internet access in the browser. For offline use, build Tailwind assets locally before deployment.

## Verification boundaries

Java compilation, JSP validation, and available tests should be run with Maven and Tomcat dependencies installed. Compilation or JSP validation alone does not verify live Neon authentication, role enforcement, account setup, CRUD behavior, or preservation of existing data. Database integration is verified only when those flows are actually executed against Neon.
