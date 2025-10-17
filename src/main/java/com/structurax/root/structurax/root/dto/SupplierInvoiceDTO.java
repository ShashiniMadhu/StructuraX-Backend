package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInvoiceDTO {

    private Integer invoiceId;
    private Integer orderId;
    private Integer supplierId;
    private BigDecimal amount;
    private String description;
    private String filePath;
    private LocalDate date;

}
