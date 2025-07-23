package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Project1DTO {

    private String projectId;
    private String name;
    private String description;
    private String location;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedValue;
    private String ownerId;
    private String qsId;
    private String spId;
    private String planId;
    private BigDecimal budget;
    private String category;



    // Default constructor
    public Project1DTO() {
    }

    // Full constructor
    public Project1DTO(String projectId, String name, String description, String location,
                      String status, LocalDate startDate, LocalDate dueDate,
                      BigDecimal estimatedValue, String ownerId, String qsId, String spId, String planId) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.estimatedValue = estimatedValue;
        this.ownerId = ownerId;
        this.qsId = qsId;
        this.spId = spId;
        this.planId = planId;
    }

    public Project1DTO(String projectId, String name, String description, String location, String status, String type, Date startDate, Date dueDate, BigDecimal estimatedValue) {
        // This constructor is currently empty
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getQsId() {
        return qsId;
    }

    public void setQsId(String qsId) {
        this.qsId = qsId;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public BigDecimal getBudget() {
    return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}