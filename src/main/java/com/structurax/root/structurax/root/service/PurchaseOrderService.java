package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;

public interface PurchaseOrderService {
    // Read operations
    PurchaseOrderDTO getPurchaseOrderById(Integer orderId);
    List<PurchaseOrderDTO> getPurchaseOrdersByProjectId(String projectId);
    List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Integer supplierId);
    List<PurchaseOrderDTO> getAllPurchaseOrders();
    
    // Get purchase orders by QS ID
    List<PurchaseOrderDTO> getPurchaseOrdersByQsId(String qsId);
    
    // Get purchase order items
    List<OrderItemDTO> getPurchaseOrderItemsByOrderId(Integer orderId);
    
    // Update operations
    boolean updatePurchaseOrder(PurchaseOrderDTO purchaseOrder);
    boolean updatePurchaseOrderStatus(Integer orderId, Boolean orderStatus);
    boolean updatePaymentStatus(Integer orderId, String paymentStatus);
    
    // Delete operations
    boolean deletePurchaseOrder(Integer orderId);
}
