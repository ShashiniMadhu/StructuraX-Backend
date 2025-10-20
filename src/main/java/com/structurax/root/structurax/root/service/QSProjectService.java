package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.DailyUpdateDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientDTO;
import com.structurax.root.structurax.root.dto.SiteVisitWithParticipantsDTO;

public interface QSProjectService {
    /**
     * Get all projects with client details and images
     * @return list of projects with client information and images
     */
    List<ProjectWithClientDTO> getAllProjects();
    
    /**
     * Get projects related to a specific employee ID with client details and images
     * @param employeeId the employee ID (can be QS, PM, or SS)
     * @return list of projects assigned to the employee with client information and images
     */
    List<ProjectWithClientDTO> getProjectsByEmployeeId(String employeeId);
    
    /**
     * Get design file details for a specific project
     * @param projectId the project ID
     * @return list of design files for the project
     */
    List<DesignDTO> getDesignFilesByProjectId(String projectId);
    
    /**
     * Get daily updates for a specific project
     * @param projectId the project ID
     * @return list of daily updates for the project
     */
    List<DailyUpdateDTO> getDailyUpdatesByProjectId(String projectId);
    
    /**
     * Get site visits with participants for a specific project
     * @param projectId the project ID
     * @return list of site visits with participants for the project
     */
    List<SiteVisitWithParticipantsDTO> getSiteVisitsByProjectId(String projectId);
}
