package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class VisitRequestDTO {

    @JsonProperty("request_id")
    private Integer id;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("from_date")
    private LocalDate fromDate;

    @JsonProperty("to_date")
    private LocalDate toDate;

    private String purpose;
    private String status;

    public VisitRequestDTO() {}

    public VisitRequestDTO(String projectId, LocalDate fromDate, LocalDate toDate, String purpose , String status) {
        this.projectId = projectId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.purpose = purpose;
        this.status = status;
    }

}
