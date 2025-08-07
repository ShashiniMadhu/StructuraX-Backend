
package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
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
    
    // BOQ management methods for SQS
    /**
     * Get all BOQs in the system with their items
     */
    List<BOQWithItemsDTO> getAllBOQs();
    
    /**
     * Get all BOQs with project information (better for SQS overview)
     */
    List<BOQWithProjectDTO> getAllBOQsWithProjectInfo();
    
    /**
     * Update BOQ with items (SQS can edit any BOQ)
     */
    boolean updateBOQWithItems(BOQWithItemsDTO boqWithItems);
    
    /**
     * Update BOQ status (approve/reject)
     */
    boolean updateBOQStatus(String boqId, BOQDTO.Status status);
    
    /**
     * Get BOQ by ID with items
     */
    BOQWithItemsDTO getBOQWithItemsById(String boqId);
}
