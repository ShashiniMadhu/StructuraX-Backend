package com.structurax.root.structurax.root.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.dto.SupplierHistoryDTO;
import com.structurax.root.structurax.root.dto.SupplierInvoiceDTO;
import com.structurax.root.structurax.root.dto.SupplierPaymentDTO;

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
    void updateOrderStatus(Integer orderId, Integer orderStatus);

    // Supplier Payment Methods
    List<SupplierPaymentDTO> getAllSupplierPayments();
    void updatePaymentStatusToPaid(Integer paymentId);

    // Supplier History Methods
    List<SupplierHistoryDTO> getAllSupplierHistory();
    SupplierHistoryDTO getSupplierHistoryById(Integer historyId);
    List<SupplierHistoryDTO> getSupplierHistoryBySupplierId(Integer supplierId);
    List<SupplierHistoryDTO> getSupplierHistoryByOrderId(Integer orderId);
    SupplierHistoryDTO createSupplierHistory(SupplierHistoryDTO historyDTO);

    // Item retrieval methods for orders and quotations
    List<OrderItemDTO> getOrderItemsByOrderIdAndSupplierId(Integer orderId, Integer supplierId);

    // Invoice Methods
    SupplierInvoiceDTO createInvoice(SupplierInvoiceDTO invoiceDTO);
    List<SupplierInvoiceDTO> getAllInvoices();
    SupplierInvoiceDTO getInvoiceById(Integer invoiceId);
    List<SupplierInvoiceDTO> getInvoicesBySupplierId(Integer supplierId);
    List<SupplierInvoiceDTO> getInvoicesByOrderId(Integer orderId);
    void updateInvoiceStatus(Integer invoiceId, String status);

}