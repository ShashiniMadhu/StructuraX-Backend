package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignDTO {

    @JsonProperty("design_id")
    private String designId;

    @JsonProperty("project_id")
    private String projectId;

    private String name;

    private String type; // enum: 'architectural', 'structural', 'electrical', etc.

    @JsonProperty("due_date")
    private Date dueDate;

    private String priority; // enum: 'high', 'medium', 'low'

    private BigDecimal price;

    @JsonProperty("design_link")
    private String designLink;

    private String description;

    @JsonProperty("additional_note")
    private String additionalNote;

    private String status; // enum: 'completed', 'ongoing'

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("employee_id")
    private String employeeId;
}
