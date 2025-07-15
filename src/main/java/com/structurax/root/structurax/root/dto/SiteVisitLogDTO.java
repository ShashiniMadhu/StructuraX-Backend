package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class SiteVisitLogDTO {
    private Integer id;

    @JsonProperty("project_id")
    private Integer projectId;
    private LocalDate date;
    private String description;
    private String status;

    public SiteVisitLogDTO() {}
    //constructor
    public SiteVisitLogDTO(Integer id, Integer projectId, LocalDate date, String description, String status ) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.description = description;
        this.status = status;
    }

}