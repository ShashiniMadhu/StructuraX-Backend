package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO;

public interface QSService {
    /**
     * Get projects related to the given QS id.
     * @param qsId the QS id
     * @return list of projects
     */
    List<Project1DTO> getProjectsByQSId(String qsId);
    
    /**
     * Get projects with client and BOQ data related to the given QS id.
     * @param qsId the QS id
     * @return list of projects with client and BOQ data
     */
    List<ProjectWithClientAndBOQDTO> getProjectsWithClientAndBOQByQSId(String qsId);
    
    /**
     * Get site resource requests assigned to the given QS id.
     * @param qsId the QS id
     * @return list of site resource requests
     */
    List<RequestSiteResourcesDTO> getRequestsByQSId(String qsId);
    
    /**
     * Update the QS approval status for a site resource request.
     * @param requestId the request id
     * @param qsApproval the QS approval status
     * @return true if update was successful, false otherwise
     */
    boolean updateRequestQSApproval(Integer requestId, String qsApproval);
}
