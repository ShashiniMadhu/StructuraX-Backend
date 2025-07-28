package com.structurax.root.structurax.root.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborSalaryDTO {


    private int salaryId;
    private int attendanceId;
    private String projectId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Labor rate must be greater than zero")
    private BigDecimal laborRate;
    private BigDecimal cost;

}
