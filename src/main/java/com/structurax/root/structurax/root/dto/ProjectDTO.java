package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class ProjectDTO {

    private Integer projectId;
    private String name;
    private String description;
    private String location;
    private String status;
    private String type;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedValue;
    private BigDecimal amountSpent = BigDecimal.ZERO;
    private BigDecimal baseAmount;
    private Integer ownerId;
    private Integer qsId;
    private Integer sqsId;
    private Integer spId;
    private Integer planId;

    // Default constructor
    public ProjectDTO() {
    }

    // Full constructor
    public ProjectDTO(Integer projectId, String name, String description, String location,
                      String status, String type, LocalDate startDate, LocalDate dueDate,
                      BigDecimal estimatedValue, BigDecimal amountSpent, BigDecimal baseAmount,
                      Integer ownerId, Integer qsId, Integer sqsId, Integer spId, Integer planId) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = status;
        this.type = type;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.estimatedValue = estimatedValue;
        this.amountSpent = amountSpent;
        this.baseAmount = baseAmount;
        this.ownerId = ownerId;
        this.qsId = qsId;
        this.sqsId = sqsId;
        this.spId = spId;
        this.planId = planId;
    }

    public ProjectDTO(int projectId, String name, String description, String location, Object o, String status, String type, Date startDate, Date dueDate, BigDecimal estimatedValue, BigDecimal amountSpent, BigDecimal baseAmount) {
    }

    // Getters and Setters
    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(BigDecimal estimatedValue) { this.estimatedValue = estimatedValue; }

    public BigDecimal getAmountSpent() { return amountSpent; }
    public void setAmountSpent(BigDecimal amountSpent) { this.amountSpent = amountSpent; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public Integer getQsId() { return qsId; }
    public void setQsId(Integer qsId) { this.qsId = qsId; }

    public Integer getSqsId() { return sqsId; }
    public void setSqsId(Integer sqsId) { this.sqsId = sqsId; }

    public Integer getSpId() { return spId; }
    public void setSpId(Integer spId) { this.spId = spId; }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }

}
