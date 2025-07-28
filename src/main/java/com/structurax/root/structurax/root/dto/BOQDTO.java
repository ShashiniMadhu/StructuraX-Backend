package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

public class BOQDTO {
    public enum Status {
        DRAFT, APPROVED, FINAL
    }

    private String boqId;
    private String projectId;
    private LocalDate date;
    private String qsId;
    private Status status;

    public BOQDTO() {}

    public BOQDTO(String boqId, String projectId, LocalDate date, String qsId, Status status) {
        this.boqId = boqId;
        this.projectId = projectId;
        this.date = date;
        this.qsId = qsId;
        this.status = status;
    }

    public String getBoqId() {
        return boqId;
    }

    public void setBoqId(String boqId) {
        this.boqId = boqId;
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

    public String getQsId() {
        return qsId;
    }

    public void setQsId(String qsId) {
        this.qsId = qsId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
