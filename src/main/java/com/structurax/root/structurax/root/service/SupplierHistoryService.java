package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.SupplierHistoryDTO;

import java.util.List;

public interface SupplierHistoryService {
    List<SupplierHistoryDTO> getAllHistory();
    SupplierHistoryDTO getHistoryById(Integer historyId);
    List<SupplierHistoryDTO> getHistoryBySupplierId(Integer supplierId);
    List<SupplierHistoryDTO> getHistoryByOrderId(Integer orderId);
    SupplierHistoryDTO createHistory(SupplierHistoryDTO historyDTO);

    // Item retrieval methods
    List<OrderItemDTO> getOrderItemsByOrderIdAndSupplierId(Integer orderId, Integer supplierId);
}
