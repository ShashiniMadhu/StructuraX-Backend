package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.SupplierHistoryDTO;
import com.structurax.root.structurax.root.service.SupplierHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierHistoryServiceImpl implements SupplierHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierHistoryServiceImpl.class);

    private final SupplierDAO supplierDAO;

    public SupplierHistoryServiceImpl(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    @Override
    public List<SupplierHistoryDTO> getAllHistory() {
        logger.info("Service: fetching all supplier history");
        return supplierDAO.getAllSupplierHistory();
    }

    @Override
    public SupplierHistoryDTO getHistoryById(Integer historyId) {
        logger.info("Service: fetching supplier history by id={}", historyId);
        return supplierDAO.getSupplierHistoryById(historyId);
    }

    @Override
    public List<SupplierHistoryDTO> getHistoryBySupplierId(Integer supplierId) {
        logger.info("Service: fetching supplier history for supplierId={}", supplierId);
        return supplierDAO.getSupplierHistoryBySupplierId(supplierId);
    }

    @Override
    public List<SupplierHistoryDTO> getHistoryByOrderId(Integer orderId) {
        logger.info("Service: fetching supplier history for orderId={}", orderId);
        return supplierDAO.getSupplierHistoryByOrderId(orderId);
    }

    @Override
    public SupplierHistoryDTO createHistory(SupplierHistoryDTO historyDTO) {
        logger.info("Service: creating supplier history for supplierId={}", historyDTO.getSupplierId());
        return supplierDAO.createSupplierHistory(historyDTO);
    }

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderIdAndSupplierId(Integer orderId, Integer supplierId) {
        logger.info("Service: fetching order items for orderId={} and supplierId={}", orderId, supplierId);
        return supplierDAO.getOrderItemsByOrderIdAndSupplierId(orderId, supplierId);
    }
}
