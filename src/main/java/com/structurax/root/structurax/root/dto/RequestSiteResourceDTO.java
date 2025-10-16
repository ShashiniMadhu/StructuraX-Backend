package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RequestSiteResourceDTO {

    @JsonProperty("request_id")
    private Integer requestId;

    @JsonProperty("pm_approval")
    private  String pmApproval;

    @JsonProperty("qs_approval")
    private Boolean qsApproval;

    @JsonProperty("request_type")
    private String requestType;

    private LocalDate date;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("site_supervisor_id")
    private String siteSupervisorId;

    @JsonProperty("qs_id")
    private String qsId;

    @JsonProperty("pm_id")
    private String pmId;

    @JsonProperty("is_received")
    private String isReceived;
}
