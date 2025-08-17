
package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO;

public interface SQSDAO {
    Project1DTO getProjectById(String id);
    boolean assignQsToProject(String pid, String eid);

    List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> getProjectsWithoutQs();
    java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> getQSOfficers();
    
    /**
     * Get all requests in the system
     */
    List<RequestSiteResourcesDTO> getAllRequests();
}
