package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllProjectDocumentDTO {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("document_url")
    private String documentUrl;

    private String type;

    private String description;



}
