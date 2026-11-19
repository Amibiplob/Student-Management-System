-- Bootstrap schema for a brand-new, empty Student Management System database.
-- This is not a migration and must not be run against an existing database.
-- The transaction prevents a partial bootstrap if any statement fails.

BEGIN;

CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    gender VARCHAR(20),
    date_of_birth DATE,
    address TEXT,
    department_id INTEGER REFERENCES departments(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Case-insensitive uniqueness supplements the case-sensitive UNIQUE constraints.
CREATE UNIQUE INDEX students_email_lower_uq
    ON students (LOWER(email));

CREATE UNIQUE INDEX departments_name_lower_uq
    ON departments (LOWER(name));

CREATE INDEX students_department_id_idx
    ON students (department_id);

CREATE INDEX students_created_at_idx
    ON students (created_at DESC);

-- Authentication tables for a brand-new database. Existing databases should
-- use the separate additive migration in db/migrations instead of this file.
CREATE TABLE app_users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    student_id INTEGER UNIQUE REFERENCES students(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT app_users_student_link_ck CHECK (
        (role = 'STUDENT' AND student_id IS NOT NULL)
        OR (role IN ('ADMIN', 'TEACHER') AND student_id IS NULL)
    )
);

CREATE UNIQUE INDEX app_users_email_lower_uq
    ON app_users (LOWER(email));

COMMIT;
