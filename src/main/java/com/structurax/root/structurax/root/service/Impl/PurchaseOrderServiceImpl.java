package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.PurchaseOrderDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.PurchaseOrderService;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    
    @Autowired
    private PurchaseOrderDAO purchaseOrderDAO;

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(Integer orderId) {
        return purchaseOrderDAO.getPurchaseOrderById(orderId);
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByProjectId(String projectId) {
        return purchaseOrderDAO.getPurchaseOrdersByProjectId(projectId);
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Integer supplierId) {
        return purchaseOrderDAO.getPurchaseOrdersBySupplierId(supplierId);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderDAO.getAllPurchaseOrders();
    }

    @Override
    public List<OrderItemDTO> getPurchaseOrderItemsByOrderId(Integer orderId) {
        return purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrderDTO purchaseOrder) {
        return purchaseOrderDAO.updatePurchaseOrder(purchaseOrder);
    }

    @Override
    public boolean updatePurchaseOrderStatus(Integer orderId, Boolean orderStatus) {
        return purchaseOrderDAO.updatePurchaseOrderStatus(orderId, orderStatus);
    }

    @Override
    public boolean updatePaymentStatus(Integer orderId, String paymentStatus) {
        return purchaseOrderDAO.updatePaymentStatus(orderId, paymentStatus);
    }

    @Override
    public boolean deletePurchaseOrder(Integer orderId) {
        // First delete all items, then the order
        purchaseOrderDAO.deletePurchaseOrderItems(orderId);
        return purchaseOrderDAO.deletePurchaseOrder(orderId);
    }
}
