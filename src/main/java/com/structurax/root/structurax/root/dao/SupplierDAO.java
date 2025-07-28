package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.ClientOneDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierDAO {

    CatalogDTO createCatalog(CatalogDTO catalogDTO);

    List<CatalogDTO> getAllCatalogs();

    void deleteCatalog(Integer itemId);

    CatalogDTO getCatalogById(Integer itemId);

    Optional<SupplierDTO> findByEmail(String email);
}