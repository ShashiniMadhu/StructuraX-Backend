package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.PaymentConfirmationDTO;
import com.structurax.root.structurax.root.dto.PaymentDTO;

import java.util.List;

public interface ProjectOwnerPaymentDAO {

    // ========== PAYMENT METHODS ==========

    /**
     * Get all payments for a specific project
     */
    List<PaymentDTO> getPaymentsByProjectId(String projectId);

    /**
     * Get a single payment by payment ID
     */
    PaymentDTO getPaymentById(Integer paymentId);

    /**
     * Get all payments by invoice ID
     */
    List<PaymentDTO> getPaymentsByInvoiceId(Integer invoiceId);

    /**
     * Get all pending payments for a project
     */
    List<PaymentDTO> getPendingPayments(String projectId);

    /**
     * Get all paid payments for a project
     */
    List<PaymentDTO> getPaidPayments(String projectId);

    /**
     * Create a new payment
     */
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    /**
     * Update payment status
     */
    void updatePaymentStatus(Integer paymentId, String status);

    // ========== PAYMENT CONFIRMATION METHODS ==========

    /**
     * Get all payment confirmations for a specific project
     */
    List<PaymentConfirmationDTO> getPaymentConfirmationsByProjectId(String projectId);

    /**
     * Get a single payment confirmation by confirmation ID
     */
    PaymentConfirmationDTO getPaymentConfirmationById(Integer confirmationId);

    /**
     * Get all payment confirmations by payment ID
     */
    List<PaymentConfirmationDTO> getPaymentConfirmationsByPaymentId(Integer paymentId);

    /**
     * Create a new payment confirmation
     */
    PaymentConfirmationDTO createPaymentConfirmation(PaymentConfirmationDTO confirmationDTO);

    /**
     * Update payment confirmation status
     */
    void updatePaymentConfirmationStatus(Integer confirmationId, String status);

    /**
     * Get all pending payment confirmations for a project
     */
    List<PaymentConfirmationDTO> getPendingConfirmations(String projectId);
}
