package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SiteResourcesDTO {
    private Integer id;
    @JsonProperty("request_id")
    private Integer requestId;
    private String name;
    private Integer quantity;
    private String priority;
}
