package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;

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
}
