package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInitiateDTO {
    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("name")
    private String name;

    private String status;

    private BigDecimal budget;

    private String description;

    private String location;

    @JsonProperty("estimated_value")
    private BigDecimal estimatedValue;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("qs_id")
    private String qsId;

    @JsonProperty("pm_id")
    private String pmId;

    private String category;

}
