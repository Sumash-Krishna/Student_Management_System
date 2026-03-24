-- ============================================
--   Student Database Management System
--   MySQL Schema Setup
-- ============================================

CREATE DATABASE IF NOT EXISTS student_dbms;
USE student_dbms;

-- Departments Table
CREATE TABLE IF NOT EXISTS departments (
    dept_id     INT AUTO_INCREMENT PRIMARY KEY,
    dept_name   VARCHAR(100) NOT NULL UNIQUE,
    dept_code   VARCHAR(10)  NOT NULL UNIQUE
);

-- Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id   INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    phone        VARCHAR(15),
    dob          DATE,
    gender       ENUM('Male', 'Female', 'Other'),
    dept_id      INT,
    enrollment_year YEAR,
    gpa          DECIMAL(3, 2) DEFAULT 0.00,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE SET NULL
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_name  VARCHAR(100) NOT NULL,
    course_code  VARCHAR(10)  NOT NULL UNIQUE,
    credits      INT DEFAULT 3,
    dept_id      INT,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE SET NULL
);

-- Enrollments Table (Many-to-Many: Students <-> Courses)
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    grade         DECIMAL(4, 2),
    semester      VARCHAR(20),
    enrolled_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)  ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id, semester)
);

-- ============================================
--   Seed Data
-- ============================================

INSERT IGNORE INTO departments (dept_name, dept_code) VALUES
('Computer Science',    'CS'),
('Mathematics',         'MATH'),
('Physics',             'PHY'),
('Electronics',         'ECE'),
('Mechanical Engineering', 'MECH');

INSERT IGNORE INTO courses (course_name, course_code, credits, dept_id) VALUES
('Data Structures',         'CS101', 4, 1),
('Database Management',     'CS102', 3, 1),
('Calculus I',              'MATH101', 3, 2),
('Linear Algebra',          'MATH102', 3, 2),
('Mechanics',               'PHY101', 3, 3),
('Digital Electronics',     'ECE101', 4, 4),
('Thermodynamics',          'MECH101', 3, 5);
