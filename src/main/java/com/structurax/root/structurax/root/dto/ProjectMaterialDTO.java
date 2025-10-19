package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMaterialDTO {

    private Integer materialId;
    private String projectId;
    private String name;
    private String brand;
    private String quantity;
    private String status; // Delivered, In Transit, Ordered, Pending
    private LocalDate deliveryDate;
    private BigDecimal cost;
    private Integer orderId;
    private String description;
    private String unit;
}