-- Additive authentication migration for an existing Student Management System database.
-- Apply once with psql -v ON_ERROR_STOP=1 after taking a database backup.
-- This migration never alters or removes existing students or departments.

BEGIN;

CREATE TABLE IF NOT EXISTS app_users (
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

CREATE UNIQUE INDEX IF NOT EXISTS app_users_email_lower_uq
    ON app_users (LOWER(email));

COMMIT;
