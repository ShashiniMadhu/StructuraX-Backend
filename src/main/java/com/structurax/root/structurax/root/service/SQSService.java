
package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;

public interface SQSService {
    Project1DTO getProjectById(String id);
    boolean assignQsToProject(String pid, String eid);

    /**
     * Get projects where qs_id is null or empty, returning project name, category, and client_id.
     */
    List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> getProjectsWithoutQs();

    /**
     * Get all QS Officers from the employee table.
     */
    java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> getQSOfficers();
}
