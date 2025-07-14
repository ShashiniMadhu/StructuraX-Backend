package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;

import java.util.List;

public interface DesignerDAO {
    DesignFullDTO getDesignById(String id);

    List<DesignFullDTO> getAllDesigns();

    DesignDTO deleteDesign(String id);

    DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign);

    List<ClientDTO> getClientsWithoutPlan();

    DesignDTO initializingDesign(DesignDTO designDTO);
}
