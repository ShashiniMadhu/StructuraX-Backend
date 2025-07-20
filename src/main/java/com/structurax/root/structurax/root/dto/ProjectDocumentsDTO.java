package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class ProjectDocumentsDTO {

    @JsonProperty("document_id")
    private Integer documentId;

    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("document_url")
    private String documentUrl;

    private String description;
    @JsonProperty("upload_date")
    private LocalDate uploadDate;


    public ProjectDocumentsDTO() {}

        public ProjectDocumentsDTO(Integer documentId, String projectId, String documentUrl, String description, LocalDate uploadDate) {
        this.documentId = documentId;
        this.projectId = projectId;
        this.documentUrl = documentUrl;
        this.description = description;
        this.uploadDate = uploadDate;
    }


}
