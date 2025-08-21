package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;

public interface PurchaseOrderDAO {
    // Create purchase order
    Integer insertPurchaseOrder(PurchaseOrderDTO purchaseOrder);
    
    // Insert purchase order items
    boolean insertPurchaseOrderItems(Integer orderId, List<OrderItemDTO> items);
    
    // Read operations
    PurchaseOrderDTO getPurchaseOrderById(Integer orderId);
    List<PurchaseOrderDTO> getPurchaseOrdersByProjectId(String projectId);
    List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Integer supplierId);
    List<PurchaseOrderDTO> getAllPurchaseOrders();
    List<PurchaseOrderDTO> getPurchaseOrdersByQsId(String qsId);
    
    // Get purchase order items
    List<OrderItemDTO> getPurchaseOrderItemsByOrderId(Integer orderId);
    
    // Update operations
    boolean updatePurchaseOrder(PurchaseOrderDTO purchaseOrder);
    boolean updatePurchaseOrderStatus(Integer orderId, Boolean orderStatus);
    boolean updatePaymentStatus(Integer orderId, String paymentStatus);
    
    // Delete operations
    boolean deletePurchaseOrder(Integer orderId);
    boolean deletePurchaseOrderItems(Integer orderId);
}
