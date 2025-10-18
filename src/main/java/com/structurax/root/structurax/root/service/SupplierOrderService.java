package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ProjectOrderResponseDTO;
import com.structurax.root.structurax.root.dto.ProjectOrdersDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;

import java.util.List;

public interface SupplierOrderService {

    List<PurchaseOrderDTO> getAllOrders();
    PurchaseOrderDTO getOrderById(Integer orderId);
    List<PurchaseOrderDTO> getOrdersBySupplierId(Integer supplierId);
    ProjectOrdersDTO getOrdersByProjectId(String projectId);
    ProjectOrderResponseDTO getProjectOrderDetails(String projectId);
    void updateOrderStatus(Integer orderId, Integer orderStatus);
//    PurchaseOrderDTO updateOrder(PurchaseOrderDTO orderDTO);
//    void updatePaymentStatus(Integer orderId, String paymentStatus);
//    void deleteOrder(Integer orderId);
}
