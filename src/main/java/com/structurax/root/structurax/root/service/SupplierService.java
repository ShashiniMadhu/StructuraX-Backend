package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface SupplierService {
    CatalogDTO createCatalog(CatalogDTO dto);
    List<CatalogDTO> getAllCatalogs();
    void deleteCatalog(Integer itemId);
    CatalogDTO getCatalogById(Integer itemId);

    SupplierDTO getSupplierById(int supplierId);


}