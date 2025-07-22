package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.CatalogDTO;

import java.util.List;

public interface SupplierDAO {

    CatalogDTO createCatalog(CatalogDTO catalogDTO);

    List<CatalogDTO> getAllCatalogs();

    void deleteCatalog(Integer itemId);

    CatalogDTO getCatalogById(Integer itemId);
}