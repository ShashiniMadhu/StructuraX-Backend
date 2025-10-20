package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PettyCashRecordDTO {

    private int recordId;
    private int pettyCashId;
    private BigDecimal expenseAmount;
    private LocalDate date;
    private String description;

}
