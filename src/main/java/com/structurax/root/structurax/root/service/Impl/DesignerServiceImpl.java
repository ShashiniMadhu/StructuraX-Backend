package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dao.DesignerDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.service.DesignerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesignerServiceImpl implements DesignerService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private DesignerDAO designerDAO;

    @Override
    public DesignFullDTO getDesignById(String id) {
        DesignFullDTO design = designerDAO.getDesignById(id);
        logger.info("Design fetched: {}", design.getDesignId());
        return design;
    }

    @Override
    public List<DesignFullDTO> getAllDesigns() {
        logger.info("Fetching all designs");
        List<DesignFullDTO> designs = designerDAO.getAllDesigns();
        logger.info("Fetched {} designs", designs.size());
        return designs;
    }

    @Override
    public DesignDTO deleteDesign(String id) {
        DesignDTO design = designerDAO.deleteDesign(id);
        logger.info("Design deleted: {}", design.getDesignId());
        return design;
    }

    @Override
    public DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign) {
        DesignFullDTO design = designerDAO.updateDesign(id, updatedDesign);
        logger.info("Design updated successfully with ID: " + id);
        return design;
    }

    @Override
    public List<ClientDTO> getClientsWithoutPlan() {
        List<ClientDTO> clients = designerDAO.getClientsWithoutPlan();
        logger.info("Clients fetched successfully");
        return clients;
    }

    @Override
    public DesignDTO initializingDesign(DesignDTO designDTO) {
        DesignDTO design = designerDAO.initializingDesign(designDTO);
        logger.info("Design created successfully with ID: {}", design.getDesignId());
        return design;
    }

}
