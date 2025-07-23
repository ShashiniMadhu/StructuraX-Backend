package com.structurax.root.structurax.root.dto;

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
    private BigDecimal laborRate;
    private BigDecimal cost;

}
