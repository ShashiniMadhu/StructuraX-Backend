package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegalProcessDTO {

    private int id;
    @JsonProperty("project_id")
    private String projectId;
    private String description;
    private String status;
    @JsonProperty("approval_date")
    private LocalDate date;
}
