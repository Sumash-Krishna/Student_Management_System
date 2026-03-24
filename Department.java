package com.studentdbms.model;

/**
 * Model class representing a Department entity.
 */
public class Department {

    private int    deptId;
    private String deptName;
    private String deptCode;

    public Department() {}

    public Department(String deptName, String deptCode) {
        this.deptName = deptName;
        this.deptCode = deptCode;
    }

    public int    getDeptId()             { return deptId; }
    public void   setDeptId(int deptId)   { this.deptId = deptId; }

    public String getDeptName()                { return deptName; }
    public void   setDeptName(String deptName) { this.deptName = deptName; }

    public String getDeptCode()                { return deptCode; }
    public void   setDeptCode(String deptCode) { this.deptCode = deptCode; }

    @Override
    public String toString() {
        return String.format("  [%d] %-30s (%s)", deptId, deptName, deptCode);
    }
}
