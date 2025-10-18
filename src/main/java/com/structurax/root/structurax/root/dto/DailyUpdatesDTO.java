package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class DailyUpdatesDTO {
    private int updateId;
    private String projectId;
    private LocalDate date;
    private String note;
    @JsonProperty("employee_id")
    private String employeeId;

    // Default constructor
    public DailyUpdatesDTO() {
    }

    // Parameterized constructor
    public DailyUpdatesDTO(int updateId, String projectId, LocalDate date, String note, String employeeId) {
        this.updateId = updateId;
        this.projectId = projectId;
        this.date = date;
        this.note = note;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public int getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "DailyUpdatesDTO{" +
                "updateId=" + updateId +
                ", projectId='" + projectId + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }
}
