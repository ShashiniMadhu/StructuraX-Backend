package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/catalog")
    public ResponseEntity<CatalogDTO> createCatalog(@RequestBody CatalogDTO dto) {
        CatalogDTO created = supplierService.createCatalog(dto);
        return ResponseEntity.ok(created);
    }

//    @GetMapping("/catalog")
//    public ResponseEntity<List<CatalogDTO>> getAllCatalogs() {
//        List<CatalogDTO> catalogs = supplierService.getAllCatalogs();
//        return ResponseEntity.ok(catalogs);
//    }
//
//    @DeleteMapping("/catalog/{catalogId}")
//    public ResponseEntity<Void> deactivateCatalog(@PathVariable Integer catalogId) {
//        supplierService.deactivateCatalog(catalogId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/catalog/{id}")
//    public ResponseEntity<CatalogDTO> getCatalogById(@PathVariable Integer id) {
//        CatalogDTO catalog = supplierService.getCatalogById(id);
//        return ResponseEntity.ok(catalog);
//    }
}