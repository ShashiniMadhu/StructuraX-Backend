package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.ProjectOrdersDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.SupplierOrderService;
import com.structurax.root.structurax.root.dto.ProjectOrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SupplierOrderServiceImpl implements SupplierOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierOrderServiceImpl.class);

    private final SupplierDAO supplierDAO;

    public SupplierOrderServiceImpl(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

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
    public ProjectOrdersDTO getOrdersByProjectId(String projectId) {
        logger.info("Service: Fetching orders for project ID: {}", projectId);
        ProjectDTO project = supplierDAO.getProjectById(projectId);
        List<PurchaseOrderDTO> orders = supplierDAO.getOrdersByProjectId(projectId);
        return new ProjectOrdersDTO(project, orders);
    }

    @Override
    public ProjectOrderResponseDTO getProjectOrderDetails(String projectId) {
        logger.info("Service: Fetching project order details for project ID: {}", projectId);

        // Fetch data from DAO
        ProjectDTO project = supplierDAO.getProjectById(projectId);
        PurchaseOrderDTO purchaseOrder = supplierDAO.getOrderByProjectId(projectId);
        BigDecimal amount = supplierDAO.getQuotationAmountByResponseId(purchaseOrder.getResponseId());
        List<OrderItemDTO> orderItems = supplierDAO.getOrderItemsByOrderId(purchaseOrder.getOrderId());

        // Build response
        ProjectOrderResponseDTO response = new ProjectOrderResponseDTO();
        response.setProjectId(project.getProjectId());
        response.setProjectName(project.getName());
        response.setOrderId(purchaseOrder.getOrderId());
        response.setOrderDate(purchaseOrder.getOrderDate());
        response.setStatus(purchaseOrder.getStatus());
        response.setAmount(amount);
        response.setOrderItems(orderItems);

        return response;
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer orderStatus) {
        logger.info("Service: Updating order status for order ID: {} to {}", orderId, orderStatus);
        supplierDAO.updateOrderStatus(orderId, orderStatus);
    }
}
