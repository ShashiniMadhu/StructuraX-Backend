package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private String projectId;
    private String projectName;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String transactionType; // "Labor Payment", "Petty Cash", "Purchase", "Client Payment"
}