package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWithClientDTO {
    
    @JsonProperty("project_id")
    private String projectId;
    
    private String name;
    
    private String description;
    
    private String location;
    
    private String status;
    
    private String category;
    
    @JsonProperty("start_date")
    private LocalDate startDate;
    
    @JsonProperty("due_date")
    private LocalDate dueDate;
    
    @JsonProperty("estimated_value")
    private BigDecimal estimatedValue;
    
    private BigDecimal budget;
    
    @JsonProperty("qs_id")
    private String qsId;
    
    @JsonProperty("pm_id")
    private String pmId;
    
    @JsonProperty("ss_id")
    private String ssId;
    
    @JsonProperty("qs_name")
    private String qsName;
    
    @JsonProperty("pm_name")
    private String pmName;
    
    @JsonProperty("ss_name")
    private String ssName;
    
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("client_name")
    private String clientName;
    
    @JsonProperty("client_email")
    private String clientEmail;
    
    @JsonProperty("client_phone")
    private String clientPhone;
    
    @JsonProperty("client_type")
    private String clientType;
    
    @JsonProperty("client_address")
    private String clientAddress;
    
    @JsonProperty("project_images")
    private List<String> projectImages;
}
