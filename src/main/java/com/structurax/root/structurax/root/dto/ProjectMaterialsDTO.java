package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMaterialsDTO {

    @JsonProperty("material_id")
    private int materialId;
    @JsonProperty("project_id")
    private String projectId;

    private String tools;
}
