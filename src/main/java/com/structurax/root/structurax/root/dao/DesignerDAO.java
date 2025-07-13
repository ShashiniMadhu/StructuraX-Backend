package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DesignerDAO {
    DesignFullDTO getDesignById(String id);

    List<DesignFullDTO> getAllDesigns();

    DesignDTO deleteDesign(String id);

    DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign);
}
