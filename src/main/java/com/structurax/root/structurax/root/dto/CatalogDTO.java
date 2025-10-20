package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CatalogDTO {
    private Integer id;

    @JsonProperty("item_id")
    private Integer itemId;

    @JsonProperty("supplier_id")
    private Integer supplierId;

    private String name;
    private String description;
    private Float rate; // Changed back to Float
    private Boolean availability = true;
    private String category;

    // Constructor without itemId for auto-increment scenarios
    public CatalogDTO(String name, String description, Float rate, Boolean availability, String category) {
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.availability = availability;
        this.category = category;
    }

    // Original constructor with itemId
    public CatalogDTO(Integer itemId, String name, String description, Float rate, Boolean availability, String category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.availability = availability;
        this.category = category;
    }
}