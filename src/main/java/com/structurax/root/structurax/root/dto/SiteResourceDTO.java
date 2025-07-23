package com.structurax.root.structurax.root.dto;


import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteResourceDTO {

    private Integer id;
    private Integer requestId;
    private String materialName;

    @DecimalMin(value = "0", inclusive = false, message = "Total amount must be greater than zero")
    private Integer quantity;
    private String priority;


}
