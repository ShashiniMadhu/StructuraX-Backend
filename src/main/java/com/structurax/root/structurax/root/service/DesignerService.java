package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;

import java.util.List;

public interface DesignerService {

    /**
     * Get a design by ID with full details including client name
     * @param id Design ID
     * @return DesignFullDTO with complete information
     */
    DesignFullDTO getDesignById(String id);

    /**
     * Get all designs with full details
     * @return List of all designs with client information
     */
    List<DesignFullDTO> getAllDesigns();

    /**
     * Delete a design by ID
     * @param id Design ID to delete
     * @return The deleted design data
     */
    DesignDTO deleteDesign(String id);

    /**
     * Update an existing design
     * @param id Design ID
     * @param updatedDesign Updated design information
     * @return Updated design with full details
     */
    DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign);

    /**
     * Get all clients who don't have a plan
     * @return List of clients without assigned plans
     */
    List<ClientDTO> getClientsWithoutPlan();

    /**
     * Create a new design
     * @param designDTO Design data
     * @return Created design with generated ID
     */
    DesignDTO initializingDesign(DesignDTO designDTO);

    List<DesignFullDTO> getOngoingProjects();

    List<DesignFullDTO> getCompletedProjects();

    DesignFullDTO markProjectAsComplete(String id);
}