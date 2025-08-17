package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

public class QuotationDTO {
    private Integer qId;
    private String projectId;
    private LocalDate date;
    private LocalDate deadline;
    private String status; // 'pending', 'sent', 'received', 'closed'

    public QuotationDTO() {
    }

    public QuotationDTO(Integer qId, String projectId, LocalDate date, LocalDate deadline, String status) {
        this.qId = qId;
        this.projectId = projectId;
        this.date = date;
        this.deadline = deadline;
        this.status = status;
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

    @Override
    public String toString() {
        return "QuotationDTO{" +
                "qId=" + qId +
                ", projectId='" + projectId + '\'' +
                ", date=" + date +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}
