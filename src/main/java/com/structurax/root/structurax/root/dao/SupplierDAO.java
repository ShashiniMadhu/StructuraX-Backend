package com.structurax.root.structurax.root.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;

public interface SupplierDAO {

    // Existing catalog methods
    CatalogDTO createCatalog(CatalogDTO catalogDTO);
    List<CatalogDTO> getAllCatalogs();
    void deleteCatalog(Integer itemId);
    CatalogDTO getCatalogById(Integer itemId);

    // Existing supplier methods
    Optional<SupplierDTO> findByEmail(String email);
    SupplierDTO getSupplierById(Integer supplierId);
    List<SupplierDTO> getAllSuppliers();

    // New purchase order and related methods
    List<PurchaseOrderDTO> getAllOrders();
    PurchaseOrderDTO getOrderById(Integer orderId);
    List<PurchaseOrderDTO> getOrdersBySupplierId(Integer supplierId);
    List<PurchaseOrderDTO> getOrdersByProjectId(String projectId);
    ProjectDTO getProjectById(String projectId);
    PurchaseOrderDTO getOrderByProjectId(String projectId);
    BigDecimal getQuotationAmountByResponseId(Integer responseId);
    List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId);

}