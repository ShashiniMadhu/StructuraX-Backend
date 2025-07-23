package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestSiteResourcesDTO {

    private Integer requestId;
    private Boolean pmApproval;
    private Boolean qsApproval;
    private String requestType;
    private Date date;
    private String projectId;
    private String siteSupervisorId;
    private String qsId;
    private Boolean isReceived;
    private List<SiteResourceDTO> materials;


   }