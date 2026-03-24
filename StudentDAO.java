package com.studentdbms.dao;

import com.studentdbms.model.Department;
import com.studentdbms.model.Student;
import com.studentdbms.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Student-related database operations.
 * Handles all CRUD operations and advanced queries using PreparedStatements.
 */
public class StudentDAO {

    // ── CREATE ──────────────────────────────────────────────────────────────

    /**
     * Inserts a new student into the database.
     * @return generated student_id, or -1 on failure.
     */
    public int addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students " +
                     "(first_name, last_name, email, phone, dob, gender, dept_id, enrollment_year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setDate(5, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(6, s.getGender());
            ps.setInt(7, s.getDeptId());
            ps.setInt(8, s.getEnrollmentYear());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /**
     * Fetches a single student by ID (with department name joined).
     */
    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Returns all students (with department name joined).
     */
    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "ORDER BY s.student_id";
        return executeQuery(sql);
    }

    /**
     * Returns only active students.
     */
    public List<Student> getActiveStudents() throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.is_active = TRUE ORDER BY s.last_name";
        return executeQuery(sql);
    }

    /**
     * Searches students by name (first or last), email, or phone (partial match).
     */
    public List<Student> searchStudents(String keyword) throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.first_name LIKE ? OR s.last_name LIKE ? " +
                     "   OR s.email LIKE ? OR s.phone LIKE ? " +
                     "ORDER BY s.last_name";

        List<Student> results = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Returns all students belonging to a given department.
     */
    public List<Student> getStudentsByDept(int deptId) throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.dept_id = ? ORDER BY s.last_name";

        List<Student> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Returns students with GPA above a specified threshold.
     */
    public List<Student> getTopStudents(double minGpa) throws SQLException {
        String sql = "SELECT s.*, d.dept_name FROM students s " +
                     "LEFT JOIN departments d ON s.dept_id = d.dept_id " +
                     "WHERE s.gpa >= ? ORDER BY s.gpa DESC";

        List<Student> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minGpa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────

    /**
     * Updates all mutable fields of an existing student.
     * @return true if update was successful.
     */
    public boolean updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, " +
                     "dob=?, gender=?, dept_id=?, enrollment_year=?, gpa=? " +
                     "WHERE student_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setDate(5, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(6, s.getGender());
            ps.setInt(7, s.getDeptId());
            ps.setInt(8, s.getEnrollmentYear());
            ps.setDouble(9, s.getGpa());
            ps.setInt(10, s.getStudentId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Toggles the active/inactive status of a student (soft delete).
     */
    public boolean setActiveStatus(int studentId, boolean active) throws SQLException {
        String sql = "UPDATE students SET is_active=? WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates only the GPA of a student.
     */
    public boolean updateGpa(int studentId, double gpa) throws SQLException {
        String sql = "UPDATE students SET gpa=? WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, gpa);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────

    /**
     * Permanently deletes a student record by ID.
     * @return true if a row was deleted.
     */
    public boolean deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── REPORTS ─────────────────────────────────────────────────────────────

    /**
     * Returns summary stats: total, active, average GPA per department.
     */
    public void printDepartmentSummary() throws SQLException {
        String sql = "SELECT d.dept_name, COUNT(s.student_id) AS total, " +
                     "SUM(s.is_active) AS active_count, " +
                     "ROUND(AVG(s.gpa), 2) AS avg_gpa " +
                     "FROM departments d " +
                     "LEFT JOIN students s ON d.dept_id = s.dept_id " +
                     "GROUP BY d.dept_id ORDER BY d.dept_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n  Department Summary Report");
            System.out.println("  " + "─".repeat(55));
            System.out.printf("  %-25s %8s %8s %8s%n",
                              "Department", "Total", "Active", "Avg GPA");
            System.out.println("  " + "─".repeat(55));

            while (rs.next()) {
                System.out.printf("  %-25s %8d %8d %8.2f%n",
                    rs.getString("dept_name"),
                    rs.getInt("total"),
                    rs.getInt("active_count"),
                    rs.getDouble("avg_gpa"));
            }
            System.out.println("  " + "─".repeat(55));
        }
    }

    // ── DEPARTMENTS ──────────────────────────────────────────────────────────

    /** Returns all departments. */
    public List<Department> getAllDepartments() throws SQLException {
        List<Department> depts = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY dept_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department d = new Department();
                d.setDeptId(rs.getInt("dept_id"));
                d.setDeptName(rs.getString("dept_name"));
                d.setDeptCode(rs.getString("dept_code"));
                depts.add(d);
            }
        }
        return depts;
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));

        Date dob = rs.getDate("dob");
        if (dob != null) s.setDob(dob.toLocalDate());

        s.setGender(rs.getString("gender"));
        s.setDeptId(rs.getInt("dept_id"));
        s.setEnrollmentYear(rs.getInt("enrollment_year"));
        s.setGpa(rs.getDouble("gpa"));
        s.setActive(rs.getBoolean("is_active"));

        // Joined column (may not always be present)
        try { s.setDeptName(rs.getString("dept_name")); }
        catch (SQLException ignored) {}

        return s;
    }

    private List<Student> executeQuery(String sql) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
