package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDTO {
    private String projectId;
    private BigDecimal totalBudget;
    private BigDecimal amountPaid;
    private BigDecimal pendingAmount;
    private Integer totalPayments;
    private Integer completedPayments;
    private Integer pendingPayments;
}