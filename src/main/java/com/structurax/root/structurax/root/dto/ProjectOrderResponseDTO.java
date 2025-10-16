package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectOrderResponseDTO {
    private String projectId;
    private String projectName;
    private Integer orderId;
    private LocalDate orderDate;
    private String status;
    private BigDecimal amount;
    private List<OrderItemDTO> orderItems;
}
