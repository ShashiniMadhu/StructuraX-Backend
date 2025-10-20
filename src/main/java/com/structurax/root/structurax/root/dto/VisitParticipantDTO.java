package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitParticipantDTO {
    
    @JsonProperty("employee_id")
    private String employeeId;
    
    @JsonProperty("employee_name")
    private String employeeName;
    
    @JsonProperty("employee_type")
    private String employeeType;
}
