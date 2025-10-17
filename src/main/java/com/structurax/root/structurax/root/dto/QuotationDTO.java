package com.structurax.root.structurax.root.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class QuotationDTO {
    private Integer qId;
    private String projectId;
    private String projectName;
    private String qsId;
    private Date date;
    private Date deadline;
    private String status;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public QuotationDTO() {
    }

    // Constructor with all fields
    public QuotationDTO(Integer qId, String projectId, String projectName, String qsId,
                       Date date, Date deadline, String status, String description) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
