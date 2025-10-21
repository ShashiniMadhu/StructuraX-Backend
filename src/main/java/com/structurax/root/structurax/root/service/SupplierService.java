package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;

public interface SupplierService {
    CatalogDTO createCatalog(CatalogDTO dto);
    List<CatalogDTO> getAllCatalogs();
    void deleteCatalog(Integer itemId);
    CatalogDTO getCatalogById(Integer itemId);
    SupplierDTO getSupplierById(int supplierId);
    List<SupplierDTO> getAllSuppliers();
}