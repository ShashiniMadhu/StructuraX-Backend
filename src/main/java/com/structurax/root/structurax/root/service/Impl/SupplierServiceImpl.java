package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.ClientDAO;
import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.dto.SupplierLoginDTO;
import com.structurax.root.structurax.root.dto.SupplierResponseDTO;
import com.structurax.root.structurax.root.service.SupplierService;
import com.structurax.root.structurax.root.util.JwtUtil;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;



    private static final Logger logger = LoggerFactory.getLogger(SupplierServiceImpl.class);

    @Autowired
    private SupplierDAO supplierDAO;

    @Override
    public SupplierResponseDTO login(SupplierLoginDTO loginDTO) {
        SupplierDTO supplierDTO = supplierDAO.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), supplierDTO.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateTokenForSupplier(supplierDTO.getEmail(), supplierDTO.getRole(), supplierDTO.getSupplier_id());

        return new SupplierResponseDTO(
                supplierDTO.getSupplier_id(),

                supplierDTO.getEmail(),
                supplierDTO.getRole(),
                token
        );
    }

    @Override
    public CatalogDTO createCatalog(CatalogDTO catalogDTO) {
        logger.info("Creating catalog with name: {}", catalogDTO.getName());

        // Validate required fields
        if (catalogDTO.getName() == null || catalogDTO.getName().trim().isEmpty()) {
            logger.error("Validation failed: Name is required");
            throw new IllegalArgumentException("Name is required");
        }
        if (catalogDTO.getRate() == null || catalogDTO.getRate() <= 0) { // Changed from BigDecimal comparison
            logger.error("Validation failed: Rate must be positive");
            throw new IllegalArgumentException("Rate must be positive");
        }
        if (catalogDTO.getCategory() == null || catalogDTO.getCategory().trim().isEmpty()) {
            logger.error("Validation failed: Category is required");
            throw new IllegalArgumentException("Category is required");
        }

        // Set default availability if not provided
        if (catalogDTO.getAvailability() == null) {
            catalogDTO.setAvailability(true);
        }

        try {
            CatalogDTO createdCatalog = supplierDAO.createCatalog(catalogDTO);
            logger.info("Catalog created successfully with item_id: {}", createdCatalog.getItemId());
            return createdCatalog;
        } catch (Exception e) {
            logger.error("Error creating catalog: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<CatalogDTO> getAllCatalogs() {
        logger.info("Retrieving all catalogs");
        try {
            List<CatalogDTO> catalogs = supplierDAO.getAllCatalogs();
            logger.info("Successfully retrieved {} catalogs", catalogs.size());
            return catalogs;
        } catch (Exception e) {
            logger.error("Error retrieving catalogs: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteCatalog(Integer itemId) {
        logger.info("Deleting catalog with item_id: {}", itemId);

        if (itemId == null || itemId <= 0) {
            logger.error("Validation failed: Valid item_id is required");
            throw new IllegalArgumentException("Valid item_id is required");
        }

        try {
            supplierDAO.deleteCatalog(itemId);
            logger.info("Catalog with item_id {} deleted successfully", itemId);
        } catch (Exception e) {
            logger.error("Error deleting catalog with item_id {}: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CatalogDTO getCatalogById(Integer itemId) {
        logger.info("Retrieving catalog with item_id: {}", itemId);

        if (itemId == null || itemId <= 0) {
            logger.error("Validation failed: Valid item_id is required");
            throw new IllegalArgumentException("Valid item_id is required");
        }

        try {
            CatalogDTO catalog = supplierDAO.getCatalogById(itemId);
            if (catalog == null) {
                logger.warn("Catalog not found with item_id: {}", itemId);
                throw new RuntimeException("Catalog not found with item_id: " + itemId);
            }
            logger.info("Successfully retrieved catalog with item_id: {}", itemId);
            return catalog;
        } catch (Exception e) {
            logger.error("Error retrieving catalog with item_id {}: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public List<SupplierDTO> getAllSuppliers() {
        logger.info("Retrieving all suppliers");
        try {
            List<SupplierDTO> suppliers = supplierDAO.getAllSuppliers();
            logger.info("Successfully retrieved {} suppliers", suppliers.size());
            return suppliers;
        } catch (Exception e) {
            logger.error("Error retrieving all suppliers: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public SupplierDTO getSupplierById(Integer supplierId) {
        logger.info("Retrieving supplier with supplier_id: {}", supplierId);
        
        if (supplierId == null || supplierId <= 0) {
            logger.error("Validation failed: Valid supplier_id is required");
            throw new IllegalArgumentException("Valid supplier_id is required");
        }
        
        try {
            SupplierDTO supplier = supplierDAO.getSupplierById(supplierId);
            if (supplier == null) {
                logger.warn("Supplier not found with supplier_id: {}", supplierId);
                return null;
            }
            logger.info("Successfully retrieved supplier with supplier_id: {}", supplierId);
            return supplier;
        } catch (Exception e) {
            logger.error("Error retrieving supplier with supplier_id {}: {}", supplierId, e.getMessage(), e);
            throw e;
        }
    }
}