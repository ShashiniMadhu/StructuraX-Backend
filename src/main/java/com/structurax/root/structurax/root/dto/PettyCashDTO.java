package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PettyCashDTO {

    private int pettyCashId;
    private String projectId;
    private LocalDate date;
    private BigDecimal amount;
    private String employeeId;



}
