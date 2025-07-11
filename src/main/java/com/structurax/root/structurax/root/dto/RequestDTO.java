package com.structurax.root.structurax.root.dto;

import java.sql.Date;
import java.util.List;

public class RequestDTO {

    private Integer requestId;

    private String approvalStatus;
    private Date date;
    private List<SiteResourceDTO> materials;
    private int projectId;
    private String requestType;
    // ✅ No-args constructor (required by frameworks like Jackson)
    public RequestDTO() {}

    // ✅ Full constructor
    public RequestDTO(Integer requestId, String approvalStatus, String requestType, Date date, Integer projectId) {
        this.requestId = requestId;
        this.approvalStatus = approvalStatus;
        this.requestType = requestType;
        this.date = date;
        this.projectId = projectId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public List<SiteResourceDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<SiteResourceDTO> materials) {
        this.materials = materials;
    }
}
