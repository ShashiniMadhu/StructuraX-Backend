package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CatalogDTO {
    private Integer id;

    @JsonProperty("item_id")
    private Integer itemId;
    private String name;
    private String description;
    private Float rate;
    private Boolean availability;
    private String category;
//    private Boolean active;
//    private String supplierId;

    public CatalogDTO(Integer itemId, String name, String description, Float rate, Boolean availability, String category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.availability = availability;
        this.category = category;
//        this.active = active;
//        this.supplierId = supplierId;
    }
}