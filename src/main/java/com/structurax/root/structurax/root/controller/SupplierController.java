package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/supplier")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("Supplier Controller is working!");
    }

    @PostMapping("/catalog")
    public ResponseEntity<?> createCatalog(@RequestBody CatalogDTO catalogDTO) {
        logger.info("Received request to create catalog: {}", catalogDTO.getName());
        try {
            CatalogDTO createdCatalog = supplierService.createCatalog(catalogDTO);
            logger.info("Catalog created successfully with item_id: {}", createdCatalog.getItemId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCatalog);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while creating catalog: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while creating catalog: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating catalog: " + e.getMessage());
        }
    }

    @GetMapping("/catalogs")
    public ResponseEntity<?> getAllCatalogs() {
        logger.info("Received request to get all catalogs");
        try {
            List<CatalogDTO> catalogs = supplierService.getAllCatalogs();
            logger.info("Successfully retrieved {} catalogs", catalogs.size());
            return ResponseEntity.ok(catalogs);
        } catch (Exception e) {
            logger.error("Error retrieving catalogs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving catalogs: " + e.getMessage());
        }
    }

    @GetMapping("/catalog/{itemId}")
    public ResponseEntity<?> getCatalogById(@PathVariable Integer itemId) {
        logger.info("Received request to get catalog with item_id: {}", itemId);
        try {
            CatalogDTO catalog = supplierService.getCatalogById(itemId);
            logger.info("Successfully retrieved catalog with item_id: {}", itemId);
            return ResponseEntity.ok(catalog);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while retrieving catalog: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Catalog not found with item_id: {}", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving catalog: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving catalog: " + e.getMessage());
        }
    }

    @DeleteMapping("/catalog/{itemId}")
    public ResponseEntity<?> deleteCatalog(@PathVariable Integer itemId) {
        logger.info("Received request to delete catalog with item_id: {}", itemId);
        try {
            supplierService.deleteCatalog(itemId);
            logger.info("Catalog with item_id {} deleted successfully", itemId);
            return ResponseEntity.ok("Catalog deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while deleting catalog: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Catalog not found for deletion with item_id: {}", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while deleting catalog: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting catalog: " + e.getMessage());
        }
    }

    @PutMapping("/catalog/{itemId}")
    public ResponseEntity<?> updateCatalog(@PathVariable Integer itemId, @RequestBody CatalogDTO catalogDTO) {
        logger.info("Received request to update catalog with item_id: {}", itemId);
        try {
            // Set the itemId from path variable
            catalogDTO.setItemId(itemId);

            // First check if catalog exists
            supplierService.getCatalogById(itemId);

            // Delete the old catalog and create new one (since we don't have update method)
            supplierService.deleteCatalog(itemId);
            CatalogDTO updatedCatalog = supplierService.createCatalog(catalogDTO);

            logger.info("Catalog with item_id {} updated successfully", itemId);
            return ResponseEntity.ok(updatedCatalog);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while updating catalog: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Catalog not found for update with item_id: {}", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating catalog: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating catalog: " + e.getMessage());
        }
    }
}