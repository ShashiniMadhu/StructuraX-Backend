package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import java.util.List;

public interface SupplierService {
    CatalogDTO createCatalog(CatalogDTO dto);
    List<CatalogDTO> getAllCatalogs();
    void deactivateCatalog(Integer catalogId);
    CatalogDTO getCatalogById(Integer id);
}