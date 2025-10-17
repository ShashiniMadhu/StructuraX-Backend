package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.SupplierPaymentDTO;
import com.structurax.root.structurax.root.service.SupplierPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierPaymentServiceImpl implements SupplierPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierPaymentServiceImpl.class);

    private final SupplierDAO supplierDAO;

    public SupplierPaymentServiceImpl(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    @Override
    public List<SupplierPaymentDTO> getAllPayments() {
        logger.info("Service: Fetching all supplier payments");
        return supplierDAO.getAllSupplierPayments();
    }

    @Override
    public void markAsPaid(Integer paymentId) {
        logger.info("Service: Marking payment with ID {} as paid", paymentId);
        supplierDAO.updatePaymentStatusToPaid(paymentId);
    }
}
