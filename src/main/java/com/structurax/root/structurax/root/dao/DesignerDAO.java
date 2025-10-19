package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;

import java.util.List;

public interface DesignerDAO {

    /**
     * Fetch a single design by ID with client information
     * @param id Design ID
     * @return DesignFullDTO with client details, or null if not found
     */
    DesignFullDTO getDesignById(String id);

    /**
     * Fetch all designs with their associated client information
     * @return List of all designs
     */
    List<DesignFullDTO> getAllDesigns();

    /**
     * Delete a design by ID
     * @param id Design ID to delete
     * @return The deleted design details
     */
    DesignDTO deleteDesign(String id);

    /**
     * Update an existing design
     * @param id Design ID to update
     * @param updatedDesign Updated design data
     * @return Updated design with full details
     */
    DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign);

    /**
     * Fetch all clients who don't have a plan assigned
     * Clients are selected where users.type = 'Client' AND client.is_have_plan = 0
     * @return List of clients without plans
     */
    List<ClientDTO> getClientsWithoutPlan();

    /**
     * Create a new design
     * @param designDTO Design data to insert
     * @return Created design with generated ID
     */
    DesignDTO initializingDesign(DesignDTO designDTO);
}