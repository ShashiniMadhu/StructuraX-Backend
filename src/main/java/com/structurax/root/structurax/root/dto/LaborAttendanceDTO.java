package com.structurax.root.structurax.root.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborAttendanceDTO {

    private Integer id;
    private String project_id;
    private Date date;
    private String hiring_type;
    private String labor_type;

    @DecimalMin(value = "0", inclusive = false, message = "Count must be greater than zero")
    private Integer count;
    private String company;

    private LaborSalaryDTO salary;



}
