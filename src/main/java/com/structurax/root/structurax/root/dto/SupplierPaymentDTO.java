package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentDTO {

    private Integer supplierPaymentId;
    private String projectId;
    private String projectName;
    private Integer invoiceId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private LocalDate payedDate;
    private BigDecimal amount;
    private String status;

}
