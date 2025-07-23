package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LegalDocumentDTO {

    @JsonProperty("legal_document_id")
    private Integer documentId;
    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("document_url")
    private String documentUrl;

    private LocalDate date;
    private String type;
    private String description;


    public LegalDocumentDTO() {}


}
