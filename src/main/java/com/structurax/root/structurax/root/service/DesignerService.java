package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DesignerService {
    DesignFullDTO getDesignById(String id);

    List<DesignFullDTO> getAllDesigns();

    DesignDTO deleteDesign(String id);

    DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign);

    List<ClientDTO> getClientsWithoutPlan();

    DesignDTO initializingDesign(DesignDTO designDTO);
}
