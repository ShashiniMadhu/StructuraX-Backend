package com.structurax.root.structurax.root.dao;

import java.util.List;
import java.util.Optional;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;

public interface SupplierDAO {

    CatalogDTO createCatalog(CatalogDTO catalogDTO);

    List<CatalogDTO> getAllCatalogs();

    void deleteCatalog(Integer itemId);

    CatalogDTO getCatalogById(Integer itemId);

    Optional<SupplierDTO> findByEmail(String email);
    
    SupplierDTO getSupplierById(Integer supplierId);
}