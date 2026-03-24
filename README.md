# Student Database Management System
### Java + MySQL — Console Application

---

## Features

| Feature | Details |
|---|---|
| **Add Student** | Name, Email, Phone, DOB, Gender, Department, Year |
| **View All Students** | Tabular format with department name |
| **Search** | By name, email, or phone (partial match) |
| **Update Student** | Update any field; keep existing on blank |
| **Update GPA** | Individual GPA update with validation |
| **Activate/Deactivate** | Soft delete — toggle active status |
| **Delete** | Permanent delete with confirmation |
| **View by Department** | Filter students by department |
| **Top Students** | Filter by minimum GPA threshold |
| **Department Report** | Summary: count, active, avg GPA per dept |

---

## Project Structure

```
StudentDBMS/
├── schema.sql                              ← Run this in MySQL first
└── src/
    └── com/studentdbms/
        ├── Main.java                       ← Entry point, console menu
        ├── model/
        │   ├── Student.java               ← Student entity (POJO)
        │   └── Department.java            ← Department entity (POJO)
        ├── dao/
        │   └── StudentDAO.java            ← All CRUD + queries (PreparedStatements)
        └── util/
            ├── DatabaseConnection.java    ← Singleton JDBC connection
            └── Validator.java             ← Input validation helpers
```

---

## Setup Instructions

### 1. Prerequisites
- Java 11 or later
- MySQL 8.x
- MySQL Connector/J JAR (`mysql-connector-j-8.x.x.jar`)

### 2. Database Setup
```sql
-- In MySQL shell or Workbench:
source /path/to/schema.sql;
```

