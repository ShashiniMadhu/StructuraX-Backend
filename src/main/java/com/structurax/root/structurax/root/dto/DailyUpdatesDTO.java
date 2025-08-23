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

public class DailyUpdatesDTO {

    @JsonProperty("update_id")
    private int updateId;
    @JsonProperty("project_id")
    private String projectId;
    private LocalDate date;
    private String note;
    @JsonProperty("employee_id")
    private String employeeId;
}
