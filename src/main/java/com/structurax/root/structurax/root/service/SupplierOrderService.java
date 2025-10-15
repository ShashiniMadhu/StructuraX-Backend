package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;

import java.util.List;

public interface SupplierOrderService {

    List<PurchaseOrderDTO> getAllOrders();
    PurchaseOrderDTO getOrderById(Integer orderId);
    List<PurchaseOrderDTO> getOrdersBySupplierId(Integer supplierId);
    List<PurchaseOrderDTO> getOrdersByProjectId(String projectId);

}
