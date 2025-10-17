package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.dto.SupplierLoginDTO;
import com.structurax.root.structurax.root.dto.SupplierResponseDTO;

public interface SupplierService {
    CatalogDTO createCatalog(CatalogDTO dto);
    List<CatalogDTO> getAllCatalogs();
    void deleteCatalog(Integer itemId);
    CatalogDTO getCatalogById(Integer itemId);

    SupplierResponseDTO login(SupplierLoginDTO supplierDTO);
    
    List<SupplierDTO> getAllSuppliers();
    
    SupplierDTO getSupplierById(Integer supplierId);
}