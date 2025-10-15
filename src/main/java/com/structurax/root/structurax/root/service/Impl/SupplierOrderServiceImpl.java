package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.SupplierOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierOrderServiceImpl implements SupplierOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierOrderServiceImpl.class);

    @Autowired
    private SupplierDAO supplierDAO;

    @Override
    public List<PurchaseOrderDTO> getAllOrders() {
        logger.info("Service: Fetching all purchase orders");
        return supplierDAO.getAllOrders();
    }

    @Override
    public PurchaseOrderDTO getOrderById(Integer orderId) {
        logger.info("Service: Fetching order with ID: {}", orderId);
        return supplierDAO.getOrderById(orderId);
    }

    @Override
    public List<PurchaseOrderDTO> getOrdersBySupplierId(Integer supplierId) {
        logger.info("Service: Fetching orders for supplier ID: {}", supplierId);
        return supplierDAO.getOrdersBySupplierId(supplierId);
    }

    @Override
    public List<PurchaseOrderDTO> getOrdersByProjectId(String projectId) {
        logger.info("Service: Fetching orders for project ID: {}", projectId);
        return supplierDAO.getOrdersByProjectId(projectId);
    }


}
