package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStartDTO {

    @JsonProperty("pm_id")
    private String pmId;

    @JsonProperty("ss_id")
    private String ssId;

    private String status;

}
