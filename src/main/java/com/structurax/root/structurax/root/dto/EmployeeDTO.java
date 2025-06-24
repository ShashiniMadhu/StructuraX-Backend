package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDTO {
    private Integer empId;
    private String fullName;
    private String email;
    private String contactNumber;
    private String address;
    private String employeeType;
    private LocalDate joinDate;
    private BigDecimal salary;
    private String password;

    public EmployeeDTO() {
        // Default constructor
    }

    public EmployeeDTO(Integer empId, String fullName, String email, String contactNumber, String address,
                       String employeeType, LocalDate joinDate, BigDecimal salary, String password) {
        this.empId = empId;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
        this.employeeType = employeeType;
        this.joinDate = joinDate;
        this.salary = salary;
        this.password = password;
    }

    // Getters and Setters
    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
