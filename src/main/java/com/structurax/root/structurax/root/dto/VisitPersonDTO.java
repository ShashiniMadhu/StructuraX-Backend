package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitPersonDTO {
    
    @JsonProperty("visit_id")
    @NotNull(message = "Visit ID cannot be null")
    private Integer visitId;

    @JsonProperty("employee_id")
    @NotBlank(message = "Employee ID cannot be blank")
    private String employeeId;
}
