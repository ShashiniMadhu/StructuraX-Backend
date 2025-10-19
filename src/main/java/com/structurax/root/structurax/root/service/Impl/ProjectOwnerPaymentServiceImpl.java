package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectOwnerPaymentDAO;
import com.structurax.root.structurax.root.dto.PaymentConfirmationDTO;
import com.structurax.root.structurax.root.dto.PaymentDTO;
import com.structurax.root.structurax.root.service.ProjectOwnerPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectOwnerPaymentServiceImpl implements ProjectOwnerPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerPaymentServiceImpl.class);
    private final ProjectOwnerPaymentDAO paymentDAO;

    public ProjectOwnerPaymentServiceImpl(ProjectOwnerPaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    // ========== PAYMENT METHODS ==========

    @Override
    public List<PaymentDTO> getPaymentsByProjectId(String projectId) {
        logger.info("Fetching payments for project_id: {}", projectId);
        return paymentDAO.getPaymentsByProjectId(projectId);
    }

    @Override
    public PaymentDTO getPaymentById(Integer paymentId) {
        logger.info("Fetching payment by payment_id: {}", paymentId);
        PaymentDTO payment = paymentDAO.getPaymentById(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        return payment;
    }

    @Override
    public List<PaymentDTO> getPaymentsByInvoiceId(Integer invoiceId) {
        logger.info("Fetching payments for invoice_id: {}", invoiceId);
        return paymentDAO.getPaymentsByInvoiceId(invoiceId);
    }

    @Override
    public List<PaymentDTO> getPendingPayments(String projectId) {
        logger.info("Fetching pending payments for project_id: {}", projectId);
        return paymentDAO.getPendingPayments(projectId);
    }

    @Override
    public List<PaymentDTO> getPaidPayments(String projectId) {
        logger.info("Fetching paid payments for project_id: {}", projectId);
        return paymentDAO.getPaidPayments(projectId);
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        logger.info("Creating payment for invoice_id: {}", paymentDTO.getInvoiceId());

        // Validation
        if (paymentDTO.getInvoiceId() == 0) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("Valid payment amount is required");
        }
        if (paymentDTO.getDuedate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }

        // Set default status if not provided
        if (paymentDTO.getStatus() == null || paymentDTO.getStatus().isEmpty()) {
            paymentDTO.setStatus("Pending");
        }

        return paymentDAO.createPayment(paymentDTO);
    }

    @Override
    public void updatePaymentStatus(Integer paymentId, String status) {
        logger.info("Updating payment status to {} for payment_id: {}", status, paymentId);

        // Validation
        if (!isValidPaymentStatus(status)) {
            throw new IllegalArgumentException("Invalid payment status: " + status);
        }

        paymentDAO.updatePaymentStatus(paymentId, status);
    }

    // ========== PAYMENT CONFIRMATION METHODS ==========

    @Override
    public List<PaymentConfirmationDTO> getPaymentConfirmationsByProjectId(String projectId) {
        logger.info("Fetching payment confirmations for project_id: {}", projectId);
        return paymentDAO.getPaymentConfirmationsByProjectId(projectId);
    }

    @Override
    public PaymentConfirmationDTO getPaymentConfirmationById(Integer confirmationId) {
        logger.info("Fetching payment confirmation by confirmation_id: {}", confirmationId);
        PaymentConfirmationDTO confirmation = paymentDAO.getPaymentConfirmationById(confirmationId);
        if (confirmation == null) {
            throw new RuntimeException("Payment confirmation not found with ID: " + confirmationId);
        }
        return confirmation;
    }

    @Override
    public List<PaymentConfirmationDTO> getPaymentConfirmationsByPaymentId(Integer paymentId) {
        logger.info("Fetching payment confirmations for payment_id: {}", paymentId);
        return paymentDAO.getPaymentConfirmationsByPaymentId(paymentId);
    }

    @Override
    public PaymentConfirmationDTO createPaymentConfirmation(PaymentConfirmationDTO confirmationDTO) {
        logger.info("Creating payment confirmation for payment_id: {}", confirmationDTO.getPaymentId());

        // Validation
        if (confirmationDTO.getPaymentId() == 0) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        if (confirmationDTO.getProjectId() == null || confirmationDTO.getProjectId().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required");
        }
        if (confirmationDTO.getAmount() == null || confirmationDTO.getAmount() <= 0) {
            throw new IllegalArgumentException("Valid confirmation amount is required");
        }

        // Set default status if not provided
        if (confirmationDTO.getStatus() == null || confirmationDTO.getStatus().isEmpty()) {
            confirmationDTO.setStatus("Pending");
        }

        return paymentDAO.createPaymentConfirmation(confirmationDTO);
    }

    @Override
    public void updatePaymentConfirmationStatus(Integer confirmationId, String status) {
        logger.info("Updating payment confirmation status to {} for confirmation_id: {}", status, confirmationId);

        // Validation
        if (!isValidConfirmationStatus(status)) {
            throw new IllegalArgumentException("Invalid confirmation status: " + status);
        }

        paymentDAO.updatePaymentConfirmationStatus(confirmationId, status);
    }

    @Override
    public List<PaymentConfirmationDTO> getPendingConfirmations(String projectId) {
        logger.info("Fetching pending payment confirmations for project_id: {}", projectId);
        return paymentDAO.getPendingConfirmations(projectId);
    }

    // ========== HELPER METHODS ==========

    private boolean isValidPaymentStatus(String status) {
        return status != null && (
            status.equalsIgnoreCase("Pending") ||
            status.equalsIgnoreCase("Paid") ||
            status.equalsIgnoreCase("Overdue") ||
            status.equalsIgnoreCase("Cancelled")
        );
    }

    private boolean isValidConfirmationStatus(String status) {
        return status != null && (
            status.equalsIgnoreCase("Pending") ||
            status.equalsIgnoreCase("Approved") ||
            status.equalsIgnoreCase("Rejected")
        );
    }
}
