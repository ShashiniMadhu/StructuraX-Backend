package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDTO {
    private Integer empId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String type;
    private LocalDate joinedDate;
    private String password;
    private Boolean availability;

    public EmployeeDTO() {
        // Default constructor
    }

    public EmployeeDTO(Integer empId, String name, String email, String phoneNumber, String address, String type, LocalDate joinedDate, String password, Boolean availability) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.type = type;
        this.joinedDate = joinedDate;
        this.password = password;
        this.availability = availability;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
}
