package com.studentdbms.model;

import java.time.LocalDate;

/**
 * Model class representing a Student entity.
 */
public class Student {

    private int     studentId;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  phone;
    private LocalDate dob;
    private String  gender;
    private int     deptId;
    private String  deptName;   // Joined field for display
    private int     enrollmentYear;
    private double  gpa;
    private boolean isActive;

    // ── Constructors ────────────────────────────────────────────────────────

    public Student() {}

    /** Constructor for inserting a new student (no ID yet). */
    public Student(String firstName, String lastName, String email,
                   String phone, LocalDate dob, String gender,
                   int deptId, int enrollmentYear) {
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.email          = email;
        this.phone          = phone;
        this.dob            = dob;
        this.gender         = gender;
        this.deptId         = deptId;
        this.enrollmentYear = enrollmentYear;
        this.isActive       = true;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int       getStudentId()      { return studentId; }
    public void      setStudentId(int id){ this.studentId = id; }

    public String    getFirstName()            { return firstName; }
    public void      setFirstName(String name) { this.firstName = name; }

    public String    getLastName()             { return lastName; }
    public void      setLastName(String name)  { this.lastName = name; }

    public String    getFullName()  { return firstName + " " + lastName; }

    public String    getEmail()              { return email; }
    public void      setEmail(String email)  { this.email = email; }

    public String    getPhone()              { return phone; }
    public void      setPhone(String phone)  { this.phone = phone; }

    public LocalDate getDob()              { return dob; }
    public void      setDob(LocalDate dob) { this.dob = dob; }

    public String    getGender()               { return gender; }
    public void      setGender(String gender)  { this.gender = gender; }

    public int       getDeptId()             { return deptId; }
    public void      setDeptId(int deptId)   { this.deptId = deptId; }

    public String    getDeptName()                { return deptName; }
    public void      setDeptName(String deptName) { this.deptName = deptName; }

    public int       getEnrollmentYear()               { return enrollmentYear; }
    public void      setEnrollmentYear(int year)       { this.enrollmentYear = year; }

    public double    getGpa()              { return gpa; }
    public void      setGpa(double gpa)    { this.gpa = gpa; }

    public boolean   isActive()                 { return isActive; }
    public void      setActive(boolean active)  { this.isActive = active; }

    // ── Display ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "┌─────────────────────────────────────────┐\n" +
            "│  Student ID    : %-24d│\n" +
            "│  Name          : %-24s│\n" +
            "│  Email         : %-24s│\n" +
            "│  Phone         : %-24s│\n" +
            "│  DOB           : %-24s│\n" +
            "│  Gender        : %-24s│\n" +
            "│  Department    : %-24s│\n" +
            "│  Enroll Year   : %-24d│\n" +
            "│  GPA           : %-24.2f│\n" +
            "│  Status        : %-24s│\n" +
            "└─────────────────────────────────────────┘",
            studentId, getFullName(), email, phone,
            (dob != null ? dob.toString() : "N/A"),
            gender,
            (deptName != null ? deptName : "Dept ID " + deptId),
            enrollmentYear, gpa,
            (isActive ? "Active" : "Inactive")
        );
    }
}
