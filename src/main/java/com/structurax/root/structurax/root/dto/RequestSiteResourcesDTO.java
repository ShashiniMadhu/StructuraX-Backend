package com.structurax.root.structurax.root.dto;

import java.sql.Date;
import java.util.List;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestSiteResourcesDTO {

    private int requestId;
    private String pmApproval;
    private String qsApproval;
    private String requestType;

    @Future(message = "Due date must be in the future")
    private Date date;
    private String projectId;
    private String siteSupervisorId;
    private String qsId;
    private String pmId;
    private Boolean isReceived;
    private List<SiteResourceDTO> materials;
    
    // Additional fields for enhanced request details
    private String siteSupervisorName;
    private String projectName;
    private String qsOfficerName;


    public RequestSiteResourcesDTO(int requestId, String pmApproval, String qsApproval,String requestType, Date date, String projectId, String siteSupervisorId, String qsId, String pmId,Boolean isReceived) {
        this.requestId = requestId;
        this.pmApproval=pmApproval;
        this.qsApproval=qsApproval;
        this.requestType=requestType;
        this.date=date;
        this.projectId = projectId;
        this.siteSupervisorId = siteSupervisorId;
        this.qsId = qsId;
        this.pmId = pmId;
        this.isReceived = isReceived;
    }

    // Constructor with enhanced details including site supervisor name and project name
    public RequestSiteResourcesDTO(int requestId, String pmApproval, String qsApproval, String requestType,
                                   Date date, String projectId, String siteSupervisorId, String qsId, 
                                   Boolean isReceived, String siteSupervisorName, String projectName) {
        this.requestId = requestId;
        this.pmApproval = pmApproval;
        this.qsApproval = qsApproval;
        this.requestType = requestType;
        this.date = date;
        this.projectId = projectId;
        this.siteSupervisorId = siteSupervisorId;
        this.qsId = qsId;
        this.isReceived = isReceived;
        this.siteSupervisorName = siteSupervisorName;
        this.projectName = projectName;
    }

    // Constructor with all enhanced details including QS officer name (for SQS use)
    public RequestSiteResourcesDTO(int requestId, String pmApproval, String qsApproval, String requestType,
                                   Date date, String projectId, String siteSupervisorId, String qsId, 
                                   Boolean isReceived, String siteSupervisorName, String projectName, String qsOfficerName) {
        this.requestId = requestId;
        this.pmApproval = pmApproval;
        this.qsApproval = qsApproval;
        this.requestType = requestType;
        this.date = date;
        this.projectId = projectId;
        this.siteSupervisorId = siteSupervisorId;
        this.qsId = qsId;
        this.isReceived = isReceived;
        this.siteSupervisorName = siteSupervisorName;
        this.projectName = projectName;
        this.qsOfficerName = qsOfficerName;
    }
   }