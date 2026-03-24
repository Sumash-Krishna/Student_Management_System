package com.studentdbms;

import com.studentdbms.dao.StudentDAO;
import com.studentdbms.model.Department;
import com.studentdbms.model.Student;
import com.studentdbms.util.DatabaseConnection;
import com.studentdbms.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════╗
 *  Student Database Management System  │  Console UI
 *  Intermediate Java + MySQL Project
 * ╚══════════════════════════════════════════════════════╝
 *
 * Run this class after executing schema.sql on your MySQL server.
 * Update credentials in DatabaseConnection.java before running.
 */
public class Main {

    private static final StudentDAO dao     = new StudentDAO();
    private static final Scanner    scanner = new Scanner(System.in);

    // ── Entry Point ──────────────────────────────────────────────────────────

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");

            try {
                switch (choice) {
                    case 1  -> menuAddStudent();
                    case 2  -> menuViewAllStudents();
                    case 3  -> menuSearchStudent();
                    case 4  -> menuUpdateStudent();
                    case 5  -> menuUpdateGpa();
                    case 6  -> menuDeactivateStudent();
                    case 7  -> menuDeleteStudent();
                    case 8  -> menuViewByDepartment();
                    case 9  -> menuTopStudents();
                    case 10 -> dao.printDepartmentSummary();
                    case 0  -> running = false;
                    default -> System.out.println("  [!] Invalid option. Try again.");
                }
            } catch (SQLException e) {
                System.err.println("  [DB Error] " + e.getMessage());
            }
        }

        System.out.println("\n  Goodbye! Closing connection...");
        DatabaseConnection.closeConnection();
        scanner.close();
    }

    // ── Menu: Add Student ────────────────────────────────────────────────────

    private static void menuAddStudent() throws SQLException {
        printSection("Add New Student");

        String firstName = readValidatedString("  First Name   : ", "First name");
        String lastName  = readValidatedString("  Last Name    : ", "Last name");

        String email;
        while (true) {
            email = readString("  Email        : ");
            if (Validator.isValidEmail(email)) break;
            System.out.println("  [!] Invalid email format. Try again.");
        }

        String phone;
        while (true) {
            phone = readString("  Phone        : ");
            if (Validator.isValidPhone(phone)) break;
            System.out.println("  [!] Invalid phone format. Try again.");
        }

        LocalDate dob = null;
        while (dob == null) {
            String dobStr = readString("  DOB (yyyy-MM-dd, or press Enter to skip): ");
            if (dobStr.isBlank()) break;
            dob = Validator.parseDate(dobStr);
            if (dob == null) System.out.println("  [!] Invalid date. Use yyyy-MM-dd.");
        }

        String gender = readGender();

        List<Department> depts = dao.getAllDepartments();
        printDepartments(depts);
        int deptId = readInt("  Department ID : ");

        int year;
        while (true) {
            year = readInt("  Enrollment Year: ");
            if (Validator.isValidYear(year)) break;
            System.out.println("  [!] Enter a valid year (1990–" + (LocalDate.now().getYear() + 1) + ").");
        }

        Student s = new Student(
            Validator.formatName(firstName),
            Validator.formatName(lastName),
            email.toLowerCase(), phone, dob, gender, deptId, year
        );

        int id = dao.addStudent(s);
        if (id > 0) {
            System.out.println("\n  ✓ Student added successfully! Assigned ID: " + id);
        } else {
            System.out.println("  [!] Failed to add student.");
        }
    }

    // ── Menu: View All ──────────────────────────────────────────────────────

    private static void menuViewAllStudents() throws SQLException {
        printSection("All Students");
        List<Student> students = dao.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("  No students found.");
            return;
        }

        printStudentTable(students);
        System.out.println("  Total: " + students.size() + " student(s).");
    }

    // ── Menu: Search ────────────────────────────────────────────────────────

    private static void menuSearchStudent() throws SQLException {
        printSection("Search Student");
        String keyword = readString("  Enter name / email / phone keyword: ");

        List<Student> results = dao.searchStudents(keyword.trim());
        if (results.isEmpty()) {
            System.out.println("  No matching students found.");
            return;
        }

        printStudentTable(results);
        System.out.println("  Found: " + results.size() + " student(s).");

        System.out.print("  View full details? Enter Student ID (0 to skip): ");
        int id = readInt("");
        if (id > 0) {
            Student s = dao.getStudentById(id);
            if (s != null) System.out.println("\n" + s);
            else System.out.println("  [!] Student not found.");
        }
    }

    // ── Menu: Update Student ────────────────────────────────────────────────

    private static void menuUpdateStudent() throws SQLException {
        printSection("Update Student");
        int id = readInt("  Enter Student ID to update: ");

        Student s = dao.getStudentById(id);
        if (s == null) { System.out.println("  [!] Student not found."); return; }

        System.out.println("\n" + s);
        System.out.println("\n  Leave field blank to keep existing value.\n");

        String firstName = readString("  First Name [" + s.getFirstName() + "]: ");
        if (!firstName.isBlank()) s.setFirstName(Validator.formatName(firstName));

        String lastName = readString("  Last Name  [" + s.getLastName() + "]: ");
        if (!lastName.isBlank()) s.setLastName(Validator.formatName(lastName));

        String email = readString("  Email      [" + s.getEmail() + "]: ");
        if (!email.isBlank()) {
            if (Validator.isValidEmail(email)) s.setEmail(email.toLowerCase());
            else System.out.println("  [!] Invalid email. Keeping existing.");
        }

        String phone = readString("  Phone      [" + s.getPhone() + "]: ");
        if (!phone.isBlank()) {
            if (Validator.isValidPhone(phone)) s.setPhone(phone);
            else System.out.println("  [!] Invalid phone. Keeping existing.");
        }

        String dobStr = readString("  DOB        [" + s.getDob() + "]: ");
        if (!dobStr.isBlank()) {
            LocalDate newDob = Validator.parseDate(dobStr);
            if (newDob != null) s.setDob(newDob);
            else System.out.println("  [!] Invalid date. Keeping existing.");
        }

        List<Department> depts = dao.getAllDepartments();
        printDepartments(depts);
        String deptStr = readString("  Dept ID    [" + s.getDeptId() + "]: ");
        if (!deptStr.isBlank()) {
            try { s.setDeptId(Integer.parseInt(deptStr.trim())); }
            catch (NumberFormatException e) { System.out.println("  [!] Invalid ID. Keeping existing."); }
        }

        boolean updated = dao.updateStudent(s);
        System.out.println(updated ? "  ✓ Student updated." : "  [!] Update failed.");
    }

    // ── Menu: Update GPA ────────────────────────────────────────────────────

    private static void menuUpdateGpa() throws SQLException {
        printSection("Update GPA");
        int id = readInt("  Enter Student ID: ");
        Student s = dao.getStudentById(id);
        if (s == null) { System.out.println("  [!] Student not found."); return; }

        System.out.println("  Current GPA: " + s.getGpa());
        String input = readString("  New GPA (0.00–10.00): ");
        try {
            double gpa = Double.parseDouble(input.trim());
            if (!Validator.isValidGpa(gpa)) { System.out.println("  [!] GPA out of range."); return; }
            boolean ok = dao.updateGpa(id, gpa);
            System.out.println(ok ? "  ✓ GPA updated to " + gpa : "  [!] Update failed.");
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid number.");
        }
    }

    // ── Menu: Deactivate ────────────────────────────────────────────────────

    private static void menuDeactivateStudent() throws SQLException {
        printSection("Activate / Deactivate Student");
        int id = readInt("  Enter Student ID: ");
        Student s = dao.getStudentById(id);
        if (s == null) { System.out.println("  [!] Student not found."); return; }

        String status = s.isActive() ? "Active" : "Inactive";
        System.out.println("  Current status: " + status);
        System.out.print("  Toggle to " + (s.isActive() ? "Inactive" : "Active") + "? (y/n): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            boolean ok = dao.setActiveStatus(id, !s.isActive());
            System.out.println(ok ? "  ✓ Status updated." : "  [!] Update failed.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // ── Menu: Delete ─────────────────────────────────────────────────────────

    private static void menuDeleteStudent() throws SQLException {
        printSection("Delete Student (Permanent)");
        int id = readInt("  Enter Student ID to delete: ");
        Student s = dao.getStudentById(id);
        if (s == null) { System.out.println("  [!] Student not found."); return; }

        System.out.println("  Student: " + s.getFullName() + " | " + s.getEmail());
        System.out.print("  ⚠  This is permanent. Confirm? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            boolean ok = dao.deleteStudent(id);
            System.out.println(ok ? "  ✓ Student deleted." : "  [!] Delete failed.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // ── Menu: View By Department ─────────────────────────────────────────────

    private static void menuViewByDepartment() throws SQLException {
        printSection("Students by Department");
        List<Department> depts = dao.getAllDepartments();
        printDepartments(depts);
        int deptId = readInt("  Enter Department ID: ");
        List<Student> students = dao.getStudentsByDept(deptId);
        if (students.isEmpty()) {
            System.out.println("  No students in this department.");
        } else {
            printStudentTable(students);
            System.out.println("  Total: " + students.size() + " student(s).");
        }
    }

    // ── Menu: Top Students ───────────────────────────────────────────────────

    private static void menuTopStudents() throws SQLException {
        printSection("Top Students by GPA");
        String input = readString("  Minimum GPA threshold (e.g. 8.0): ");
        try {
            double gpa = Double.parseDouble(input.trim());
            List<Student> students = dao.getTopStudents(gpa);
            if (students.isEmpty()) {
                System.out.println("  No students found above GPA " + gpa);
            } else {
                printStudentTable(students);
            }
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid number.");
        }
    }

    // ── Helpers: Print ───────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("\n" +
            "  ╔══════════════════════════════════════════════════╗\n" +
            "  ║      Student Database Management System          ║\n" +
            "  ║          Java + MySQL  │  Console App            ║\n" +
            "  ╚══════════════════════════════════════════════════╝");
    }

    private static void printMainMenu() {
        System.out.println("\n" +
            "  ┌─────────────────────────────────────┐\n" +
            "  │              MAIN MENU              │\n" +
            "  ├─────────────────────────────────────┤\n" +
            "  │  1. Add New Student                 │\n" +
            "  │  2. View All Students               │\n" +
            "  │  3. Search Student                  │\n" +
            "  │  4. Update Student Info             │\n" +
            "  │  5. Update GPA                      │\n" +
            "  │  6. Activate / Deactivate Student   │\n" +
            "  │  7. Delete Student                  │\n" +
            "  │  8. View Students by Department     │\n" +
            "  │  9. Top Students by GPA             │\n" +
            "  │ 10. Department Summary Report       │\n" +
            "  │  0. Exit                            │\n" +
            "  └─────────────────────────────────────┘");
    }

    private static void printSection(String title) {
        System.out.println("\n  ── " + title + " " + "─".repeat(Math.max(0, 40 - title.length())));
    }

    private static void printStudentTable(List<Student> students) {
        String fmt = "  %-5s %-20s %-28s %-20s %6s %8s%n";
        System.out.println();
        System.out.printf(fmt, "ID", "Name", "Email", "Department", "Year", "GPA");
        System.out.println("  " + "─".repeat(93));
        for (Student s : students) {
            System.out.printf(fmt,
                s.getStudentId(),
                truncate(s.getFullName(), 20),
                truncate(s.getEmail(), 28),
                truncate(s.getDeptName() != null ? s.getDeptName() : "-", 20),
                s.getEnrollmentYear(),
                String.format("%.2f", s.getGpa())
            );
        }
        System.out.println("  " + "─".repeat(93));
    }

    private static void printDepartments(List<Department> depts) {
        System.out.println("\n  Available Departments:");
        depts.forEach(System.out::println);
        System.out.println();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ── Helpers: Input ───────────────────────────────────────────────────────

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String readValidatedString(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String val = scanner.nextLine().trim();
            if (Validator.isNotBlank(val)) return val;
            System.out.println("  [!] " + fieldName + " cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) {
                System.out.print("  [!] Enter a valid number: ");
            }
        }
    }

    private static String readGender() {
        System.out.println("  Gender Options: 1=Male  2=Female  3=Other");
        while (true) {
            int g = readInt("  Select gender: ");
            switch (g) {
                case 1: return "Male";
                case 2: return "Female";
                case 3: return "Other";
                default: System.out.println("  [!] Enter 1, 2, or 3.");
            }
        }
    }
}
