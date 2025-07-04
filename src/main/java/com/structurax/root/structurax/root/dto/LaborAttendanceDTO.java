package com.structurax.root.structurax.root.dto;

import java.sql.Date;
import java.time.LocalDate;

public class LaborAttendanceDTO {

    private Integer id;
    private Integer project_id;
    private Date date;
    private String hiring_type;
    private String labor_type;
    private Integer count;
    private String company;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter and Setter for project_id
    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    // Getter and Setter for date
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Getter and Setter for hiring_type
    public String getHiring_type() {
        return hiring_type;
    }

    public void setHiring_type(String hiring_type) {
        this.hiring_type = hiring_type;
    }

    // Getter and Setter for labor_type
    public String getLabor_type() {
        return labor_type;
    }

    public void setLabor_type(String labor_type) {
        this.labor_type = labor_type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // Getter and Setter for company
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
