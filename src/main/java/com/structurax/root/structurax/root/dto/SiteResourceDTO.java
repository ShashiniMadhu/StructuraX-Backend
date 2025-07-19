package com.structurax.root.structurax.root.dto;


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
    private Integer quantity;
    private String priority;


}
