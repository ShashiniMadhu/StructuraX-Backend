package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectImageDTO {

    @JsonProperty("image_id")
    private Integer imageId;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("image_url")
    private String imageUrl;

    private String description;

    @JsonProperty("upload_date")
    private LocalDate uploadDate;

    public ProjectImageDTO() {}

    public ProjectImageDTO(Integer imageId, String projectId, String imageUrl, String description, LocalDate uploadDate) {
        this.imageId = imageId;
        this.projectId = projectId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.uploadDate = uploadDate;
    }
}
