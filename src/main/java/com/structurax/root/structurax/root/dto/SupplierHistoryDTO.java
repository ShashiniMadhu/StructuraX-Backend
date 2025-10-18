package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierHistoryDTO {
    private int historyId;

    private int supplierId;

    private int orderId;

    private Date supplyDate;

    private BigDecimal amount;


}
