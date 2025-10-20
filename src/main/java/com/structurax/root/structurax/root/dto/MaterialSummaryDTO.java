package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSummaryDTO {

    private String projectId;
    private Integer totalMaterials;
    private Integer deliveredCount;
    private Integer inTransitCount;
    private Integer pendingCount;
    private BigDecimal totalCost;
    private BigDecimal rawMaterialsCost;
    private BigDecimal finishingMaterialsCost;
}