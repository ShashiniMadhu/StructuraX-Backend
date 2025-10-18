package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptDTO {
    private Integer receiptId;
    private Integer installmentId;
    private String projectId;
    private String phase;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String receiptFilePath;
    private String description;
    private String status; // Pending, Verified, Rejected
    private LocalDate uploadedDate;
}