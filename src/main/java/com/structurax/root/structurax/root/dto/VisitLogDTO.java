package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;
public class VisitLogDTO {
    private Integer id;

    @JsonProperty("project_id")
    private Integer projectId;
    private LocalDate date;
    private String description;
    private String status;

    public VisitLogDTO() {}

    public VisitLogDTO(Integer id, Integer projectId, LocalDate date,  String description, String status ) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.description = description;
        this.status = status;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProjectId() {
        return projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}