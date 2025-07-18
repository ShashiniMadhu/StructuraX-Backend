package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.CatalogDTO;

import java.util.List;

public interface SupplierDAO {

    CatalogDTO createCatalog(CatalogDTO catalogDTO);

    List<CatalogDTO> getAllCatalogs();

    void deactivateCatalog(Integer catalogId);

    CatalogDTO getCatalogById(Integer id);
}