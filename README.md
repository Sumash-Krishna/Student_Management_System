# Student Database Management System
### Java + MySQL — Intermediate Level Console App

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

```
  ╔══════════════════════════════════════════════════╗
  ║      Student Database Management System          ║
  ║          Java + MySQL  │  Console App            ║
  ╚══════════════════════════════════════════════════╝

  ┌─────────────────────────────────────┐
  │              MAIN MENU              │
  ├─────────────────────────────────────┤
  │  1. Add New Student                 │
  │  2. View All Students               │
  ...
```
