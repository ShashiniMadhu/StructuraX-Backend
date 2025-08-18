package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

public class QuotationDTO {
    private Integer qId;
    private String projectId;
    private String projectName;
    private String qsId;
    private LocalDate date;
    private LocalDate deadline;
    private String status; // 'pending', 'sent', 'received', 'closed'
    private String description;

    public QuotationDTO() {
    }

    public QuotationDTO(Integer qId, String projectId, String projectName, String qsId, LocalDate date, LocalDate deadline, String status) {
        this.qId = qId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.qsId = qsId;
        this.date = date;
        this.deadline = deadline;
        this.status = status;
    }

    public QuotationDTO(Integer qId, String projectId, String projectName, String qsId, LocalDate date, LocalDate deadline, String status, String description) {
        this.qId = qId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.qsId = qsId;
        this.date = date;
        this.deadline = deadline;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public Integer getQId() {
        return qId;
    }

    public void setQId(Integer qId) {
        this.qId = qId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getQsId() {
        return qsId;
    }

    public void setQsId(String qsId) {
        this.qsId = qsId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "QuotationDTO{" +
                "qId=" + qId +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", qsId='" + qsId + '\'' +
                ", date=" + date +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