### 3. Configure DB Credentials
Edit `src/com/studentdbms/util/DatabaseConnection.java`:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/student_dbms?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";         // your MySQL username
private static final String PASSWORD = "your_password"; // your MySQL password
```

### 4. Compile & Run
```bash
# Compile (with MySQL connector JAR on classpath)
javac -cp ".:mysql-connector-j-8.x.x.jar" -d out \
  src/com/studentdbms/util/*.java \
  src/com/studentdbms/model/*.java \
  src/com/studentdbms/dao/*.java \
  src/com/studentdbms/Main.java

# Run
java -cp ".:out:mysql-connector-j-8.x.x.jar" com.studentdbms.Main

# Windows: replace : with ;
java -cp ".;out;mysql-connector-j-8.x.x.jar" com.studentdbms.Main
```

### 5. (Optional) Using an IDE
- IntelliJ IDEA / Eclipse: Add the MySQL JAR as a project dependency
- Right-click `Main.java` → Run

---

## Key Java Concepts Used

- **OOP** — Model classes, DAO pattern, utility classes
- **JDBC** — `PreparedStatement`, `ResultSet`, `DriverManager`
- **Singleton Pattern** — Single shared DB connection
- **Collections** — `List<Student>` returned from DAO
- **Exception Handling** — `try-with-resources`, `SQLException`
- **Input Validation** — Regex email/phone, date parsing
- **Enums / Switch** — Menu routing in `Main`

---

## Sample Console Output

### Startup & Main Menu
```
  ╔══════════════════════════════════════════════════╗
  ║      Student Database Management System          ║
  ║          Java + MySQL  │  Console App            ║
  ╚══════════════════════════════════════════════════╝
  [DB] Connected to MySQL successfully.

  ┌─────────────────────────────────────┐
  │              MAIN MENU              │
  ├─────────────────────────────────────┤
  │  1. Add New Student                 │
  │  2. View All Students               │
  │  3. Search Student                  │
  │  4. Update Student Info             │
  │  5. Update GPA                      │
  │  6. Activate / Deactivate Student   │
  │  7. Delete Student                  │
  │  8. View Students by Department     │
  │  9. Top Students by GPA             │
  │ 10. Department Summary Report       │
  │  0. Exit                            │
  └─────────────────────────────────────┘
  Enter choice: 1
```

---

### Option 1 — Add New Student
```
  ── Add New Student ──────────────────────────────

  First Name   : Alice
  Last Name    : Johnson
  Email        : alice.johnson@email.com
  Phone        : +91-9876543210
  DOB (yyyy-MM-dd, or press Enter to skip): 2002-04-15
  Gender Options: 1=Male  2=Female  3=Other
  Select gender: 2

  Available Departments:
  [1] Computer Science               (CS)
  [2] Mathematics                    (MATH)
  [3] Physics                        (PHY)
  [4] Electronics                    (ECE)
  [5] Mechanical Engineering         (MECH)

  Department ID : 1
  Enrollment Year: 2021

  ✓ Student added successfully! Assigned ID: 5
```

---

### Option 2 — View All Students
```
  ── All Students ─────────────────────────────────

  ID    Name                 Email                        Department            Year      GPA
  ─────────────────────────────────────────────────────────────────────────────────────────────────
  1     Rahul Sharma         rahul.sharma@email.com       Computer Science      2020     8.50
  2     Priya Nair           priya.nair@email.com         Mathematics           2021     9.10
  3     Arjun Mehta          arjun.mehta@email.com        Electronics           2020     7.80
  4     Sneha Iyer           sneha.iyer@email.com         Mechanical Engineer…  2022     8.20
  5     Alice Johnson        alice.johnson@email.com      Computer Science      2021     0.00
  ─────────────────────────────────────────────────────────────────────────────────────────────────

  Total: 5 student(s).
```

---

### Option 3 — Search Student
```
  ── Search Student ───────────────────────────────

  Enter name / email / phone keyword: priya

  ID    Name                 Email                        Department            Year      GPA
  ─────────────────────────────────────────────────────────────────────────────────────────────────
  2     Priya Nair           priya.nair@email.com         Mathematics           2021     9.10
  ─────────────────────────────────────────────────────────────────────────────────────────────────

  Found: 1 student(s).
  View full details? Enter Student ID (0 to skip): 2

  ┌─────────────────────────────────────────┐
  │  Student ID    : 2                       │
  │  Name          : Priya Nair              │
  │  Email         : priya.nair@email.com    │
  │  Phone         : +91-9845001122          │
  │  DOB           : 2001-08-23              │
  │  Gender        : Female                  │
  │  Department    : Mathematics             │
  │  Enroll Year   : 2021                    │
  │  GPA           : 9.10                    │
  │  Status        : Active                  │
  └─────────────────────────────────────────┘
```

---

### Option 5 — Update GPA
```
  ── Update GPA ───────────────────────────────────

  Enter Student ID: 5
  Current GPA: 0.0
  New GPA (0.00–10.00): 8.75

  ✓ GPA updated to 8.75
```

---

### Option 6 — Activate / Deactivate Student
```
  ── Activate / Deactivate Student ───────────────

  Enter Student ID: 3
  Current status: Active
  Toggle to Inactive? (y/n): y

  ✓ Status updated.
```

---

### Option 7 — Delete Student
```
  ── Delete Student (Permanent) ──────────────────

  Enter Student ID to delete: 5
  Student: Alice Johnson | alice.johnson@email.com
  ⚠  This is permanent. Confirm? (yes/no): yes

  ✓ Student deleted.
```

---

### Option 9 — Top Students by GPA
```
  ── Top Students by GPA ──────────────────────────

  Minimum GPA threshold (e.g. 8.0): 8.5

  ID    Name                 Email                        Department            Year      GPA
  ─────────────────────────────────────────────────────────────────────────────────────────────────
  2     Priya Nair           priya.nair@email.com         Mathematics           2021     9.10
  1     Rahul Sharma         rahul.sharma@email.com       Computer Science      2020     8.50
  ─────────────────────────────────────────────────────────────────────────────────────────────────
```

---

### Option 10 — Department Summary Report
```
  Department Summary Report
  ───────────────────────────────────────────────────────
  Department                 Total   Active  Avg GPA
  ───────────────────────────────────────────────────────
  Computer Science               2        2     8.50
  Electronics                    1        0     7.80
  Mathematics                    1        1     9.10
  Mechanical Engineering         1        1     8.20
  Physics                        0        0     0.00
  ───────────────────────────────────────────────────────
```

---

### Option 0 — Exit
```
  Enter choice: 0

  Goodbye! Closing connection...
  [DB] Connection closed.
```
