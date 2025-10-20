package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyUpdateDTO {
    
    @JsonProperty("update_id")
    private Integer updateId;
    
    @JsonProperty("project_id")
    private String projectId;
    
    private LocalDate date;
    
    private String note;
    
    @JsonProperty("employee_id")
    private String employeeId;
    
    @JsonProperty("employee_name")
    private String employeeName;
}
