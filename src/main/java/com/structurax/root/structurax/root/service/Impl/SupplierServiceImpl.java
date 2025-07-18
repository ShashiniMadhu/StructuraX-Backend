package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierDAO supplierDAO;

    @Override
    public CatalogDTO createCatalog(CatalogDTO catalogDTO) {
        return supplierDAO.createCatalog(catalogDTO);
    }

    @Override
    public List<CatalogDTO> getAllCatalogs() {
        return supplierDAO.getAllCatalogs();
    }

    @Override
    public void deactivateCatalog(Integer catalogId) {
        supplierDAO.deactivateCatalog(catalogId);
    }

    @Override
    public CatalogDTO getCatalogById(Integer id) {
        return supplierDAO.getCatalogById(id);
    }
}