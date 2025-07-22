package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectWithClientAndBOQDTO {
    // Project fields
    @JsonProperty("project_id")
    private String projectId;
    private String name;
    private String description;
    private String location;
    private String status;
    
    @JsonProperty("start_date")
    private LocalDate startDate;
    
    @JsonProperty("due_date")
    private LocalDate dueDate;
    
    @JsonProperty("estimated_value")
    private BigDecimal estimatedValue;
    
    @JsonProperty("qs_id")
    private String qsId;
    
    private BigDecimal budget;
    private String category;

    // Client fields
    @JsonProperty("client_data")
    private ClientDTO clientData;

    // BOQ fields
    @JsonProperty("boq_data")
    private BOQWithItemsDTO boqData;

    // Default constructor
    public ProjectWithClientAndBOQDTO() {}

    // Constructor
    public ProjectWithClientAndBOQDTO(String projectId, String name, String description, String location,
                                     String status, LocalDate startDate, LocalDate dueDate,
                                     BigDecimal estimatedValue, String qsId, BigDecimal budget, 
                                     String category, ClientDTO clientData, BOQWithItemsDTO boqData) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.estimatedValue = estimatedValue;
        this.qsId = qsId;
        this.budget = budget;
        this.category = category;
        this.clientData = clientData;
        this.boqData = boqData;
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

    public String getQsId() {
        return qsId;
    }

    public void setQsId(String qsId) {
        this.qsId = qsId;
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

    public ClientDTO getClientData() {
        return clientData;
    }

    public void setClientData(ClientDTO clientData) {
        this.clientData = clientData;
    }

    public BOQWithItemsDTO getBoqData() {
        return boqData;
    }

    public void setBoqData(BOQWithItemsDTO boqData) {
        this.boqData = boqData;
    }
}
